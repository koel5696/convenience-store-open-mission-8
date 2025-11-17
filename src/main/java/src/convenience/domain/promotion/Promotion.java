package src.convenience.domain.promotion;

import org.springframework.stereotype.Component;
import src.convenience.domain.entity.Product;

@Component
public class Promotion {
    public static final String NONE_PROMOTION = "null";
    private static final String ONE_PLUS_ONE = "1+1";
    private static final String TWO_PLUS_ONE = "2+1";

    public boolean checkNonePromotion(String promotion) {
        return promotion.equals(NONE_PROMOTION);
    }

    public boolean isMissingPromotion(Product product, int quantity) {
        return (product.checkPromotion(ONE_PLUS_ONE) && quantity % 2 == 1) ||
                (product.checkPromotion(TWO_PLUS_ONE) && quantity % 3 == 2);
    }

    public PromotionResult checkPromotion(Product product, int quantity) {
        if (product.checkPromotion(ONE_PLUS_ONE)) {
            return applyOnePlusOne(quantity);
        }

        if (product.checkPromotion(TWO_PLUS_ONE)) {
            return applyTwoPlusOne(quantity);
        }

        return new PromotionResult(quantity, 0);
    }

    private PromotionResult applyOnePlusOne(int quantity) {
        int giftQuantity = quantity / 2;
        int paidQuantity = giftQuantity + quantity % 2;
        return new PromotionResult(paidQuantity, giftQuantity);
    }

    private PromotionResult applyTwoPlusOne(int quantity) {
        int giftQuantity = quantity / 3;
        int paidQuantity = quantity - giftQuantity;
        return new PromotionResult(paidQuantity, giftQuantity);
    }
}
