//region 검색어 입력 시 검색 결과
const $searchContainer = document.body.querySelector('.search-container');
if ($searchContainer) {
    const $area = $searchContainer.querySelector(':scope > .area');
    const $form = $area.querySelector(':scope > .form');
    const $searchForm = $form.querySelector(':scope > .searchForm');
    const $result = $form.querySelector(':scope > .result');
    const $resultWrapper = $result.querySelector(':scope > .result-wrapper');
    const $gameResultInit = $resultWrapper.querySelector(':scope > .init');
    const $gameResultLoading = $resultWrapper.querySelector(':scope > .loading');
    const $gameResultEmpty = $resultWrapper.querySelector(':scope > .empty');
    const $gameResultError = $resultWrapper.querySelector(':scope > .error');

    let gameSearchTimeout;
    let gameSearchLastKeyword;

    $searchForm['keyword'].addEventListener('focus', () => {
        $result.style.display = 'block';
        if ($searchForm['keyword'].value === '') {
            $gameResultInit.style.display = 'flex';
            $gameResultLoading.style.display = 'none';
        }
    });

    $searchForm['keyword'].addEventListener('blur', () => {
        if ($searchForm['keyword'].value === '') {
            $result.style.display = 'none';
        }
    });

    $searchForm['keyword'].addEventListener('keyup', () => {
        $result.querySelectorAll(':scope > .result-wrapper > .item').forEach((x) => x.remove());

        $gameResultEmpty.style.display = 'none';
        $gameResultError.style.display = 'none';

        if ($searchForm['keyword'].value === '') {
            $gameResultInit.style.display = 'flex';
            $gameResultLoading.style.display = 'none';
        } else {
            $gameResultLoading.style.display = 'flex';
            $gameResultInit.style.display = 'none';

            if (typeof gameSearchTimeout === 'number') {
                clearTimeout(gameSearchTimeout);
            }

            gameSearchLastKeyword = $searchForm['keyword'].value;

            gameSearchTimeout = setTimeout(() => {
                if (gameSearchLastKeyword !== $searchForm['keyword'].value) {
                    return;
                }

                const xhr = new XMLHttpRequest();
                const url = new URL(location.href);
                url.pathname = 'game/search-game';
                url.searchParams.set('keyword', $searchForm['keyword'].value);

                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }

                    $gameResultLoading.style.display = 'none';

                    if (xhr.status < 200 || xhr.status >= 300 || xhr.responseText.length === 0) {
                        $gameResultError.style.display = 'flex';
                        return;
                    }

                    const response = JSON.parse(xhr.responseText);

                    if (response.length === 0) {
                        $gameResultEmpty.style.display = 'flex';
                    } else {
                        const maxResults = 4;
                        const displayedGames = response.slice(0, maxResults);
                        displayedGames.forEach((game) => {
                            const $item = document.createElement('a');
                            $item.classList.add('item');
                            $item.target = '_self';
                            $item.href = `/game/page?index=${game['index']}`;

                            const $image = document.createElement('img');
                            $image.classList.add('image');
                            $image.src = game['base64Image'];

                            const $name = document.createElement('span');
                            $name.classList.add('name');
                            $name.innerText = game['name'];

                            $item.append($image, $name);
                            $resultWrapper.append($item);
                        });

                        if (response.length > maxResults) {
                            const $item = document.createElement('a');
                            $item.classList.add('item');
                            $item.target = '_self';
                            $item.href = `/game/browse?keyword=${gameSearchLastKeyword}`;
                            $item.innerText = '더 보기';
                            $resultWrapper.append($item);
                        }
                    }
                };

                xhr.open('GET', url.toString());
                xhr.send();

            }, 1000);
        }
    });

    document.addEventListener('click', (event) => {
        if (!($searchForm.contains(event.target))) {
            $result.style.display = 'none';
        }
    });

    $searchForm['keyword'].addEventListener('click', (event) => {
        event.stopPropagation();
    });

    $searchForm.addEventListener('submit', (event) => {
        setTimeout(() => {
            $searchForm['keyword'].value = '';
            $searchForm['keyword'].focus();
        }, 1000);
    });
}
//endregion

// region header 언어버튼 클릭 시 나타나는 영역
document.addEventListener('DOMContentLoaded', () => {
    const languageToggle = document.querySelector('.language');
    const button = document.querySelector('.global.icon');

    button.addEventListener('click', (e) => {
        e.stopPropagation();
        languageToggle.classList.toggle('active');
    });

    document.addEventListener('click', (e) => {
        if (!languageToggle.contains(e.target)) {
            languageToggle.classList.remove('active');
        }
    });
});
//endregion

// region footer 화살표 누르면 페이지 상단으로 바로 이동
const $pageup = document.body.querySelector('[name="pageup"]');
if ($pageup != null) {
    $pageup.onclick = () => {
        // 페이지 최상단으로 즉시 이동
        window.scrollTo(0, 0);
    }
}
//endregion

//region 로그아웃 버튼 클릭 시 모달창
{
    const $logoutButton = document.getElementById('logoutButton');
    const $logout = document.getElementById('logout');
    const $cancelButton = $logout.querySelector(':scope > .logoutDialog > .content > .button-container > [name="cancel"]');
    const $confirmButton = $logout.querySelector(':scope > .logoutDialog > .content > .button-container > [name="confirm"]');

    if ($logoutButton) {
        $logoutButton.onclick = () => {
            $logout.classList.add('--visible');
        };
    }

    if ($cancelButton) {
        $cancelButton.onclick = () => {
            $logout.classList.remove('--visible');
        };
    }
    if ($confirmButton) {
        $confirmButton.onclick = () => {
            location.href = '/user/logout';
        };
    }
}
//endregion

// region 필터 관련 함수(게임 분류 서버에서 불러옴)
/**
 * @Param element - 부모 HMTL 요소, 생성될 HTML요소를 담을 부모 요소
 * */
function filterGenre(element) {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            Swal.fire({
                title: "게임 분류 목록을 불러오는데 실패하였습니다.",
                text: "잠시 후 시도해 주세요.",
                icon: "warning"
            });
            return;
        }
        const response = JSON.parse(xhr.responseText);


        // 기존에 있는 필터 옵션을 초기화합니다.
        element.innerHTML = '';

        for (const genre of response['genres']) {
            // 장르 이름을 기준으로 <div class="filter-option"> 요소를 생성합니다.
            const filterOptionDiv = document.createElement('div');
            filterOptionDiv.classList.add('filter-option');

            // 체크박스와 라벨을 포함하는 내부 div 요소 생성
            const checkboxDiv = document.createElement('div');
            checkboxDiv.classList.add('checkbox');
            checkboxDiv.setAttribute('aria-checked', 'false');
            checkboxDiv.setAttribute('role', 'checkbox');
            checkboxDiv.setAttribute('tabindex', '0');

            // 장르 이름을 표시할 라벨 생성
            const labelSpan = document.createElement('span');
            labelSpan.classList.add('checkbox-label');
            labelSpan.textContent = genre.name + " 게임";  // 장르 이름 (예: '액션' 게임)
            if (genre.name === '롤플레잉') {
                labelSpan.textContent = genre.tag + " 게임";  // 'RPG' 게임
            }
            if (genre.name === '실시간전략') {
                labelSpan.textContent = genre.tag + " 게임";  // 'RTS' 게임
            }

            // 체크 아이콘 생성
            const checkIconSpan = document.createElement('span');
            checkIconSpan.classList.add('check-icon');
            checkIconSpan.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" class="svg css-uwwqev"
                                                 viewBox="0 0 10 8"><g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"><g transform="translate(-320.000000, -202.000000)" stroke="currentColor" stroke-width="2"><polyline
                                                    points="321 205.569101 323.596499 208 329 203"></polyline></g></g>
                                            </svg>`;

            // 생성된 요소들을 DOM에 추가
            checkboxDiv.appendChild(labelSpan);
            checkboxDiv.appendChild(checkIconSpan);
            filterOptionDiv.appendChild(checkboxDiv);

            // 장르를 필터 옵션 컨테이너에 추가
            element.appendChild(filterOptionDiv);
        }
    };
    xhr.open('GET', '/genre/');
    xhr.send();
}

//endregion

//region hide, show 및 findLabel 구현
HTMLElement.prototype.hide = function () {
    this.classList.remove('-visible');
    return this;
}

HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    return this;
}

HTMLElement.prototype.removeHide = function () {
    this.classList.remove('remove');
    return this;
}

HTMLElement.prototype.removeShow = function () {
    this.classList.add('remove');
    return this;
}
/**
 * @param{string} dataId
 * @returns {HTMLLabelElement}
 */
HTMLFormElement.prototype.findLabel = function (dataId) {
    return this.querySelector(`label.--obj-label[data-id="${dataId}"]`);
}
//endregion
