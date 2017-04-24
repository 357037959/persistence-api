package com.ctvit.framework.core;

/**
 * @author liuzh
 */
public class MapperException extends RuntimeException {

	private static final long serialVersionUID = 4200571589142600931L;

	public MapperException() {
        super();
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }

}
