//region 장르별 설명
const description = {
    'ACT': `Epic Games Store는 최고의 액션 게임을 제공합니다.<br>오늘 다운로드해서 재미있고 흥미진진한 액션 게임을 플레이하세요.`,
    'OW': `Epic Games Store에서 오픈 월드 게임을 다운로드하고 플레이하세요.<br>최고의 가장 흥미진진한 오픈 월드 게임 몇 가지를 제공합니다.`,
    'RPG': `Epic Games Store에서 RPG 게임을 다운로드하고 플레이하세요.<br>최고의 가장 흥미진진한 RPG 게임 몇 가지를 제공합니다.`,
    'RTS': `Epic Games Store는 최고의 RTS 게임을 제공합니다.<br>오늘 다운로드해서 재미있고 흥미진진한 RTS 게임을 플레이하세요.`,
    'SG': `Epic Games Store에서 서바이벌 게임을 다운로드하고 플레이하세요.<br>최고의 가장 흥미진진한 서바이벌 게임 몇 가지를 제공합니다.`,
    'STR': `Epic Games Store에서 전략 게임을 다운로드하고 플레이하세요.<br>최고의 가장 흥미진진한 전략 게임 몇 가지를 제공합니다.`
};

document.addEventListener('DOMContentLoaded', () => {
    const $genreTagElement = document.getElementById('genreTag');
    const genreTag = $genreTagElement ? $genreTagElement.textContent.trim() : null;

    const $subTitle = document.getElementById('subTitle');
    if (genreTag && description[genreTag]) {
        $subTitle.innerHTML = description[genreTag];
    } else {
        $subTitle.textContent = '해당 장르에 대한 설명이 없습니다.';
    }
});
//endregion

//region 찾아보기 정렬 필터
{
    // 드롭다운 버튼과 리스트 선택
    const dropdownButton = document.querySelector('.btn-select');
    const list = document.querySelector('.list-member');
    const gameWrapper = document.querySelector('.game-wrapper');

// 드롭다운 토글
    dropdownButton.addEventListener('click', () => {
        dropdownButton.classList.toggle('-selected');
    });

// 드롭다운 리스트 클릭 이벤트
    list.addEventListener('click', (event) => {
        if (event.target.nodeName === "BUTTON") {
            const selectedValue = event.target.innerText;

            // 버튼 텍스트 업데이트 및 드롭다운 닫기
            dropdownButton.innerText = selectedValue;
            dropdownButton.classList.remove('-selected');

            // 선택된 값에 따라 정렬 실행
            sortGames(selectedValue);
        }
    });

// 게임 정렬 함수
    const sortGames = (criteria) => {
        // .game-wrapper 안의 모든 .game 요소를 배열로 변환
        const games = Array.from(gameWrapper.querySelectorAll('.game'));
        let sortedGames;

        // 정렬 조건
        switch (criteria) {

            case '이름순':
                sortedGames = games.sort((a, b) => {
                    const nameA = a.querySelector('.gameName').innerText;
                    const nameB = b.querySelector('.gameName').innerText;
                    return nameA.localeCompare(nameB, 'ko');
                });
                break;

            case '가격 내림차순':
                sortedGames = games.sort((a, b) => {
                    const priceA = a.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(a.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));
                    const priceB = b.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(b.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                    // '무료'를 마지막에 정렬하고, 나머지는 가격 내림차순으로 정렬
                    if (priceA === 0 && priceB !== 0) return 1;
                    if (priceB === 0 && priceA !== 0) return -1;
                    return priceB - priceA;
                });
                break;


            case '가격 오름차순':
                sortedGames = games.sort((a, b) => {
                    const priceA = a.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(a.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));
                    const priceB = b.querySelector('.gamePrice').innerText.trim() === '무료' ? 0 : parseFloat(b.querySelector('.gamePrice').innerText.replace(/[^0-9.-]+/g, ''));

                    // '무료'를 우선 정렬하고, 나머지는 가격 오름차순으로 정렬
                    if (priceA === 0 && priceB !== 0) return -1;
                    if (priceB === 0 && priceA !== 0) return 1;
                    return priceA - priceB;
                });
                break;


            default:
                sortedGames = games; // 정렬하지 않음
        }

        // 정렬된 게임을 DOM에 재배치
        sortedGames.forEach((game) => {
            gameWrapper.appendChild(game); // game-wrapper에 정렬된 순서대로 재추가
        });
    };

}
//endregion

//region 모든게임 가격 양식
document.addEventListener("DOMContentLoaded", function () {
    const priceElements = document.querySelectorAll('.gamePrice');
    priceElements.forEach(priceElement => {
        const rawPrice = parseInt(priceElement.textContent.trim(), 10);

        if (rawPrice === 0) {
            priceElement.textContent = "무료";
        } else if (!isNaN(rawPrice)) {
            priceElement.textContent = `￦${rawPrice.toLocaleString()}`;
        }
    });
});
//endregion

//region 필터관련
document.addEventListener('DOMContentLoaded', function () {
    // 필터 토글 버튼 클릭 시 필터 목록 표시/숨기기
    const $buttons = document.querySelectorAll('.filter-toggle-button');

    $buttons.forEach((button) => {
        button.addEventListener('click', () => {
            // 'aria-expanded' 속성 값 가져오기
            const isExpanded = button.getAttribute('aria-expanded') === 'true';

            // 'aria-expanded' 값 토글
            button.setAttribute('aria-expanded', !isExpanded);

            // 버튼의 부모 .title 요소에서 다음 형제 요소인 .filter-list-content 찾기
            const $filterContent = button.closest('.title').nextElementSibling;

            // filter-list-content 표시/숨기기
            if ($filterContent && $filterContent.classList.contains('filter-list-content')) {
                $filterContent.style.display = isExpanded ? 'none' : 'block';
            }
        });
    });

    // 필터 옵션 클릭 시 하나만 선택하도록 제한
    const $checkboxes = document.querySelectorAll('.checkbox');

    $checkboxes.forEach((checkbox) => {
        checkbox.addEventListener('click', () => {
            const isChecked = checkbox.getAttribute('aria-checked') === 'true';

            // 현재 필터 그룹이 "가격", "게임 연령" 또는 "장르"인지 확인
            const listName = checkbox.closest('.filter-list-content').previousElementSibling.querySelector('.list-name').innerText;

            if (listName === '가격' && !isChecked) {
                // "가격" 필터 그룹 내의 모든 체크박스를 해제
                $checkboxes.forEach((box) => {
                    const parentListName = box.closest('.filter-list-content').previousElementSibling.querySelector('.list-name').innerText;
                    if (parentListName === '가격') {
                        box.setAttribute('aria-checked', 'false');
                        box.style.backgroundColor = 'inherit';
                        box.querySelector('.check-icon').style.display = 'none';
                    }
                });
            }

            // "게임 연령" 관련 필터가 선택되었을 때 다른 연령 필터는 해제
            if (listName === '게임 연령' && !isChecked) {
                $checkboxes.forEach((box) => {
                    const parentListName = box.closest('.filter-list-content').previousElementSibling.querySelector('.list-name').innerText;
                    if (parentListName === '게임 연령' && box !== checkbox) {
                        box.setAttribute('aria-checked', 'false');
                        box.style.backgroundColor = 'inherit';
                        box.querySelector('.check-icon').style.display = 'none';
                    }
                });
            }

            // 클릭된 체크박스의 선택 상태 토글
            checkbox.setAttribute('aria-checked', !isChecked);
            checkbox.style.backgroundColor = !isChecked ? '#918D8D38' : 'inherit';

            const $checkIcon = checkbox.querySelector('.check-icon');
            $checkIcon.style.display = !isChecked ? 'block' : 'none';

            // 필터링 함수 호출
            applyFilters();
        });
    });

    // 필터링 적용 함수
    function applyFilters() {
        // 선택된 필터 조건 가져오기
        const selectedFilters = Array.from($checkboxes)
            .filter((checkbox) => checkbox.getAttribute('aria-checked') === 'true')
            .map((checkbox) => checkbox.querySelector('.checkbox-label').innerText);

        const $games = document.querySelectorAll('.game');
        let gameVisible = false; // 게임이 하나라도 표시되는지 여부

        $games.forEach((game) => {
            const gamePriceText = game.querySelector('.gamePrice').innerText.trim();
            const gamePrice = gamePriceText === '무료' ? 0 : parseFloat(gamePriceText.replace(/[^\d]/g, ''));

            let isVisible = true;

            // 가격 필터 적용
            if (selectedFilters.includes('무료')) {
                isVisible = gamePrice === 0;
            } else if (selectedFilters.includes('￦11,000원 미만')) {
                isVisible = gamePrice < 11000;
            } else if (selectedFilters.includes('￦22,000원 미만')) {
                isVisible = gamePrice < 22000;
            } else if (selectedFilters.includes('￦33,000원 미만')) {
                isVisible = gamePrice < 33000;
            } else if (selectedFilters.includes('￦33,000원 이상')) {
                isVisible = gamePrice >= 33000;
            }

            // 게임 연령 필터 적용
            if (selectedFilters.includes('전체이용가')) {
                const eventText = game.querySelector('.gameAge').innerText; // 게임 연령
                if (!eventText.includes('ALL')) {
                    isVisible = false;
                }
            } else if (selectedFilters.includes('12세이용가')) {
                const eventText = game.querySelector('.gameAge').innerText;
                if (!eventText.includes('12Y')) {
                    isVisible = false;
                }
            } else if (selectedFilters.includes('15세이용가')) {
                const eventText = game.querySelector('.gameAge').innerText;
                if (!eventText.includes('15Y')) {
                    isVisible = false;
                }
            } else if (selectedFilters.includes('청소년 이용불가')) {
                const eventText = game.querySelector('.gameAge').innerText;
                if (!eventText.includes('ADULT')) {
                    isVisible = false;
                }
            }

            // 장르 필터 적용
            if (selectedFilters.includes('액션 게임')) {
                const gameGenre = game.querySelector('.gameGenre').innerText; // 게임 장르
                if (!gameGenre.includes('ACT')) {
                    isVisible = false;
                }
            }
            if (selectedFilters.includes('RPG 게임')) {
                const gameGenre = game.querySelector('.gameGenre').innerText;
                if (!gameGenre.includes('RPG')) {
                    isVisible = false;
                }
            }
            if (selectedFilters.includes('오픈 월드')) {
                const gameGenre = game.querySelector('.gameGenre').innerText;
                if (!gameGenre.includes('OW')) {
                    isVisible = false;
                }
            }
            if (selectedFilters.includes('RTS 게임')) {
                const gameGenre = game.querySelector('.gameGenre').innerText;
                if (!gameGenre.includes('RTS')) {
                    isVisible = false;
                }
            }
            if (selectedFilters.includes('전략')) {
                const gameGenre = game.querySelector('.gameGenre').innerText;
                if (!gameGenre.includes('STR')) {
                    isVisible = false;
                }
            }
            if (selectedFilters.includes('생존')) {
                const gameGenre = game.querySelector('.gameGenre').innerText;
                if (!gameGenre.includes('SG')) {
                    isVisible = false;
                }
            }

            // 게임 표시/숨기기
            if (isVisible) {
                game.style.display = 'block';
                gameVisible = true; // 게임이 하나라도 표시되면 true
            } else {
                game.style.display = 'none';
            }
            const $noGames = document.querySelector('.no-games');
            if (gameVisible) {
                $noGames.style.display = 'none';
            } else {
                $noGames.style.display = 'flex';
            }
        });
    }
});
//endregion

//region 게임 위시리스트 추가
const $WishlistButtons = document.querySelectorAll('.add');

if ($WishlistButtons.length > 0) {
    $WishlistButtons.forEach(button => {
        button.onclick = (e) => {
            e.preventDefault();

            const gameIndex = button.getAttribute('data-game-index');
            const formData = new FormData();
            const userEmail = document.getElementById('userEmail')?.value; // userEmail 값 가져오기

            // 로그인 여부 확인
            if (!userEmail) {
                alert('로그인 후 이용가능합니다. 로그인 페이지로 이동합니다.');
                window.location.href = '/user/';
                return;
            }

            // 폼 데이터 설정
            formData.append('gameIndex', gameIndex);
            formData.append('userEmail', userEmail);

            // AJAX 요청 생성 및 처리
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response.result === 'failure') {
                    alert('위시리스트 담기에 실패하였습니다.');
                } else if (response.result === 'failure_duplicate_cart') {
                    alert('이미 위시리스트에 있습니다.');
                } else if (response.result === 'failure_not_found') {
                    alert('찾을 수 없습니다.');
                } else if (response.result === 'success') {
                    alert('위시리스트에 추가되었습니다.');
                    location.reload();
                }
            };
            xhr.open('POST', '../purchase/wishlist/add');
            xhr.send(formData);
        };
    });
}
//endregion

//region 게임 위시리스트 제거
const $WishlistDeleteButtons = document.querySelectorAll('.newBack');

if ($WishlistDeleteButtons.length > 0) {
    $WishlistDeleteButtons.forEach(button => {
        button.onclick = (e) => {
            e.preventDefault();

            // 각 버튼에 해당하는 wishlistIndex 값을 가져옴
            const wishlistIndex = button.getAttribute('data-wishlist-index'); // data-wishlist-index 속성 값 가져옴

            const formData = new FormData();
            formData.append('index', wishlistIndex);  // 위시리스트 인덱스를 전달

            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 300) {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response.result === 'failure') {
                    alert('위시리스트에서 제거에 실패하였습니다.');
                } else if (response.result === 'failure_not_found') {
                    alert('위시리스트에서 이미 삭제되었거나 오류로 인해 제거에 실패하였습니다.');
                } else if (response.result === 'success') {
                    alert('위시리스트에서 제거하였습니다.');
                    location.reload();
                }
            }
            xhr.open('PATCH', '../purchase/wishlist/delete');
            xhr.send(formData);
        };
    });
}
//endregion
