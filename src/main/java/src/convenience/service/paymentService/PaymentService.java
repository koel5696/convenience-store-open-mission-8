package src.convenience.service.paymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.cart.Cart;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;
import src.convenience.domain.entity.promotion.PromotionResult;
import src.convenience.domain.receiptGenerator.ReceiptGenerator;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse;

@Service
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentProcessor processor;
    private final Cart cart;

    public PaymentService(ProductRepository productRepository, PaymentProcessor processor, Cart cart) {
        this.productRepository = productRepository;
        this.processor = processor;
        this.cart = cart;
    }

    @Transactional
    public ReceiptResponse pay(PayRequest request) {
        Map<Long, Integer> items = cart.getItems();
        cart.checkCartEmpty();

        processor.checkPaymentFlow(request, items); //사용자 중간 입력사항 검증
        List<ReceiptItem> paymentItems = generatePaymentItems(items); // 영수증에 들어갈 아이템 구분
        int membershipDiscount = processor.membershipDiscount(request, paymentItems); // 멤버십 적용

        cart.clear();
        return ReceiptGenerator.generateReceipt(paymentItems, membershipDiscount);
    }

    private List<ReceiptItem> generatePaymentItems(Map<Long, Integer> items) {
        List<ReceiptItem> finalItems = new ArrayList<>();

        items.forEach((productId, quantity) -> {
            Product product = productRepository.getByIdOrThrow(productId);
            PromotionResult result = processor.promotionFlow(product, quantity);
            finalItems.add(new ReceiptItem(product.getName(), product.getPromotion(), product.getPrice(),
                    result.paidQuantity(), result.giftQuantity()));
            decreaseStock(product, quantity);
        });
        return finalItems;
    }

    private void decreaseStock(Product product, Integer quantity) {
        product.decreaseQuantity(quantity);
        productRepository.save(product);
    }
}
