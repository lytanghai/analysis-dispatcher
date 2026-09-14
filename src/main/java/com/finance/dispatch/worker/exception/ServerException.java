package com.finance.dispatch.worker.exception;

public class ServerException extends BaseException {

    public ServerException(String message) {
        super("SERVER_ERROR", message);
    }

    public ServerException(String message, Throwable cause) {
        super("SERVER_ERROR", message, cause);
    }
}