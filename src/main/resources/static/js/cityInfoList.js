<!--모달 이벤트 처리(댓글 수정 API)-->
{
    // 모달 요소 선택
    const commentEditModal = document.querySelector("#city-manage-modal");

    // 모달 이벤트 감지
    commentEditModal.addEventListener("show.bs.modal", function (event) {
        console.log("모달이 열렸습니다.");
        // 트리거 버튼 선택
        const modalBtn = event.relatedTarget;

        // 트리거 버튼에서 데이터 가져오기
        const id = modalBtn.getAttribute("data-bs-id");
        const name = modalBtn.getAttribute("data-bs-name");
        const username = modalBtn.getAttribute("data-bs-username");
        const body = modalBtn.getAttribute("data-bs-body");
        const articleId = modalBtn.getAttribute("data-bs-article-id");

        // 데이터 반영
        document.querySelector("#edit-comment-id").value = id;
        document.querySelector("#edit-comment-name").value = name;
        document.querySelector("#edit-comment-username").value = username;
        document.querySelector("#edit-comment-body").value = body;
        document.querySelector("#edit-comment-article-id").value = articleId;
    });
}

<!--도시 수정 API 요청-->
{
    // 수정 완료 버튼 변수화
    const commentUpdateBtn = document.querySelector("#edit-comment-btn");

    // 클릭 이벤트 감지 및 처리
    commentUpdateBtn.addEventListener("click", event => {
        comment = {
            id: document.querySelector("#edit-comment-id").value,
            username: document.querySelector("#edit-comment-username").value,
            body: document.querySelector("#edit-comment-body").value,
            articleId: document.querySelector("#edit-comment-article-id").value
        };
        console.log(comment);

        // fetch() - Talend API 요청을 JS로 보내줌!
        const url = `/api/comments/${comment.id}`;
        fetch(url, {
            credentials: "include",
            method: "PATCH",
            body: JSON.stringify(comment),
            headers: {
                "Content-Type": "application/json"
            }
        }).then(response => {
            const msg = (response.ok) ? "댓글 수정 완료!" : "댓글 수정 실패!";
            alert(msg);

            // 페이지 새로고침
            window.location.reload();
        });
    });
}


<!-- 도시 삭제 API 요청-->
{
    // 모든 도시에 삭제 버튼 선택
    const commentDeleteBtns = document.querySelectorAll(".delete-comment-btn");

    // 각 버튼에 이벤트 리스너 추가
    commentDeleteBtns.forEach(btn => {
        btn.addEventListener("click", event => {
            console.log("삭제 버튼이 클릭되었습니다.");
            const commentDeleteBtn = event.target;
            const commentId = commentDeleteBtn.getAttribute("data-comment-id");
            console.log(`댓글 ${commentId} 삭제 요청!`);

            const url = `/api/comments/${commentId}`;
            fetch(url, {
                credentials: "include",
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
            }).then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        // 특정 상태 코드를 처리합니다
                        if (response.status === 400) {
                            alert("댓글 삭제 실패: " + text);
                        } else if (response.status === 403) {
                            alert("댓글 삭제 실패: 권한이 없습니다.");
                        } else if (response.status === 404) {
                            alert("댓글 삭제 실패: 댓글을 찾을 수 없습니다.");
                        } else {
                            alert("댓글 삭제 실패: " + text);
                        }
                    });
                } else {
                    alert("댓글이 성공적으로 삭제되었습니다!");
                    window.location.reload();
                }
            }).catch(error => {
                console.error('Error:', error);
                alert("댓글 삭제 실패: 예상치 못한 오류가 발생했습니다.");
            });
        });
    });
}
