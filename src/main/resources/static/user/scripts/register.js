const $loading = document.getElementById("loading");

//region 약관 클릭, 이용약관에 동의하셔야합니다 눌렀을때 license 나오게 하는 방법
document.addEventListener('DOMContentLoaded', function () {
    const licenseDiv = document.getElementById("license");
    const registerContent = document.querySelector(".register-content");
    const cover = document.getElementById("cover");
    const licenseInputs = document.querySelectorAll('.license input[type="checkbox"]');
    const notAgreeButton = document.querySelector(".not-button");
    const agreeButton = document.querySelector(".button");
    const notAgreeMessage = document.querySelector(".not-agree");
    const registerButton = document.querySelector(".register-form .button"); // 회원가입 버튼

    // 초기 상태 설정: license가 보이면 cover 활성화, register-content 비활성화
    if (!licenseDiv.classList.contains("remove")) {
        cover.style.opacity = 1;
        cover.style.pointerEvents = "auto"; // 뒤쪽 클릭 차단
        registerContent.classList.add("disabled"); // 투명 처리
        registerButton.disabled = true; // 동의하지 않으면 회원가입 버튼 비활성화
    }

    // "이용약관을 동의하셔야 합니다" label 클릭 시
    const notAgreeLabel = document.querySelector('.register-form .not-agree'); // "이용약관을 동의하셔야 합니다" 문구가 있는 label 선택

    notAgreeLabel.addEventListener('click', function () {
        licenseDiv.classList.remove('remove'); // 라이선스 화면을 보이게 함
        registerContent.classList.add("remove"); // register-form 숨기기
        notAgreeMessage.classList.remove("remove"); // "이용약관을 동의하셔야 합니다" 문구 보이기
        cover.style.opacity = 1;
        cover.style.pointerEvents = "auto"; // 뒤쪽 클릭 차단
    });

    // "동의하지 않습니다" 버튼 클릭 시
    notAgreeButton.addEventListener("click", function () {
        // 라이선스 화면 숨기기
        licenseDiv.classList.add("remove");
        // 등록 화면 보이기
        registerContent.classList.remove("remove");
        // 배경 커버 숨기기
        cover.style.opacity = 0;
        cover.style.pointerEvents = "none";
        // '동의하지 않습니다' 문구 표시
        notAgreeMessage.classList.remove("remove");

        // register-form은 계속 활성화
        registerContent.classList.remove("disabled"); // disabled 클래스 제거하여 활성화
        registerButton.disabled = true; // 회원가입 버튼 비활성화
        registerButton.style.cursor = "not-allowed";
    });

    // "동의합니다" 버튼 클릭 시
    agreeButton.addEventListener("click", function () {
        // 모든 약관이 체크되었는지 확인
        const allChecked = [...licenseInputs].every(input => input.checked);

        if (allChecked) {
            // 라이선스 화면 숨기기
            licenseDiv.classList.add("remove");
            // 등록 화면 보이기
            registerContent.classList.remove("remove");
            // 배경 커버 숨기기
            cover.style.opacity = 0;
            cover.style.pointerEvents = "none";
            // '동의하지 않습니다' 문구 숨기기
            notAgreeMessage.classList.add("remove");
            registerButton.style.cursor = "pointer";

            // register-form 활성화
            registerContent.classList.remove("disabled"); // disabled 클래스 제거하여 활성화
            registerButton.disabled = false; // 회원가입 버튼 활성화
        } else {
            alert("모든 약관을 동의하셔야 합니다.");
        }
    });

    // 약관 체크박스 클릭 시 상세 내용 표시
    licenseInputs.forEach((checkbox, index) => {
        checkbox.addEventListener('click', function (e) {
            e.preventDefault(); // 기본 체크박스 동작 막기
            const agreePopup = document.querySelector(`.agree${index + 1}`);
            if (agreePopup) {
                agreePopup.classList.add('visible');
            }
        });
    });

    // 약관 팝업 내 확인 버튼 클릭 시
    const agreeButtons = document.querySelectorAll('.agree button');
    agreeButtons.forEach((button, index) => {
        button.addEventListener('click', () => {
            const agreePopup = document.querySelector(`.agree${index + 1}`);
            agreePopup.classList.remove('visible');
            document.querySelector(`input[name="license${index + 1}"]`).checked = true;
        });
    });

    // 약관 팝업 내 뒤로가기 버튼 처리
    const backButtons = document.querySelectorAll('.back-button');
    backButtons.forEach(backButton => {
        backButton.addEventListener('click', (event) => {
            event.preventDefault();
            const agreePopup = backButton.closest('.agree');
            agreePopup.classList.remove('visible');
        });
    });
});

//endregion

//region registerform에서 input을 누르고 뗏을때 필수 나타나게,
document.addEventListener('DOMContentLoaded', function () {
    const inputs = document.querySelectorAll('.register-form .input');

    // 입력 필드가 클릭될 때
    inputs.forEach(input => {
        input.addEventListener('focus', function () {
            const warning = input.closest('.label').querySelector('.-warning');
            input.classList.remove('invalid'); // 빨간 테두리 제거
            warning.classList.remove('active'); // 필수 문구 제거
        });
    });

    // 입력 필드를 벗어날 때 (blur 이벤트)
    inputs.forEach(input => {
        input.addEventListener('blur', function () {
            const warning = input.closest('.label').querySelector('.-warning');
            if (input.value.trim() === '') { // 값이 비어있으면
                input.classList.add('invalid'); // 빨간 테두리 추가
                warning.classList.add('active'); // 필수 문구 추가
            }
        });
    });
});
//endregion

const $registerForm = document.getElementById("registerForm");

$registerForm.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', $registerForm['email'].value);
    formData.append('name', $registerForm['name'].value);
    formData.append('nickname', $registerForm['nickname'].value);
    formData.append('birthdate', $registerForm['birthdate'].value);
    formData.append('postcode', $registerForm['postcode'].value);   // 우편번호
    formData.append('addr', $registerForm['addr'].value);   // 도로명 주소
    formData.append('detailAddress', $registerForm['detail_address'].value);    // 상세주소
    formData.append('phone', $registerForm['phone'].value);
    formData.append('password', $registerForm['password'].value);
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
        if (response['result'] === 'failure') {
            Swal.fire({
                title : "회원가입 도중 문제가 발생하였습니다.",
                text : "입력한 정보를 확인하고 다시 시도해 주세요.",
                icon : "warning"
            });
        } else if (response['result'] === 'failure_duplicate_email') {
            Swal.fire({
                title : "입력하신 이메일은 이미 사용중입니다.",
                text : "다른 이메일을 사용해주세요.",
                icon : "warning"
            });
        } else if(response['result'] === 'failure_not_email_format') {
            Swal.fire({
                title : "이메일 주소가 올바르지 않습니다.",
                text : "메일 형식은 'example@domain.com'와 같이 '@'와 '.'가 포함되어야 합니다.",
                icon : "warning"
            });
        } else if (response['result'] === 'failure_duplicate_phone') {
            Swal.fire({
                title : "입력하신 연락처는 이미 사용중입니다.",
                text : "다른 연락처를 사용해 주세요.",
                icon : "warning"
            });
        } else if (response['result'] === 'failure_duplicate_nickname') {
            Swal.fire({
                title : "입력하신 닉네임은 이미 사용중입니다.",
                text : "다른 닉네임을 사용해 주세요.",
                icon : "warning"
            });
        } else if(response['result'] === 'failure_invalid_date_format') {
            Swal.fire({
                title : "날짜를 다시 확인해 주세요.",
                text : "입력하신 날짜는 현재시간을 넘어간 시간입니다. 사용자의 생년월일을 작성해주세요.",
                icon : "warning"
            });
        } else if (response['result'] === 'success') {
            Swal.fire({
                icon: "success",
                title: "회원가입이 완료되었습니다.",
                text: "입력하신 이메일로 계정을 인증할 수 있는 링크를 전송하였습니다. 계정 인증 후 로그인 할 수 있으며, 해당 링크는 24시간 동안만 유효합니다."
            }).then(() => {
                location.href = '/user/';
            });
        }
    };
    xhr.open('POST', '/user/register');
    xhr.send(formData);
    $loading.style.display = 'flex'
}
