package com.iamk.weTeam.common.expection;

import javax.servlet.http.HttpServletRequest;


import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.utils.ResultUtil;
import org.apache.ibatis.binding.BindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 * @program: 测试
 * @description:全局的异常处理类
 * @author:
 * @create: 2018-10-19 11:38
 **/
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class SpringExceptionHandle {


    private static final Logger logger = LoggerFactory.getLogger(SpringExceptionHandle.class);

    /**
     * 自定义异常的捕获
     * 自定义抛出异常。统一的在这里捕获返回JSON格式的友好提示。
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = UnicomRuntimeException.class)
    @ResponseBody
    public <T> ResultUtil sendError(UnicomRuntimeException exception, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        System.out.println(exception.getCode());
        logger.error("自定义异常 url ={} ,message {}", requestURI, exception.getMsg());
        return new ResultUtil(false, exception.getCode(), exception.getMsg());
    }

    /**
     * 空指针异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultUtil exceptionHandler(HttpServletRequest req, NullPointerException e) {
        logger.error("发生空指针异常！原因是:", e);
        return new ResultUtil(UnicomResponseEnums.BODY_NOT_MATCH);
    }

    /**
     * 请求方法不存在
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NoSuchMethodException.class)
    @ResponseBody
    public ResultUtil exceptionHandler(HttpServletRequest req, NoSuchMethodException e) {
        logger.error("请求方法不存在！原因是:", e);
        return new ResultUtil(UnicomResponseEnums.REQUEST_METHOD_SUPPORT_ERROR);
    }


    /**
     * 处理请求方法不支持的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultUtil exceptionHandler(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
        logger.error("处理请求方法不支持的异常！原因是:", e);
        return ResultUtil.error(UnicomResponseEnums.REQUEST_METHOD_SUPPORT_ERROR);
    }

    /**
     * 请求参数类型错误异常的捕获
     *
     * @param e e
     * @return r
     */
    @ExceptionHandler(value = {BindException.class})
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResultUtil badRequest(BindException e) {
        logger.error("请求参数类型错误异常的捕获 ,message {}", e.getMessage());
        return new ResultUtil(UnicomResponseEnums.BAD_REQUEST);

    }


    /**
     * 404错误异常的捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResultUtil badRequestNotFound(BindException e) {
        logger.error("404 错误 {}", e.getMessage());
        return new ResultUtil(UnicomResponseEnums.NOT_FOUND);
    }


    /**
     * mybatis未绑定异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindingException.class)
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultUtil mybatis(Exception e) {
        logger.error(" mybatis未绑定异常 ,message {}", e.getMessage());
        return new ResultUtil(false, UnicomResponseEnums.BOUND_STATEMENT_NOT_FOUNT);
    }

    /**
     * 数据库操作出现异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {SQLException.class, DataAccessException.class})
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultUtil systemError(Exception e) {
        logger.error("occurs error when execute method ,message {}", e.getMessage());
        return new ResultUtil(false, UnicomResponseEnums.DATABASE_ERROR);
    }

    /**
     * 网络连接失败！
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ConnectException.class})
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultUtil connect(Exception e) {
        logger.error("occurs error when execute method ,message {}", e.getMessage());
        return new ResultUtil(false, UnicomResponseEnums.CONNECTION_ERROR);
    }

    /**
     * 其他异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    // @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public ResultUtil notAllowed(Exception e) {
        logger.error("其他异常 ,message {}", e.getMessage());
        return new ResultUtil(false, UnicomResponseEnums.METHOD_NOT_ALLOWED);
    }


}
