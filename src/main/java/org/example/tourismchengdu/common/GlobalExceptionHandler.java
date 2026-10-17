package org.example.tourismchengdu.common;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        return Result.fail(ex.getResultCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception ex) {
        String message = ResultCode.BAD_REQUEST.getMessage();
        if (ex instanceof MethodArgumentNotValidException validationException) {
            if (validationException.getBindingResult().getFieldError() != null) {
                message = validationException.getBindingResult().getFieldError().getDefaultMessage();
            }
        } else if (ex instanceof BindException bindException) {
            if (bindException.getBindingResult().getFieldError() != null) {
                message = bindException.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        ex.printStackTrace();
        return Result.fail(ResultCode.INTERNAL_ERROR, ResultCode.INTERNAL_ERROR.getMessage());
    }
}
