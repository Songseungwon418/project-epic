//region 필터관련
document.addEventListener('DOMContentLoaded', function() {
    const $buttons = document.querySelectorAll('.filter-toggle-button');

    $buttons.forEach(button => {
        button.addEventListener('click', () => {
            // 해당 버튼에 대한 'aria-expanded' 속성 가져오기
            const isExpanded = button.getAttribute('aria-expanded') === 'true';

            // aria-expanded 값 변경 (열리거나 닫히도록)
            button.setAttribute('aria-expanded', !isExpanded);

            // 해당 버튼의 부모 요소인 .filter-list-title의 다음 형제 요소로 .filter-list-content를 찾기
            const $filterListTitle = button.closest('.filter-list-title'); // 버튼이 속한 .filter-list-title 찾기
            const $filterContent = $filterListTitle.nextElementSibling; // 그 아래의 형제 .filter-list-content 찾기

            // filter-list-content 표시/숨기기
            if ($filterContent && $filterContent.classList.contains('filter-list-content')) {
                $filterContent.style.display = isExpanded ? 'none' : 'block'; // 접거나 펼침
            }
        });
    });
});

// 필터 목록에서 목록 클릭시
const $checkboxs = document.querySelectorAll('.checkbox');

$checkboxs.forEach((checkbox) => {
    checkbox.addEventListener('click', () => {
        const isChecked = checkbox.getAttribute('aria-checked') === 'true';

        checkbox.setAttribute('aria-checked', !isChecked);

        checkbox.style.backgroundColor = !isChecked ? '#918D8D38' : 'inherit';

        const $checkIcon = checkbox.querySelector('.check-icon');
        $checkIcon.style.display = !isChecked ? 'block' : 'none';
    });
});

//endregion