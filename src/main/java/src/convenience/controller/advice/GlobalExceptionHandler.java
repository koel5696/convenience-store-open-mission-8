package src.convenience.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import src.convenience.controller.ApiResponse;
import src.convenience.exception.ApiError;
import src.convenience.exception.BusinessException;
import src.convenience.exception.constants.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleStockIssue(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiError apiError = errorCode.toApiError();

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(apiError));
    }

    //400 클라이언트쪽 잘못된 방식의 요청 에러 처리.
    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<?>> handleJsonParseError() {
        ApiError error = ApiError.of(
                "INVALID_REQUEST_FORMAT",
                "요청 형식이 올바르지 않습니다."
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(error));
    }

    // 500 서버의 오류 클라이언트 문제가 아님(버그나 장애 발생 시)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnexpected(Exception e) {

        log.error("Unhandled exception", e); // 필수

        ApiError error = ApiError.of(
                "INTERNAL_SERVER_ERROR",
                "서버 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

}

