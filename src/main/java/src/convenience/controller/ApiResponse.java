package src.convenience.controller;

import src.convenience.exception.ApiError;

public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ApiError error;

    private ApiResponse(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public ApiError getError() {
        return error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null); // T인거 알고 컴파일러가 알아서 판단
    }

    public static ApiResponse<?> error(ApiError error) {
        return new ApiResponse<>(false, null, error); // ?인거 알고 컴파일러가 알아서 판단
    }
}
