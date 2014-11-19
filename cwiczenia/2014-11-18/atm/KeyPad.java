package atm;

import java.util.concurrent.TimeoutException;


public interface KeyPad {

	/**
	 * Gets a single key press.
	 * 
	 * @param timeout
	 *            timeout, in milliseconds
	 * @return the key
	 * @throws TimedOutException
	 *             timed out waiting for a key to be pressed
	 */
	public Key getKey(int timeout) throws TimeoutException;

}
