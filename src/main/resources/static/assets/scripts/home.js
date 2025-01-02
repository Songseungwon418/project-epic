// region 사이드배너 클릭 이벤트 및 타이머 설정

{
    document.addEventListener("DOMContentLoaded", () => {
        const $sideBanners = document.querySelectorAll(".side-banner > a");
        const $mainBanners = document.querySelectorAll(".main-banner > div");
        let currentSelected = 0; // 초기 선택된 항목 (first side)
        let canNavigate = false; // 첫 번째 클릭에서는 이동하지 않도록 설정
        let timer; // 타이머 변수

        // 7초마다 -selected 클래스 변경
        function startInterval() {
            timer = setInterval(() => {
                // 현재 -selected 클래스 제거
                $sideBanners[currentSelected].classList.remove("-selected");
                $mainBanners[currentSelected].classList.remove("-selected");

                // 다음 항목으로 이동 (마지막 항목은 첫 번째로 순환)
                currentSelected = (currentSelected + 1) % $sideBanners.length;

                // 새 항목에 -selected 클래스 추가
                $sideBanners[currentSelected].classList.add("-selected");
                $mainBanners[currentSelected].classList.add("-selected");

                canNavigate = false; // 자동으로 변경된 항목에서는 href 이동 불가
            }, 7000);
        }

        // 타이머 시작
        startInterval();

        // 클릭 이벤트 처리
        $sideBanners.forEach((banner, index) => {
            banner.addEventListener("click", (e) => {
                // -selected가 없는 항목을 클릭한 경우
                if (!banner.classList.contains("-selected")) {
                    e.preventDefault(); // href 이동 방지
                    // 기존에 -selected 클래스가 있는 항목에서 제거
                    $sideBanners[currentSelected].classList.remove("-selected");
                    $mainBanners[currentSelected].classList.remove("-selected");

                    // 클릭한 항목에 -selected 클래스 추가
                    currentSelected = index;
                    $sideBanners[currentSelected].classList.add("-selected");
                    $mainBanners[currentSelected].classList.add("-selected");

                    canNavigate = false; // 첫 번째 클릭 시에는 href로 이동하지 않음

                } else {
                    // -selected가 있는 항목을 클릭한 경우
                    if (canNavigate) {
                        // href로 이동
                        window.location.href = banner.href;
                    }
                }

                // 7초 타이머가 끝나기 전에 클릭했을 경우, href로 이동할 수 있도록 설정
                clearInterval(timer); // 기존 타이머 종료
                canNavigate = true; // 두 번째 클릭에서 href로 이동 가능
                startInterval(); // 타이머 다시 시작
            });
        });
    });
}


//endregion

//region 새로운게임 버튼 누르면 게임 이동
{
    const $newGame = document.querySelector('.newGame');
    const $titleWrapper = $newGame.querySelector(':scope > .title-wrapper');
    const $gameWrapper = $newGame.querySelector(':scope > .newGame-container > .newGame-wrapper');
    const $prevButton = $titleWrapper.querySelector(':scope > .button-container > .prev.button');
    const $nextButton = $titleWrapper.querySelector(':scope > .button-container > .next.button');
    const $prevSvgPath = $prevButton.querySelector(':scope > button > svg path');
    const $nextSvgPath = $nextButton.querySelector(':scope > button > svg path');

    const games = document.querySelectorAll('.game');
    const gap = parseFloat(getComputedStyle($gameWrapper).gap); // .game-wrapper의 gap 값을 가져오기
    let visibleGames = 6; // 한 번에 보여지는 게임 카드 개수
    let currentIndex = 0; // 현재 슬라이드 위치

// 초기화 함수
    function initSlider() {
        updateSlider(); // 초기 슬라이더 위치 설정
        $prevButton.style.pointerEvents = 'none';  // 초기 페이지에서 prev 버튼 비활성화
        $prevSvgPath.style.stroke = '#818181';
    }

// 슬라이드 위치 업데이트 함수
    function updateSlider() {
        const slideWidth = document.querySelector('.game').offsetWidth; // 한 게임 카드의 너비
        const totalSlideWidth = slideWidth + gap; // 카드의 너비에 gap을 더함
        const slideOffset = currentIndex * totalSlideWidth;  // 슬라이드 이동 위치 계산

        // 슬라이드 이동을 부드럽게 처리하기 위해 transition을 설정합니다.
        $gameWrapper.style.transition = 'transform 0.3s ease';  // 슬라이드 애니메이션 적용

        // 슬라이드 이동
        $gameWrapper.style.transform = `translateX(-${slideOffset}px)`; // 슬라이드 이동

        // 이전 버튼 활성화 여부 설정
        if (currentIndex === 0) {
            $prevButton.style.pointerEvents = 'none';  // 첫 페이지일 때 prev 버튼 비활성화
            $prevSvgPath.style.stroke = '#818181';  // 색상 변경
        } else {
            $prevButton.style.pointerEvents = 'auto';  // 첫 페이지가 아니면 활성화
            $prevSvgPath.style.stroke = '#ffffff';  // 색상 변경
        }

        // 마지막 게임에 도달했을 때 next 버튼 비활성화
        if (currentIndex >= games.length - visibleGames - 1) {
            $nextButton.style.pointerEvents = 'none';  // next 버튼 클릭 비활성화
            $nextSvgPath.style.stroke = '#818181';  // 색상 변경
        } else {
            $nextButton.style.pointerEvents = 'auto';  // next 버튼 클릭 활성화
            $nextSvgPath.style.stroke = '#ffffff';  // 색상 변경
        }
    }

// 화면 크기 변화에 따른 재설정
    window.addEventListener('resize', () => {
        updateSlider(); // 카드 너비가 변경될 수 있으므로 슬라이더 위치 업데이트
    });

// 이전 버튼 클릭 이벤트
    $prevButton.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateSlider();
        }
    });

// 다음 버튼 클릭 이벤트
    $nextButton.addEventListener('click', () => {
        // 마지막 게임에 도달했을 때, 더 이상 슬라이드 넘어가지 않도록 처리
        if (currentIndex < games.length - visibleGames - 1) {
            currentIndex++;
            updateSlider();
        }
    });

// 초기화 실행
    initSlider();
}

//endregion

//region 새로운게임 가격 양식
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

//region 할인게임 정상가 천단위 표시
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

//region 할인게임 할인가 천단위 표시
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
    const $saleGame = document.querySelector('.saleGame');
    const $titleWrapper = $saleGame.querySelector(':scope > .title-wrapper');
    const $gameWrapper = $saleGame.querySelector(':scope > .saleGame-container > .saleGame-wrapper');
    const $prevButton = $titleWrapper.querySelector(':scope > .button-container > .prevButton');
    const $nextButton = $titleWrapper.querySelector(':scope > .button-container > .nextButton');
    const $pSvgPath = $prevButton.querySelector(':scope > button > svg path');
    const $nSvgPath = $nextButton.querySelector(':scope > button > svg path');

    const saleGames = document.querySelectorAll('.sale-game');
    const gap = parseFloat(getComputedStyle($gameWrapper).gap); // .game-wrapper의 gap 값을 가져오기
    let visibleGames = 5; // 한 번에 보여지는 게임 카드 개수
    let currentIndex = 0; // 현재 슬라이드 위치

    // 초기화 함수
    function initSliders() {
        updateSliders(); // 초기 슬라이더 위치 설정
        $prevButton.style.pointerEvents = 'none';  // 초기 페이지에서 prev 버튼 비활성화
        $pSvgPath.style.stroke = '#818181';
    }

    // 슬라이드 위치 업데이트 함수
    function updateSliders() {
        const slideWidth = document.querySelector('.sale-game').offsetWidth; // 한 게임 카드의 너비
        const totalSlideWidth = slideWidth + gap; // 카드의 너비에 gap을 더함
        const slideOffset = currentIndex * totalSlideWidth;  // 슬라이드 이동 위치 계산

        // 슬라이드 이동을 부드럽게 처리하기 위해 transition을 설정합니다.
        $gameWrapper.style.transition = 'transform 0.3s ease';  // 슬라이드 애니메이션 적용

        // 슬라이드 이동
        $gameWrapper.style.transform = `translateX(-${slideOffset}px)`; // 슬라이드 이동

        // 이전 버튼 활성화 여부 설정
        if (currentIndex === 0) {
            $prevButton.style.pointerEvents = 'none';  // 첫 페이지일 때 prev 버튼 비활성화
            $pSvgPath.style.stroke = '#818181';  // 색상 변경
        } else {
            $prevButton.style.pointerEvents = 'auto';  // 첫 페이지가 아니면 활성화
            $pSvgPath.style.stroke = '#ffffff';  // 색상 변경
        }

        // 마지막 게임에 도달했을 때 next 버튼 비활성화
        if (currentIndex >= saleGames.length - visibleGames) {
            $nextButton.style.pointerEvents = 'none';  // next 버튼 클릭 비활성화
            $nSvgPath.style.stroke = '#818181';  // 색상 변경
        } else {
            $nextButton.style.pointerEvents = 'auto';  // next 버튼 클릭 활성화
            $nSvgPath.style.stroke = '#ffffff';  // 색상 변경
        }
    }

    // 화면 크기 변화에 따른 재설정
    window.addEventListener('resize', () => {
        updateSliders(); // 카드 너비가 변경될 수 있으므로 슬라이더 위치 업데이트
    });

    // 이전 버튼 클릭 이벤트
    $prevButton.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--; // 한 번에 하나씩 이동
            updateSliders();
        }
    });

    // 다음 버튼 클릭 이벤트
    $nextButton.addEventListener('click', () => {
        if (currentIndex < saleGames.length - visibleGames) {  // 게임이 남아있는 경우
            currentIndex++; // 한 번에 하나씩 이동
            updateSliders();
        }
    });

    // 초기화 실행
    initSliders();
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



