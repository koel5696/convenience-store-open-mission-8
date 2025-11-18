package src.convenience.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.convenience.dto.cart.CartRequest;
import src.convenience.service.cartService.CartService;


@RestController
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/api/cart/add")
    public ResponseEntity<String> addItemToCart(@RequestBody CartRequest request) {
        cartService.addItem(request.productId(), request.quantity());
        return ResponseEntity.ok("상품이 성공적으로 추가되었습니다.");
    }

    @PostMapping("api/cart/remove")
    public ResponseEntity<String> removeItemToCart(@RequestBody CartRequest request) {
        cartService.removeItem(request.productId());
        return  ResponseEntity.ok().build();
    }

    @GetMapping("/api/cart")
    public ResponseEntity<Map<Long, Integer>> getCart() {
        return ResponseEntity.ok(cartService.getCartItems());
    }


}
