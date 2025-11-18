package src.convenience.dto.receipt;

import java.util.List;

public record ReceiptResponse(
        List<ReceiptItem> items,
        int totalPrice,
        int promotionDiscount,
        int membershipDiscount
) {
    public record ReceiptItem(String productName,
                              String promotion,
                              int price,
                              int paidQuantity,
                              int giftQuantity
    ) {
        public boolean isNonPromotion() {
            return promotion.equals("null");
        }
    }
}