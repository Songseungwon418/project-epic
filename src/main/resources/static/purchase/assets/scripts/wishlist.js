// 로딩 화면 요소
const $loading = document.getElementById("loading");
// 게임 목록 가져옴
const $gameList = document.querySelector('.game-list');
// $gameList 안의 모든 .game-card 요소를 배열로 변환
const $games = Array.from($gameList.querySelectorAll('.game-card'));

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

    // 필터 목록에서 목록 클릭시
    const $filterListContents = document.querySelectorAll('.filter-list-content');
    const selectedFilters = [];  // 선택된 필터를 저장할 배열

    $filterListContents.forEach( $filterListContent => {
        $filterListContent.addEventListener('click', (e) => {
            const $checkbox = e.target.closest('.checkbox');
            if($checkbox) {
                const isChecked = $checkbox.getAttribute('aria-checked') === 'true';
                // aria-checked 값을 반전시킴
                $checkbox.setAttribute('aria-checked', !isChecked);
                // 배경 색상 변경
                $checkbox.style.backgroundColor = !isChecked ? '#918D8D38' : 'inherit';
                // 체크아이콘 표시여부
                const $checkIcon = $checkbox.querySelector('.check-icon');
                $checkIcon.style.display = !isChecked ? 'block' : 'none';

                const filterText = $checkbox.querySelector('.checkbox-label').innerText.replace(/\s/g, '');

                if (!isChecked) {
                    // 필터가 체크되었으면 selectedFilters에 추가
                    if (!selectedFilters.includes(filterText)) {
                        selectedFilters.push(filterText);
                    }
                } else {
                    // 필터가 체크 해제되었으면 selectedFilters에서 제거
                    const index = selectedFilters.indexOf(filterText);
                    if (index !== -1) {
                        selectedFilters.splice(index, 1);
                    }
                }

                // 모든 체크박스 해제 시 본래의 게임 목록을 보여줌
                if (selectedFilters.length > 0) {
                    console.log(selectedFilters);
                    checkboxFilter(selectedFilters);// 필터링 함수 호출
                }else {
                    $filterBtn.click();
                }
            }
        });
    });

    // 필터 함수
    function checkboxFilter(filters) {
        let gameVisible = false; // 게임이 하나라도 표시되는지 여부
        $games.forEach(game => {
            let isVisible = true; // 초기값을 true로 설정해서 필터를 만족하지않을 시 false
            // 모든 필터 조건에 맞는지 확인
            filters.forEach(filter => {
                const gameGenre = game.querySelector('.gameGenre').innerText;

                // 각 필터 조건이 게임 장르에 포함되는지 체크
                if (filter.includes('액션') && !gameGenre.includes('ACT')) {
                    isVisible = false; // 액션 필터에 맞지 않으면 숨김
                }
                if (filter.includes('오픈월드') && !gameGenre.includes('OW')) {
                    isVisible = false; // 오픈월드 필터에 맞지 않으면 숨김
                }
                if (filter.includes('RPG') && !gameGenre.includes('RPG')) {
                    isVisible = false; // RPG 필터에 맞지 않으면 숨김
                }
                if (filter.includes('RTS') && !gameGenre.includes('RTS')) {
                    isVisible = false; // RTS 필터에 맞지 않으면 숨김
                }
                if (filter.includes('생존') && !gameGenre.includes('SG')) {
                    isVisible = false; // 생존 필터에 맞지 않으면 숨김
                }
                if (filter.includes('전략') && !gameGenre.includes('STR')) {
                    isVisible = false; // 전략 필터에 맞지 않으면 숨김
                }

                // 게임의 플렛폼을 가져옴
                const $os = game.querySelectorAll('.logo');
                let osMatch = false; // OS 필터에 매칭되는지 여부를 추적

                // 하나의 게임에 플랫폼이 여러개 있을 수 있으니까 하나씩 비교함
                $os.forEach((os) => {
                    let osResult = os.textContent.trim().replace(/\s+/g, '').toLowerCase(); // 게임의 os 문자열을 가져옴
                    // 필터에 포함된 플랫폼이 게임의 플랫폼 목록에 포함되는지 체크
                    if (filter.includes('windows') && osResult.includes('windows')) {
                        osMatch = true; // Windows가 포함된 게임 찾음
                    }
                    if (filter.includes('mac') && osResult.includes('mac')) {
                        osMatch = true; // Mac이 포함된 게임 찾음
                    }
                });

                // 필터에 해당하는 OS가 없으면 숨김
                if ((filter.includes('windows') || filter.includes('mac')) && !osMatch) {
                    isVisible = false;
                }
            });

            if (isVisible) {
                game.style.display = 'grid';
                gameVisible = true;
            } else {
                game.style.display = 'none';
            }
        });

        const $noGames = document.querySelector('.no-games');
        if (gameVisible) {
            $noGames.style.display = 'none';
        } else {
            $noGames.style.display = 'flex';
        }
    }

    // 모든 필터 제거 클릭 시
    const $filterBtn = document.getElementById('filter-btn');
    if ($filterBtn != null) {
        $filterBtn.onclick = () => {
            // 검색 결과 없음 화면 안보이게
            const $noGames = document.querySelector('.no-games');
            $noGames.style.display = 'none';
            // 게임 목록 보이기
            $games.forEach(game => {
                game.style.display = 'grid';
            });
            // 체크박스 초기화
            const $checkbox = document.querySelectorAll('.checkbox');
            $checkbox.forEach(checkbox => {
                checkbox.setAttribute('aria-checked', 'false');
                // 배경 색상 변경
                checkbox.style.backgroundColor = 'inherit';
                // 체크아이콘 표시여부
                const $checkIcon = checkbox.querySelector('.check-icon');
                $checkIcon.style.display = 'none';
            });
            // 선택된 필터 초기화
            selectedFilters.length = 0;
        }
    }
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

    // 필터에 해당하는 정렬 실행
    const filterName = element.getAttribute('name') || element.textContent;
    sortGameList(filterName); // 정렬
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

// 실제 정렬
const sortGameList = (criteria) => {
    let sortedGames;
    // 정렬 조건
    switch (criteria) {
        case 'sale':
            sortedGames = $games.sort((a, b) => {
                // 할인률
                const discountAElement = a.querySelector('.discount-per-text');
                const discountBElement = b.querySelector('.discount-per-text');

                // 할인률이 있을 경우 할인된 가격을, 없을 경우에는 gamePrice를 사용
                const priceA = discountAElement
                    ? (discountAElement.innerText.trim() === '무료' ? 0 : parseFloat(discountAElement.innerText.replace(/[^0-9.-]+/g, '')))
                    : null;  // 할인률이 없다면 null로 설정

                const priceB = discountBElement
                    ? (discountBElement.innerText.trim() === '무료' ? 0 : parseFloat(discountBElement.innerText.replace(/[^0-9.-]+/g, '')))
                    : null;  // 할인률이 없다면 null로 설정

                // 1. 할인률이 있을 경우 우선 정렬
                if (priceA !== null && priceB === null) return -1;  // A는 할인률이 있고 B는 없을 경우 A가 앞
                if (priceB !== null && priceA === null) return 1;   // B는 할인률이 있고 A는 없을 경우 B가 앞

                // 2. 할인률이 있으면 할인률로 정렬, 없으면 gamePrice를 사용
                const priceAFinal = priceA !== null
                    ? priceA
                    : parseFloat(a.querySelector('.price.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                const priceBFinal = priceB !== null
                    ? priceB
                    : parseFloat(b.querySelector('.price.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                // 3. 할인률이 높은 순서로 정렬
                return priceAFinal - priceBFinal;
            });
            break;

        case 'alpha':
            sortedGames = $games.sort((a, b) => {
                const nameA = a.querySelector('.gameName').innerText;
                const nameB = b.querySelector('.gameName').innerText;
                return nameA.localeCompare(nameB, 'ko');
            });
            break;

        case 'newest':
            sortedGames = $games.sort((a, b) => {
                const dateA = new Date(a.querySelector('.game-open-date').innerText);
                const dateB = new Date(b.querySelector('.game-open-date').innerText);
                return dateB - dateA; // 최신순 정렬 (내림차순)
            });
            break;

        case 'price-desc':
            sortedGames = $games.sort((a, b) => {
                const priceA = a.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(a.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));
                const priceB = b.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(b.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                // '무료'를 마지막에 정렬하고, 나머지는 가격 내림차순으로 정렬
                if (priceA === 0 && priceB !== 0) return 1;
                if (priceB === 0 && priceA !== 0) return -1;
                return priceB - priceA;
            });
            break;


        case 'price-asc':
            sortedGames = $games.sort((a, b) => {
                const priceA = a.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(a.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));
                const priceB = b.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(b.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                // '무료'를 우선 정렬하고, 나머지는 가격 오름차순으로 정렬
                if (priceA === 0 && priceB !== 0) return -1;
                if (priceB === 0 && priceA !== 0) return 1;
                return priceA - priceB;
            });
            break;

        default:
            sortedGames = $games; // 정렬하지 않음
    }

    // 정렬된 게임을 DOM에 재배치
    sortedGames.forEach((game) => {
        $gameList.appendChild(game); //정렬된 순서대로 재추가
    });
};

// 위시리스트 맨 처음 정렬(할인중으로 선택되어있음)
const starfilterName = document.querySelector('.order-filter-container > .order-filter-list > .order-filter-item.selected').getAttribute('name');//처음 선택된 정렬필터의 이름(sale)
sortGameList(starfilterName); //정렬함

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
            $loading.style.display = 'none'; // 로딩화면 제거
            if (xhr.status < 200 || xhr.status >= 300){
                Swal.fire({
                    title: "서버가 알 수 없는 응답을 반환하였습니다.",
                    text: "잠시 후 시도해 주세요.",
                    icon: "warning"
                });
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if (response.result === 'failure'){
                Swal.fire({
                    title: "실패",
                    text: "제거에 실패하였습니다.",
                    icon: "error"
                });
            }
            else if(response.result === 'failure_not_found'){
                Swal.fire({
                    title: "실패",
                    text: "이미 위시리스트에 없어서 제거에 실패하였습니다.",
                    icon: "error"
                });
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

                // 실행 취소버튼 누를 시
                $cancelBtn.onclick = () => {
                    console.log(wishlistIndex)
                    const xhr = new XMLHttpRequest();
                    xhr.onreadystatechange = () => {
                        if(xhr.readyState !== XMLHttpRequest.DONE){
                            return;
                        }
                        document.body.style.cursor = 'default';
                        if (xhr.status < 200 || xhr.status >= 300){
                            Swal.fire({
                                title: "서버가 알 수 없는 응답을 반환하였습니다.",
                                text: "잠시 후 시도해 주세요.",
                                icon: "warning"
                            });
                            return;
                        }
                        const response = JSON.parse(xhr.responseText);
                        if (response.result === 'failure'){
                            Swal.fire({
                                title: "실패",
                                text: "잘못된 요청입니다.",
                                icon: "error"
                            });
                        }
                        else if(response.result === 'failure_not_found'){
                            Swal.fire({
                                title: "실패",
                                text: "찾을 수 없습니다.",
                                icon: "error"
                            });
                        }
                        else if (response.result === 'failure_duplicate_wishlist'){
                            Swal.fire({
                                title: "실패",
                                text: "이미 실행 취소된 목록입니다.",
                                icon: "error"
                            });
                        }
                        else if(response.result === 'success'){
                            $removeContainer.remove(); // 제거됨부분 없앰
                            $gameCard.removeHide(); // 해당 게임 목록 다시 보여줌
                        } else {
                            Swal.fire({
                                title: "실패",
                                text: "알수없는 이유로 실행 취소 요청이 실패하였습니다.",
                                icon: "error"
                            });
                        }
                    };
                    xhr.open('PATCH', '/purchase/wishlist/cancel');
                    xhr.send(formData);
                    document.body.style.cursor = 'not-allowed';
                };


                // 게임 이름 제거됨, 실행 취소버튼 추가
                $removeWrapper.appendChild($iconWrapper);
                $removeWrapper.appendChild($text);
                $removeContainer.appendChild($removeWrapper);
                $cancelBtnWrapper.appendChild($cancelBtn);
                $removeContainer.appendChild($cancelBtnWrapper);
                $removeGameCard.append($removeContainer);

                // 해당 게임 목록 없앰
                $gameCard.removeShow();
            } else {
                Swal.fire({
                    title: "실패",
                    text: "알수없는 이유로 제거에 실패하였습니다.",
                    icon: "error"
                });
            }
        };
        xhr.open('PATCH', '/purchase/wishlist/delete');
        xhr.send(formData);
        $loading.style.display = 'flex'; //로딩화면 출력
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
            $loading.style.display = 'none';
            if (xhr.status < 200 || xhr.status >= 300){
                Swal.fire({
                    title: "서버가 알 수 없는 응답을 반환하였습니다.",
                    text: "잠시 후 시도해 주세요.",
                    icon: "warning"
                });
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if (response.result === 'failure'){
                Swal.fire({
                    title: "실패",
                    text: "장바구니 담기에 실패하였습니다.",
                    icon: "error"
                });
            }
            else if(response.result === 'failure_duplicate_cart'){
                Swal.fire({
                    title: "실패",
                    text: "이미 장바구니에 있습니다.",
                    icon: "error"
                });
            }
            else if(response.result === 'failure_not_found'){
                Swal.fire({
                    title: "실패",
                    text: "찾을 수 없습니다.",
                    icon: "error"
                });
            }
            else if(response.result === 'failure_age_limit'){
                Swal.fire({
                    title: "실패",
                    text: "구매할 수 없는 나이의 게임이라서 장바구니 담기에 실패하였습니다.",
                    icon: "error"
                });
            }
            else if(response.result === 'success'){
                // btn.querySelector(':scope > .text').innerText = '장바구니 보기';
                // btn.onclick = () => {
                //     window.location.href = '/purchase/cart';
                // };
                location.reload();
            }
        };
        xhr.open('POST', '/purchase/cart/add'); // 장바구니에 추가
        xhr.send(formData);
        $loading.style.display = 'flex';
    });
}
//endregion