package br.com.jerimum.fw.exception;

/**
 * Exception class for services.
 * 
 * @author Dali Freire: dalifreire@gmail.com
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 3473094129626736298L;

    public ServiceException() {

    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
