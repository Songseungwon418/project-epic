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
    const formatDateTime = (dateTime) => {
        return dateTime
            ? dateTime.replace('T', ' ').slice(0, 16).replace(/-/g, '.')
            : '';
    };

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
                <span class="datetime">${formatDateTime(review['createdAt'])}</span>
                ${review['updatedAt'] ? `<span class="update-date">(최종수정일 : ${formatDateTime(review['updatedAt'])})</span>` : ''}
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
                        <textarea name="modifyContent" class="field" spellcheck="false" maxlength="16777215" minlength="1" required placeholder="수정할 내용을 입력해 주세요.">${review['content']}</textarea>
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
                Swal.fire("내용을 입력해 주세요.");
                return;
            }

            if (!$starRating) {
                Swal.fire("별점을 선택해 주세요.");
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
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "수정 결과를 반드시 확인해 주세요.",
                        icon: "warning"
                    });
                    return;
                }

                const response = JSON.parse(xhr.responseText);
                switch (response['result']) {
                    case 'failure':
                        Swal.fire({
                            title: "실패",
                            text: "리뷰를 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.",
                            icon: "error"
                        });
                        break;
                    case 'success':
                        Swal.fire({
                            icon: "success",
                            title: "성공",
                            text: "리뷰를 수정하였습니다."
                        }).then(() => {
                            loadComments(currentPage)
                        });
                        break;
                    default:
                        Swal.fire({
                            title: "서버가 알 수 없는 응답을 반환하였습니다.",
                            text: "수정 결과를 반드시 확인해 주세요.",
                            icon: "warning"
                        });
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


//region 리뷰 불러오기
let currentPage = 1;

const loadComments = (page = 1) => {
    currentPage = page > 0 ? page : 1; // 1보다 작은 값은 기본값 1로 처리
    const url = new URL(location.href);
    const xhr = new XMLHttpRequest();

    const params = new URLSearchParams({
        gameIndex: url.searchParams.get('index'),
        page: currentPage,
    });

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);

            const reviews = response.reviews || [];
            const pageVo = response.pageVo;

            $list.innerHTML = ''; // 리뷰 리스트 초기화
            if (reviews.length === 0) {
                const $noReviews = document.createElement('div');
                $noReviews.className = 'nothing';

                const $noReviewImage = document.createElement('img');
                $noReviewImage.classList.add('image')
                $noReviewImage.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAAsTAAALEwEAmpwYAAADrklEQVR4nO3ZW4ydUxQH8I1WK1Va4cU1k6pLSkIj4UFKPGk8SCvVUYk3SUWFCHGLCE9IXcoTwQsRDaJJXeoaEkHdBq1BkCalioigF0r4yc5ZJ/kyvm/OmZnvnPkq809OMjNr77X+e/Ze66z93ylNYQpT6AtwFC7EnXgaQ/gaP8Qn//xh2O7AMhyZmgAci9uwSTV+iU8VPsGt2VeahAUswgb8HWT+wfu4B4M4FXNK5s3BQlyEe2OH2si+XsCZ/dqB5wvBv8Q1OHoCPo/Btfiq4PdZzKuXfSvYPrgauyPQp1iS/15jjH1xAYYjxi5cWVsMHIinCs7zgvarxXl5vGmxQ+1/2pOZw0SdHoKNhV04vjbGnWOfiM8i9juYO15HB0UJzXgJs2tn25nDbLwSHD4YM4d8dKKCtBNvRs/YduYys8DluTEda9wcE3NJndVTpt3n6VBwuqHbSafgT/yKgdQQYB5+wx6c3M2E9pm8NDUMuKyds50Gnh0DP+5liR0vtHJ3c3BcNNrA3NBlLE8NBVYEx7VVAw6O3NiO6amhwP7RTe8pLcfRVmc8mBoOPBxcl5YZ7w7jstRwYHlwXV1mfDmMx6WGAycE1xfLjO2+pmN+4Dx8i2+wuF/jRuRJxnCZ8TvsTF0gAraxtV/jiohOfFuZIf9Hdqf/wUI2haOZXThZHMG34tx+jev2aLWTfX5qOLTuKpXJfnsYV6SGQ0vgqCy/+Q6e8UBqOPBIcF1SZpyFHfgpn8HUUGAGfsQflTdGPBYrHUwNBS4Ojk+MNmhhiG3DDW3jp4UIoqOQh2di4OWpYcCq4Lahm8EDkSu7mtR3YX7uPKJ9X9DtpJWx8s1lGm6/oSVN5VtrxnVjnfxQTHxjMpUUHBC6Wsb6LK2O1cH0gp6Ulb5De8Z29J14LTi8N27pNGp2+x6f1fLTa2dbHXsBPo/Yb0/4iIdysTrK8l+4qTa21SX2evwei1g7YRF7RIDzw/H3tTn977PCIL6IOLlCrepFoMMjwJs1+x2IHdhSuJOs65nKWdCT1lTYb8RduCRk17kjH2nimeK0aDPuw0cjnt7W44yeLKBAIivhGeeUnOk1qrEjdOQqZHH6lr7ozDgiEn1LsY7nhx+8HoS2xyNnPiaPR6XJPdu2nFfxPP1uvEDlu8/SfFx7Tr4I3B9kV8bvh4UOlpVJQbhvr1njAk4Kwjvj/XBd9DttPFpreewF4jvkrZJznb9TXsVZaW8ArsDPoXAMxfm+am8QKKaQJhn/Alh2rBucIzJ9AAAAAElFTkSuQmCC'

                const $noReviewsMessage = document.createElement('span');
                $noReviewsMessage.classList.add('text');
                $noReviewsMessage.innerText = '아직 작성된 리뷰가 없습니다.';

                $noReviews.append($noReviewImage);
                $noReviews.append($noReviewsMessage);

                $list.append($noReviews);
            } else {
                reviews.forEach(comment => {
                    appendComment(comment); // 리뷰 추가
                });
            }

            // 페이지네이션 업데이트
            if (pageVo.totalCount > 0) {
                updatePagination(pageVo);
            }
        } else {
            Swal.fire({
                title: "실패",
                text: "리뷰를 불러오지 못하였습니다. 잠시 후 다시 시도해 주세요.",
                icon: "error"
            });
        }
    };

    xhr.open('GET', `../review/?${params.toString()}`);
    xhr.send();
};


const updatePagination = (pageVo) => {
    const $paginationContainer = document.getElementById('pagination');
    $paginationContainer.innerHTML = ''; // 기존 페이지네이션 비우기

    if (pageVo.requestPage > pageVo.movableMinPage) {
        const firstButton = document.createElement('button');
        firstButton.classList.add('page', 'first');
        firstButton.innerText = '<<';
        firstButton.onclick = () => {
            currentPage = pageVo.movableMinPage;
            loadComments(currentPage);
        };
        $paginationContainer.appendChild(firstButton);
    }

    if (pageVo.requestPage > 1) {
        const prevButton = document.createElement('button');
        prevButton.classList.add('page', 'prev');
        prevButton.innerText = '<';
        prevButton.onclick = () => {
            currentPage = pageVo.requestPage - 1;
            loadComments(currentPage);
        };
        $paginationContainer.appendChild(prevButton);
    }

    for (let i = pageVo.displayMinPage; i <= pageVo.displayMaxPage; i++) {
        const pageButton = document.createElement('button');
        pageButton.innerText = i;
        pageButton.classList.toggle('-selected', i === pageVo.requestPage);
        pageButton.onclick = () => {
            currentPage = i;
            loadComments(currentPage);
        };
        $paginationContainer.appendChild(pageButton);
    }

    if (pageVo.requestPage < pageVo.movableMaxPage) {
        const nextButton = document.createElement('button');
        nextButton.classList.add('page', 'next');
        nextButton.innerText = '>';
        nextButton.onclick = () => {
            currentPage = pageVo.requestPage + 1;
            loadComments(currentPage);
        };
        $paginationContainer.appendChild(nextButton);
    }

    if (pageVo.requestPage < pageVo.movableMaxPage) {
        const lastButton = document.createElement('button');
        lastButton.classList.add('page', 'last');
        lastButton.innerText = '>>';
        lastButton.onclick = () => {
            currentPage = pageVo.movableMaxPage;
            loadComments(currentPage);
        };
        $paginationContainer.appendChild(lastButton);
    }
};

// 처음 로드 시 리뷰 불러오기
loadComments(currentPage);

//endregion

//endregion 리뷰 전송
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
            Swal.fire({
                title: "서버가 알 수 없는 응답을 반환하였습니다.",
                text: "작성 결과를 반드시 확인해 주세요.",
                icon: "warning"
            });
            return;
        }
        const response = JSON.parse(xhr.responseText)
        switch (response['result']) {
            case 'failure':
                Swal.fire({
                    title: "실패",
                    text: "리뷰를 작성하지 못하였습니다. 잠시 후 시도해 주세요.",
                    icon: "error"
                });
                break;

            case 'failure_deleted_review':
                Swal.fire({
                    title: "실패",
                    text: "이미 삭제된 리뷰가 있습니다.",
                    icon: "error"
                });
                break;

            case 'failure_duplicate_review':
                Swal.fire({
                    title: "실패",
                    text: "이미 작성한 리뷰가 있습니다.",
                    icon: "error"
                });
                break;

            case 'success':
                Swal.fire({
                    icon: "success",
                    title: "성공",
                    text: "리뷰를 작성했습니다."
                }).then(() => {
                    $commentForm.reset();
                    loadComments();
                });
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

//region 리뷰 수정 버튼 동작
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

//region 리뷰 삭제 버튼 동작
document.addEventListener('DOMContentLoaded', () => {
    // 삭제 취소 버튼 클릭 시
    const $deleteCancelButton = document.querySelector('[name="DeleteCancel"]');
    if ($deleteCancelButton) {
        $deleteCancelButton.addEventListener('click', () => {
            const $deleteReview = document.getElementById('deleteReview');

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
            const $reviewIndex = $main.querySelector(':scope > .deleteReview > .deleteDialog > .content > .text > .reviewIndex');

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', $reviewIndex.value);

            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "삭제 결과를 반드시 확인해 주세요.",
                        icon: "warning"
                    });
                    return;
                }

                const response = JSON.parse(xhr.responseText);
                if (response['result'] === 'success') {
                    Swal.fire({
                        icon: "success",
                        title: "성공",
                        text: "리뷰를 삭제했습니다."
                    }).then(() => {
                        if ($list.children.length === 1 && currentPage > 1) {
                            currentPage--;
                        }
                        loadComments(currentPage);
                        $deleteReview.classList.remove('--visible')
                    });
                } else {
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "잠시 후 시도해 주세요.",
                        icon: "warning"
                    });
                }
            };
            xhr.open('DELETE', '../review/');
            xhr.send(formData);
        });
    }
});
//endregion


