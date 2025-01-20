const $forgotForm = document.getElementById("forgotForm");
const $loading = document.getElementById("loading");

$forgotForm.onsubmit = (e) => {
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
                title: "NOT_FOUND",
                text: "입력하신 이메일과 일치하는 계정 정보를 찾을 수 없습니다. 다시 입력해주세요.",
                icon: "warning"
            });
        } else if (response['result'] === 'success') {
            Swal.fire({
                icon: "success",
                title: "이메일을 확인해 주세요.",
                text: "입력하신 이메일로 비밀번호를 재설정할 수 있는 링크를 포함한 메일을 전송하였습니다."
            }).then(() => {
                location.href = '/user/'
            })
        }
    };
    xhr.open('POST', `/user/forgot-password?email=${$forgotForm['email'].value}`);
    xhr.send();
    $loading.style.display = 'flex';
}
