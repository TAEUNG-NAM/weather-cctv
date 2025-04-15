// 새로운 CCTV Info ROW 생성
const addRowBtn = document.querySelector("#addRowBtn");
const saveCctvDataBtn = document.querySelector("#saveCctvDataBtn");
addRowBtn.addEventListener("click", () => {
    const tableBody = document.getElementById('data-table-body');
    const tr = document.createElement('tr');
    // console.log(row);
    tr.innerHTML = `
                    <input type="hidden" name="id" value="" class="form-control" required>
                    <td><input type="text" name="name" value="" class="form-control" required></td>
                    <td><input type="text" name="cctvSrc" value="" class="form-control" required></td>
                    <td><input type="text" name="mapSrc" value="" class="form-control" required></td>
                    <td style="display: flex; justify-content: center;">
                      <button class="btn btn-outline-danger btn-sm delete-btn" data-id="">삭제</button>
                    </td>
                `;
    tableBody.appendChild(tr);
});

let originalData = [];


// 도시별 CCTV 데이터 가져오기
function fetchCctvData(cityName) {
    const inData = {
        city: cityName
    }
    const param = new URLSearchParams(inData).toString();

    // API 호출로 DB에서 데이터 가져오기
    fetch('/admin/api/get-cctv-data?' + param)
        .then(response => response.json())
        .then(data => {
            originalData = JSON.parse(JSON.stringify(data));    // 원본 데이터
            const tableBody = document.getElementById('data-table-body');
            tableBody.innerHTML = ''; // 기존 내용 삭제

            data.forEach(row => {
                const tr = document.createElement('tr');
                tr.dataset.id = row.id; // 행에 id를 추가하여 추후 사용
                // console.log(row);
                tr.innerHTML = `
                    <input type="hidden" name="id" value="${row.id}" class="form-control" required>
                    <td><input type="text" name="name" value="${row.name}" class="form-control" required></td>
                    <td><input type="text" name="cctvSrc" value="${row.cctvSrc}" class="form-control" required></td>
                    <td><input type="text" name="mapSrc" value="${row.mapSrc}" class="form-control" required></td>
                    <td style="display: flex; justify-content: center;">
                      <button class="btn btn-outline-danger btn-sm delete-btn" data-id="${row.id}">삭제</button>
                    </td>
                `;
                tableBody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error("Error fetching data:", error);
        });
}

// CCTV 정보 CUD
const dataForm = document.getElementById('dataForm');
dataForm.addEventListener('submit', function (event) {
    event.preventDefault();

    const updatedData = [];
    console.log(originalData);
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
        delete tempRowData.id;
        delete tempOriginalData.id;
        if (JSON.stringify(tempRowData) !== JSON.stringify(tempOriginalData)) {
            rowData["id"] = row.dataset.id;
            updatedData.push(rowData);
        }

    });

    if (updatedData.length === 0) {
        alert("No changes detected.");
        return;
    }

    console.log(JSON.stringify(updatedData));
    // 서버로 수정된 데이터 전송
    fetch('/admin/api/update-cctv-data', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedData),
    })
        .then(response => response.json())
        .then(result => {
            alert("업데이트 완료.");
            fetchCctvData(); // 데이터 새로 고침
        })
        .catch(error => {
            console.error("Error updating data:", error);
        });
});

