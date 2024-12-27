//region 필터 아래위로
function toggleArrow() {
    const filter = document.querySelector('.filter');
    const dropdown = document.getElementById('filter-dropdown');
    filter.classList.toggle('active');
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
}

function applyFilter(criteria) {
    const dropdown = document.getElementById('filter-dropdown');
    dropdown.style.display = 'none'; // 드롭다운 닫기
    const games = Array.from(document.querySelectorAll('.game'));

    // 정렬 로직
    if (criteria === 'name') {
        games.sort((a, b) => {
            const nameA = a.querySelector('.game-name').textContent.trim();
            const nameB = b.querySelector('.game-name').textContent.trim();
            return nameA.localeCompare(nameB); // 이름순 정렬
        });
    } else if (criteria === 'achievements-desc') {
        games.sort((a, b) => {
            const achievementsA = a.querySelectorAll('.achievement-image .image').length;
            const achievementsB = b.querySelectorAll('.achievement-image .image').length;
            return achievementsB - achievementsA; // 업적 많은순
        });
    } else if (criteria === 'achievements-asc') {
        games.sort((a, b) => {
            const achievementsA = a.querySelectorAll('.achievement-image .image').length;
            const achievementsB = b.querySelectorAll('.achievement-image .image').length;
            return achievementsA - achievementsB; // 업적 적은순
        });
    }

    // 정렬된 게임을 다시 렌더링
    const container = document.querySelector('.games-container'); // 부모 요소
    container.innerHTML = ''; // 기존 내용 제거
    games.forEach(game => container.appendChild(game)); // 정렬된 요소 추가
}

//endregion
