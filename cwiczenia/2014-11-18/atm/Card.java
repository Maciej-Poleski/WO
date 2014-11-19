package atm;

public interface Card {

	/**
	 * Checks whether a given PIN is correct.
	 * 
	 * @param pin
	 *            the PIN
	 * @return true, if correct
	 */
	public boolean verifyPin(String pin);

	/**
	 * Gets a String identifying the account associated with this card.
	 * 
	 * @return the account identifier
	 */
	public String accountId();

}
