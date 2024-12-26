//region 게임상세페이지 서브이미지 클릭 시 메인이미지 변경
window.onload = function () {
    const $sliderWrapper = document.querySelector(".slider-wrapper");
    const $slides = document.querySelectorAll(".slider-wrapper li img");
    const $prevButton = document.querySelector(".prev");
    const $nextButton = document.querySelector(".next");
    const $subImages = document.querySelectorAll(".image-wrapper .image");
    const $mainImage = document.getElementById("mainImage");

    let currentIndex = 0;

    const updateSlider = () => {
        $sliderWrapper.style.transform = `translateX(-${currentIndex * 100}%)`;

        $subImages.forEach((image, index) => {
            if (index === currentIndex) {
                image.classList.add("-selected");
            } else {
                image.classList.remove("-selected");
            }
        });

        // $mainImage.src = $subImages[currentIndex].querySelector("img").src;
    };

    $prevButton.addEventListener("click", () => {
        currentIndex = (currentIndex - 1 + $slides.length) % $slides.length;
        updateSlider();
    });

    $nextButton.addEventListener("click", () => {
        currentIndex = (currentIndex + 1) % $slides.length;
        updateSlider();
    });

    $subImages.forEach((image, index) => {
        image.addEventListener("click", () => {
            currentIndex = index;
            updateSlider();
        });
    });

    // 초기 상태 설정
    updateSlider();
};

//endregion

//region 상세페이지 펼치기 접기
document.addEventListener("DOMContentLoaded", function () {
    const $toggleButton = document.querySelector(".toggle-button");
    const $buttonText = $toggleButton.querySelector(".button-text");
    const $descriptionWrapper = document.querySelector(".description-wrapper");

    $toggleButton.addEventListener("click", function () {
        const isExpanded = $descriptionWrapper.classList.toggle("expanded");

        // 버튼에 expanded 상태 추가/제거
        $toggleButton.classList.toggle("expanded", isExpanded);

        // 버튼 텍스트 변경
        $buttonText.textContent = isExpanded ? "접기" : "펼치기";
    });
});

//endregion

//region 가격 양식
document.addEventListener("DOMContentLoaded", function () {
    // 모든 .game-price 요소를 선택
    const $prices = document.querySelectorAll('.price');

    $prices.forEach(price => {
        const rawPrice = parseInt(price.textContent.trim(), 10);

        if (!isNaN(rawPrice)) {
            price.innerText = `￦ ${rawPrice.toLocaleString()}`;
        }
    });
});
//endregion

//region 위시리스트 추가
const $wishlistButton = document.querySelector('.wishlistAdd');

if ($wishlistButton) {
    $wishlistButton.onclick = () => {
        const url = new URL(location.href);
        const formData = new FormData();
        const userEmail = document.getElementById('userEmail').value;
        const gameIndex = url.searchParams.get('index');

        if (!userEmail) {
            alert('로그인 후 이용가능합니다. 로그인 페이지로 이동합니다.');
            window.location.href = '../user/';
            return;
        }
        formData.append('gameIndex', gameIndex);
        formData.append('userEmail', userEmail);

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
}
//endregion

//region 위시리스트 제거
const $wishlistDeleteButton = document.querySelector('.wishlistDelete');
const $wishlistIndex = document.getElementById('wishlistIndex');

if ($wishlistDeleteButton) {
    $wishlistDeleteButton.onclick = () => {

        const formData = new FormData();
        formData.append('index', $wishlistIndex.value);

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const response = JSON.parse(xhr.responseText);
                    if (response.result === 'failure') {
                        alert('위시리스트에서 제거에 실패하였습니다.');
                    } else if (response.result === 'failure_not_found') {
                        alert('위시리스트에서 이미 삭제되었거나 오류로 인해 제거에 실패하였습니다.');
                    } else if (response.result === 'success') {
                        alert('위시리스트에서 제거하였습니다.');
                        location.reload();
                    }
                } else {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                }
            }
        };
        xhr.open('PATCH', '../purchase/wishlist/delete');
        xhr.send(formData);
    };
}
//endregion

//region 장바구니 추가
const $cartAddButton = document.body.querySelector('.cartAdd');

if ($cartAddButton) {
    $cartAddButton.onclick = () => {
        const url = new URL(location.href);
        const formData = new FormData();
        const userEmail = document.getElementById('userEmail').value;
        const gameIndex = url.searchParams.get('index');

        if (!userEmail) {
            alert('로그인 후 이용가능합니다. 로그인 페이지로 이동합니다.');
            window.location.href = '../user/';
            return;
        }

        formData.append('gameIndex', gameIndex);
        formData.append('userEmail', userEmail);

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
                alert('장바구니 담기에 실패하였습니다.');
            } else if (response.result === 'failure_duplicate_cart') {
                alert('이미 장바구니에 있습니다.');
            } else if (response.result === 'failure_not_found') {
                alert('장바구니 담기에 실패하였습니다.');
            }else if(response.result === 'failure_age_limit'){
                alert('구매할 수 없는 나이의 게임이라서 장바구니 담기에 실패하였습니다.');
            } else if (response.result === 'success') {
                alert('장바구니에 추가되었습니다.');
                location.reload();
            }
        };
        xhr.open('POST', '../purchase/cart/add');
        xhr.send(formData);
    };
}


//endregion

//region 장바구니 제거
const $cartDeleteButton = document.querySelector('.cartDelete');
const $cartIndex = document.getElementById('cartIndex');

if ($cartDeleteButton) {
    $cartDeleteButton.onclick = () => {

        const formData = new FormData();
        formData.append('index', $cartIndex.value);

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const response = JSON.parse(xhr.responseText);
                    if (response.result === 'failure') {
                        alert('장바구니에서 제거에 실패하였습니다.');
                    } else if (response.result === 'failure_not_found') {
                        alert('장바구니에서 이미 삭제되었거나 오류로 인해 제거에 실패하였습니다.');
                    } else if (response.result === 'success') {
                        alert('장바구니에서 제거하였습니다.');
                        location.reload();
                    }
                } else {
                    alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                }
            }
        };
        xhr.open('DELETE', '../purchase/cart/delete');
        xhr.send(formData);
    }
}
//endregion

//region 결제 모달창(지금 구매버튼 누를 시) 관련
{
    // 버튼 클릭 시 모달 열기
    const $gameBuyButton = document.getElementById('buy-btn');
    const modal = document.getElementById('pay-modal');
    const iframe = document.getElementById('paymentIframe');

    if ($gameBuyButton !== null) {
        // 모달을 여는 함수
        $gameBuyButton.addEventListener('click', function () {
            // 현제 페이지에서 주소의 파라미터값들을 가져옴
            const urlParams = new URLSearchParams(window.location.search);
            // 가져온 파라미터값들 중 'index'라는 파라미터 값을 가져옴
            let gameIndex = urlParams.get('index');

            // 모달을 보이도록 설정
            iframe.src = `/purchase/pay?index=${gameIndex}`; //주소에 파라미터값을 설정하여 보냄
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        });

        // 모달 외부 클릭 시 닫기
        window.addEventListener('click', function (event) {
            if (event.target === modal) {
                iframe.src = "";
                modal.style.display = 'none';
                document.body.style.overflow = 'hidden auto';
            }
        });

        // 모달창 닫기 버튼 누를 시 (iframe으로부터 메시지를 받으면 모달 닫기)
        window.addEventListener('message', function (event) {
            // 이벤트를 받았을 때 'closeModal' 메시지를 받으면 모달을 닫음
            if (event.data === 'closeModal') {
                modal.style.display = 'none';
                document.body.style.overflow = 'hidden auto';
            }
        });
    }
}
//endregion




