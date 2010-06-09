package improject;

/**
 * Listener for status online/offline changes
 * 
 * @author kindcoder
 */
public interface UserListener {

	public void statusChanged(String user, String newStatus);
	
}
