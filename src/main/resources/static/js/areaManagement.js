const addRowBtn = document.querySelector("#addRowBtn");
const saveCctvDataBtn = document.querySelector("#saveCctvDataBtn");

// 새로운 CCTV Info ROW 생성
addRowBtn.addEventListener("click", () => {
    const tableBody = document.getElementById('data-table-body');
    const tr = document.createElement('tr');
    // console.log(row);
    tr.innerHTML = `
                    <input type="hidden" name="id" value="" class="form-control" required>
                    <input type="hidden" name="city" value="${sltCity.value}" class="form-control" required>
                    <td><input type="text" name="name" value="" class="form-control" autocomplete="off" required></td>
                    <td><input type="text" name="cctvSrc" value="" class="form-control" autocomplete="off" required></td>
                    <td><input type="text" name="mapSrc" value="" class="form-control" autocomplete="off" required></td>
                    <td><select name="delYn" class="form-control text-center" required>
                        <option value="y">Y</option>
                        <option value="n">N</option>
                    </select></td>
                    <td style="display: flex; justify-content: center;">
                      <button class="btn btn-outline-danger btn-sm delete-btn" data-id="">삭제</button>
                    </td>
                `;

    // 삭제 버튼에 이벤트 리스너 등록
    const deleteBtn = tr.querySelector('.delete-btn');
    deleteBtn.addEventListener('click', function() {
        // tr(행) 삭제
        tr.remove();
    });

    /* 수정 중 */
    const city = tr.querySelector('input[name="city"]');
    city.value = document.querySelector("#slt-city").value;

    tableBody.appendChild(tr);
});

// 도시별 CCTV 데이터 가져오기
let originalData = [];
function getCctvData(cityName) {
    if(cityName === "total") {
        return;
    }

    const inData = {
        city: cityName
    }
    const param = new URLSearchParams(inData).toString();

    // API 호출로 DB에서 데이터 가져오기
    fetch('/api/admin/cctv-data?' + param)
        .then(response => response.json())
        .then(data => {
            originalData = JSON.parse(JSON.stringify(data));    // 원본 데이터
            const tableBody = document.getElementById('data-table-body');
            tableBody.innerHTML = ''; // 기존 내용 삭제

            data.forEach(row => {
                const tr = document.createElement('tr');
                tr.dataset.id = row.id; // 행에 id를 추가하여 추후 사용
                tr.innerHTML = `
                    <input type="hidden" name="id" value="${row.id}" class="form-control" required>
                    <input type="hidden" name="city" value="${cityName}" class="form-control" required>
                    <td><input type="text" name="name" value="${row.name}" class="form-control" autocomplete="off" required></td>
                    <td><input type="text" name="cctvSrc" value="${row.cctvSrc}" class="form-control" autocomplete="off" required></td>
                    <td><input type="text" name="mapSrc" value="${row.mapSrc}" class="form-control" autocomplete="off" required></td>
                    <td><select name="delYn" class="form-control text-center" required>
                        <option value="y">Y</option>
                        <option value="n">N</option>
                    </select></td>
                    <td style="display: flex; justify-content: center;">
                      <button class="btn btn-outline-danger btn-sm delete-btn" data-id="${row.id}">삭제</button>
                    </td>
                `;

                // 삭제 버튼에 이벤트 리스너 등록
                const deleteBtn = tr.querySelector('.delete-btn');
                deleteBtn.addEventListener('click', function(e) {

                    const targetCity = tr.querySelector('input[name="city"]').value;
                    const targetRowId = deleteBtn.getAttribute("data-id");

                    deleteCctvData(targetRowId, targetCity);

                    // tr(행) 삭제
                    // tr.remove();
                });

                const select = tr.querySelector('select[name="delYn"]');
                select.value = row.delYn;

                tableBody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error("Error fetching data:", error);
        });
}

/**
 * CCTV 정보 생성,수정
 * @type {HTMLElement}
 */
saveCctvDataBtn.addEventListener('click', function (event) {
    if(!confirm("저장하시겠습니까?")) {
        return false;
    }

    event.preventDefault();

    const updatedData = [];
    // 모든 행의 데이터 수집
    const rows = document.querySelectorAll('#data-table-body tr');
    rows.forEach((row, index) => {
        const inputs = row.querySelectorAll("input, select");
        const rowData = {};
        inputs.forEach(input => {
            rowData[input.name] = input.value;
        });

        // 변경된 데이터만 수집
        let tempRowData = { ...rowData };
        let tempOriginalData = { ...originalData[index] };
        // tempOriginalData.city = tempOriginalData.city.engName;

        if ((tempRowData.cctvSrc !== tempOriginalData.cctvSrc) || (tempRowData.delYn !== tempOriginalData.delYn) ||
            (tempRowData.name !== tempOriginalData.name) || (tempRowData.mapSrc !== tempOriginalData.mapSrc)) {
            rowData["id"] = row.dataset.id;
            updatedData.push(rowData);
        }

    });

    if (updatedData.length === 0) {
        alert("수정된 내용이 없습니다.");
        return;
    }

    const selectedCity = document.querySelector("#slt-city").value;
    console.log(JSON.stringify(updatedData));
    // 서버로 수정된 데이터 전송
    fetch('/api/admin/cctv-data', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedData),
    }).then(response => {
        if(!response.ok) {
            return response.json().then(errorData => {
                console.log(errorData);
                throw new Error(errorData.message || '알 수 없는 에러 발생');
            });
        }
        return response.json();
    }).then(result => {
        alert("저장 완료");
        console.log(result);
        getCctvData(selectedCity); // 데이터 새로 고침
    }).catch(err => {
        alert("에러 : " + err.message);
    })
});

/**
 * CCTV 삭제
 * @param cctvId
 * @param selectedCity
 */
function deleteCctvData(cctvId, selectedCity) {
    if(!confirm("해당 CCTV를 삭제하시겠습니까?")) {
        return false;
    }

    const inData = {
        cctvId: cctvId
    }
    const param = new URLSearchParams(inData).toString();

    const url = '/api/admin/cctv-data?' + param;
    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                // 특정 상태 코드를 처리합니다
                if (response.status === 400) {
                    alert("CCTV 삭제 실패: " + text);
                } else if (response.status === 403) {
                    alert("CCTV 삭제 실패: 권한이 없습니다.");
                } else if (response.status === 404) {
                    alert("CCTV 삭제 실패: CCTV 정보를 찾을 수 없습니다.");
                } else {
                    alert("CCTV 삭제 실패: " + text);
                }
            });
        } else {
            getCctvData(selectedCity); // 데이터 새로 고침
            alert("성공적으로 삭제되었습니다!");
        }
    }).catch(error => {
        console.error('Error:', error);
        alert("CCTV 삭제 실패: 예상치 못한 오류가 발생했습니다.");
    });

}