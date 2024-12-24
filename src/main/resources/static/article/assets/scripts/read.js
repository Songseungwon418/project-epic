const $main = document.getElementById('main');
const $$deleteButton = document.querySelector('.delete');
const $deleteArticle = document.getElementById('deleteArticle');
$$deleteButton.onclick = () => {
    $deleteArticle.classList.add('--visible');
};

//region 게시글 삭제
document.addEventListener('DOMContentLoaded', () => {
    const $deleteCancelButton = document.querySelector('[name="DeleteCancel"]');
    if ($deleteCancelButton) {
        $deleteCancelButton.addEventListener('click', () => {

            if ($deleteArticle) {
                $deleteArticle.classList.remove('--visible');
            }
        });
    }

    const $deleteConfirmButton = document.querySelector('[name="DeleteConfirm"]');
    if ($deleteConfirmButton) {
        $deleteConfirmButton.addEventListener('click', () => {
            const deleteArticle = document.getElementById('deleteArticle');
            const $articleIndex = document.getElementById('articleIndex');

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', $articleIndex.value);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('게시글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                switch (response['result']) {
                    case 'failure':
                        alert('게시글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        alert('게시글을 삭제했습니다.');
                        location.href = $main.querySelector('.button.back').href;
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다. 삭제 결과를 반드시 확인해 주세요.')
                        break;
                }
            };
            xhr.open('DELETE', location.href);
            xhr.send(formData);

        });
    }
});
//endregion