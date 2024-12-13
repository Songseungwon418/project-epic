window.onload = function () {
    // 페이지 로딩 시 첫 번째 이미지를 기본으로 설정
    const images = document.querySelectorAll('.image-wrapper .image');
    const mainImage = document.getElementById('mainImage');

    // 첫 번째 이미지를 main-image에 표시
    const firstImage = images[0].querySelector('img');
    mainImage.src = firstImage.src;

    // 첫 번째 이미지에 -selected 클래스 추가
    images[0].classList.add('-selected');

    // 이미지 클릭 이벤트 추가
    images.forEach((image, index) => {
        image.addEventListener('click', function () {
            // 모든 이미지에서 -selected 클래스 제거
            images.forEach(img => img.classList.remove('-selected'));

            // 클릭된 이미지에 -selected 클래스 추가
            image.classList.add('-selected');

            // 클릭된 이미지의 src를 main-image로 설정
            const clickedImage = image.querySelector('img');
            mainImage.src = clickedImage.src;
        });
    });
};

