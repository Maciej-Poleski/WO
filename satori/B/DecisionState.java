package atm;

class DecisionState {
    boolean isPrinterWorking;
    boolean continueWithoutPrints;
    int pinAttempts;
    boolean print;
    int numberOfAttempts;
    int numberOfAttemptsW;
    int _enterCount_6;

    DecisionState(DecisionState other) {
        isPrinterWorking = other.isPrinterWorking;
        continueWithoutPrints = other.continueWithoutPrints;
        pinAttempts = other.pinAttempts;
        print = other.print;
        numberOfAttempts = other.numberOfAttempts;
        numberOfAttemptsW = other.numberOfAttemptsW;
        _enterCount_6 = other._enterCount_6;
    }

    DecisionState() {
    }
}
