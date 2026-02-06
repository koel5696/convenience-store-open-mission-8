package src.convenience.exception.constants;

import org.springframework.http.HttpStatus;
import src.convenience.exception.ApiError;

public enum ErrorCode {
    INTEGER(HttpStatus.CONFLICT, "INTEGER", "정수 입력이 아님"),
    NON_EXIST_PRODUCT(HttpStatus.CONFLICT, "NON_EXIST_PRODUCT", "편의점에 존재하지 않는 상품"),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "STOCK_ISSUE", "재고 부족"),
    NOT_EXIST_IN_CART(HttpStatus.CONFLICT, "NOT_EXIST_IN_CART", "카트에 존재하지 않는 상품"),
    EMPTY_CART(HttpStatus.CONFLICT, "EMPTY_CART", "장바구니가 비어있음"),
    PROMOTION_MISS(HttpStatus.CONFLICT, "PROMOTION_MISS", "프로모션 상품 누락"),
    PROMOTION_MISS_BUT_OUT_OF_STOCK(HttpStatus.CONFLICT, "PROMOTION_MISS_BUT_OUT_OF_STOCK", "프로모션 상품 누락이나 증정할 상품이 부족"),
    MEMBERSHIP_INVALID(HttpStatus.BAD_REQUEST, "MEMBERSHIP_NOT_SELECTED", "멤버십 적용 여부 미선택");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public ApiError toApiError() {
        return ApiError.of(code, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
