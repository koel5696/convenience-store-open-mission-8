package src.convenience.service.cartService;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.cart.Cart;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;

@Service
public class CartService {
    private final ProductRepository productRepository;
    private final Cart cart;


    public CartService(ProductRepository productRepository, Cart cart) {
        this.productRepository = productRepository;
        this.cart = cart;
    }

    @Transactional
    public void addItem(Long productId, Integer quantity) {
        Product product = productRepository.getByIdOrThrow(productId);

        int totalQuantity = cart.currentQuantity(productId) + quantity;
        product.checkQuantity(totalQuantity);

        cart.addItem(productId, quantity);
    }

    public void removeItem(Long productId) {
        cart.removeItem(productId);
    }

    public Map<Long, Integer> getCartItems() {
        return cart.getItems();
    }

}
