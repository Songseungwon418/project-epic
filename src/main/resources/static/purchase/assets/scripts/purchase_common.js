//region 가격 포맷
window.onload = function() {
    const $priceElement = document.querySelectorAll('.price');
    $priceElement.forEach( $price => {
        let price = parseInt($price.textContent, 10);
        if (isNaN(price)) {
            $price.textContent = '무료';
        } else {
            const formattedPrice = price.toLocaleString();
            $price.textContent = '￦' + formattedPrice;
        }
    });
}
//endregion