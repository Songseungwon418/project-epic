//region 필터관련
// 장르 목록 서버에서 가져오기
if(document.querySelector('.filter-list-content') != null) {
    const $genreList = document.querySelector('.filter-list-content');
    filterGenre($genreList);
}

// 필터 목록들 접고 펴기
document.addEventListener('DOMContentLoaded', function() {
    const $buttons = document.querySelectorAll('.filter-toggle-button');

    $buttons.forEach(button => {
        button.addEventListener('click', () => {
            // 해당 버튼에 대한 'aria-expanded' 속성 가져오기
            const isExpanded = button.getAttribute('aria-expanded') === 'true';

            // aria-expanded 값 변경 (열리거나 닫히도록)
            button.setAttribute('aria-expanded', !isExpanded);

            // 해당 버튼의 부모 요소인 .filter-list-title의 다음 형제 요소로 .filter-list-content를 찾기
            const $filterListTitle = button.closest('.filter-list-title'); // 버튼이 속한 .filter-list-title 찾기
            const $filterContent = $filterListTitle.nextElementSibling; // 그 아래의 형제 .filter-list-content 찾기

            // filter-list-content 표시/숨기기
            if ($filterContent && $filterContent.classList.contains('filter-list-content')) {
                $filterContent.style.display = isExpanded ? 'none' : 'block'; // 접거나 펼침
            }
        });
    });
});

// 필터 목록에서 목록 클릭시
const $checkboxs = document.querySelectorAll('.checkbox');

$checkboxs.forEach((checkbox) => {
    checkbox.addEventListener('click', () => {
        const isChecked = checkbox.getAttribute('aria-checked') === 'true';

        checkbox.setAttribute('aria-checked', !isChecked);

        checkbox.style.backgroundColor = !isChecked ? '#918D8D38' : 'inherit';

        const $checkIcon = checkbox.querySelector('.check-icon');
        $checkIcon.style.display = !isChecked ? 'block' : 'none';
    });
});
//endregion

//region 정렬 필터 관련
// 필터 항목을 클릭했을 때 선택된 항목을 처리하는 함수
function selectFilter(element) {
    const $filterListContainer = document.querySelector('.order-filter-container');
    const $filterItems = $filterListContainer.querySelectorAll(' :scope > .order-filter-list > .order-filter-item');
    const $sortingFilterBtn = document.querySelector('.sorting-filter-btn');

    // 모든 필터 항목에서 'selected' 클래스를 제거
    $filterItems.forEach(item => item.classList.remove('selected'));

    // 클릭된 항목에 'selected' 클래스를 추가
    element.classList.add('selected');

    // 'filter-text' 부분의 텍스트를 선택된 항목의 텍스트로 변경
    const $filterText = document.querySelector('.filter-text .text');
    $filterText.textContent = element.textContent;

    // 목록 닫기
    toggleFilterList($sortingFilterBtn);

    // 서버로 name 값 전송 (예시로 콘솔 출력)
    const filterName = element.getAttribute('name') || element.textContent;
    console.log('선택된 필터 name:', filterName);
}

// 필터 목록의 표시/숨김을 토글하는 함수
function toggleFilterList(btn) {
    const $filterListContainer = document.querySelector('.order-filter-container');

    // 해당 버튼에 대한 'aria-expanded' 속성 가져오기
    const isExpanded = btn.getAttribute('aria-expanded') === 'true';

    // aria-expanded 값 변경 (열리거나 닫히도록)
    btn.setAttribute('aria-expanded', !isExpanded);

    if($filterListContainer) {
        $filterListContainer.style.display = isExpanded ? 'none' : 'block';
    }
}
//endregion

//region 제거 버튼 누를 시
{
    const $removeBtns = document.querySelectorAll('.game-card-btn.-remove');
    const $removeGameCard = document.getElementById('remove-game-card');

    $removeBtns.forEach(btn => btn.addEventListener('click', () => {
        const $gameCard = btn.closest('.game-card');
        const $wishlistIndexTag = $gameCard.querySelector(':scope > .info-data > .wishlist-index');
        const $gameNameTag = $gameCard.querySelector('.title');  // 게임 이름
        const gameName = $gameNameTag.innerText;

        const wishlistIndex = $wishlistIndexTag.innerText;

        const formData = new FormData();
        formData.append('index', wishlistIndex);

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState !== XMLHttpRequest.DONE){
                return;
            }
            document.body.style.cursor = 'default';
            if (xhr.status < 200 || xhr.status >= 300){
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                return;
            }
            //요청 성공 로직 구현
            const response = JSON.parse(xhr.responseText);
            if (response.result === 'failure'){
                alert('오류: 제거에 실패하였습니다.');
            }
            else if(response.result === 'failure_not_found'){
                alert('오류: 이미 위시리스트에 없어서 제거에 실패하였습니다.');
            }
            else if(response.result === 'success'){
                const $removeContainer = document.createElement('div');
                $removeContainer.classList.add('remove-container');
                const $removeWrapper = document.createElement('div');
                $removeWrapper.classList.add('remove-wrapper');

                const $iconWrapper = document.createElement('div');
                $iconWrapper.classList.add('icon-wrapper');
                $iconWrapper.innerHTML = `<svg aria-hidden="true" width="24" height="24" viewBox="0 0 24 24">
                <path d="m17.012 3.954.574 2.296H21a.75.75 0 0 1 0 1.5H3a.75.75 0 0 1 0-1.5h3.414l.574-2.296A2.25 2.25 0 0 1 9.171 2.25h5.658a2.25 2.25 0 0 1 2.183 1.704m-8.568.364a.75.75 0 0 1 .727-.568h5.658a.75.75 0 0 1 .727.568l.483 1.932H7.961z" clip-rule="evenodd" fill-rule="evenodd"></path>
                <path d="M6.11 19.74 5.067 10h1.508l1.027 9.58a.75.75 0 0 0 .746.67h7.304a.75.75 0 0 0 .746-.67L17.424 10h1.509l-1.044 9.74a2.25 2.25 0 0 1-2.237 2.01H8.348a2.25 2.25 0 0 1-2.238-2.01"></path></svg>`;

                const $text = document.createElement('span');
                $text.classList.add('text');
                $text.textContent = `${gameName} 제거됨`;

                const $cancelBtnWrapper = document.createElement('div');
                $cancelBtnWrapper.classList.add('cancel-btn-wrapper');

                const $cancelBtn = document.createElement('button');
                $cancelBtn.classList.add('cancel-btn');
                $cancelBtn.type = 'button';
                $cancelBtn.innerHTML = `<span class="text">실행 취소</span>`;

                $cancelBtn.addEventListener('click', () => {
                    // 취소 버튼을 누르면 해당 메시지를 삭제하고, 게임을 복원하는 로직을 추가할 수 있음
                    $removeContainer.remove();
                    // 게임 복원 로직 추가
                    // 예: 복원 API 호출하여 위시리스트에 다시 추가
                });

                $removeWrapper.appendChild($iconWrapper);
                $removeWrapper.appendChild($text);
                $removeContainer.appendChild($removeWrapper);
                $cancelBtnWrapper.appendChild($cancelBtn);
                $removeContainer.appendChild($cancelBtnWrapper);
                $removeGameCard.append($removeContainer);

                // 게임 카드 삭제
                $gameCard.remove();
            }
        };
        xhr.open('PATCH', '/purchase/wishlist/delete');
        xhr.send(formData);
        document.body.style.cursor = 'not-allowed';
    }));
}

//endregion


//region 장바구니 담기 버튼을 누를 시
{
    const $cartAddBtns = document.querySelectorAll('.game-card-btn.-cart-add.-add');
    $cartAddBtns.forEach(btn => btn.onclick = () => {
        const $gameCard = btn.closest('.game-card');
        const $wishlistIndexTag = $gameCard.querySelector(':scope > .info-data > .wishlist-index');
        const $gameIndexTag = $gameCard.querySelector(':scope > .info-data > .game-index');
        const $userEmail = $gameCard.querySelector(':scope > .info-data > .user-email');

        const index = $wishlistIndexTag.innerText;
        const gameIndex = $gameIndexTag.innerText;
        const userEmail = $userEmail.innerText;

        const formData = new FormData();
        formData.append('index', index);
        formData.append('gameIndex', gameIndex);
        formData.append('userEmail', userEmail);

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState !== XMLHttpRequest.DONE){
                return;
            }
            document.body.style.cursor = 'default';
            if (xhr.status < 200 || xhr.status >= 300){
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                return;
            }
            //요청 성공 로직 구현
            const response = JSON.parse(xhr.responseText);
            if (response.result === 'failure'){
                alert('오류: 장바구니 담기에 실패하였습니다.');
            }
            else if(response.result === 'failure_duplicate_cart'){
                alert('오류: 이미 장바구니에 있습니다.');
            }
            else if(response.result === 'failure_not_found'){
                alert('오류: 찾을 수 없습니다.');
            }
            else if(response.result === 'success'){
                btn.querySelector(':scope > .text').innerText = '장바구니 보기';
                btn.onclick = () => {
                    window.location.href = '/purchase/cart';
                };
            }
        };
        xhr.open('POST', '/purchase/cart/add'); // 장바구니에 추가
        xhr.send(formData);
        document.body.style.cursor = 'not-allowed';
    });
}
//endregion