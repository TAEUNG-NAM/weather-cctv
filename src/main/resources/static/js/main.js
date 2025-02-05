// 페이지 로드 후 첫번째 영상 클릭
window.addEventListener('DOMContentLoaded', function() {
    const firstCollapse = document.querySelector("#accordionExample > div:nth-child(1) > h2 > button").click();
})

function searchCity(city) {
    fetch('/japan?city='+city, {
        method: "GET"
    }).then(response => {
        if(response.ok) {
            alert("OK");
        }
    });
}
