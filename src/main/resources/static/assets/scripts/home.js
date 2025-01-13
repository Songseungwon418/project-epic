// region 사이드배너 클릭 이벤트 및 타이머 설정
{
    document.addEventListener("DOMContentLoaded", () => {
        const $sideBanners = document.querySelectorAll(".side-banner > a");
        const $mainBanners = document.querySelectorAll(".main-banner > div");
        let currentSelected = 0; // 초기 선택된 항목 (first side)
        let canNavigate = false; // 첫 번째 클릭에서는 이동하지 않도록 설정
        let timer; // 타이머 변수
        let isNavigating = false; // 페이지 이동 상태 관리 변수

        // 7초마다 -selected 클래스 변경
        function startInterval() {
            if (timer) {
                clearInterval(timer); // 기존 타이머가 있으면 제거
            }
            timer = setInterval(() => {
                updateSelected((currentSelected + 1) % $sideBanners.length);
                canNavigate = false; // 자동 변경된 경우 href 이동 불가
            }, 7000);
        }

        // 선택된 배너를 업데이트하는 함수
        function updateSelected(newIndex) {
            if (newIndex === currentSelected) return; // 이미 선택된 항목은 처리하지 않음

            // 기존 -selected 클래스 제거
            $sideBanners[currentSelected]?.classList.remove("-selected");
            $mainBanners[currentSelected]?.classList.remove("-selected");

            // 새 항목에 -selected 클래스 추가
            currentSelected = newIndex;
            $sideBanners[currentSelected]?.classList.add("-selected");
            $mainBanners[currentSelected]?.classList.add("-selected");
        }

        // 클릭 이벤트 처리 함수
        function handleBannerClick(e, index) {
            if (isNavigating) return; // 페이지 이동 중이면 중복 처리 방지

            if (!$sideBanners[index].classList.contains("-selected")) {
                e.preventDefault(); // href 이동 방지
                updateSelected(index);
                canNavigate = false; // 첫 번째 클릭에서는 이동 불가
            } else {
                // 이미 선택된 항목을 클릭한 경우
                if (canNavigate) {
                    isNavigating = true; // 페이지 이동 상태로 설정
                    window.location.href = $sideBanners[index].href; // href로 이동
                }
            }

            // 타이머 재설정
            clearInterval(timer); // 기존 타이머 종료
            canNavigate = true; // 두 번째 클릭부터 이동 가능
            startInterval(); // 타이머 다시 시작
        }

        // 클릭 이벤트 등록 (이벤트 위임 방식 활용)
        $sideBanners.forEach((banner, index) => {
            banner.addEventListener("click", (e) => handleBannerClick(e, index));
        });

        // 초기화 및 타이머 시작
        updateSelected(currentSelected); // 초기 선택 상태 반영
        startInterval(); // 타이머 시작
    });

}


//endregion

//region 새로운게임 버튼 누르면 게임 이동
{
    document.addEventListener("DOMContentLoaded", () => {
        const $newGame = document.querySelector('.newGame');
        const $titleWrapper = $newGame.querySelector(':scope > .title-wrapper');
        const $gameWrapper = $newGame.querySelector(':scope > .newGame-container > .newGame-wrapper');
        const $prevButton = $titleWrapper.querySelector(':scope > .button-container > .prev.button');
        const $nextButton = $titleWrapper.querySelector(':scope > .button-container > .next.button');
        const $prevSvgPath = $prevButton.querySelector(':scope > button > svg path');
        const $nextSvgPath = $nextButton.querySelector(':scope > button > svg path');

        let games = document.querySelectorAll('.game');
        const gap = parseFloat(getComputedStyle($gameWrapper).gap);
        let slideWidth = document.querySelector('.game').offsetWidth;
        let visibleGames = 5;
        let currentIndex = 0;
        let resizeTimeout;

        function updateSlider() {
            const totalSlideWidth = slideWidth + gap;
            const slideOffset = currentIndex * totalSlideWidth;

            $gameWrapper.style.transition = 'transform 0.3s ease';
            $gameWrapper.style.transform = `translateX(-${slideOffset}px)`;

            const isAtStart = currentIndex === 0;
            const isAtEnd = currentIndex >= games.length - visibleGames - 1;

            $prevButton.style.pointerEvents = isAtStart ? 'none' : 'auto';
            $prevSvgPath.style.stroke = isAtStart ? '#818181' : '#ffffff';

            $nextButton.style.pointerEvents = isAtEnd ? 'none' : 'auto';
            $nextSvgPath.style.stroke = isAtEnd ? '#818181' : '#ffffff';
        }

        function initSlider() {
            updateSlider();
            $prevButton.style.pointerEvents = 'none';
            $prevSvgPath.style.stroke = '#818181';
        }

        window.addEventListener('resize', () => {
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(() => {
                slideWidth = document.querySelector('.game').offsetWidth;
                updateSlider();
            }, 200);
        });

        $prevButton.addEventListener('click', () => {
            if (currentIndex > 0) {
                currentIndex--;
                updateSlider();
            }
        });

        $nextButton.addEventListener('click', () => {
            if (currentIndex < games.length - visibleGames - 1) {
                currentIndex++;
                updateSlider();
            }
        });

        initSlider();
    });
}

//endregion

//region 게임 정상가 양식
{
    document.addEventListener("DOMContentLoaded", function () {
        // 모든 .game-price 요소를 선택
        const $gamePrices = document.querySelectorAll('.game-price');

        $gamePrices.forEach(gamePrice => {
            const rawPrice = parseInt(gamePrice.textContent.trim(), 10); // 현재 텍스트를 정수로 변환

            // 가격이 0이면 "무료" 표시, 아니면 천 단위 포맷 추가
            if (rawPrice === 0) {
                gamePrice.textContent = "무료";
            } else if (!isNaN(rawPrice)) {
                gamePrice.textContent = `￦${rawPrice.toLocaleString()}`; // 천 단위 쉼표 추가 및 \ 기호 붙이기
            }
        });
    });
}
//endregion

//region 할인게임 정상가 양식
{
    document.addEventListener("DOMContentLoaded", function () {
        const $gamePrices = document.querySelectorAll('.origin-price');

        $gamePrices.forEach(gamePrice => {
            const rawPrice = parseInt(gamePrice.textContent.trim(), 10);

            gamePrice.textContent = `￦${rawPrice.toLocaleString()}`;
        });
    });
}
//endregion

//region 할인게임 할인가 양식
{
    document.addEventListener("DOMContentLoaded", function () {
        const $gamePrices = document.querySelectorAll('.sale-price');

        $gamePrices.forEach(gamePrice => {
            const rawPrice = parseInt(gamePrice.textContent.trim(), 10);

            gamePrice.textContent = `￦${rawPrice.toLocaleString()}`;
        });
    });
}
//endregion

//region 할인게임 버튼 누르면 게임 이동
{
    document.addEventListener("DOMContentLoaded", () => {
        const $saleGame = document.querySelector('.saleGame');
        const $titleWrapper = $saleGame.querySelector(':scope > .title-wrapper');
        const $gameWrapper = $saleGame.querySelector(':scope > .saleGame-container > .saleGame-wrapper');
        const $prevButton = $titleWrapper.querySelector(':scope > .button-container > .prevButton');
        const $nextButton = $titleWrapper.querySelector(':scope > .button-container > .nextButton');
        const $pSvgPath = $prevButton.querySelector(':scope > button > svg path');
        const $nSvgPath = $nextButton.querySelector(':scope > button > svg path');

        let saleGames = document.querySelectorAll('.sale-game');
        const gap = parseFloat(getComputedStyle($gameWrapper).gap); // 카드 간격 가져오기
        let slideWidth = document.querySelector('.sale-game').offsetWidth; // 초기 카드 너비
        let visibleGames = 5; // 한 번에 보여지는 게임 카드 개수
        let currentIndex = 0; // 현재 슬라이드 위치
        let resizeTimeout; // 리사이즈 이벤트 디바운싱 변수

        // 슬라이드 위치 업데이트 함수
        function updateSliders() {
            const totalSlideWidth = slideWidth + gap; // 카드 너비 + 간격
            const slideOffset = currentIndex * totalSlideWidth; // 슬라이드 이동 위치 계산

            // 슬라이드 이동
            $gameWrapper.style.transition = 'transform 0.3s ease'; // 애니메이션 설정
            $gameWrapper.style.transform = `translateX(-${slideOffset}px)`; // 위치 이동

            // 이전 버튼 활성화 여부 설정
            const isAtStart = currentIndex === 0;
            $prevButton.style.pointerEvents = isAtStart ? 'none' : 'auto';
            $pSvgPath.style.stroke = isAtStart ? '#818181' : '#ffffff';

            // 다음 버튼 활성화 여부 설정
            const isAtEnd = currentIndex >= saleGames.length - visibleGames;
            $nextButton.style.pointerEvents = isAtEnd ? 'none' : 'auto';
            $nSvgPath.style.stroke = isAtEnd ? '#818181' : '#ffffff';
        }

        // 초기화 함수
        function initSliders() {
            updateSliders();
            $prevButton.style.pointerEvents = 'none'; // 초기 이전 버튼 비활성화
            $pSvgPath.style.stroke = '#818181';
        }

        // 화면 크기 변화 시 슬라이드 너비 재계산
        window.addEventListener('resize', () => {
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(() => {
                slideWidth = document.querySelector('.sale-game').offsetWidth; // 새 카드 너비 계산
                updateSliders(); // 슬라이더 업데이트
            }, 200); // 디바운싱: 200ms 대기
        });

        // 이전 버튼 클릭 이벤트
        $prevButton.addEventListener('click', () => {
            if (currentIndex > 0) {
                currentIndex--;
                updateSliders();
            }
        });

        // 다음 버튼 클릭 이벤트
        $nextButton.addEventListener('click', () => {
            if (currentIndex < saleGames.length - visibleGames) {
                currentIndex++;
                updateSliders();
            }
        });

        // 동적으로 추가/삭제된 카드 처리 함수
        function refreshSaleGames() {
            saleGames = document.querySelectorAll('.sale-game'); // 카드 목록 갱신
            updateSliders(); // 슬라이더 상태 업데이트
        }

        // 초기화 실행
        initSliders();
    });

}
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
                Swal.fire({
                    icon: "info",
                    title: "로그인 후 이용 가능합니다.",
                    text: "로그인 페이지로 이동합니다."
                }).then(() => {
                    window.location.href = '/user/';
                });
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
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "잠시 후 시도해 주세요.",
                        icon: "warning"
                    });
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response.result === 'failure') {
                    Swal.fire({
                        title: "실패",
                        text: "위시리스트 담기에 실패하였습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'failure_duplicate_cart') {
                    Swal.fire({
                        title: "실패",
                        text: "이미 위시리스트에 있습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'failure_not_found') {
                    Swal.fire({
                        title: "실패",
                        text: "찾을 수 없습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'success') {
                    Swal.fire({
                        icon: "success",
                        title: "성공",
                        text: "위시리스트에 추가되었습니다."
                    }).then(() => {
                        location.reload();
                    });
                }
            };
            xhr.open('POST', '../purchase/wishlist/add');
            xhr.send(formData);
        };
    });
}
//endregion

//region 새로운게임 위시리스트 제거
const $newWishlistDeleteButtons = document.querySelectorAll('.newBack');

if ($newWishlistDeleteButtons.length > 0) {
    $newWishlistDeleteButtons.forEach(button => {
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
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "잠시 후 시도해 주세요.",
                        icon: "warning"
                    });
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response.result === 'failure') {
                    Swal.fire({
                        title: "실패",
                        text: "위시리스트에서 제거에 실패하였습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'failure_not_found') {
                    Swal.fire({
                        title: "실패",
                        text: "위시리스트에서 이미 삭제되었거나 오류로 인해 제거에 실패하였습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'success') {
                    Swal.fire({
                        icon: "success",
                        title: "성공",
                        text: "위시리스트에서 제거하였습니다."
                    }).then(() => {
                        location.reload();
                    });
                }
            }
            xhr.open('PATCH', '../purchase/wishlist/delete');
            xhr.send(formData);
        };
    });
}
//endregion

//region 세일게임 위시리스트 제거
const $saleWishlistDeleteButtons = document.querySelectorAll('.saleBack');

if ($saleWishlistDeleteButtons.length > 0) {
    $saleWishlistDeleteButtons.forEach(button => {
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
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "잠시 후 시도해 주세요.",
                        icon: "warning"
                    });
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response.result === 'failure') {
                    Swal.fire({
                        title: "실패",
                        text: "위시리스트에서 제거에 실패하였습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'failure_not_found') {
                    Swal.fire({
                        title: "실패",
                        text: "위시리스트에서 이미 삭제되었거나 오류로 인해 제거에 실패하였습니다.",
                        icon: "error"
                    });
                } else if (response.result === 'success') {
                    Swal.fire({
                        icon: "success",
                        title: "성공",
                        text: "위시리스트에서 제거하였습니다."
                    }).then(() => {
                        location.reload();
                    });
                }
            }
            xhr.open('PATCH', '../purchase/wishlist/delete');
            xhr.send(formData);
        };
    });
}
//endregion

