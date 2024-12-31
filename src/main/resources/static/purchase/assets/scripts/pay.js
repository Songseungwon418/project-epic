const $mainContainer = document.getElementById('main-container');
const $loading = document.getElementById('loading');
const $loadingSuccess = document.getElementById('loading-success');

//region 라디오버튼 선택되었을 시 디자인 관련 - 사용X
// 모든 라디오 버튼을 가져옵니다.
// const $radioButtons = document.querySelectorAll('input[type="radio"][name="payment"]');
//
// // 각 라디오 버튼에 클릭 이벤트 리스너를 추가합니다.
// $radioButtons.forEach(radio => {
//     radio.addEventListener('change', function() {
//         const $parentLabel = this.closest('.payment-radio'); // 라디오 버튼의 부모인 .payment-radio 요소
//
//         // 모든 라디오 버튼의 부모에서 --active 클래스를 제거합니다.
//         document.querySelectorAll('.payment-radio').forEach(label => label.classList.remove('--active'));
//
//         // 라디오 버튼이 체크되면 해당 부모 요소에 --active 클래스 추가
//         if (this.checked) {
//             $parentLabel.classList.add('--active');
//         }
//     });
// });
//endregion


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

document.addEventListener("DOMContentLoaded", function () {
    // 페이지 로딩 시 로딩 화면 보이기
    window.addEventListener('beforeunload', function () {
        $loading.show();
        $mainContainer.style.display = 'none';
    });

    window.addEventListener('load', function () {
        $loading.hide();
        $mainContainer.style.display = 'flex';
    });
});
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

//region 결제 관련
{
    const $mainContainer = document.getElementById('main-container');
    const $payTitle =$mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-content > .pay-content-info > .pay-title');
    const $userEmail = $mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-content > .info-data > .user-email');
    const $totalPrice = $mainContainer.querySelector(':scope > .payment-summaries > .pay-summaries-container > .pay-order-prices > .payment-price > .payment-price-value');
    const $payBtn = document.getElementById('payment-btn');// 구매 버튼
    const uuid = crypto.randomUUID().substring(0,8);
    const merchantUid = `pid-${uuid}`; // 주문번호 생성
    let name = $payTitle.textContent.substring(0, 10);
    const amount = $totalPrice.textContent; // 총 가격(숫자)

    // 주문하기 버튼 누를 시
    $payBtn.onclick = () => {
        if (amount === '0' ) { //결제api X
            name = name.length > 10 ? `${name}...` : `${name}`; // 주문명 생성
            let date = new Date();
            const koreaTime = new Date(date.getTime() + (9 * 60 * 60 * 1000));
            const formattedDate = koreaTime.toISOString();
            const pay = {
                id: merchantUid,
                userEmail: userEmail,
                title: name,
                name: userName,
                amount: amount,
                paidAt: formattedDate,
                status: 'paid',
                currency: 'KRW',
            }

            // pay 객체를 JSON 문자열로 변환
            const payJson = JSON.stringify(pay);
            payment_xhrRequest(payJson);
        }
        else if (amount !== '0' ) {
            IMP.init(impNumber);// IMPORT 설정
            payment(); // 결제요청(결제api진행)
        }
    }

    // 결재 진행
    function payment() {
        let requestData;
        name = name.length > 10 ? `${name}...그 외` : `${name}그 외`; // 주문명 생성 -> 게임이 1개가 아닐 시 설정하게 조건 추가 필요
        try{
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
            alert('결제에 실패하였습니다.');
            console.log(e);
            return;
        }
        // PORTONE 결제 요청
        IMP.request_pay(requestData, payment_response);
    }

    // 결제가 완료되었을 시 실행되는 함수
    function payment_response(portOneResponse) {
        console.log(portOneResponse);

        //Unix timestamp를 밀리초 단위로 변환 (1000을 곱함)
        const date = new Date(portOneResponse['paid_at'] * 1000);
        // 한국 시간(KST)은 UTC보다 9시간 빠르므로 9시간을 더해줌
        const koreaTime = new Date(date.getTime() + (9 * 60 * 60 * 1000));
        // 날짜 포맷을 한국 시간으로 설정
        const formattedDate = koreaTime.toISOString();

        const pay = {
            id: portOneResponse['merchant_uid'],
            userEmail: portOneResponse['buyer_email'],
            impUid: portOneResponse['imp_uid'],
            title: portOneResponse['name'],
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
        // DB 삽입 요청 진행
       payment_xhrRequest(payJson)
    }

    // 결제 내역 및 구매 내역 DB 삽입 요청
    function payment_xhrRequest(pay) {
        // 로그인 한 유저와 장바구니안에 등록된 유저가 일치한 지 보기위함
        const userEmail = $userEmail.textContent;

        // 현제 페이지에서 주소의 파라미터값들을 가져옴
        const urlParams = new URLSearchParams(window.location.search);
        // 가져온 파라미터값들 중 'index'라는 파라미터 값을 가져옴
        let gameIndex = urlParams.get('index');

        const formData = new FormData();
        formData.append('userEmail', userEmail);
        formData.append('pay', pay);
        if (gameIndex != null || gameIndex > 0) {
            formData.append('gameIndex', gameIndex);
        }

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
                attemptCancel(); // 결제창 닫기
            }else if(response['result'] === 'failure_not_found'){
                alert('구매할 목록을 찾을 수 없습니다. 구매에 실패하였습니다.');
                attemptCancel(); // 결제창 닫기
            } else if (response['result'] === 'failure_duplicate_purchase'){
                alert('이미 구매한 게임이 포함되어있습니다. 구매에 실패하였습니다.');
                attemptCancel(); // 결제창 닫기
            } else if (response['result'] === 'failure_age_limit') {
                alert('구매할 수 없는 나이의 게임이 있습니다. 구매에 실패하였습니다.');
                attemptCancel(); // 결제창 닫기
            } else if(response['result'] === 'success'){
                return window.parent.location.href = `/purchase/paysuccess?id=${merchantUid}`; //결과가 true이면 성공페이지로 이동
            }else {
                alert('알수 없는 이유로 구매에 실패하였습니다.');
                attemptCancel(); // 결제창 닫기
            }
        };
        xhr.open('POST', '/purchase/pay/confirm');
        xhr.send(formData);
        $loadingSuccess.show();
    }

}
//endregion