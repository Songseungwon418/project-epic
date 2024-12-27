const $main = document.getElementById('main');
const $deleteArticleButton = document.querySelector('.delete-article');
const $deleteArticle = document.getElementById('deleteArticle');
const $deleteComment = document.getElementById('deleteComment');

if ($deleteArticleButton) {
    $deleteArticleButton.onclick = () => {
        $deleteArticle.classList.add('--visible');
    };
}

document.addEventListener('click', (event) => {
    if (event.target.matches('.commentDelete')) {
        const $deleteComment = document.getElementById('deleteComment');
        if ($deleteComment) {
            $deleteComment.classList.add('--visible');
        }
    }
});

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

//region 댓글 삭제
document.addEventListener('DOMContentLoaded', () => {
    const $DeleteCommentCancel = document.querySelector('[name="DeleteCommentCancel"]');
    if ($DeleteCommentCancel) {
        $DeleteCommentCancel.addEventListener('click', () => {

            if ($deleteComment) {
                $deleteComment.classList.remove('--visible');
            }
        });
    }
    let selectedCommentIndex = null;

    document.addEventListener('click', (event) => {
        if (event.target.matches('.commentDelete')) {
            selectedCommentIndex = event.target.getAttribute('data-index');

            const $deleteCommentModal = document.getElementById('deleteComment');
            if ($deleteCommentModal) {
                $deleteCommentModal.classList.add('--visible');
            }
        }
    });

    const $DeleteCommentConfirm = document.querySelector('[name="DeleteCommentConfirm"]');
    if ($DeleteCommentConfirm) {
        $DeleteCommentConfirm.addEventListener('click', () => {
            if (!selectedCommentIndex) {
                alert('삭제할 댓글이 선택되지 않았습니다.');
                return;
            }
            const url = location.href;
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', selectedCommentIndex);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('댓글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                switch (response['result']) {
                    case 'failure':
                        alert('댓글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        alert('댓글을 삭제했습니다.');
                        location.reload();
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다. 삭제 결과를 반드시 확인해 주세요.')
                        break;
                }
            };
            xhr.open('DELETE', '../comment/');
            xhr.send(formData);

        });
    }
});
//endregion

//region 댓글 입력 및 답글, 수정, 삭제

const $list = $main.querySelector(':scope > .mainSection > .comment-container > .list');
const appendComment = (comment) => {
    const i = comment['index'];
    const $item = new DOMParser().parseFromString(`            
            <li class="item">
                <div class="top">
                    <span class="nickname">${comment['nickname']}</span>
                    <span class="spring"></span>
                    <span class="datetime">${comment['createdAt'].replace('T', ' ')}</span>

                </div>
                <div class="content">${comment['content']}</div>
                <div class="action-container">
                    <input checked id="commentNoAction${i}" name="comment${i}" value="noAction" type="radio">
                    <label class="action">
                        <input name="comment${i}" type="radio" value="reply">
                        <span class="text">답글 쓰기</span>
                    </label>

                    <label class="action">
                        <input name="comment${i}" type="radio" value="modify">
                        <span class="text">수정</span>
                    </label>
                    <button class="action commentDelete" data-index="${comment['index']}" name="delete" type="button">삭제</button>
                </div>

                <form class="form reply">
                <input name="commentIndex" type="hidden" value="${comment['index']}">
                    <label class="label spring">
                        <span class="text">내용</span>
                        <textarea name="content" class="field" maxlength="100" minlength="1" placeholder="답글 내용을 작성해 주세요." required></textarea>
                    </label>
                    <div class="button-container">
                        <button class="button reply" type="submit">답글 쓰기</button>
                        <label for="commentNoAction${i}" class="cancel">취소</label>
                    </div>
                </form>

                <form class="form modify">
                    <label class="label spring">
                        <span class="text">내용</span>
                        <textarea name="content" class="field" maxlength="100" minlength="1" required>${comment['content']}</textarea>
                    </label>
                    <div class="button-container">
                        <button class="button modify" type="submit">댓글 수정</button>
                        <label for="commentNoAction${i}" class="cancel">취소</label>
                    </div>
                </form>
            </li>`, 'text/html').querySelector('li.item');

    if (!isLoggedIn) {
        const replyButton = $item.querySelector('.action input[value="reply"]');
        replyButton.disabled = true;
        replyButton.parentElement.style.display = 'none';
    }

    if (comment['userEmail'] !== userEmail) {
        const modifyButton = $item.querySelector('.action input[value="modify"]');
        const deleteButton = $item.querySelector('.commentDelete');

        modifyButton.disabled = true;
        modifyButton.parentElement.style.display = 'none';

        deleteButton.style.display = 'none';
    }

    const $replyForm = $item.querySelector('.form.reply');
    $replyForm.onsubmit = (e) => {
        e.preventDefault();
        postComment($replyForm);
    }

    const $modifyForm = $item.querySelector('.form.modify');
    $modifyForm.onsubmit = (e) => {
        e.preventDefault();

        if ($modifyForm['content'].value === '') {
            alert('내용을 입력해 주세요.');
            return;
        }

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('index', comment['index']);
        formData.append('content', $modifyForm['content'].value);
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.state < 200 || xhr.state >= 300) {
                alert('댓글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
                return;
            }
            const response = JSON.parse(xhr.responseText);
            switch (response['result']) {
                case 'failure':
                    alert('알 수 없는 이유로 댓글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                    break;
                case 'success':
                    loadComments();
                    alert('댓글을 수정했습니다.')
                    break;
                default:
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 수정 결과를 반드시 확인해 주세요.')
                    break;
            }
        };
        xhr.open('PATCH', '../comment/');
        xhr.send(formData);
    };

    $list.append($item);
    return $item;
};

const appendComments = (allComments, targetComments, step) => {
    for (const comment of targetComments) {
        appendComment(comment).style.marginLeft = `${step * 50}px`;
        const subComments = allComments.filter((x) => x['commentIndex'] === comment['index']);
        appendComments(allComments, subComments, step + 1);
    }
}

const loadComments = () => {
    const url = new URL(location.href);
    $list.innerHTML = '';
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.state < 200 || xhr.state >= 300) {
            alert('댓글 정보를 불러오지 못하였습니다. 잠시 후 다시 시도해 주세요.')
            return;
        }
        const allComments = JSON.parse(xhr.responseText);
        const rootComments = allComments.filter((x) => x['commentIndex'] == null);
        appendComments(allComments, rootComments, 0);
    };
    xhr.open('GET', `../comment/comments?articleIndex=${url.searchParams.get('index')}`);
    xhr.send();
};

loadComments();

const postComment = ($form) => {

    if ($form['content'].value === '') {
        alert('내용을 입력해 주세요.');
        return;
    }

    const url = new URL(location.href);
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const $userEmail = document.getElementById('userEmail');

    if ($form['commentIndex'] != null) {
        formData.append('commentIndex', $form['commentIndex'].value);
    }
    formData.append('articleIndex', url.searchParams.get('index'));
    formData.append('userEmail', $userEmail.value);
    formData.append('content', $form['content'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.state < 200 || xhr.state >= 300) {
            alert('댓글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
            return;
        }
        $form['content'].value = '';
        $form['content'].focus();
        loadComments();

    };
    xhr.open('POST', '../comment/');
    xhr.send(formData);
};

const $commentForm = document.getElementById('commentForm');
$commentForm.onsubmit = (e) => {
    e.preventDefault();
    postComment($commentForm);
};
//endregion