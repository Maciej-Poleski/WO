package atm;

public interface Printer {

	/**
	 * Checks for paper.
	 * 
	 * @return whether new messages can be printed
	 */
	public boolean hasPaper();

	/**
	 * Prints a message.
	 * 
	 * @param message
	 *            the message to be printed
	 */
	public void print(String message) throws NotEnoughPaperException;

}
