const $recoverForm = document.getElementById("recoverForm");

$recoverForm.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('userEmail', $recoverForm['userEmail'].value);
    formData.append('key', $recoverForm['key'].value);
    formData.append('password', $recoverForm['password'].value);
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
        if(response['result'] === 'failure') {
            Swal.fire({
                title : "비밀번호를 재설정 할 수 없습니다.",
                text : "링크가 올바르지 않거나 링크가 손상되었을 수 있습니다. 동일한 문제가 반복된다면 비밀번호 재설정 링크를 다시 생성해 주세요.",
                icon : "warning"
            });
        } else if (response['result'] === 'failure_expired') {
            Swal.fire({
                title : "비밀번호를 재설정 할 수 없습니다.",
                text : "해당 링크는 더 이상 유효하지 않습니다.",
                icon : "warning"
            });
        } else if (response['result'] === 'success') {
            Swal.fire({
                icon : "success",
                title : "비밀번호를 성공적으로 작성하였습니다.",
                text : "확인버튼을 클릭하면 로그인페이지로 이동합니다."
            }).then(() => {
                location.href = '/user/logout';
                location.href = '/user/';
            })
        }
    };
    xhr.open('PATCH', '/user/recover-password');
    xhr.send(formData);
}