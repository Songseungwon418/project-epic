// 닫기 버튼 누를 시 아래 함수 실행
function attemptCancel() {
    // 부모 페이지로 'closeModal' 메시지 보내기
    window.parent.postMessage('closeModal', '*');

    //TODO 폼제출 및 통신 취소하는 구문도 추가
}

const $mian = document.body.querySelector(':scope > .pay-page > .pay-container');
const $closeBtn = $mian.querySelector(':scope > .main-container > .payment-purchase-close');

$closeBtn.onclick = () => {
    attemptCancel();
}