// 도시 검색 빈값 체크
function validForm() {
    const searchInput = document.getElementById('searchInput').value.trim();
    if (searchInput === '') {
        alert('도시명을 입력해주세요.');
        return false;
    }
    return true;
}