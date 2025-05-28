const sltManageCountry = document.querySelector("#slt-manage-country");
const addCityBtn = document.querySelector("#addCityBtn");
const saveCityBtn = document.querySelector("#saveCityBtn");

/* 도시 목록 불러오기 */
function getCities(selectedCountry) {
    // const url = "/api/admin/management/cities"

    const inData = {
        country: selectedCountry
    }
    const param = new URLSearchParams(inData).toString();
    const url = "/api/admin/area-chart/city-list?" + param;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            const citiesTable = document.querySelector('#city-data-list');
            citiesTable.innerHTML = ''; // 기존 내용 삭제

            data.forEach(row => {
                const tr = document.createElement('tr');
                tr.dataset.id = row.id; // 행에 id를 추가하여 추후 사용
                const imagePath = row.thumbnail;
                const imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);

                tr.innerHTML = `
                    <input type="hidden" name="id" value="${row.id}" class="form-control" disabled required>
                    <td><input type="text" name="country" value="${row.country}" class="form-control" disabled required></td>
                    <td><input type="text" name="city" value="${row.name}" class="form-control" disabled required></td>
                    <td>
                        <input type="text" name="thumbnail" value="${imageName}" class="form-control thumbnail-name" disabled required>
                        <div class="img-thumbnail image-preview" style="background-image: url(${row.thumbnail});"></div>
                    </td>
                    <td>
                        <input type="text" name="del_yn" value="${row.delYn}" class="form-control" style="text-align: center; padding: 0.5rem 0.0rem" disabled required>
                    </td>
                    <td style="display: flex; justify-content: space-around;">
                        <button type="button" class="btn btn-outline-primary btn-sm" data-id="${row.id}"
                            data-bs-toggle="modal"
                            data-bs-target="#city-manage-modal"
                            data-bs-id="${row.id}"
                            data-bs-city="${row.name}"
                            data-bs-country="${row.country}"
                            data-bs-del = "${row.delYn}"
                            data-bs-article-id="{{articleId}}">수정</button>
                        <button type="button" class="btn btn-outline-danger btn-sm delete-city-btn" data-city-id="${row.id}">삭제</button>
                    </td>
                `;

                const deleteBtn = tr.querySelector('.delete-city-btn');
                deleteBtn.addEventListener('click', function(e) {

                    const targetCountry = row.country;
                    const targetCityId = deleteBtn.getAttribute("data-city-id");

                    deleteCity(targetCountry, targetCityId);
                });

                citiesTable.appendChild(tr);
            });
        })
        .catch(error => {
            console.error("도시 조회 실패 :", error);
        });
}

/* 도시 썸네일 미리보기 */
document.addEventListener('mouseover', function(event) {
    const thumbnailInput = event.target.closest('.thumbnail-name');
    if (thumbnailInput) {
        const imagePreview = thumbnailInput.parentElement.querySelector('.image-preview');
        if (imagePreview) {
            imagePreview.style.display = 'block';
        }
    }
});

document.addEventListener('mousemove', function(event) {
    const thumbnailInput = event.target.closest('.thumbnail-name');
    if (thumbnailInput) {
        const imagePreview = thumbnailInput.parentElement.querySelector('.image-preview');
        if (imagePreview) {
            imagePreview.style.position = 'fixed';
            imagePreview.style.top = event.clientY  + 10 + 'px'; // 마우스 위치에 따라 조정
            imagePreview.style.left = event.clientX  + 10 + 'px';
        }
    }
});

document.addEventListener('mouseout', function(event) {
    const thumbnailInput = event.target.closest('.thumbnail-name');
    if (thumbnailInput) {
        const imagePreview = thumbnailInput.parentElement.querySelector('.image-preview');
        if (imagePreview) {
            imagePreview.style.display = 'none';
        }
    }
});
//도시 썸네일 미리보기

/* 국가 선택 시 [도시 추가] 버튼 활성화 */
sltManageCountry.addEventListener("change", () => {
    if(sltManageCountry.value !== "country") {
        getCities(sltManageCountry.value);
        addCityBtn.disabled = false;
    } else {
        addCityBtn.disabled = true;
    }
});


<!--모달 이벤트 처리(도시 추가)-->
{
    // 모달 요소 선택
    const addCityModal = document.querySelector("#city-manage-modal");

    // 모달 이벤트 감지
    addCityModal.addEventListener("show.bs.modal", function (event) {
        // 트리거 버튼 선택
        const modalBtn = event.relatedTarget;

        // 트리거 버튼에서 데이터 가져오기
        // const id = modalBtn.getAttribute("data-bs-id");
        const cityId = modalBtn.getAttribute("data-bs-id");
        const city = modalBtn.getAttribute("data-bs-city");
        const countryEng = modalBtn.getAttribute("data-bs-country");
        const countryKor = sltManageCountry.options[sltManageCountry.selectedIndex].text;
        const delYn = modalBtn.getAttribute("data-bs-del");

        // 데이터 반영
        document.querySelector("#manage-city-name").value = city;
        document.querySelector("#manage-country-name").value = countryKor;
        document.querySelector("#edit-country-name").value = countryEng;
        document.querySelector("#edit-city-id").value = cityId;
        document.querySelector("#manage-city-del").value = delYn;
    });
}

<!--도시 추가/수정 API 요청-->
{
    // 클릭 이벤트 감지 및 처리
    saveCityBtn.addEventListener("click", event => {
        let inData = new FormData();
        const cityImage = document.querySelector("#manage-city-image").files[0];
        const countryName = document.querySelector("#edit-country-name").value;
        const cityId = document.querySelector("#edit-city-id").value;

        inData.append("country", sltManageCountry.value);
        inData.append("countryName", countryName);
        inData.append("cityName", document.querySelector("#manage-city-name").value);
        inData.append("cityId", cityId);
        inData.append("delYn", document.querySelector("#manage-city-del").value);
        inData.append("image", cityImage);


        let fetchMethod;
        let msgComment;
        if((sltManageCountry.value === countryName) && (cityId != null)) {
            fetchMethod = 'PATCH';
            msgComment = "수정";
        } else {
            console.log("도시 추가!");
            fetchMethod = 'POST';
            msgComment = "추가";
        }
        // console.log(JSON.stringify(inData));

        const url = "/api/admin/management/city";
        fetch(url, {
            method: fetchMethod,
            body: inData,
            headers: {
                // "Content-Type": "application/json"
            }
        }).then(response => {
            const msg = (response.ok) ? "도시 "+ msgComment + "완료!" : "도시" +  msgComment +"실패!";
            alert(msg);

            // 페이지 새로고침(도시 목록 조회)
            // window.location.reload();
            document.querySelector(".btn-close").click();
            getCities(sltManageCountry.value);
        });
    });
}


<!-- 도시 삭제 API 요청-->
// {
//     // 모든 도시에 삭제 버튼 선택
//     const cityDeleteBtns = document.querySelectorAll(".delete-city-btn");
//
//     // 각 버튼에 이벤트 리스너 추가
//     cityDeleteBtns.forEach(btn => {
//         btn.addEventListener("click", event => {
//             const cityDeleteBtn = event.target;
//             const cityId = cityDeleteBtn.getAttribute("data-city-id");
//             console.log("cityId: " + cityId);
//
//             const url = `/api/admin/management/city/${cityId}`;
//             fetch(url, {
//                 method: "DELETE",
//                 headers: {
//                     "Content-Type": "application/json"
//                 }
//             }).then(response => {
//                 if (!response.ok) {
//                     return response.text().then(text => {
//                         // 특정 상태 코드를 처리합니다
//                         if (response.status === 400) {
//                             alert("댓글 삭제 실패: " + text);
//                         } else if (response.status === 403) {
//                             alert("댓글 삭제 실패: 권한이 없습니다.");
//                         } else if (response.status === 404) {
//                             alert("댓글 삭제 실패: 댓글을 찾을 수 없습니다.");
//                         } else {
//                             alert("댓글 삭제 실패: " + text);
//                         }
//                     });
//                 } else {
//                     alert("댓글이 성공적으로 삭제되었습니다!");
//                     window.location.reload();
//                 }
//             }).catch(error => {
//                 console.error('Error:', error);
//                 alert("댓글 삭제 실패: 예상치 못한 오류가 발생했습니다.");
//             });
//         });
//     });
// }

function deleteCity(targetCountry, targetCityId) {
    if(!confirm("해당 도시를 삭제하시겠습니까?")) {
        return false;
    }

    const inData = {
        country: targetCountry,
        cityId: targetCityId
    }

    const url = '/api/admin/management/city';
    fetch(url, {
        method: "DELETE",
        body: JSON.stringify(inData),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                // 특정 상태 코드를 처리합니다
                if (response.status === 400) {
                    alert("도시 삭제 실패: " + text);
                } else if (response.status === 403) {
                    alert("도시 삭제 실패: 권한이 없습니다.");
                } else if (response.status === 404) {
                    alert("도시 삭제 실패: 도시 정보를 찾을 수 없습니다.");
                } else {
                    alert("도시 삭제 실패: " + text);
                }
            });
        } else {
            alert("성공적으로 삭제되었습니다!");
            getCities(sltManageCountry.value);
        }
    }).catch(error => {
        console.error('Error:', error);
        alert("도시 삭제 실패: 예상치 못한 오류가 발생했습니다.");
    });

}
