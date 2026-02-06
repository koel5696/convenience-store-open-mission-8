package src.convenience.exception;


public class ApiError {
    private final String code;
    private final String message;

    // getter를 왜 써야 하는지 파이널은 안해도 되는지
    // 여기에 이제 동시성 이슈를 확인해야함.
    private ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ApiError of(String code, String message) {
        return new ApiError(code, message);
    }
}
