package atm;

public abstract class AbstractATMImpl implements ATM {

  protected final BankService bankService;
  protected final CardReader cardReader;
  protected final KeyPad keyPad;
  protected final Screen screen;
  protected final Printer printer;
  protected final Dispenser dispenser;

  public AbstractATMImpl(BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) {
    this.bankService = bankService;
    this.cardReader = cardReader;
    this.keyPad = keyPad;
    this.screen = screen;
    this.printer = printer;
    this.dispenser = dispenser;
  }

}
