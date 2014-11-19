package atm;

import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ATMImpl extends AbstractATMImpl {

  private boolean userAgreedToOperateWithoutPrints;
  private boolean isPrinterFunctional;

  public ATMImpl(BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) {
    super(bankService, cardReader, keyPad, screen, printer, dispenser);

    this.isPrinterFunctional = true;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////general_methods///////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private int query(String queryName, String[] options) {
    try {
      return screen.displayChoice(queryName, options, Config.CHOICE_TIMEOUT);
    } catch (TimeoutException e) {
      eject();
    }
    throw new AssertionError();
  }

  private boolean queryYesNo(String query) {
    return query(query, Messages.YES_NO) == 0;
  }

  private void print(String message) {
    printer.print(message);
  }

  private boolean checkUserWishesPrints() {
    boolean userWishesPrints = false;

    if (isPrinterFunctional) {
      userWishesPrints = queryYesNo(Messages.PRINT_QUERY);
    }

    return userWishesPrints;
  }

  private void eject() {
    try {
      cardReader.ejectCard(Config.EJECT_TIMEOUT);

    } catch (TimeoutException e) {
      cardReader.holdCard();
      screen.displayMessage(Messages.CARD_HELD);
    }
    throw new InnerException();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////pin_methods///////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private String getPin() throws TimeoutException {
    String pin = "";
    while (true) {
      Key selectedKey = keyPad.getKey(Config.PIN_TIMEOUT);
      switch (selectedKey) {
        case DIGIT0:
        case DIGIT1:
        case DIGIT2:
        case DIGIT3:
        case DIGIT4:
        case DIGIT5:
        case DIGIT6:
        case DIGIT7:
        case DIGIT8:
        case DIGIT9:
          pin += selectedKey.getValue();
        break;
        case CORRECT:
          pin = "";
        break;
        case CANCEL:
          eject();
        case OK:
          if (pin.length() < Config.PIN_MIN_LENGTH)
            break;
          return pin;
      }
      if (pin.length() > Config.PIN_MAX_LENGTH)
        pin = pin.substring(0, Config.PIN_MAX_LENGTH);
    }
  }

  private boolean verifyPin(Card card) {

    for (int attempts = Config.PIN_ATTEMPTS; attempts > 0; --attempts) {
      try {
        screen.displayMessage(Messages.PLEASE_ENTER_PIN);
        String pin = getPin();

        if (card.verifyPin(pin))
          return true;

        screen.displayMessage(Messages.INVALID_PIN);

      } catch (TimeoutException e) {
        eject();
      }
    }

    return false;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////withdraw_methods//////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void performWithdrawal(String accountId, boolean userWishesPrints) {

    int amount = selectAmount();

    for (int attempts = Config.CONNECT_ATTEMPTS; attempts > 0; --attempts) {
      try {
        Transaction transaction = bankService.withdraw(accountId, amount);
        Map<Nominal, Integer> banknotes = null;

        try {
          banknotes = dispenser.prepareBanknotes(amount);

        } catch (NotEnoughMoneyException e) {
          transaction.rollback();
          screen.displayMessage(Messages.OUT_OF_CASH);
          eject();
        }

        try {
          cardReader.ejectCard(Config.EJECT_TIMEOUT);

        } catch (TimeoutException e) {
          transaction.rollback();
          cardReader.holdCard();
          screen.displayMessage(Messages.CARD_HELD);
          throw new InnerException();
        }

        try {
          dispenser.dispense(banknotes);
        } catch (TimeoutException e) {
          transaction.rollback();
          throw new InnerException();
        }

        transaction.commit();

        if (userWishesPrints) {
          print(String.format(Messages.RECEIPT_FORMAT, amount));
        }

        throw new InnerException();

      } catch (TransactionRefusedException e) {
        screen.displayMessage(Messages.TRANSACTION_REFUSED);
        if (userWishesPrints) {
          print(Messages.TRANSACTION_REFUSED);
        }
        eject();

      } catch (ConnectException e) {
        continue;
      }
    }

    screen.displayMessage(Messages.CONNECTION_ERROR);
    eject();
  }

  private int selectAmount() {
    int choice = query(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS);

    if (choice == 0) {
      eject();

    } else if (choice <= ATM.AVAILABLE_AMOUNTS.length) {
      return ATM.AVAILABLE_AMOUNTS[choice - 1];

    }

    return getAmount();

  }

  private int getAmount() {
    screen.displayMessage(Messages.CHOOSE_AMOUNT);
    try {
      int currentAmount = 0;
      while (true) {
        screen.displayMessage(String.format(Messages.CURRENT_AMOUNT_FORMAT, currentAmount));
        Key selectedKey = keyPad.getKey(Config.CHOICE_TIMEOUT);
        switch (selectedKey) {
          case DIGIT0:
          case DIGIT1:
          case DIGIT2:
          case DIGIT3:
          case DIGIT4:
          case DIGIT5:
          case DIGIT6:
          case DIGIT7:
          case DIGIT8:
          case DIGIT9:
            currentAmount = 10 * currentAmount + selectedKey.getValue();
          break;
          case CORRECT:
            currentAmount /= 10;
          break;
          case CANCEL:
            eject();
          case OK:
            return currentAmount;
          default:
            throw new AssertionError();
        }
      }
    } catch (TimeoutException ex) {
      eject();
    }
    throw new AssertionError();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////balance_query_methods/////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void performBalanceQuery(String accountId, boolean userWishesPrints) {

    for (int attempts = Config.CONNECT_ATTEMPTS; attempts > 0; --attempts) {
      try {
        int balance = bankService.getBalance(accountId);
        String message = String.format(Messages.BALANCE_FORMAT, balance);
        screen.displayMessage(message);

        if (userWishesPrints) {
          print(message);
        }

        return;
      } catch (UnsupportedOperationException e) {
        screen.displayMessage(Messages.BALANCE_NOT_SUPPORTED);

        return;
      } catch (ConnectException e) {
        continue;
      }
    }

    screen.displayMessage(Messages.CONNECTION_ERROR);
    eject();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////main_methods//////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void checkPrinterIsFunctional() {
    if (isPrinterFunctional) {
      this.isPrinterFunctional = printer.hasPaper();
    }
  }

  private void requestCustomerIfContinueWhenPrinterIsNotFunctional() {

    if (!isPrinterFunctional && !userAgreedToOperateWithoutPrints && !(userAgreedToOperateWithoutPrints = queryYesNo(Messages.NO_RECEIPTS))) {
      eject();
    }
  }

  private void mainMenu(Card card) {

    switch (query(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS)) {

      case 0:
        boolean userWishesPrints = checkUserWishesPrints();
        performBalanceQuery(card.accountId(), userWishesPrints);
        checkPrinterIsFunctional();
        if (!isPrinterFunctional && !userAgreedToOperateWithoutPrints) {
          if (!(userAgreedToOperateWithoutPrints = queryYesNo(Messages.CONTINUE_QUERY_WITHOUT_PAPER))) {
            eject();
          }
        } else {
          if (!queryYesNo(Messages.CONTINUE_QUERY)) {
            eject();
          }
        }
        mainMenu(card); // recursion

      case 1:
        userWishesPrints = checkUserWishesPrints();
        performWithdrawal(card.accountId(), userWishesPrints);

      case 2:
        eject();

      default:
        throw new AssertionError("Unavailable");
    }
  }

  @Override
  public void singleSession() {
    try {
      userAgreedToOperateWithoutPrints = false;

      screen.displayMessage(Messages.INSERT_CARD);

      try {
        cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT);
      } catch (InvalidCardException ex) {
        screen.displayMessage(Messages.INVALID_CARD);
        eject();
      }

      Card card = cardReader.card();

      checkPrinterIsFunctional();
      if (!isPrinterFunctional) {
        requestCustomerIfContinueWhenPrinterIsNotFunctional();
      }

      if (!verifyPin(card)) {
        cardReader.holdCard();
        screen.displayMessage(Messages.CARD_HELD);
        return;
      }

      mainMenu(card);

    } catch (TimeoutException e) {
      // e.printStackTrace();
    } catch (InnerException e) {
      // e.printStackTrace();
    }
  }

  public class InnerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

  }

}
