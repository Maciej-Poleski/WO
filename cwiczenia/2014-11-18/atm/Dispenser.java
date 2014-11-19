package atm;

import java.util.Map;
import java.util.concurrent.TimeoutException;



public interface Dispenser {

  public int getThroughput();

  public Map<Nominal, Integer> getAvailable();

  public Map<Nominal, Integer> prepareBanknotes(int amount) throws NotEnoughMoneyException;

  public void dispense(Map<Nominal, Integer> nominalsToDispense) throws TimeoutException;

}
