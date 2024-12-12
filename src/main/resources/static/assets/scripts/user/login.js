HTMLElement.prototype.hide = function () {      // 모든 html 요소에 hide라는 함수가 붙는다
    this.classList.remove('-visible');
    return this;
}

HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    return this;
}

HTMLElement.prototype.removeHide = function () {
    this.classList.remove('remove');
    return this;
}

HTMLElement.prototype.removeShow = function () {
    this.classList.add('remove');
    return this;
}

/**
 * @param{string} dataId
 * @returns {HTMLLabelElement}
 */
HTMLFormElement.prototype.findLabel = function (dataId) {
    return this.querySelector(`label.--obj-label[data-id="${dataId}"]`);
}

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
    const url = new URL(location.href);
    url.pathname = '/user/login';
    url.searchParams.set('email', $loginForm['email'].value);
    url.searchParams.set('password', $loginForm['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert("요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.")
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'failure') {
            alert('이메일 혹은 비밀번호가 올바르지 않습니다. 다시 한번 확인해 주세요.');
            return;
        } else if (response['result'] === 'failure_not_verified') {
            alert('해당 계정은 이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.');
            return;
        } else if(response['result'] === 'success') {
            location.href = '/';
        }
    };
    xhr.open('GET', url.toString());
    xhr.send();
}
//endregion