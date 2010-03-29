package improject;

/**
 * Wraps all exceptions of back-end libraries
 * @author kindcoder
 *
 */
public class IMException extends Exception {

	private static final long serialVersionUID = 1L;

	public IMException(String message) {
		super(message);
	}

}
