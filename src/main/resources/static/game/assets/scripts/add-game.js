const $addGameform = document.getElementById('add-game-form');

//region 이미지 관련
// 이미지 미리보기 처리 함수
const handleImageChange = (inputElement, textElement, imageElement) => {
    // 파일이 없으면 텍스트를 표시하고 이미지를 숨김
    if ((inputElement.files?.length ?? 0) === 0) {
        textElement.style.display = 'flex';
        imageElement.style.display = 'none';
        imageElement.src = '';
        return;
    }
    // 이미지 첨부
    textElement.style.display = 'none';
    imageElement.style.display = 'block';
    // 첫 번째 파일을 임시 URL로 변환 후 이미지에 삽입 (미리보기)
    imageElement.src = URL.createObjectURL(inputElement.files[0]);
};

// 메인 이미지 관련
$addGameform['mainImage'].onchange = () => {
    const $mainText = $addGameform.querySelector(':scope > .mainImage > .row > .input-wrapper > ._text');
    const $mainImage = $addGameform.querySelector(':scope > .mainImage > .row > .input-wrapper > .image');
    handleImageChange($addGameform['mainImage'], $mainText, $mainImage);
}

// 메인 로고 관련
$addGameform['mainLogo'].onchange = () => {
    const $logoText = $addGameform.querySelector(':scope > .logo > .row > .input-wrapper > ._text');
    const $logoImage = $addGameform.querySelector(':scope > .logo > .row > .input-wrapper > .image');
    handleImageChange($addGameform['mainLogo'], $logoText, $logoImage);
}
//endregion