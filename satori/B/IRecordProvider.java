package atm;


import java.util.Map;

interface IRecordProvider {
    IMocksRecord getMocksRecord(Map<String, Object> branchingState);

    /**
     * Musi być wywołane przed {@code getMocksRecord}
     * @param branchingState
     * @return
     */
    String nextChunk(Map<String, Object> branchingState);
}
