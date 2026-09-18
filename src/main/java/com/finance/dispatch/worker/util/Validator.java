package com.finance.dispatch.worker.util;

import com.finance.dispatch.worker.exception.LogicException;

public class Validator {

    public static void validateCronExpress(String cronExpression) {
        int countExpression = cronExpression.split(" ").length;

        if(countExpression != 6) {
            throw new LogicException("cron expression required 6 fields");
        }
    }
}
