package atm;


import org.easymock.IMocksControl;

import java.util.Map;

interface IMocksRecord {
    void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception;

}
