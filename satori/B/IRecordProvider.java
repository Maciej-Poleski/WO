package atm;


interface IRecordProvider {
    IMocksRecord getMocksRecord(DecisionState branchingState);

    /**
     * Musi być wywołane przed {@code getMocksRecord}
     *
     * @param branchingState
     * @return
     */
    String nextChunk(DecisionState branchingState);
}
