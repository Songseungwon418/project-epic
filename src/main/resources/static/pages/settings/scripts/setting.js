const $mainContainer = document.getElementById('mainContainer');
const $deleteDialog = document.getElementById('deleteDialog');
const $recoverPassword = document.getElementById('recoverPassword');
const $loading = document.getElementById("loading");

//region 계정 수정
$mainContainer.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', $mainContainer['name'].value);
    formData.append('nickname', $mainContainer['nickname'].value);
    formData.append('birthdate', $mainContainer['birthdate'].value);
    formData.append('addr', $mainContainer['addr'].value);
    formData.append('phone', $mainContainer['phone'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            Swal.fire({
                title: "서버가 알 수 없는 응답을 반환하였습니다.",
                text: "잠시 후 시도해 주세요.",
                icon: "warning"
            });
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'success') {
            Swal.fire({
                icon: "success",
                title: "성공",
                text: "계정 수정에 성공하였습니다."
            }).then(() => {
                location.reload();
            });
        }else if (response['result'] === 'failure') {
            Swal.fire({
                title: "실패하였습니다.",
                text: "계정수정에 실패하였습니다. 다시 한번 확인해 주세요.",
                icon: "warning"
            });
        } else if (response['result'] === 'failure_duplicate_phone') {
            Swal.fire({
                title: "연락처가 이미 사용중 입니다.",
                text: "입력하신 연락처는 이미 사용중입니다. 다른 연락처를 사용해 주세요.",
                icon: "warning"
            });
        } else if (response['result'] === 'failure_duplicate_nickname') {
            Swal.fire({
                title: "닉네임이 이미 사용중 입니다.",
                text: "입력하신 닉네임은 이미 사용중입니다. 다른 닉네임을 사용해 주세요.",
                icon: "warning"
            });
        } else if(response['result'] === 'failure_invalid_date_format') {
            Swal.fire({
                title: "날짜를 다시 확인해 주세요.",
                text: "입력하신 날짜는 현재시간을 넘어간 시간입니다. 사용자의 생년월일을 입력해주세요.",
                icon: "warning"
            });
        }
    };
    xhr.open('PATCH', '/page/setting');
    xhr.send(formData);
}
//endregion

//region setting에서 비밀번호 재설정 버튼 클릭시 post
$recoverPassword.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        $loading.style.display = 'none';
        if (xhr.status < 200 || xhr.status >= 300) {
            Swal.fire({
                title: "서버가 알 수 없는 응답을 반환하였습니다.",
                text: "잠시 후 시도해 주세요.",
                icon: "warning"
            });
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response['result'] === 'failure') {
            Swal.fire({
                title: "찾을 수 없습니다.",
                text: "입력하신 이메일과 일치하는 계정 정보를 찾을 수 없습니다. 다시 입력해주세요.",
                icon: "warning"
            });
        } else if (response['result'] === 'success') {
            Swal.fire({
                icon: "success",
                title: "이메일을 확인해 주세요.",
                text: "이메일로 비밀번호를 재설정할 수 있는 링크를 포함한 메일을 전송하였습니다."
            }).then(() => {
                location.href = '/page/setting'
            });
        }
    };
    xhr.open('POST', `/page/setting`);
    xhr.send();
    $loading.style.display = 'flex'
}
//endregion

//region 계정 삭제
$deleteDialog.onsubmit = (e) => {
    e.preventDefault();

    if($deleteDialog['password'].value === '') {
        alert('닫기 또는 비밀번호를 입력해 주세요.')
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData()
    formData.append('password', $deleteDialog['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        $loading.style.display = 'none';
        if (xhr.status < 200 || xhr.status >= 300) {
            Swal.fire({
                title: "서버가 알 수 없는 응답을 반환하였습니다.",
                text: "잠시 후 시도해 주세요.",
                icon: "warning"
            });
            return;
        }
        const response = JSON.parse(xhr.responseText);
        console.log(response['result']);
        if(response['result'] === 'user_not_found') {
            Swal.fire({
                title: "찾을 수 없습니다.",
                text: "입력하신 이메일과 일치하는 계정 정보를 찾을 수 없습니다. 다시 확인해 주세요.",
                icon: "warning"
            });
        } else if(response['result'] === 'failure_duplicate_email') {
            Swal.fire({
                title: "찾을 수 없습니다.",
                text: "이메일이 등록된 이메일과 일치하지 않습니다. 다시 확인해 주세요.",
                icon: "warning"
            });
        } else if(response['result'] === 'failure_duplicate_password') {
            Swal.fire({
                title: "비밀번호가 맞지 않습니다.",
                text: "입력하신 비밀번호가 맞지 않습니다. 올바른 비밀번호를 입력해 주세요.",
                icon: "warning"
            });
        } else if(response['result'] === 'success') {
            Swal.fire({
                title: "탈퇴 성공",
                text: "회원 탈퇴가 성공적으로 완료되었습니다. 이용해 주셔서 감사합니다.",
                icon: "success"
            }).then(() => {
                location.href = '/user/logout';
            });
        }
    };
    xhr.open('DELETE', '/page/setting');
    xhr.send(formData);
    $loading.style.display = 'flex'
}
//endregion

//region 계정 탈퇴 요청 클릭
const $deleteRequestBtn = document.getElementById('deleteRequestBtn');
const $overlay = document.querySelector('.overlay');
const $container = document.querySelector('.container');
const $closeDialogBtn = document.getElementById('closeDialogBtn');

//계정 탈퇴 버튼 눌렀을 시
$deleteRequestBtn.addEventListener('click', () => {
    // dialog와 overlay 보이게 하기
    $deleteDialog.classList.add('show');
    $overlay.classList.add('show');

    // container 투명하게 만들기 (배경을 클릭 못하게)
    $container.classList.add('transparent');
});

// 닫기 버튼 클릭 시 dialog 닫기 및 배경 복원
$closeDialogBtn.addEventListener('click', () => {
    $deleteDialog.classList.remove('show');
    $overlay.classList.remove('show');

    // container 투명도 제거
    $container.classList.remove('transparent');
});

// 오버레이 클릭 시 dialog 닫기 및 배경 복원
$overlay.addEventListener('click', () => {
    $deleteDialog.classList.remove('show');
    $overlay.classList.remove('show');

    // container 투명도 제거
    $container.classList.remove('transparent');
});
//endregion

const $nav = document.getElementById('nav');
const $navItems = Array.from($nav.querySelectorAll(':scope > .menu > .item[rel]'));
const $main = document.getElementById('main');
const $mainContents = Array.from($main.querySelectorAll(':scope > .content[rel]'));

//region 옆 메뉴 동작
$navItems.forEach(($navItem) => {
    $navItem.onclick = () => {
        // alert($navItem.innerText); // 잘되는지 확인용
        const rel = $navItem.getAttribute('rel');

        const $mainContent = $mainContents.find((x) => x.getAttribute('rel') === rel);

        $mainContents.forEach(($mainContent) => $mainContent.classList.remove('-visible')); // 전체 -visible 클래스 제거 -> 초기화

        $mainContent.classList.add('-visible'); // 선택한 메뉴에 -visible 클래스 추가

        $navItems.forEach((x) => x.classList.remove('-selected')); // 초기화
        $navItem.classList.add('-selected'); // 선택된 네비메뉴에 -selected 추가

        // 선택된 항목을 localStorage에 저장
        localStorage.setItem('selectedRel', rel);
    };
});
//endregion

//region 페이지 새로고침 시 선택한 페이지 새로고침
document.addEventListener('DOMContentLoaded', () => {
    // 페이지 새로고침 여부를 체크
    const navigationType = performance.getEntriesByType("navigation")[0]?.type;
    if (navigationType === "reload") {
        // 저장된 선택 항목 복원
        const selectedRel = localStorage.getItem('selectedRel');

        // 화면 초기화 (-visible 클래스 제거)
        $mainContents.forEach(($mainContent) => $mainContent.classList.remove('-visible'));
        // 네비 선택 초기화
        $navItems.forEach((x) => x.classList.remove('-selected'));

        // localStorage에 값이 있으면 저장된 항목을 복원
        if (selectedRel) {
            const $selectedContent = Array.from($mainContents).find((x) => x.getAttribute('rel') === selectedRel);
            if ($selectedContent) {
                $selectedContent.classList.add('-visible');
            }
            // 선택된 네비게이션 항목에 -selected 클래스 추가
            const $selectedNavItem = Array.from($navItems).find((x) => x.getAttribute('rel') === selectedRel);
            if ($selectedNavItem) {
                $selectedNavItem.classList.add('-selected');
            }
        }
    }
});
//endregion

//region 환불관련
{
    // 로딩 화면 요소
    const $loading = document.getElementById("loading");
    const $purchaseTable = document.getElementById('purchase-table');

    if ($purchaseTable != null ) {
        const $RefundRequestBtns = $purchaseTable.querySelectorAll('.refund-btn');
        const $refundDialog = document.getElementById('refund-dialog');
        const $closeRefundDialogBtn = document.getElementById('refund-cancel-btn');
        const $payId = document.getElementById('payId');
        const $gameIndex = document.getElementById('gameIndex');
        const $gameName =  document.querySelector('input[name="gameName"]');
        const $price = document.querySelector(`input[name="price"]`);

        // 전체 환불 혹은 환불 누를 시
        $RefundRequestBtns.forEach(btn => btn.addEventListener('click', (e) => {
                const row = e.target.closest('tr'); // 클릭한 버튼이 속한 행

                // payId 가지고 옴
                const payIdRow = row.parentNode.querySelector(':scope > .purchase-id');
                const payIdData = payIdRow ? payIdRow.querySelector('.content').innerText : "";
                const payId = payIdData.replace("주문Id: ", "").trim();
                if (btn.dataset.id === 'all') {
                    // 폼에 값 채우기
                    $payId.innerText = payId;
                    $gameIndex.innerText = "";
                    $gameName.value = row.querySelector('.title.content').innerText;
                    $price.value = row.querySelector('.price.price-info').innerText;
                } else {
                    // '환불' 버튼 클릭 시, 해당 행의 게임 정보 추출
                    const gameIndex = row.querySelector('.gameIndex').innerText;
                    const gameName = row.querySelector('.line-bottom').innerText;
                    const price = row.querySelector('.price.-current').innerText;

                    // 폼에 값 채우기
                    $payId.innerText = payId;
                    $gameIndex.innerText = gameIndex;
                    $gameName.value = gameName;
                    $price.value = price;
                }

                // dialog와 overlay 보이게 하기
                $refundDialog.classList.add('-visible');
                $overlay.classList.add('show');
            })
        );

        // 닫기 버튼 클릭 시 dialog 닫기 및 배경 복원
        $closeRefundDialogBtn.addEventListener('click', () => {
            $refundDialog.reset(); // 모든 입력 값 초기화
            $refundDialog.classList.remove('-visible');
            $overlay.classList.remove('show');
        });

        // 오버레이 클릭 시 dialog 닫기 및 배경 복원
        $overlay.addEventListener('click', () => {
            $refundDialog.classList.remove('-visible');
            $overlay.classList.remove('show');
        });

        // 모달창에서 확인버튼을 누를 시
        $refundDialog.onsubmit = (e) => {
            e.preventDefault();
            const payId = $payId.textContent;
            const gameIndex = $gameIndex.textContent;
            const formData = new FormData();
            formData.append('payId', payId);
            formData.append('gameIndex', gameIndex);

            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = () => {
                if(xhr.readyState !== XMLHttpRequest.DONE){
                    return;
                }
                $loading.style.display = 'none';  // 로딩 화면 숨기기
                if (xhr.status < 200 || xhr.status >= 300){
                    Swal.fire({
                        title: "서버가 알 수 없는 응답을 반환하였습니다.",
                        text: "잠시 후 시도해 주세요.",
                        icon: "warning"
                    });
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                if (response['result'] === 'failure' || response['result'] === 'failure_unsigned'){
                    Swal.fire({
                        title: "실패",
                        text: "환불에 실패하였습니다.",
                        icon: "error"
                    }).then(() => {
                        // 모달창 닫기
                        $closeRefundDialogBtn.click();
                    });
                }else if (response['result'] === 'failure_duplicate_refund'){
                    Swal.fire({
                        title: "실패",
                        text: "이미 환불한 내역입니다. 환불에 실패하였습니다.",
                        icon: "error"
                    }).then(() => {
                        // 모달창 닫기
                        $closeRefundDialogBtn.click();
                    });
                } else if (response['result'] === 'failure_date_passed') {
                    Swal.fire({
                        title: "실패",
                        text: "환불할 수 있는 기간이 지났습니다. 환불에 실패하였습니다.",
                        icon: "error"
                    }).then(() => {
                        // 모달창 닫기
                        $closeRefundDialogBtn.click();
                    });
                } else if(response['result'] === 'success'){
                    $closeRefundDialogBtn.click();// 모달창 닫기
                    Swal.fire({
                        icon: "success",
                        title: "성공",
                        text: "환불 성공하였습니다."
                    }).then(() => {
                        document.body.style.cursor = 'not-allowed'; // 마우스 클릭 금지표시
                        return window.parent.location.href = '/page/setting?showPurchaseList=true'; // 결제내역 재접속
                    });
                }else {
                    Swal.fire({
                        title: "실패",
                        text: "알수 없는 이유로 환불에 실패하였습니다.",
                        icon: "error"
                    }).then(() => {
                        // 모달창 닫기
                        $closeRefundDialogBtn.click();
                    });
                }
            };
            xhr.open('PATCH', '/purchase/patch');
            xhr.send(formData);
            $loading.style.display = 'flex';  // 페이지 이동 전 로딩 화면 표시
        };
    }
}
//endregion