package com.ssw.epicgames.resutls.purchase;

import com.ssw.epicgames.resutls.Result;

public enum PurchaseResult implements Result {
    FAILURE_DUPLICATE_CART,
    FAILURE_DUPLICATE_WISHLIST,
    FAILURE_NOT_FOUND,
    FAILURE_DUPLICATE_PURCHASE,
    FAILURE_DUPLICATE_REFUND,
    FAILURE_AGE_LIMIT,
    FAILURE_DATE_PASSED,
}
