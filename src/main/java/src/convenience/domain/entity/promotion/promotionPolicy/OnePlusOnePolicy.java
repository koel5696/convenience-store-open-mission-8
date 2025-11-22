package src.convenience.domain.entity.promotion.promotionPolicy;

import src.convenience.dto.payment.PayRequest;

public class OnePlusOnePolicy implements PromotionPolicy {

    private static final int PROMOTION_UNIT = 2;
    private static final int NONE_PROMOTION_QUANTITY = 1;

    public int noPromotionQuantity(PayRequest request, int quantity) {
        boolean insufficientStock = Boolean.TRUE.equals(request.insufficientStock());
        boolean missingPromotion = Boolean.TRUE.equals(request.missingPromotion());

        if (quantity % PROMOTION_UNIT == NONE_PROMOTION_QUANTITY &&
                (insufficientStock || missingPromotion)) {
            return NONE_PROMOTION_QUANTITY;
        }

        return 0;
    }
}