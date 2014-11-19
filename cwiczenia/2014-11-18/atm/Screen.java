package atm;

import java.util.concurrent.TimeoutException;

public interface Screen {

	/**
	 * Display a message.
	 * 
	 * @param message
	 *            the message to be displayed
	 */
	public void displayMessage(String message);

	/**
	 * Display a query with some options, and wait for user's choice.
	 * 
	 * @param query
	 *            the query
	 * @param options
	 *            the options to be chosen from
	 * 
	 * @param timeout
	 *            timeout, in milliseconds
	 * 
	 * @return the index of the chosen option
	 * 
	 * @throws TimeoutException
	 *             timed out waiting for user input
	 */
	public int displayChoice(String query, String[] options, int timeout) throws TimeoutException;
}
