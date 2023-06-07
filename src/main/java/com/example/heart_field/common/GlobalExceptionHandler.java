package com.example.heart_field.common;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理SQLIntegrityConstraintViolationException异常
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("失败了");
    }

    /**
     * 处理CustomException异常
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public R<String> exceptionHandler(RuntimeException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> exceptionHandler(MethodArgumentNotValidException ex){
        log.error(ex.getMessage());
        String error = "";
        for(int i = 0; i < ex.getBindingResult().getAllErrors().size(); i++){
            error += ex.getBindingResult().getAllErrors().get(i).getDefaultMessage()+" ";
        }
        return R.error(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public R<String> exceptionHandler(ExpiredJwtException ex){
        log.error(ex.getMessage());
        return R.auth_error(ex.getMessage());
    }
}
