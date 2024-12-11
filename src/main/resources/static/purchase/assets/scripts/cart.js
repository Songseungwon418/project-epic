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

//region 제거 버튼 누를 시
const $removeBtns = document.querySelectorAll(' .game-card-btn.-remove');


$removeBtns.forEach(btn => btn.addEventListener('click', () => {
    const $gameCard = btn.closest('.game-card');
    const $cartIndexTag = $gameCard.querySelector(':scope > .info-data > .cart-index');
    const $gameIndexTag = $gameCard.querySelector(':scope > .info-data > .game-index');

    const cartIndex = $cartIndexTag.innerText;
    const gameIndex = $gameIndexTag.innerText;

    const formData = new FormData();
    formData.append('index', cartIndex);
    formData.append('gameIndex', gameIndex);

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if(xhr.readyState !== XMLHttpRequest.DONE){
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300){
            //요청 실패 로직 구현
            return;
        }
        //요청 성공 로직 구현
        const response = JSON.parse(xhr.responseText);
        console.log(response);
    };
    xhr.open('DELETE', '/purchase/cart/delete');
    xhr.send(formData);
    })
);
//endregion
