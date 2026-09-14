package com.finance.dispatch.worker.exception;

public class LogicException extends BaseException {

    public LogicException(String message) {
        super("LOGIC_ERROR", message);
    }

    public LogicException(String code, String message) {
        super(code, message);
    }

    public LogicException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}