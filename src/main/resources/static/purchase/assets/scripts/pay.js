const $mainContainer = document.getElementById('main-container');
const $loading = document.getElementById('loading');

// 실행 시 로딩화면 띄워주고 메인화면 숨김처리
$loading.classList.add('-visible');
$mainContainer.style.display = 'none';

// 3초 뒤에 로딩화면 숨김처리, 메인 화면 보여줌
setTimeout(() => {
    $loading.classList.remove('-visible');
    $mainContainer.style.display = 'flex';
}, 1500);

// 닫기 버튼 누를 시 아래 함수 실행
function attemptCancel() {
    // 부모 페이지로 'closeModal' 메시지 보내기
    window.parent.postMessage('closeModal', '*');
}

const $closeBtn = $mainContainer.querySelector(':scope > .payment-purchase-close');

$closeBtn.onclick = () => {
    attemptCancel();
};

//region 주문하기
{
    const $mainContainer = document.getElementById('main-container');
    const $summaries = $mainContainer.querySelector(':scope > .cart-summary');
    const $payBtn = document.getElementById('payment-btn');
    $payBtn.onclick = () => {

    };
}
//endregion