document.addEventListener('DOMContentLoaded', function () {
    const licenseDiv = document.getElementById("license");
    const agreeSections = [
        document.getElementById("agree1"),
        document.getElementById("agree2"),
        document.getElementById("agree3"),
        document.getElementById("agree4")
    ];
    const registerContent = document.querySelector(".register-content");
    const cover = document.getElementById("cover");

    // "동의하지 않습니다" 버튼 클릭 시
    const disagreeButton = document.querySelector(".not-button");
    disagreeButton.addEventListener("click", function() {
        // 라이선스 화면 숨기기
        licenseDiv.classList.add("remove");

        // 등록 화면 보이기
        registerContent.classList.remove("remove");

        // 배경 커버 숨기기
        cover.style.opacity = 0;
        cover.style.pointerEvents = "none";
    });

    // 각 약관 체크박스 클릭 시
    $('.license input').click(function(e) {
        var idx = $(this).attr('name').replace('license', '');

        if ($(this).prop('checked')) {
            $('.agree' + idx).addClass('visible');
            $(this).prop('checked', false);
        } else {
            $(this).prop('checked', false);
        }
    });

    $('.agree button').click(function() {
        var idx = $(this).parents('.agree').attr('class').replace('agree agree', '').replace(' visible', '');
        $(this).parents('.agree').removeClass('visible');
        $(`input[name="license${idx}"]`).prop('checked', true);
    });

    // "동의합니다" 버튼 클릭 시
    const agreeButton = document.querySelector(".button");
    agreeButton.addEventListener("click", function() {
        // 모든 약관을 체크했는지 확인
        if (document.querySelector('input[name="license1"]').checked &&
            document.querySelector('input[name="license2"]').checked &&
            document.querySelector('input[name="license3"]').checked &&
            document.querySelector('input[name="license4"]').checked) {

            // 라이선스 화면 숨기기
            licenseDiv.classList.add("remove");

            // 등록 화면 보이기
            registerContent.classList.remove("remove");

            // 배경 커버 숨기기
            cover.style.opacity = 0;
            cover.style.pointerEvents = "none";
        } else {
            alert("모든 약관에 동의해야 합니다.");
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    // 모든 input 요소를 가져옵니다.
    const inputs = document.querySelectorAll(".input");

    inputs.forEach((input) => {
        // blur 이벤트: 포커스에서 벗어날 때
        input.addEventListener("blur", () => {
            const warning = input.closest(".label").querySelector(".-warning");

            // 유효성 검사: 입력 값이 비어 있는지 확인
            if (!input.value.trim()) {
                input.classList.add("invalid"); // 빨간색 border 추가
                warning.classList.add("active"); // 경고 메시지 표시
            }
        });

        // focus 이벤트: 입력 필드에 포커스될 때
        input.addEventListener("focus", () => {
            const warning = input.closest(".label").querySelector(".-warning");
            input.classList.remove("invalid"); // 빨간색 border 제거
            warning.classList.remove("active"); // 경고 메시지 숨김
        });
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const licenseButtons = document.querySelectorAll(".license button");
    const agreeSections = document.querySelectorAll(".agree");
    const backButtons = document.querySelectorAll(".back-button");

    licenseButtons.forEach((button, index) => {
        button.addEventListener("click", () => {
            // Hide all agree sections
            agreeSections.forEach(section => section.classList.remove("visible"));
            // Show the corresponding agree section
            document.querySelector(`.agree${index + 1}`).classList.add("visible");
        });
    });

    backButtons.forEach((backButton) => {
        backButton.addEventListener("click", (event) => {
            event.preventDefault();
            // Hide all agree sections
            agreeSections.forEach(section => section.classList.remove("visible"));
        });
    });
});