package com.sancaijia.building.core.exception;

import com.sancaijia.building.core.exception.param.ApiErrorResponse;
import com.sancaijia.building.core.exception.param.FieldErrorListVO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常处理返回
     * 2020年9月14日15:49:25
     * @param exception
     * @param request
     * @return
     */
    @ResponseBody
    @ExceptionHandler({Exception.class})
    protected org.springframework.http.ResponseEntity<?> handleConflict(Exception exception, HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = simpleDateFormat.format(new Date());
        String requestPath = request.getRequestURI();
        apiErrorResponse.setTimestamp(timestamp);
        apiErrorResponse.setPath(requestPath);
        if (logger.isDebugEnabled()) {
            exception.printStackTrace();
        }

        if (exception instanceof BaseException) {
            BaseException basicException = (BaseException)exception;
            if (logger.isDebugEnabled()) {
                apiErrorResponse.setException(basicException.toString());
            }

            apiErrorResponse.setMessage(basicException.getErrorMsg());
            apiErrorResponse.setCode(basicException.getErrorCode());
        } else if (exception instanceof HttpMessageNotReadableException) {
            if (logger.isDebugEnabled()) {
                apiErrorResponse.setException(exception.toString());
            }

            apiErrorResponse.setMessage(BasicExceptionEnums.BAD_REQUEST.getResultMsg());
            apiErrorResponse.setCode(BasicExceptionEnums.BAD_REQUEST.getResultCode());
        } else if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException)exception;
            BindingResult result = validException.getBindingResult();
            List<FieldError> fieldErrors = result.getFieldErrors();
            apiErrorResponse.setMessage(BasicExceptionEnums.FIELD_VALIDA_ERROR.getResultMsg());
            apiErrorResponse.setCode(BasicExceptionEnums.FIELD_VALIDA_ERROR.getResultCode());
            if (logger.isDebugEnabled()) {
                apiErrorResponse.setException(validException.getClass().getSimpleName());
                apiErrorResponse.setErrorData((new FieldErrorListVO()).build(fieldErrors));
            }
        }  else {
            if (logger.isDebugEnabled()) {
                apiErrorResponse.setException(ExceptionUtils.getMessage(exception));
                apiErrorResponse.setMessage(exception.getMessage());
            }

            apiErrorResponse.setCode(BasicExceptionEnums.SYSTEM_ERROR.getResultCode());
        }

        return new org.springframework.http.ResponseEntity(apiErrorResponse, HttpStatus.OK);
    }
}
