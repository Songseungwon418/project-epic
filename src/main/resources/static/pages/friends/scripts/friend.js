//region 친구 첫번째 닉네임 background 색상 랜덤 변환
document.addEventListener('DOMContentLoaded', () => {
    const colors = ['#c9372c', '#de911d', '#80ad26', '#caffbf', '#1fc4b7', '#1d378f', '#4f258f', '#bf2ab3'];

    document.querySelectorAll('.first-nickname').forEach(el => {
        const randomColor = colors[Math.floor(Math.random() * colors.length)];
        el.style.backgroundColor = randomColor;
    });
})
//endregion