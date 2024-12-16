//region 가격 포맷
window.onload = function() {
    const $priceElement = document.querySelectorAll('.price');
    $priceElement.forEach( $price => {
        let price = parseInt($price.textContent, 10);
        let formattedPrice;

        // payment-price-value 클래스를 가진 요소를 확인
        if (isNaN(price)) {
            $price.textContent = '무료';
        }
        else if ($price.classList.contains('-minus')) {
            formattedPrice = price.toLocaleString();
            $price.textContent = '-￦' + formattedPrice;
        } else {
            formattedPrice = price.toLocaleString();
            $price.textContent = '￦' + formattedPrice;
        }
    });
}
//endregion