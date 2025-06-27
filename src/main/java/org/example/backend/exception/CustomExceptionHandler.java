package org.example.backend.exception;

import org.example.backend.dto.ApiResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(CustomConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponseDto handleConflictException(CustomConflictException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.CONFLICT, "Email này đã được sử dụng!", errors);
    }

    @ExceptionHandler(CustomRequestTooSoonException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiResponseDto handleRequestTooSoonException(CustomRequestTooSoonException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.TOO_MANY_REQUESTS, "Bạn gửi quá nhiều yêu cầu. Vui lòng thử lại sau!", errors);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto handleBadRequestException(CustomBadRequestException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ!", errors);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto handleNotFoundException(CustomNotFoundException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu!", errors);
    }

    @ExceptionHandler(CustomAccountLockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponseDto handleCustomAccountLockedException(CustomAccountLockedException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.UNAUTHORIZED, ex.getMessage(), errors);
    }

    @ExceptionHandler(CustomDeletedAccountException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ApiResponseDto handleCustomDeletedAccountException(CustomDeletedAccountException ex, WebRequest req) {
        List<Map<String, String>> errors = List.of(
                Map.of("field", ex.getField(), "message", ex.getMessage())
        );
        String path = req.getDescription(false); // ví dụ: uri=/api/v1/user/register
        // Ghi log chi tiết
        logger.warn("Lỗi tại {}: {}", path, ex.getMessage(), ex);
        return new ApiResponseDto(HttpStatus.GONE, "Tài khoản bị khóa liên hệ admin để mở khóa!", errors);
    }
}
