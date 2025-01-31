//region 친구 추가
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
                title: "친구 추가 실패",
                text: "친구 추가를 실패하였습니다. 다시 시도해 주세요.",
                icon: "warning"
            });
        }
        else if(response['result'] === 'friendship_exists') {
            Swal.fire("이미 친구가 되어있습니다.");
        } else if(response['result'] === 'user_not_found') {
            Swal.fire("친구의 정보를 찾을 수 없습니다.");
        } else if(response['result'] === 'success') {
            Swal.fire({
                icon: "success",
                title: "친구추가 성공!",
                text: "친구가 성공적으로 추가되었습니다."
            }).then(() => {
                location.reload();
            });
        }
    };
    xhr.open('POST', `/page/profile?email=${$friendAdd['friend_email'].value}`);
    xhr.send(formData);
}
//endregion
//region 필터 아래위로
function toggleArrow() {
    const filter = document.querySelector('.filter');
    const dropdown = document.getElementById('filter-dropdown');
    filter.classList.toggle('active');
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
}

//endregion

function applyFilter(_type) {
    document.querySelector('.filter-text').innerText = _type;
    document.querySelector('.filter-dropdown').style.display = 'none';

    const gameWrapper = document.querySelector('.gameWrap');

    // .game-wrapper 안의 모든 .game 요소를 배열로 변환
    const games = Array.from(gameWrapper.querySelectorAll('.game'));
    let sortedGames;

    // 정렬 조건
    switch (_type) {

        case '이름 순서': // 이름순
            sortedGames = games.sort((a, b) => {
                const nameA = a.querySelector('.game-name').innerText;
                const nameB = b.querySelector('.game-name').innerText;
                return nameA.localeCompare(nameB, 'ko');
            });
            break;

        case '업적 많은 순':
            sortedGames = games.sort((a, b) => {
                const priceA = Number(a.querySelector('.gauge-text').innerText.replace('%', ''));
                const priceB = Number(b.querySelector('.gauge-text').innerText.replace('%', ''));

                // '무료'를 마지막에 정렬하고, 나머지는 가격 내림차순으로 정렬
                if (priceA === 0 && priceB !== 0) return 1;
                if (priceB === 0 && priceA !== 0) return -1;
                return priceB - priceA;
            });
            break;


        case '업적 적은 순':
            sortedGames = games.sort((a, b) => {
                const priceA = Number(a.querySelector('.gauge-text').innerText.replace('%', ''));
                const priceB = Number(b.querySelector('.gauge-text').innerText.replace('%', ''));

                // '무료'를 우선 정렬하고, 나머지는 가격 오름차순으로 정렬
                if (priceA === 0 && priceB !== 0) return -1;
                if (priceB === 0 && priceA !== 0) return 1;
                return priceA - priceB;
            });
            break;


        default:
            sortedGames = games; // 정렬하지 않음
    }

    // 정렬된 게임을 DOM에 재배치
    sortedGames.forEach((game) => {
        gameWrapper.appendChild(game); // game-wrapper에 정렬된 순서대로 재추가
    });
}