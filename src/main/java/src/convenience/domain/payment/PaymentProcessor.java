package src.convenience.domain.payment;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import src.convenience.domain.entity.Product;
import src.convenience.domain.membership.Membership;
import src.convenience.domain.promotion.Promotion;
import src.convenience.domain.promotion.PromotionResult;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;
import src.convenience.service.ResponseService;

@Component
public class PaymentProcessor {

    private final ResponseService responseService;
    private final Promotion promotion;
    private final Membership membership;

    public PaymentProcessor(ResponseService responseService, Promotion promotion, Membership membership) {
        this.responseService = responseService;
        this.promotion = promotion;
        this.membership = membership;
    }

    public void validate(PayRequest request, Map<Long, Integer> items) {
        responseService.responses(request, items);
    }

    public PromotionResult applyPromotion(Product product, int quantity) {
        return promotion.checkPromotion(product, quantity);
    }

    public int membershipDiscount(PayRequest request, List<ReceiptItem> items) {
        return membership.discount(request, items);
    }
}

