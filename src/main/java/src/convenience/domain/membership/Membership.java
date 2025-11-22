package src.convenience.domain.membership;

import java.util.List;
import org.springframework.stereotype.Component;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

@Component
public class Membership {
    private static final int DISCOUNT_PERCENT = 10;
    private static final int MAX_DISCOUNT_AMOUNT = 10000;

    public int discount(PayRequest request, List<ReceiptItem> items) {
        if (Boolean.TRUE.equals(request.membership())) {
            int nonPromotionPriceTotal = calculateNonPromotionAmount(items);
            int discount = (int) (nonPromotionPriceTotal * (DISCOUNT_PERCENT / 100.0));
            return Math.min(discount, MAX_DISCOUNT_AMOUNT);
        }
        return 0;
    }

    private int calculateNonPromotionAmount(List<ReceiptItem> items) {
        return items.stream()
                .mapToInt(item -> item.price() * item.nonePromotionQuantity())
                .sum();
    }
}
