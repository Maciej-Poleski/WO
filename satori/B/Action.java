package atm;

import java.util.Map;

/**
 * Jedna instrukcja + dodatkowe ograniczenia (np maksymalna ilość wywołań)
 */
abstract class Action implements IMocksRecord, IRecordProvider {
    @Override
    public IMocksRecord getMocksRecord(Map<String, Object> branchingState) {
        return this;
    }

}
