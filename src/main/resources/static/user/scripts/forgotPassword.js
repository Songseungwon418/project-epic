const $forgotForm = document.getElementById("forgotForm");

$forgotForm.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert("요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.")
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response['result'] === 'failure') {
            alert("입력하신 이메일과 일치하는 계정 정보를 찾을 수 없습니다. 다시 입력해주세요.")
            return;
        } else if (response['result'] === 'success') {
            alert("입력하신 이메일로 비밀번호를 재설정할 수 있는 링크를 포함한 메일을 전송하였습니다.")
            location.href = '/user/'
        }
    };
    xhr.open('POST', `/user/forgot-password?email=${$forgotForm['email'].value}`);
    xhr.send();
}
