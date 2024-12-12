//region 필터관련
// 장르 목록 서버에서 가져오기
if(document.querySelector('.filter-list-content') != null) {
    const $genreList = document.querySelector('.filter-list-content');
    filterGenre($genreList);
}

// 필터 목록들 접고 펴기
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

//region 정렬 필터 관련
// 필터 항목을 클릭했을 때 선택된 항목을 처리하는 함수
function selectFilter(element) {
    const $filterListContainer = document.querySelector('.order-filter-container');
    const $filterItems = $filterListContainer.querySelectorAll(' :scope > .order-filter-list > .order-filter-item');
    const $sortingFilterBtn = document.querySelector('.sorting-filter-btn');

    // 모든 필터 항목에서 'selected' 클래스를 제거
    $filterItems.forEach(item => item.classList.remove('selected'));

    // 클릭된 항목에 'selected' 클래스를 추가
    element.classList.add('selected');

    // 'filter-text' 부분의 텍스트를 선택된 항목의 텍스트로 변경
    const $filterText = document.querySelector('.filter-text .text');
    $filterText.textContent = element.textContent;

    // 목록 닫기
    toggleFilterList($sortingFilterBtn);

    // 서버로 name 값 전송 (예시로 콘솔 출력)
    const filterName = element.getAttribute('name') || element.textContent;
    console.log('선택된 필터 name:', filterName);
}

// 필터 목록의 표시/숨김을 토글하는 함수
function toggleFilterList(btn) {
    const $filterListContainer = document.querySelector('.order-filter-container');

    // 해당 버튼에 대한 'aria-expanded' 속성 가져오기
    const isExpanded = btn.getAttribute('aria-expanded') === 'true';

    // aria-expanded 값 변경 (열리거나 닫히도록)
    btn.setAttribute('aria-expanded', !isExpanded);

    if($filterListContainer) {
        $filterListContainer.style.display = isExpanded ? 'none' : 'block';
    }
}
//endregion