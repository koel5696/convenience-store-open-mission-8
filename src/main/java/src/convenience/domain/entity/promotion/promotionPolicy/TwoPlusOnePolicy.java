package src.convenience.domain.entity.promotion.promotionPolicy;

import src.convenience.dto.payment.PayRequest;

public class TwoPlusOnePolicy implements PromotionPolicy {

    private static final int PROMOTION_UNIT = 3;
    private static final int NONE_PROMOTION_QUANTITY = 2;

    public int noPromotionQuantity(PayRequest request, int quantity) {
        int remain = quantity % PROMOTION_UNIT;

        boolean insufficientStock = Boolean.TRUE.equals(request.insufficientStock());
        boolean missingPromotion = Boolean.TRUE.equals(request.missingPromotion());

        if (remain == 1) {
            return 1;
        }

        if (remain == NONE_PROMOTION_QUANTITY &&
                (insufficientStock || missingPromotion)) {
            return NONE_PROMOTION_QUANTITY;
        }

        return 0;
    }

}
