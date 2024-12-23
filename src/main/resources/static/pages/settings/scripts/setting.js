const $mainContainer = document.getElementById('mainContainer');
const $deleteDialog = document.getElementById('deleteDialog');

//region 계정 수정
$mainContainer.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', $mainContainer['email'].value);
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
            alert("오류가 발생하였습니다. 다시 시도해 주세요.");
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if (response['result'] === 'success') {
            alert("계정 수정에 성공하였습니다");
        }else if (response['result'] === 'failure') {
            alert("계정 수정에 실패하였습니다");
        }
    };
    xhr.open('PATCH', '/page/setting');
    xhr.send(formData);
}
//endregion

//region 계정 삭제
$deleteDialog.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData()
    formData.append('password', $deleteDialog['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert("오류가 발생하였습니다. 다시 시도해 주세요.");
            return;
        }
        const response = JSON.parse(xhr.responseText);
        if(response['result'] === 'user_not_found') {
            alert("입력하신 이메일에 해당하는 사용자를 찾을 수 없습니다. 이미 탈퇴했거나 가입하지 않았을 수 있습니다.");
        } else if(response['result'] === 'failure_duplicate_email') {
            alert("이메일이 등록된 이메일과 일치하지 않습니다. 다시 확인해 주세요.");
        } else if(response['result'] === 'failure_duplicate_password') {
            alert("입력하신 비밀번호가 맞지 않습니다. 올바른 비밀번호를 입력해 주세요.");
        } else if(response['result'] === 'success') {
            alert("회원 탈퇴가 성공적으로 완료되었습니다. 이용해 주셔서 감사합니다.");
            location.href = '/user/logout';
        }
    };
    xhr.open('DELETE', '/page/setting');
    xhr.send(formData);
}
//endregion

//region 계정 탈퇴 요청 클릭
const $deleteRequestBtn = document.getElementById('deleteRequestBtn');
const $overlay = document.querySelector('.overlay');
const $container = document.querySelector('.container');
const $closeDialogBtn = document.getElementById('closeDialogBtn');

// 계정 삭제 요청 버튼 클릭
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

// //region 옆 메뉴 동작
$navItems.forEach(($navItem) => {
    $navItem.onclick = () => {
        alert($navItem.innerText); // 잘되는지 확인용
        // const rel = $navItem.getAttribute('rel');
        // console.log(rel);
        //
        // const $mainContent = $mainContents.find((x) => x.getAttribute('rel') === rel);
        // $mainContent.classList.add('-visible');
        //
        // $navItems.forEach((x) => x.classList.remove('-selected'));
        // $navItem.classList.add('-selected');
    };
});
//endregion