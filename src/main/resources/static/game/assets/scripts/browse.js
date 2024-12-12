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
        });
    }
});
//endregion


