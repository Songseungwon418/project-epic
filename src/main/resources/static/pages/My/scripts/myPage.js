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

const $friendAdd = document.getElementById('friendAdd');

$friendAdd.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('user_email', $friendAdd['user_email'].value);
    formData.append('friend_email', $friendAdd['friend_email'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert("요청을 보내던중 오류가 발생하였습니다.");
            return;
        }

        const response = JSON.parse(xhr.responseText);
        if(response['result'] === 'failure') {
            alert('친구 추가를 실패하였습니다. 다시 시도해 주세요.')
        }
        else if(response['result'] === 'friendship_exists') {
            alert('이미 친구가 되어 있습니다.')
        } else if(response['result'] === 'user_not_found') {
            alert('사용자를 찾을 수 없습니다.')}
        else if(response['result'] === 'success') {
            alert('친구가 추가되었습니다.')
            location.reload();
        }
    };
    xhr.open('POST', `/page/profile?email=${$friendAdd['friend_email'].value}`);
    xhr.send(formData);
}
