const $addGameform = document.getElementById('add-game-form');

//region 이미지 관련
// 이미지 미리보기 처리 함수
const handleImageChange = (inputElement, textElement, imageElement) => {
    // 파일이 없으면 텍스트를 표시하고 이미지를 숨김
    if ((inputElement.files?.length ?? 0) === 0) {
        textElement.style.display = 'flex';
        imageElement.style.display = 'none';
        imageElement.src = '';
        return;
    }
    // 이미지 첨부
    textElement.style.display = 'none';
    imageElement.style.display = 'block';
    // 첫 번째 파일을 임시 URL로 변환 후 이미지에 삽입 (미리보기)
    imageElement.src = URL.createObjectURL(inputElement.files[0]);
};

// 메인 이미지 관련
$addGameform['image'].onchange = () => {
    const $mainText = $addGameform.querySelector(':scope > .mainImage > .row > .image-wrapper > ._text');
    const $mainImage = $addGameform.querySelector(':scope > .mainImage > .row > .image-wrapper > .image');
    handleImageChange($addGameform['image'], $mainText, $mainImage);
}

// 메인 로고 관련
$addGameform['logo'].onchange = () => {
    const $logoText = $addGameform.querySelector(':scope > .logo > .row > .image-wrapper > ._text');
    const $logoImage = $addGameform.querySelector(':scope > .logo > .row > .image-wrapper > .image');
    handleImageChange($addGameform['logo'], $logoText, $logoImage);
}

// 추가 이미지 관련
/**이미지 선택 처리 함수*/
function handleFileSelect(event, $imageTag) {
    const files = event.target.files;

    // 선택한 파일들이 4개 이하인 경우만 처리
    if (files.length > 4) {
        alert('최대 4개의 이미지만 선택 가능합니다.');
        return;
    }

    // 기존의 이미지를 초기화 (기존 이미지들만 제거)
    $imageTag.innerHTML = '';

    // 파일을 하나씩 처리
    for (let i = 0; i < files.length; i++) {
        const file = files[i];

        // 파일이 이미지인지 확인
        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 선택할 수 있습니다.');
            continue;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            // 새로운 input-wrapper 요소를 생성
            const newWrapper = document.createElement('label');
            newWrapper.classList.add('input-wrapper');

            // 미리보기 이미지를 추가
            const image = document.createElement('img');
            image.src = e.target.result;
            image.alt = file.name;

            // 미리보기 이미지가 나타나게 설정
            newWrapper.appendChild(image);

            // 새로운 input[type="file"]을 추가 (파일을 다시 선택할 수 있도록 하기 위함)
            const newInput = document.createElement('input');
            newInput.type = 'file';
            newInput.name = 'images';
            newInput.accept = 'image/jpeg';
            newInput.hidden = true;  // input은 숨김 상태로 유지

            // 파일 선택 이벤트 리스너 추가 (선택 시 이미지 갱신)
            newInput.addEventListener('change', function(event) {
                const selectedFile = event.target.files[0];
                if (selectedFile && selectedFile.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        image.src = e.target.result;  // 기존 이미지를 새로 선택한 이미지로 업데이트
                    };
                    reader.readAsDataURL(selectedFile);
                } else {
                    alert('이미지 파일만 선택할 수 있습니다.');
                }
            });

            newWrapper.appendChild(newInput);

            // 새로 생성된 wrapper를 image-column에 추가
            $imageTag.appendChild(newWrapper);
        };

        // 파일을 읽기
        reader.readAsDataURL(file);
    }
}

document.querySelector('input[name="images"]').addEventListener('change', function(event) {
    const files = event.target.files;
    const $imageColumn = document.querySelector('.image-column');

    handleFileSelect(event, $imageColumn);
});

// 추가 이미지 초기화 버튼
document.querySelector('.reset-button').onclick = (e) => {
    e.preventDefault();

    // 파일 입력을 초기화
    const fileInput = document.querySelector('input[name="images"]');
    fileInput.value = '';  // 파일 선택 초기화

    // 미리보기 이미지들을 모두 제거
    const imageColumn = document.querySelector('.image-column');
    imageColumn.innerHTML = '';  // 미리보기 이미지들 초기화
    imageColumn.innerHTML =
        `<label class="input-wrapper">
            <span class="_text">이미지 찾기</span>
            <img alt="" class="image" src="" style="display: none;">
            <input hidden required accept="image/jpeg" name="images" type="file" multiple>
        </label>`;

    // 새로 추가된 input[type="file"]에 change 이벤트 리스너 재등록
    document.querySelector('input[name="images"]').addEventListener('change', function(event) {
        const imageColumn = document.querySelector('.image-column');
        handleFileSelect(event, imageColumn);
    });
};

//endregion

//region CKEditor5 사용(게임 상세 설명란)
const {
    ClassicEditor,
    Autoformat,
    AutoImage,
    Autosave,
    BalloonToolbar,
    BlockQuote,
    Bold,
    Essentials,
    FontBackgroundColor,
    FontColor,
    FontFamily,
    FontSize,
    FullPage,
    GeneralHtmlSupport,
    Heading,
    HorizontalLine,
    HtmlComment,
    ImageBlock,
    ImageCaption,
    ImageInline,
    ImageInsert,
    ImageInsertViaUrl,
    ImageResize,
    ImageStyle,
    ImageTextAlternative,
    ImageToolbar,
    ImageUpload,
    Italic,
    List,
    ListProperties,
    Markdown,
    Paragraph,
    PasteFromMarkdownExperimental,
    PasteFromOffice,
    ShowBlocks,
    SimpleUploadAdapter,
    SourceEditing,
    TextTransformation
} = CKEDITOR;

const editorConfig = {
    toolbar: {
        items: [
            'sourceEditing',
            'showBlocks',
            '|',
            'heading',
            '|',
            'fontSize',
            'fontFamily',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            '|',
            'horizontalLine',
            'insertImage',
            'blockQuote',
            '|',
            'bulletedList',
            'numberedList'
        ],
        shouldNotGroupWhenFull: true
    },
    plugins: [
        Autoformat,
        AutoImage,
        Autosave,
        BalloonToolbar,
        BlockQuote,
        Bold,
        Essentials,
        FontBackgroundColor,
        FontColor,
        FontFamily,
        FontSize,
        FullPage,
        GeneralHtmlSupport,
        Heading,
        HorizontalLine,
        HtmlComment,
        ImageBlock,
        ImageCaption,
        ImageInline,
        ImageInsert,
        ImageInsertViaUrl,
        ImageResize,
        ImageStyle,
        ImageTextAlternative,
        ImageToolbar,
        ImageUpload,
        Italic,
        List,
        ListProperties,
        Markdown,
        Paragraph,
        PasteFromMarkdownExperimental,
        PasteFromOffice,
        ShowBlocks,
        SimpleUploadAdapter,
        SourceEditing,
        TextTransformation
    ],
    balloonToolbar: ['bold', 'italic', '|', 'insertImage', '|', 'bulletedList', 'numberedList'],
    fontFamily: {
        supportAllValues: true
    },
    fontSize: {
        options: [10, 12, 14, 'default', 18, 20, 22],
        supportAllValues: true
    },
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    htmlSupport: {
        allow: [
            {
                name: /^.*$/,
                styles: true,
                attributes: true,
                classes: true
            }
        ]
    },
    image: {
        toolbar: [
            'toggleImageCaption',
            'imageTextAlternative',
            '|',
            'imageStyle:inline',
            'imageStyle:wrapText',
            'imageStyle:breakText',
            '|',
            'resizeImage'
        ]
    },
    language: 'ko',
    list: {
        properties: {
            styles: true,
            startIndex: true,
            reversed: true
        }
    },
    placeholder: '내용(HTML)을 입력하세요.',
};

const $textDescription = document.getElementById('description');

ClassicEditor.create($textDescription, editorConfig);

//endregion