package atm;

import java.util.Map;

class DataState {
    String accountId;
    Card card;
    String pinString;
    int amount;
    Transaction transaction;
    Map<Nominal, Integer> banknotesCollection;
    int currentAmount;
    //TestMethod testMethod;

    DataState(int name) {
        //testMethod = new TestMethod(name);
    }
}
