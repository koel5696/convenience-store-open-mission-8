package src.convenience.domain.cart;


import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Cart {
    Map<Long, Integer> items = new HashMap<>();

    public void addItem(Long productId, int quantity) {
            items.put(productId, items.getOrDefault(productId, 0) + quantity);
    }

    public void removeItem(Long productId) {
        if (!items.containsKey(productId)) {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다.");
        }
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public Map<Long, Integer> getItems() {
        return new HashMap<>(items);
    }

    public void checkCartEmpty() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }
    }

    public int currentQuantity(Long productId) {
        Integer quantity = items.get(productId);
        if(quantity == null) {
            return 0;
        }
        return quantity;
    }

}
