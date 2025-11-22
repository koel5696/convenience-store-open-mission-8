package src.convenience.service.paymentService;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;
import src.convenience.domain.entity.promotion.PromotionResult;
import src.convenience.domain.membership.Membership;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

@Component
public class PaymentProcessor {

    private final PaymentFlowChecker paymentFlowChecker;
    private final PromotionRepository promotionRepository;
    private final Membership membership;

    public PaymentProcessor(PaymentFlowChecker paymentFlowChecker, PromotionRepository promotionRepository,
                            Membership membership) {
        this.paymentFlowChecker = paymentFlowChecker;
        this.promotionRepository = promotionRepository;
        this.membership = membership;
    }

    public void checkPaymentFlow(PayRequest request, Map<Long, Integer> items) {
        paymentFlowChecker.checkFlow(request, items);
    }

    public PromotionResult promotionFlow(PayRequest request, Product product, int quantity) {
        Promotion promotion = promotionRepository.promotionCreate(product.getPromotion());
        return promotion.applyPromotion(request, quantity);
    }

    public int membershipDiscount(PayRequest request, List<ReceiptItem> items) {
        return membership.discount(request, items);
    }
}

