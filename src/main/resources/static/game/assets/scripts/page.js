//region 게임상세페이지 서브이미지 클릭 시 메인이미지 변경
window.onload = function () {
    // 메인 슬라이더 요소 및 서브 이미지 요소 가져오기
    const $sliderWrapper = document.querySelector(".slider-wrapper");
    const $slides = document.querySelectorAll(".slider-wrapper li img");
    const $prevButton = document.querySelector(".prev");
    const $nextButton = document.querySelector(".next");
    const $subImages = document.querySelectorAll(".image-wrapper .image");
    const $mainImage = document.getElementById("mainImage");

    let currentIndex = 0;

    // 슬라이더 업데이트 함수
    const updateSlider = () => {
        // 슬라이더 이동
        $sliderWrapper.style.transform = `translateX(-${currentIndex * 100}%)`;

        // 서브 이미지 -selected 클래스 업데이트
        $subImages.forEach((image, index) => {
            if (index === currentIndex) {
                image.classList.add("-selected");
            } else {
                image.classList.remove("-selected");
            }
        });

        // 메인 이미지 src 업데이트
        $mainImage.src = $slides[currentIndex].src;
    };

    // 이전 버튼 이벤트
    $prevButton.addEventListener("click", () => {
        currentIndex = (currentIndex - 1 + $slides.length) % $slides.length;
        updateSlider();
    });

    // 다음 버튼 이벤트
    $nextButton.addEventListener("click", () => {
        currentIndex = (currentIndex + 1) % $slides.length;
        updateSlider();
    });

    // 서브 이미지 클릭 이벤트
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
