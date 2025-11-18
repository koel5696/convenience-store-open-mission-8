package src.convenience.controller.advice;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import src.convenience.exception.MembershipException;
import src.convenience.exception.PromotionProductException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "type", "EMPTY_CART",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(PromotionProductException.class)
    public ResponseEntity<?> handleMissPromotion(PromotionProductException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getSuggestions());
    }

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<?> handleMembership(MembershipException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "type", "MEMBERSHIP",
                        "message", e.getMessage()
                ));
    }


}

