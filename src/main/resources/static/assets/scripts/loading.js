document.addEventListener("DOMContentLoaded", function () {
    // 로딩 화면 요소
    const $loading = document.getElementById("loading");

    // 페이지 로딩 시 로딩 화면 보이기
    window.addEventListener('beforeunload', function () {
        if ($loading) {
            $loading.style.display = 'flex';  // 페이지 이동 전 로딩 화면 표시
            // 스크롤 방지
            document.body.style.overflow = 'hidden';
        }
    });

    // 페이지 로딩 후 로딩 화면 숨기기
    window.addEventListener('load', function () {
        if ($loading) {
            $loading.style.display = 'none'; // 페이지 로딩 후 로딩 화면 숨기기
        }
    });
});
