// 버튼 클릭 시 모달 열기
const openModalBtn = document.getElementById('openModalBtn');
const modal = document.getElementById('cart-pay-modal');
const iframe = document.getElementById('paymentIframe');

// 모달을 여는 함수
openModalBtn.addEventListener('click', function() {
    iframe.src = '/purchase/pay';
    // 모달을 보이도록 설정
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
});

// 모달 외부 클릭 시 닫기
window.addEventListener('click', function(event) {
    if (event.target === modal) {
        iframe.src = "";
        modal.style.display = 'none';
        document.body.style.overflow = 'hidden auto';
    }
});

// 모달창 닫기 버튼 누를 시 (iframe으로부터 메시지를 받으면 모달 닫기)
window.addEventListener('message', function(event) {
    // 이벤트를 받았을 때 'closeModal' 메시지를 받으면 모달을 닫음
    if (event.data === 'closeModal') {
        modal.style.display = 'none';
        document.body.style.overflow = 'hidden auto';
    }
});
