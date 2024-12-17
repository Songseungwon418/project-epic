const $mainContainer = document.getElementById('main-container');
const $loading = document.getElementById('loading');
const $loadingSuccess = document.getElementById('loading-success');

//region 로딩화면 관련

// 로딩화면 보여주기
HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    $mainContainer.style.display = 'none';
    return this;
};

// 로딩화면 숨기기
HTMLElement.prototype.hide = function () {
    this.classList.remove('-visible');
    $mainContainer.style.display = 'flex';
    return this;
};

// 페이지 접속 시 자바스크립트를 불러오면서 로딩화면 띄워주고 메인화면 숨김처리
$loading.show();

// 3초 뒤에 로딩화면 숨김처리, 메인 화면 보여줌
setTimeout(() => {
   $loading.hide();
}, 1500);
//endregion

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
    // const $summaries = $mainContainer.querySelector(':scope > .payment-summaries');
    // const $paymentConfirmContainer = $summaries.querySelector(':scope > .payment-confirm-container');

    const $payBtn = document.getElementById('payment-btn');
    $payBtn.onclick = () => {
        const $userEmailTag = document.getElementById('userEmail');
        const userEmail = $userEmailTag.value;

        const formData = new FormData();
        formData.append('userEmail', userEmail);
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState !== XMLHttpRequest.DONE){
                return;
            }
            $loadingSuccess.hide();
            if (xhr.status < 200 || xhr.status >= 300){
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 시도해 주세요.');
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if (response['result'] === 'failure'){
                alert('구매에 실패하였습니다.');
                attemptCancel();
                window.parent.location.href = '/purchase/cart';
            }else if(response['result'] === 'failure_not_found'){
                alert('구매할 목록을 찾을 수 없습니다. 구매에 실패하였습니다.');
                attemptCancel();
                window.parent.location.href = '/purchase/cart';
            }else if(response['result'] === 'failure_age_limit'){
                alert('구매할 수 없는 나이의 게임이 포함되어있습니다. 구매에 실패하였습니다.');
                attemptCancel();
                window.parent.location.href = '/purchase/cart';
            }else if(response['result'] === 'success'){
                attemptCancel();
                window.parent.location.href = '/purchase/paysuccess';
            }else {
                alert('알수 없는 이유로 구매에 실패하였습니다.');
                window.parent.location.href = '/purchase/cart';
            }
        };
        xhr.open('POST', '/purchase/pay/confirm');
        xhr.send(formData);
        $loadingSuccess.show();
    };
}
//endregion