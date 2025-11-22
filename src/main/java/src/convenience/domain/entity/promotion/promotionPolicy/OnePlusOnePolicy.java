package src.convenience.domain.promotionPolicy;

import src.convenience.dto.payment.PayRequest;

public class OnePlusOnePolicy implements PromotionPolicy {

    private static final int PROMOTION_UNIT = 2;
    private static final int NONE_PROMOTION_QUANTITY = 1;

    public int noPromotionQuantity(PayRequest request, int quantity) {
        if (quantity % PROMOTION_UNIT == NONE_PROMOTION_QUANTITY
                && !request.missingPromotion()) { // 놓친 프로모션 상품이 존재하나 받지 않고 결제를 고른경유
            return NONE_PROMOTION_QUANTITY;
        }

        if (quantity % PROMOTION_UNIT == NONE_PROMOTION_QUANTITY
                && request.insufficientStock()) { // 재고 부족 프로모션 상품이지만 그대로 결제를 선택한 경우
            return NONE_PROMOTION_QUANTITY;
        }
        return 0;
    }
}