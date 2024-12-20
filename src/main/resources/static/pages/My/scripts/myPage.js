//region 필터 아래위로
function toggleArrow() {
    const filter = document.querySelector('.filter');
    filter.classList.toggle('active'); // active 클래스 추가/제거
}

// 새로고침 시 초기 상태 설정
document.addEventListener('DOMContentLoaded', () => {
    const filter = document.querySelector('.filter');
    filter.classList.remove('active'); // active 클래스 제거
});
//endregion