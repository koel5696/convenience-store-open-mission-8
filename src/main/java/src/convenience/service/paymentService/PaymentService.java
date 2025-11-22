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
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;

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
        Map<Long, Integer> cartItems = cart.getItems();
        cart.checkCartEmpty();

        processor.checkPaymentFlow(request, cartItems);

        List<ReceiptItem> ReceiptItems = generateReceiptItems(request, cartItems);
        int membershipDiscount = processor.membershipDiscount(request, ReceiptItems);

        cart.clear();
        return ReceiptGenerator.generateReceipt(ReceiptItems, membershipDiscount);
    }

    private List<ReceiptItem> generateReceiptItems(PayRequest request, Map<Long, Integer> items) {
        List<ReceiptItem> receiptItems = new ArrayList<>();

        items.forEach((productId, quantity) -> {
            Product product = productRepository.getByIdOrThrow(productId);
            PromotionResult result = processor.promotionFlow(request, product, quantity);
            receiptItems.add(new ReceiptItem(product.getName(), product.getPromotion(), product.getPrice(),
                    result.paidQuantity(), result.giftQuantity(), result.nonePromotionQuantity()));
            decreaseStock(product, quantity);
        });

        return receiptItems;
    }

    private void decreaseStock(Product product, Integer quantity) {
        product.decreaseQuantity(quantity);
        productRepository.save(product);
    }
}
