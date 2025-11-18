package src.convenience.domain.receiptGenerator;

import java.util.List;
import org.springframework.stereotype.Component;
import src.convenience.dto.receipt.ReceiptResponse;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

@Component
public class ReceiptGenerator {

    public static ReceiptResponse generateReceipt(List<ReceiptItem> items, int membershipDiscount) {
        int totalPrice = 0;
        int promotionDiscount = 0;

        for (ReceiptItem item : items) {
            promotionDiscount += item.price() * item.giftQuantity();
            totalPrice += item.price() * (item.paidQuantity() + item.giftQuantity());
        }
        return new ReceiptResponse(items, totalPrice, promotionDiscount, membershipDiscount);
    }
}