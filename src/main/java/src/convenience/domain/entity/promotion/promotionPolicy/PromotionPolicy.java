package src.convenience.domain.entity.promotion.promotionPolicy;

import src.convenience.dto.payment.PayRequest;

public interface PromotionPolicy {
    int noPromotionQuantity(PayRequest request, int quantity);
}
