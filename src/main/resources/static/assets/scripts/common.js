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

$pageup.onclick = () => {
    window.scrollTo(0, 0);
}
//endregion

