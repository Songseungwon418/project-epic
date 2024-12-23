//region 리뷰 입력하면 리뷰영역에 나타내기
const $main = document.getElementById('main');
const $list = $main.querySelector(':scope > .mainSection > .comment > .commentArea > .review-container > .list');
const $userEmailValue = document.getElementById('userEmail').value;
const $deleteReview = document.getElementById('deleteReview');
const $reviewIndex = $main.querySelector(':scope > .deleteReview > .deleteDialog > .content > .text > .reviewIndex')

const generateStars = (starValue) => {
    let starsHtml = '';
    for (let i = 1; i <= starValue; i++) {
        starsHtml += '<span class="star">&#9733;</span>';
    }
    return starsHtml;
}

const appendComment = (review) => {
    const $item = new DOMParser().parseFromString(`
       <li class="item">
            <div class="top">
                <div class="name-wrapper">
                    <span class="nickname">${review['nickname']}</span>
                    <span class="email">${'(' + review['userEmail'].slice(0, 3) + '*****' + ')'}</span>
                </div>
                <span class="spring"></span>
                <div class="action-container">
                    ${review['userEmail'] === $userEmailValue ? `
                    <label class="action">
                        <input name="comment" type="radio" value="modify">
                        <span class="text">수정</span>
                    </label>
                    <button class="action" name="delete" type="button">삭제</button>
                    ` : ''}
                </div>
            </div>

            <div class="star-wrapper">
                ${generateStars(review['star'])}
            </div>
            <div class="content-wrapper">
                <span class="content">${review['content']}</span>
            </div>
            <div class="date-wrapper">
                <span class="datetime">${review['createdAt'].replace('T', ' ')}</span>
            </div>

            <form class="form modify">
                <input name="index" type="hidden" value="${review['index']}">

                <div class="review-write">
                    <span class="text">리뷰 수정</span>
                    <div class="star-review">
                        <div class="title">
                            <span class="main">게임 만족도</span>
                            <span class="sub">(별점을 눌러 평가해 주세요.)</span>
                        </div>
                        <div class="star-rating">
                            ${[5, 4, 3, 2, 1].map(star => `
                                <input type="radio" id="stars-${review['index']}-${star}" name="starRating" value="${star}" ${review['star'] === star ? 'checked' : ''} />
                                <label for="stars-${review['index']}-${star}" class="star">&#9733;</label>
                            `).join('')}
                        </div>
                    </div>
                    <label class="label">
                        <span class="text">구매후기</span>
                        <textarea name="modifyContent" class="field" maxlength="16777215" minlength="1" required placeholder="수정할 내용을 입력해 주세요.">${review['content']}</textarea>
                    </label>
                </div>
                <div class="button-container">
                    <span class="spring"></span>
                    <button class="modify button reviewModify" type="submit">리뷰 수정</button>
                    <button class="cancel button" type="button">취소</button>
                </div>
            </form>
        </li>`

        , 'text/html').querySelector('li.item');

    const $modifyForm = $item.querySelector('.form.modify');
    if ($modifyForm) {
        $modifyForm.onsubmit = (e) => {
            e.preventDefault();

            const $modifyContent = $modifyForm.querySelector('[name="modifyContent"]');
            const $starRating = $modifyForm.querySelector('[name="starRating"]:checked');

            if (!$modifyContent.value.trim()) {
                alert('내용을 입력해 주세요.');
                return;
            }

            if (!$starRating) {
                alert('별점을 선택해 주세요.');
                return;
            }

            const xhr = new XMLHttpRequest();
            const formData = new FormData();

            formData.append('index', $modifyForm['index'].value);
            formData.append('starRating', $starRating.value);
            formData.append('content', $modifyContent.value);

            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('댓글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                    return;
                }

                const response = JSON.parse(xhr.responseText);
                switch (response['result']) {
                    case 'failure':
                        alert('알 수 없는 이유로 댓글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        alert('댓글을 수정하였습니다.');
                        loadComments();  // 댓글 리스트 다시 로드
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다. 수정 결과를 반드시 확인해 주세요.');
                        break;
                }
            };

            xhr.open('PATCH', '../review/');
            xhr.send(formData);
        };
    }

    const $deleteButton = $item.querySelector('[name="delete"]');
    if ($deleteButton) {
        $deleteButton.onclick = () => {
            $deleteReview.classList.add('--visible');
            $reviewIndex.value = review['index'];
        };
    }
    $list.append($item);
    return $item;
}


//region 댓글 불러오기
let currentPage = 1;

const loadComments = (page = 1) => {
    const url = new URL(location.href);
    const xhr = new XMLHttpRequest();

    const params = new URLSearchParams({
        gameIndex: url.searchParams.get('index'),
        page: page,
    });

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);

            console.log('Response Data:', response);

            const reviews = response.reviews || [];
            const pageVo = response.pageVo;

            $list.innerHTML = ''; // 댓글 리스트 초기화
            if (reviews.length === 0) {
                const $noReviewsMessage = document.createElement('div');
                $noReviewsMessage.className = 'nothing';
                $noReviewsMessage.innerText = '아직 작성된 리뷰가 없습니다.';
                $list.append($noReviewsMessage);
            } else {
                reviews.forEach(comment => {
                    appendComment(comment); // 댓글 추가
                });
            }

            // 페이지네이션 업데이트 (totalCount가 0이면 페이지네이션 하지 않음)
            if (pageVo.totalCount > 0) {
                updatePagination(pageVo);
            }
        } else {
            alert('댓글을 불러오지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }
    };

    xhr.open('GET', `../review/?${params.toString()}`);
    xhr.send();
};

const updatePagination = (pageVo) => {
    const $paginationContainer = document.getElementById('pagination');
    $paginationContainer.innerHTML = '';

    const url = new URL(location.href);
    const gameIndex = url.searchParams.get('index');

    if (pageVo.requestPage > pageVo.movableMinPage) {
        const firstButton = document.createElement('button');
        firstButton.classList.add('page', 'first');
        firstButton.innerText = '<<'; // 맨 앞 버튼
        firstButton.onclick = () => loadComments(pageVo.movableMinPage);
        $paginationContainer.appendChild(firstButton);
    }

    if (pageVo.requestPage > 1) {
        const prevButton = document.createElement('button');
        prevButton.classList.add('page', 'prev');
        prevButton.innerText = '<';
        prevButton.onclick = () => loadComments(pageVo.requestPage - 1);
        $paginationContainer.appendChild(prevButton);
    }

    for (let i = pageVo.displayMinPage; i <= pageVo.displayMaxPage; i++) {
        const pageButton = document.createElement('button');
        pageButton.innerText = i;
        pageButton.classList.toggle('-selected', i === pageVo.requestPage); // 현재 페이지 강조
        pageButton.onclick = () => loadComments(i);
        $paginationContainer.appendChild(pageButton);
    }

    if (pageVo.requestPage < pageVo.movableMaxPage) {
        const nextButton = document.createElement('button');
        nextButton.classList.add('page', 'next')
        nextButton.innerText = '>';
        nextButton.onclick = () => loadComments(pageVo.requestPage + 1);
        $paginationContainer.appendChild(nextButton);
    }

    if (pageVo.requestPage < pageVo.movableMaxPage) {
        const lastButton = document.createElement('button');
        lastButton.classList.add('page', 'last')
        lastButton.innerText = '>>'; // 맨 끝 버튼
        lastButton.onclick = () => loadComments(pageVo.movableMaxPage);
        $paginationContainer.appendChild(lastButton);
    }
};

loadComments(currentPage);

//endregion

//endregion 댓글 전송
const $commentForm = document.getElementById('commentForm');
const postComment = ($form) => {
    const $userEmail = document.getElementById('userEmail');

    const $star = document.querySelector('[name="rating"]:checked');

    const url = new URL(location.href);
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('userEmail', $userEmail.value);
    formData.append('gameIndex', url.searchParams.get('index'));
    formData.append('content', $form['content'].value);
    formData.append('purchaseIndex', $form['purchaseIndex'].value);
    formData.append('star', $star.value);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('리뷰를 작성하지 못하였습니다. 잠시 후 시도해 주세요.')
            return;
        }
        const response = JSON.parse(xhr.responseText)
        switch (response['result']) {
            case 'failure':
                alert('리뷰 작성에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
                break;

            case 'failure_deleted_review':
                alert('이미 삭제된 리뷰가 있습니다.');
                break;

            case 'failure_duplicate_review':
                alert('이미 작성한 리뷰가 있습니다.');
                break;

            case 'success':
                alert('리뷰를 작성했습니다.')
                $commentForm.reset();
                loadComments();
                break;
        }
    };
    xhr.open('POST', '../review/write');
    xhr.send(formData);
}
if ($commentForm) {
    $commentForm.onsubmit = (e) => {
        e.preventDefault();
        postComment($commentForm);
    };
}
//endregion

//region 댓글 수정 버튼 동작
document.addEventListener('DOMContentLoaded', () => {
    // 수정 버튼 처리
    $list.addEventListener('change', (event) => {
        const $radioButton = event.target;

        if ($radioButton.type === 'radio' && $radioButton.value === 'modify') {
            const li = $radioButton.closest('li');
            const form = li.querySelector('.form.modify');

            if ($radioButton.checked) {
                form.classList.add('-selected');
            } else {
                form.classList.remove('-selected');
            }
        }
    });

    // 취소 버튼 처리
    $list.addEventListener('click', (event) => {
        const $cancelButton = event.target.closest('.cancel.button');
        if ($cancelButton) {
            const li = $cancelButton.closest('li');
            const form = li.querySelector('.form.modify');
            const radio = li.querySelector('input[type="radio"][value="modify"]');

            form.classList.remove('-selected');
            radio.checked = false;
        }
    });
});
//endregion

//region 댓글 삭제 버튼 동작
document.addEventListener('DOMContentLoaded', () => {
    // 삭제 취소 버튼 클릭 시
    const $deleteCancelButton = document.querySelector('[name="DeleteCancel"]');
    if ($deleteCancelButton) {
        $deleteCancelButton.addEventListener('click', () => {
            const $deleteReview = document.getElementById('deleteReview');

            // deleteReview에서 --visible 클래스 제거
            if ($deleteReview) {
                $deleteReview.classList.remove('--visible');
            }
        });
    }

    // 삭제 확인 버튼 클릭 시
    const $deleteConfirmButton = document.querySelector('[name="DeleteConfirm"]');
    if ($deleteConfirmButton) {
        $deleteConfirmButton.addEventListener('click', () => {
            const $deleteReview = document.getElementById('deleteReview');
            const $reviewIndex = $main.querySelector(':scope > .deleteReview > .deleteDialog > .content > .text > .reviewIndex')

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', $reviewIndex.value);
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
                        loadComments();
                        $deleteReview.classList.remove('--visible');
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다. 삭제 결과를 반드시 확인해 주세요.')
                        break;
                }
            };
            xhr.open('DELETE', '../review/.');
            xhr.send(formData);

        });
    }
});
//endregion


