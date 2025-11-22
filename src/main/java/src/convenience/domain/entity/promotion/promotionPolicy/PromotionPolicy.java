package src.convenience.domain.promotionPolicy;

import src.convenience.dto.payment.PayRequest;

public interface PromotionPolicy {
    int noPromotionQuantity(PayRequest request, int quantity);
}
