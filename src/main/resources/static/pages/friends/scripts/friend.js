//region 친구 첫번째 닉네임 background 색상 랜덤 변환
document.addEventListener('DOMContentLoaded', () => {
    // 어두운 색상 배열 (회색과 검정색 제외)
    const darkColors = [
        '#2c3e50',  // 어두운 파랑
        '#8e44ad',  // 어두운 보라
        '#e74c3c',  // 어두운 빨강
        '#27ae60',  // 어두운 초록
        '#2980b9',  // 어두운 파랑
        '#f39c12',  // 어두운 노랑
        '#d35400',  // 어두운 오렌지
        '#c0392b'   // 어두운 붉은 색
    ];

    // 닉네임 요소들 선택
    const nicknames = document.querySelectorAll('.first-nickname');
    const totalNicknames = nicknames.length;

    // 색상 배열을 랜덤하게 섞기 위한 함수 (Fisher-Yates Shuffle 알고리즘)
    const shuffleArray = (array) => {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
    };

    // 색상 배열을 섞음
    shuffleArray(darkColors);

    // 색상 배열을 중복되지 않게 순차적으로 할당
    nicknames.forEach((el, index) => {
        // 색상을 인덱스에 맞게 선택하되, 색상 배열을 반복 사용
        const randomColor = darkColors[index % darkColors.length];

        // 배경색과 테두리 색상 설정
        el.style.backgroundColor = randomColor;
        el.style.borderColor = randomColor;
    });
});
//endregion