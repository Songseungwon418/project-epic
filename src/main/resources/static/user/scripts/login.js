const $loading = document.getElementById("loading");

//region login
const $loginForm = document.getElementById("loginForm");

$loginForm.onsubmit = (e) => {
    e.preventDefault()

    if($loginForm['email'].value.length <= 8 && $loginForm['email'].value.length >= 50) {
        alert("올바른 이메일을 입력해 주세요.")
        return;
    }
    if ($loginForm['password'].value.length <= 6 && $loginForm['password'].value.length >= 50) {
        alert("올바른 비밀번호르 입력해 주세요.")
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', $loginForm['email'].value);
    formData.append('password', $loginForm['password'].value);
    // const url = new URL(location.href);
    // url.pathname = '/user/login';
    // url.searchParams.set('email', $loginForm['email'].value);
    // url.searchParams.set('password', $loginForm['password'].value);
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
                title: "다시 확인해 주세요.",
                text : "이메일 혹은 비밀번호가 올바르지 않습니다. 다시 한번 확인해 주세요.",
                icon : "warning"
            });
        } else if (response['result'] === 'failure_not_verified') {
            Swal.fire({
                title: "다시 확인해 주세요.",
                text : "해당 계정은 이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.",
                icon : "warning"
            });
        } else if(response['result'] === 'failure_deleted') {
            Swal.fire({
                title: "이미 삭제된 아이디입니다.",
                text : "해당 이메일은 이미 삭제된 아이디입니다. 다른 아이디로 회원가입 후 로그인 해주세요.",
                icon : "warning"
            });
        } else if(response['result'] === 'success') {
            const previousUrl = document.referrer; // 이전 URL을 저장

            // 현재 호스트가 localhost:8080이 아닌 경우, 홈페이지로 리디렉션
            if (window.location.hostname !== 'localhost' || window.location.port !== '8080') {
                location.href = '/'; // 홈페이지로 리디렉션
            } else {
                // localhost:8080에서 접근한 경우, 이전 페이지로 리디렉션
                if (previousUrl && previousUrl.indexOf('forgot-password') && previousUrl.indexOf('recover-password') === -1) {
                    location.href = previousUrl; // 이전 페이지로 리디렉션
                } else {
                    location.href = '/'; // 이전 페이지 정보가 없으면 홈페이지로
                }
            }
        }
    };
    xhr.open('POST', '/user/');
    xhr.send(formData);
    $loading.style.display = 'flex'
}
//endregion