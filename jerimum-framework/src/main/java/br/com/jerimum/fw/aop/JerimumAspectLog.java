package br.com.jerimum.fw.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;

import br.com.jerimum.fw.exception.JerimumException;
import br.com.jerimum.fw.exception.ValidationException;
import br.com.jerimum.fw.logging.LoggerUtils;

/**
 * Aspect help to logs.
 * 
 * @author Dali Freire - dalifreire@gmail.com
 */
public class JerimumAspectLog {

    private static int sequence = 1;

    /**
     * Logs the entry of the method.
     * 
     * @param jp
     * @throws Exception
     */
    protected void logEntry(JoinPoint jp) throws Exception {

        Logger logger = JerimumAspectUtils.getLogger(jp);
        if (logger.isDebugEnabled()) {

            StringBuilder sb = new StringBuilder();
            sb.append("Entry --> ");
            sb.append(JerimumAspectUtils.methodName(jp));
            sb.append('(');
            JerimumAspectUtils.appendArguments(jp.getArgs(), sb);
            sb.append(')');
            LoggerUtils.logDebug(logger, sb.toString());
        }
    }

    /**
     * Logs the exit of the method (void type).
     * 
     * @param jp
     * @throws Exception
     */
    protected void logExit(JoinPoint jp) throws Exception {

        Logger logger = JerimumAspectUtils.getLogger(jp);
        LoggerUtils.logDebug(logger, "Exit <-- " + JerimumAspectUtils.methodName(jp) + " - void");
    }

    /**
     * Logs the exit of the method.
     * 
     * @param jp
     * @param returningValue
     * @throws Exception
     */
    protected void logExit(JoinPoint jp, Object returningValue) throws Exception {

        Logger logger = JerimumAspectUtils.getLogger(jp);
        LoggerUtils.logDebug(logger,
            "Exit <-- " + JerimumAspectUtils.methodName(jp) + " - " + JerimumAspectUtils.displayObject(returningValue));
    }

    /**
     * Logs the exception that occurred in the method.
     * 
     * @param jp
     * @param ex
     * @throws Throwable
     */
    protected void logException(JoinPoint jp, Throwable ex) throws Throwable {

        String occurrenceId = nextOccurrenceId();
        String methodName = JerimumAspectUtils.methodName(jp);

        Logger logger = JerimumAspectUtils.getLogger(jp);
        if (ex instanceof ValidationException) {

            LoggerUtils.logDebug(logger, "Exit <-- {} - EXCEPTION {} - {}", methodName, ex.getClass().getName(), ex.getMessage());
            
        } else {
            LoggerUtils.logError(logger, "Exit <-- {} - EXCEPTION [{}] {} - {}", methodName, ex.getClass().getName(), ex.getMessage());
            if (logger.isErrorEnabled()) {
                if (ex instanceof JerimumException) {
                    JerimumException fex = (JerimumException) ex;
                    dumpException(fex, true, logger);
                }  else {
                    JerimumException fex = new JerimumException(ex.getMessage(), ex, new Date(), occurrenceId, methodName,
                        jp.getThis(), jp.getArgs());
                    dumpException(fex, true, logger);
                }
            }
        }

        throw ex;
    }

    /**
     * Logs he exception stack trace.
     * 
     * @param fex
     * @param appendRootCause
     * @param logger
     */
    protected void dumpException(JerimumException fex, boolean appendRootCause, Logger logger) {

        StringBuilder args = new StringBuilder();
        JerimumAspectUtils.appendArguments(fex.getArgs(), args);

        StringBuilder dump = new StringBuilder();
        dump.append("# Error ID: {} \n");
        dump.append("# Timestamp: {} \n");
        dump.append("# Error message: {} \n");
        dump.append("# Service: {} \n");
        dump.append("# Method: {} \n");
        dump.append("# Arguments: ({}) \n");

        String rootCauseString = null;
        if (appendRootCause) {
            Throwable rootCause = fex;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            rootCauseString = getRootCause(rootCause);
            dump.append("# Exception Root Cause: {} \n");
        }
        dump.append("-");
        LoggerUtils.logError(logger, dump.toString(), fex.getOccurrenceId(), fex.getTimeStamp(), fex.getMessage(),
            fex.getCurrent(), fex.getMethodName(), args, rootCauseString);
    }

    /**
     * Returns the exception stack in {@link String} format.
     * 
     * @param cause
     * @return {@link String}
     */
    protected String getRootCause(Throwable cause) {

        if (cause instanceof ValidationException) {

            return cause.getMessage();

        } else {

            Throwable rootCause = cause;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            StringWriter rootCauseStack = new StringWriter();
            PrintWriter writer = new PrintWriter(rootCauseStack);
            rootCause.printStackTrace(writer);
            return rootCauseStack.toString();
        }
    }

    /**
     * Returns the next occurrence id.
     * 
     * @return {@link String}
     */
    protected static synchronized String nextOccurrenceId() {
        return String.valueOf(++sequence);
    }

}

