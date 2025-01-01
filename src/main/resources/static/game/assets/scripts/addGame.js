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
// 각 추가 이미지 필드에 이벤트 추가
const addAdditionalImageEvents = () => {
    // 1번 이미지
    const $image1Text = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(1) > ._text');
    const $image1Image = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(1) > .image');
    $addGameform['image1'].onchange = () => handleImageChange($addGameform['image1'], $image1Text, $image1Image);

    // 2번 이미지
    const $image2Text = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(2) > ._text');
    const $image2Image = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(2) > .image');
    $addGameform['image2'].onchange = () => handleImageChange($addGameform['image2'], $image2Text, $image2Image);

    // 3번 이미지
    const $image3Text = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(3) > ._text');
    const $image3Image = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(3) > .image');
    $addGameform['image3'].onchange = () => handleImageChange($addGameform['image3'], $image3Text, $image3Image);

    // 4번 이미지
    const $image4Text = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(4) > ._text');
    const $image4Image = $addGameform.querySelector(':scope > .image > .image-column > .image-wrapper:nth-child(4) > .image');
    $addGameform['image4'].onchange = () => handleImageChange($addGameform['image4'], $image4Text, $image4Image);

}

// 추가 이미지 초기화 버튼 처리
const resetAdditionalImages = () => {
    const imageWrappers = $addGameform.querySelectorAll('.image-column .image-wrapper');

    imageWrappers.forEach((wrapper) => {
        const inputElement = wrapper.querySelector('input[type="file"]');
        const textElement = wrapper.querySelector('._text');
        const imageElement = wrapper.querySelector('img');

        // 파일 초기화
        inputElement.value = '';
        // 텍스트는 보이고 이미지는 숨김
        textElement.style.display = 'flex';
        imageElement.style.display = 'none';
    });
};

// 초기화 버튼 이벤트 리스너 등록
document.querySelector('.reset-button').addEventListener('click', (e) => {
    e.preventDefault();
    resetAdditionalImages();
});

// 초기화 시 추가 이미지 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {
    addAdditionalImageEvents();
});
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