// 도시 검색 빈값 체크
function validForm() {
    const searchInput = document.getElementById('searchInput').value.trim();
    if (searchInput === '') {
        alert('도시명을 입력해주세요.');
        return false;
    }
    return true;
}

// Bootstrap Tooltip 활성화
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
});