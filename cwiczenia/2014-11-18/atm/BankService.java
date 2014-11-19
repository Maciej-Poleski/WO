package atm;

import java.net.ConnectException;



public interface BankService {

  /**
   * Gets the account balance.
   * 
   * @param accountId
   *          the account id
   * @return the available balance in PLN
   * @throws UnsupportedOperationException
   *           operation not supported
   * @throws ConnectException
   *           failed to communicate with the service
   */
  public int getBalance(String accountId) throws UnsupportedOperationException, ConnectException;

  /**
   * Reserves funds to be withdrawn.
   * 
   * @param accountId
   *          the account id
   * @param amount
   *          the amount to be reserved in PLN
   * @throws TransactionRefusedException
   *           the specified amount is invalid / insufficient funds
   * @throws ConnectException
   *           failed to communicate with the service
   */
  public Transaction withdraw(String accountId, int amount) throws TransactionRefusedException, ConnectException;

}
