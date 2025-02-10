// 페이지 로드 후 첫번째 영상 클릭
window.addEventListener('DOMContentLoaded', function() {
    const firstCollapse = document.querySelector("#accordionExample > div:nth-child(1) > h2 > button").click();
})

function playVideo(collapseBtn) {
    // 모든 iframe 가져와 배열에 저장 후
    // 다른 collapse 클릭 시 해당 iframe 빼고 다 끌 수 있게 설계 필요
    let iframe = collapseBtn.parentNode.nextElementSibling.firstElementChild.firstElementChild;
    iframe.contentWindow.postMessage(
        '{"event":"command","func":"' + 'playVideo' + '","args":""}',
        '*',
    );

    console.log(JSON.stringify(iframe));
}

function searchCity(city) {
    fetch('/japan?city='+city, {
        method: "GET"
    }).then(response => {
        if(response.ok) {
            alert("OK");
        }
    });
}
