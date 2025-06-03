/* 콜랩스 클릭 시 해당 영상 재생 및 다른 재생 일시정지*/
function playVideo(collapseBtn, iframeSize) {

    // 모든 영상 일시정지
    let iframe;
    for(let i=1; i <= iframeSize; i++) {
        iframe = document.querySelector("#iframe-" + i);
        iframe.contentWindow.postMessage(
            '{"event":"command","func":"' + 'pauseVideo' + '","args":""}',
            '*',
        );
    }

    // 선택된 클릭 영상 재생
    let selectedIframe = collapseBtn.parentNode.nextElementSibling.firstElementChild.firstElementChild;
    selectedIframe.contentWindow.postMessage(
        '{"event":"command","func":"' + 'playVideo' + '","args":""}',
        '*',
    );
}

// 페이지 로드 후 첫번째 영상 클릭
window.addEventListener('DOMContentLoaded', function() {
    // 첫번째 영상 재생
    this.addEventListener('load', function () {
        let firstIframe = document.querySelector('#iframe-1');
        firstIframe.contentWindow.postMessage(
            '{"event":"command","func":"' + 'playVideo' + '","args":""}',
            '*',
        );
    });
    
    // 첫 Collapse 클릭
    const firstCollapse = document.querySelector("#accordionExample > div:nth-child(1) > h2 > button").click();
})

// API 요청용(미완)
function searchCity(city) {
    fetch('/japan?city='+city, {
        method: "GET"
    }).then(response => {
        if(response.ok) {
            alert("OK");
        }
    });
}

// 도시 검색 빈값 체크
function validForm() {
    const searchInput = document.getElementById('searchInput').value.trim();
    if (searchInput === '') {
        alert('도시명을 입력해주세요.');
        return false;
    }
    return true;
}
