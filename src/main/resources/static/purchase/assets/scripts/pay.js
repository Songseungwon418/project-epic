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
    const $payTitle =$mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-content-info > .pay-title');
    const $userEmailTag = document.getElementById('userEmail');
    const userEmail = $userEmailTag.value;
    // 구매 버튼
    const $payBtn = document.getElementById('payment-btn');

    $payBtn.onclick = payment; // 주문하기 버튼 누를 시

    // IMP.init("imp07816522");// IMPORT 설정
    // // 결재 진행
    // function payment() {
    //     let requestData;
    //     try{
    //         const uuid = crypto.randomUUID().substring(0,8);
    //         const merchantUid = `order-${uuid}`; // 주문번호 생성
    //         let name = $payTitle.textContent.substring(0, 10);
    //         name = name.length > 10 ? `${name}...` : name; // 주문명 생성
    //         const amount = +totalPriceSpan.getAttribute('data'); // 총 가격(숫자)
    //         requestData = {
    //             userEmail: userEmail,
    //             pg: "kakaopay.TC0ONETIME",
    //             merchant_uid: merchantUid, // 상점에서 생성한 고유 주문번호
    //             name: name,
    //             amount: amount,
    //             currency: "KRW",
    //         };
    //         console.log(requestData);
    //     }
    //     catch (e){
    //         alert('예약을 위해서는 로그인 해야합니다.');
    //         console.log(e);
    //         return;
    //     }
    //     // PORTONE 결제 요청
    //     IMP.request_pay(requestData, payment_response);
    // }


    function payment_response() {
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
        // xhr.open('POST', '/purchase/pay/confirm');
        // xhr.send(formData);
        $loadingSuccess.show();
    }
}
//endregion