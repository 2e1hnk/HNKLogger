package uk.co.mattcarus.hnklogger.exceptions;

public class HNKPropertyNotFoundException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HNKPropertyNotFoundException()
    {

    }

    public HNKPropertyNotFoundException(String message)
    {
        super(message);
    }

    public HNKPropertyNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public HNKPropertyNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public HNKPropertyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
