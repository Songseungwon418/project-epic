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
    const $payTitle =$mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-content > .pay-content-info > .pay-title');
    const $userEmail = $mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-content > .info-data > .user-email');
    const $totalPrice = $mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-order-prices > .payment-price > .payment-price-value');
    const $payBtn = document.getElementById('payment-btn');// 구매 버튼

    $payBtn.onclick = payment; // 주문하기 버튼 누를 시

    IMP.init(impNumber);// IMPORT 설정
    // 결재 진행
    function payment() {
        let requestData;
        try{
            const uuid = crypto.randomUUID().substring(0,8);
            const merchantUid = `pid-${uuid}`; // 주문번호 생성
            let name = $payTitle.textContent.substring(0, 10);
            name = name.length > 10 ? `${name}... 그 외` : `${name} 그 외`; // 주문명 생성
            const amount = $totalPrice.textContent; // 총 가격(숫자)
            requestData = {
                ...user,
                pg: "kakaopay.TC0ONETIME",
                merchant_uid: merchantUid, // 상점에서 생성한 고유 주문번호
                name: name,
                amount: amount,
                currency: "KRW",
            };
            console.log(requestData);
        }
        catch (e){
            alert('구매를 위해서는 로그인이 필요합니다.');
            console.log(e);
            return;
        }
        // PORTONE 결제 요청
        IMP.request_pay(requestData, payment_response);
    }

    function payment_response(portOneResponse) {
        console.log(portOneResponse);
        // Unix timestamp를 밀리초 단위로 변환 (1000을 곱함)
        const date = new Date(portOneResponse['paid_at'] * 1000);
        // 날짜 포맷 설정
        const formattedDate = date.toISOString();

        const pay = {
            id: portOneResponse['merchant_uid'],
            userEmail: portOneResponse['buyer_email'],
            impUid: portOneResponse['imp_uid'],
            name: portOneResponse['buyer_name'],
            amount: portOneResponse['paid_amount'],
            method: portOneResponse['pay_method'],
            paidAt: formattedDate,
            pgProvider: portOneResponse['pg_provider'],
            status: portOneResponse['status'],
            currency: portOneResponse['currency'],
            cardName: portOneResponse['card_name'],
            cardNumber: portOneResponse['card_number'],
            pgTid: portOneResponse['pg_tid'],
        }

        // pay 객체를 JSON 문자열로 변환
        const payJson = JSON.stringify(pay);

        // 로그인 한 유저와 장바구니안에 등록된 유저가 일치한 지 보기위함
        const userEmail = $userEmail.textContent;


        const formData = new FormData();
        formData.append('userEmail', userEmail);
        formData.append('pay', payJson);
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
            }else if (response['result'] === 'failure_duplicate_purchase'){
                alert('이미 구매한 게임이 포함되어있습니다. 구매에 실패하였습니다.');
                attemptCancel();
                window.parent.location.href = '/purchase/cart';
            } else if(response['result'] === 'success'){
                attemptCancel();
                window.parent.location.href = `/purchase/paysuccess?id=${portOneResponse['merchant_uid']}`;
            }else {
                alert('알수 없는 이유로 구매에 실패하였습니다.');
                window.parent.location.href = '/purchase/cart';
            }
        };
        xhr.open('POST', '/purchase/pay/confirm');
        xhr.send(formData);
        $loadingSuccess.show();
    }
}
//endregion