package atm;

import java.util.concurrent.TimeoutException;



public interface CardReader {

  /**
   * Accepts and reads a card.
   * 
   * @param timeout
   *          timeout, in milliseconds
   * @throws InvalidCardException
   *           the card entered could not be read
   * @throws TimeoutException
   *           timed out waiting for a card
   */
  public void acceptCard(int timeout) throws InvalidCardException, TimeoutException;

  /**
   * Eject the current card.
   * 
   * @throws TimeoutException
   *           timed out waiting for a card to be taken
   */
  public void ejectCard(int timeout) throws TimeoutException;

  /**
   * Hold the card.
   */
  public void holdCard();

  /**
   * Card.
   * 
   * @return the Card currently in the reader (null if there is none)
   */
  public Card card();
}
