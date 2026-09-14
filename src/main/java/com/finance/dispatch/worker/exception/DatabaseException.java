package com.finance.dispatch.worker.exception;

public class DatabaseException extends BaseException {

    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }

    public DatabaseException(String code, String message) {
        super(code, message);
    }
}