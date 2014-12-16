package atm;

/**
 * Jedna instrukcja + dodatkowe ograniczenia (np maksymalna ilość wywołań)
 */
abstract class Action implements IMocksRecord, IRecordProvider {
    @Override
    public IMocksRecord getMocksRecord(DecisionState branchingState) {
        return this;
    }

}
