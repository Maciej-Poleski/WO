package atm;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static org.easymock.EasyMock.*;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ATMImplTest {

    private static final String ABORT_CHUNK = "__abort";
    private static final Random random = new Random(404);
    private final IMocksControl mocksControl;
    private final BankService bankService;
    private final CardReader cardReader;
    private final KeyPad keyPad;
    private final Screen screen;
    private final Printer printer;
    private final Dispenser dispenser;

    public ATMImplTest(IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) {
        this.mocksControl = mocksControl;
        this.bankService = bankService;
        this.cardReader = cardReader;
        this.keyPad = keyPad;
        this.screen = screen;
        this.printer = printer;
        this.dispenser = dispenser;
    }

    private static IRecordProvider makeForwardRecordProvider(final String nextChunk) {
        return new IRecordProvider() {
            @Override
            public IMocksRecord getMocksRecord(DecisionState branchingState) {
                return new IMocksRecord() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        // FALL THROUGH
                    }
                };
            }

            @Override
            public String nextChunk(DecisionState branchingState) {
                return nextChunk;
            }
        };
    }

    @Parameters
    public static Iterable<Object[]> data() throws Exception {

        final HashMap<String, Iterable<IRecordProvider>> code = new HashMap<String, Iterable<IRecordProvider>>() {
            private String makeAccount(DataState fullState) {
                if (fullState.accountId == null) {
                    String accountId = makeRandomString();
                    fullState.accountId = accountId;
                    Card card = fullState.card;
                    // samodekorujący statement
                    expect(card.accountId()).andStubReturn(accountId);
                    //fullState.testMethod.addStatement("expect(card.accountId()).andStubReturn(\"" + accountId + "\")");
                    return accountId;
                } else {
                    return fullState.accountId;
                }
            }

            {
                put("1", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        screen.displayMessage(Messages.INSERT_CARD);
                        //fullState.testMethod.addStatement("screen.displayMessage(Messages.INSERT_CARD)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "2";
                    }
                }));

                put("2", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT);
                        //fullState.testMethod.addStatement("cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "3";
                    }
                }, new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT);
                        expectLastCall().andThrow(new TimeoutException());
                        //fullState.testMethod.addStatement("cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT)");
                        //fullState.testMethod.addStatement("expectLastCall().andThrow(new TimeoutException())");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }, new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT);
                        expectLastCall().andThrow(new InvalidCardException());
                        screen.displayMessage(Messages.INVALID_CARD);
                        //fullState.testMethod.addStatement("cardReader.acceptCard(Config.CARD_ACCEPT_TIMEOUT)");
                        //fullState.testMethod.addStatement("expectLastCall().andThrow(new InvalidCardException())");
                        //fullState.testMethod.addStatement("screen.displayMessage(Messages.INVALID_CARD)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("3", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        Card card = mocksControl.createMock(Card.class);
                        fullState.card = card;
                        expect(cardReader.card()).andReturn(card);
                        //fullState.testMethod.addStatement("Card card = mocksControl.createMock(Card.class)");
                        //fullState.testMethod.addStatement("expect(cardReader.card()).andReturn(card)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "4";
                    }
                }));

                put("4", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        // FALL THROUGH
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "4.1";
                    }
                }));

                put("4.1", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(printer.hasPaper()).andReturn(true);
                                //fullState.testMethod.addStatement("expect(printer.hasPaper()).andReturn(true)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.isPrinterWorking = true;
                        return "5";
                    }
                }, new IRecordProvider() {

                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(printer.hasPaper()).andReturn(false);
                                //fullState.testMethod.addStatement("expect(printer.hasPaper()).andReturn(false)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.isPrinterWorking = false;
                        return "4.2";
                    }
                }));

                put("4.2", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        expect(screen.displayChoice(Messages.NO_RECEIPTS, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(0); // yes
                        //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.NO_RECEIPTS, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(0)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.continueWithoutPrints = true;
                        return "5";
                    }
                }, new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        expect(screen.displayChoice(Messages.NO_RECEIPTS, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(1); // no
                        //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.NO_RECEIPTS, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(1)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.continueWithoutPrints = false;
                        return "7";
                    }
                }));

                put("5", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        // FALL THROUGH
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.pinAttempts = 0;
                        return "5.1";
                    }
                }));

                put("5.1", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "5.2";
                    }

                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        screen.displayMessage(Messages.PLEASE_ENTER_PIN);
                        //fullState.testMethod.addStatement("screen.displayMessage(Messages.PLEASE_ENTER_PIN)");
                    }
                }));

                put("5.2", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "5.3";
                    }

                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        fullState.pinString = "";
                    }
                }));

                put("5.3", new Iterable<IRecordProvider>() {

                    final Key[] digits = new Key[]{Key.DIGIT0, Key.DIGIT1, Key.DIGIT2, Key.DIGIT3, Key.DIGIT4, Key.DIGIT5, Key.DIGIT6, Key.DIGIT7, Key.DIGIT8, Key.DIGIT9};

                    List<Key> generate(int length) {
                        List<Key> result = new ArrayList<>(length);
                        for (int i = 0; i < length; ++i) {
                            result.add(digits[random.nextInt(digits.length)]);
                        }
                        return result;
                    }

                    List<Key> generateEmpty(int lengthMin, int lengthMax) {
                        int length = random.nextInt(lengthMax - lengthMin + 1) + lengthMin;
                        List<Key> result = new ArrayList<>(length + 1);
                        for (int i = 0; i < length; ++i) {
                            result.add(digits[random.nextInt(digits.length)]);
                        }
                        result.add(Key.CORRECT);
                        return result;
                    }

                    List<Key> generateEmpty(int length) {
                        return generateEmpty(length, length);
                    }

                    int getShortLength() {
                        return random.nextInt(Config.PIN_MIN_LENGTH);
                    }

                    int getCorrectLength() {
                        return random.nextInt(Config.PIN_MAX_LENGTH - Config.PIN_MIN_LENGTH + 1) + Config.PIN_MIN_LENGTH;
                    }

                    int getLongLength() {
                        return random.nextInt(Config.PIN_MAX_LENGTH)+1 + Config.PIN_MAX_LENGTH;
                    }

                    int getLength(int mode) {
                        assert mode >= 0;
                        assert mode < 3;
                        if (mode == 0) {
                            return getShortLength();
                        } else if (mode == 1) {
                            return getCorrectLength();
                        } else {
                            return getLongLength();
                        }
                    }

                    @Override
                    public Iterator<IRecordProvider> iterator() {
                        List<IRecordProvider> result = new ArrayList<>();

                        for (int i = 0; i < 3; ++i) {
//                            if(i==1)
//                                continue;
// nie wyczyszczony
// wielokrotnie czyszczony
                            final List<Key> keys = generateEmpty(0, Config.PIN_MAX_LENGTH);
                            int clearCount = random.nextInt(3);
                            for (int j = 0; j < clearCount; ++j) {
                                int mode = random.nextInt(3);
                                keys.addAll(generateEmpty(getLength(mode)));
                            }
                            int effectiveLength = getLength(i);
                            keys.addAll(generate(effectiveLength));
                            for (int j = 0; j < 3; ++j) {
                                if (j == 0) {
                                    result.add(new Action() {
                                        @Override
                                        public String nextChunk(DecisionState branchingState) {
                                            return "7";
                                        }

                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            for (Key key : keys) {
                                                expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(key);
                                                //fullState.testMethod.addStatement("expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(Key." + key.name() + ")");
                                            }
                                            expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(Key.CANCEL);
                                            //fullState.testMethod.addStatement("expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(Key.CANCEL)");
                                        }
                                    });
                                } else if (j == 1) {
                                    result.add(new Action() {
                                        @Override
                                        public String nextChunk(DecisionState branchingState) {
                                            return "7";
                                        }

                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            for (Key key : keys) {
                                                expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(key);
                                                //fullState.testMethod.addStatement("expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(Key." + key.name() + ")");
                                            }
                                            expect(keyPad.getKey(Config.PIN_TIMEOUT)).andThrow(new TimeoutException());
                                            //fullState.testMethod.addStatement("expect(keyPad.getKey(Config.PIN_TIMEOUT)).andThrow(new TimeoutException())");
                                        }
                                    });
                                } else {
                                    final List<Key> keysWithOk = new ArrayList<>(keys);
                                    keysWithOk.add(Key.OK);
                                    String pin;
                                    if (effectiveLength < Config.PIN_MIN_LENGTH) {
                                        int additionalLength = random.nextInt(Config.PIN_MIN_LENGTH + 1) + Config.PIN_MIN_LENGTH - effectiveLength;
                                        keysWithOk.addAll(generate(additionalLength));
                                        keysWithOk.add(Key.OK);
                                        StringBuilder pinString = new StringBuilder();
                                        for (int k = keysWithOk.size() - effectiveLength - additionalLength - 2; k < keysWithOk.size() - 1; ++k) {
                                            Key key = keysWithOk.get(k);
                                            if (key.getValue() < 0)
                                                continue;
                                            pinString.append(Character.forDigit(key.getValue(), 10));
                                        }
                                        pin = pinString.toString();
                                    } else {
                                        StringBuilder pinString = new StringBuilder();
                                        for (int k = keysWithOk.size() - effectiveLength - 1; k < keysWithOk.size() - 1; ++k) {
                                            Key key = keysWithOk.get(k);
                                            pinString.append(Character.forDigit(key.getValue(), 10));
                                        }
                                        pin = pinString.toString();
                                    }
                                    final String pinForAction = pin.substring(0, Math.min(Config.PIN_MAX_LENGTH, pin.length()));
                                    result.add(new Action() {
                                        @Override
                                        public String nextChunk(DecisionState branchingState) {
                                            return "5.4";
                                        }

                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            fullState.pinString = pinForAction;
                                            for (Key key : keysWithOk) {
                                                expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(key);
                                                //fullState.testMethod.addStatement("expect(keyPad.getKey(Config.PIN_TIMEOUT)).andReturn(Key." + key.name() + ")");
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        return result.iterator();
                    }
                });

                put("5.4", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6";
                    }

                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        Card card = fullState.card;
                        String pinString = fullState.pinString;
                        expect(card.verifyPin(pinString)).andReturn(true);
                        //fullState.testMethod.addStatement("expect(card.verifyPin(\"" + pinString + "\")).andReturn(true)");
                    }
                }, new Action() {
                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "5.5";
                    }

                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        Card card = fullState.card;
                        String pinString = fullState.pinString;
                        expect(card.verifyPin(pinString)).andReturn(false);
                        //fullState.testMethod.addStatement("expect(card.verifyPin(\"" + pinString + "\")).andReturn(false)");
                    }
                }));

                put("5.5", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "5.6";
                    }

                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        screen.displayMessage(Messages.INVALID_PIN);
                        //fullState.testMethod.addStatement("screen.displayMessage(Messages.INVALID_PIN)");
                    }
                }));

                put("5.6", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        final int pinAttempts = branchingState.pinAttempts;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                if (pinAttempts == Config.PIN_ATTEMPTS) {
                                    cardReader.holdCard();
                                    screen.displayMessage(Messages.CARD_HELD);
                                    //fullState.testMethod.addStatement("cardReader.holdCard()");
                                    //fullState.testMethod.addStatement("screen.displayMessage(Messages.CARD_HELD)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int pinAttempts = branchingState.pinAttempts;
                        pinAttempts++;
                        branchingState.pinAttempts = pinAttempts;
                        if (pinAttempts < Config.PIN_ATTEMPTS) {
                            return "5.1";
                        }
                        if (pinAttempts == Config.PIN_ATTEMPTS) {
                            return "8";
                        }
                        throw new AssertionError("Wartość pinAttempts przeskoczyła granice");
                    }
                }));

                put("6", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                // FALL THROUGH
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int count = branchingState._enterCount_6;
                        branchingState._enterCount_6 = count + 1;
                        if (count < 2) {
                            return "6.1";
                        } else {
                            return ABORT_CHUNK;
                        }
                    }
                }));

                // nie skończone
                put("6.1", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        branchingState.numberOfAttempts = 0;
                        branchingState.numberOfAttemptsW = 0;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_GET_BALANCE));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_GET_BALANCE) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.a";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_WITHDRAW_CASH));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_WITHDRAW_CASH) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_RETURN_CARD));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.MAIN_MENU_OPTIONS, Messages.OPTION_RETURN_CARD) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.MAIN_MENU_QUERY, ATM.MAIN_MENU_OPTIONS, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.a", Arrays.asList(makeForwardRecordProvider("6.1.a.1")));

                put("6.1.a.1", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                // FALL THROUGH
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        boolean isPrinterWorking = (boolean) branchingState.isPrinterWorking;
                        if (!isPrinterWorking) {
                            branchingState.print = false;
                            return "6.1.a.3";
                        } else {
                            return "6.1.a.2";
                        }
                    }
                }));

                put("6.1.a.2", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        branchingState.print = true;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.YES));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.YES) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.a.3";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        branchingState.print = false;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.NO));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.NO) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.a.3";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.a.3", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(final DecisionState branchingState) {
                        final boolean print = (boolean) branchingState.print;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                int balance = 1234;
                                expect(bankService.getBalance(accountId)).andReturn(balance);
                                //fullState.testMethod.addStatement("expect(bankService.getBalance(\"" + accountId + "\")).andReturn(" + balance + ")");
                                screen.displayMessage(String.format(Messages.BALANCE_FORMAT, balance));
                                //fullState.testMethod.addStatement("screen.displayMessage(String.format(Messages.BALANCE_FORMAT, " + balance + "))");
                                if (print) {
                                    printer.print(String.format(Messages.BALANCE_FORMAT, balance));
                                    //fullState.testMethod.addStatement("printer.print(String.format(Messages.BALANCE_FORMAT, " + balance + "))");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttempts;
                        if (attempts % 2 == 1)
                            return ABORT_CHUNK;
                        return "6.1.a.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                expect(bankService.getBalance(accountId)).andThrow(new UnsupportedOperationException());
                                //fullState.testMethod.addStatement("expect(bankService.getBalance(\"" + accountId + "\")).andThrow(new UnsupportedOperationException())");
                                screen.displayMessage(Messages.BALANCE_NOT_SUPPORTED);
                                //fullState.testMethod.addStatement("screen.displayMessage(Messages.BALANCE_NOT_SUPPORTED)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttempts;
                        if (attempts % 2 == 1)
                            return ABORT_CHUNK;
                        return "6.1.a.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(final DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttempts;
                        final int finalAttempts = attempts + 1;
                        branchingState.numberOfAttempts = finalAttempts;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                expect(bankService.getBalance(accountId)).andThrow(new ConnectException());
                                //fullState.testMethod.addStatement("expect(bankService.getBalance(\"" + accountId + "\")).andThrow(new ConnectException())");
                                if (finalAttempts >= Config.CONNECT_ATTEMPTS) {
                                    screen.displayMessage(Messages.CONNECTION_ERROR);
                                    //fullState.testMethod.addStatement("screen.displayMessage(Messages.CONNECTION_ERROR)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttempts;
                        attempts += 1;
                        if (attempts < Config.CONNECT_ATTEMPTS) {
                            return "6.1.a.3";
                        } else {
                            return "7";
                        }
                    }
                }));

                put("6.1.a.4", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(final DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                if (branchingState.isPrinterWorking) {
                                    expect(printer.hasPaper()).andReturn(true);
                                    //fullState.testMethod.addStatement("expect(printer.hasPaper()).andReturn(true)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        if (branchingState.isPrinterWorking) {
                            return "6.1.a.7";
                        } else {
                            return "6.1.a.5";
                        }
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(final DecisionState branchingState) {
                        final boolean isPrinterWorking = branchingState.isPrinterWorking;
                        branchingState.isPrinterWorking = false;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                if (isPrinterWorking) {
                                    expect(printer.hasPaper()).andReturn(false);
                                    //fullState.testMethod.addStatement("expect(printer.hasPaper()).andReturn(false)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.a.5";
                    }
                }));

                // może wymagać poprawy
                put("6.1.a.5", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                // FALL THROUGH
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        boolean userAgreedToOperateWithoutPrints = branchingState.continueWithoutPrints;
                        if (userAgreedToOperateWithoutPrints) {
                            return "6.1.a.7";
                        } else {
                            return "6.1.a.6";
                        }
                    }
                }));

                put("6.1.a.6", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.YES));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.YES) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        branchingState.continueWithoutPrints = true;
                        return "6";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.NO));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.NO) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY_WITHOUT_PAPER, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.a.7", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.YES));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.YES) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.NO));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.NO) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.b", Arrays.<IRecordProvider>asList(makeForwardRecordProvider("6.1.b.1")));

                put("6.1.b.1", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                makeAccount(fullState);
                                // FALL THROUGH
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        boolean isPrinterWorking = (boolean) branchingState.isPrinterWorking;
                        if (!isPrinterWorking) {
                            branchingState.print = false;
                            return "6.1.b.3";
                        } else {
                            return "6.1.b.2";
                        }
                    }
                }));

                put("6.1.b.2", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        branchingState.print = true;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.YES));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.YES) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.3";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        branchingState.print = false;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(Messages.YES_NO, Messages.NO));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(Messages.YES_NO, Messages.NO) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.3";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.PRINT_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.CONTINUE_QUERY, Messages.YES_NO, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.b.3", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, Messages.CANCEL));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, Messages.CANCEL) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.amount = 20;
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 20)));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 20)) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.amount = 50;
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 50)));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 50)) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.amount = 100;
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 100)));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 100)) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.amount = 200;
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 200)));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 200)) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.amount = 300;
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 300)));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, String.format(Messages.AMOUNT_FORMAT, 300)) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.4";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(dequeryize(ATM.AMOUNT_QUERY_OPTIONS, Messages.OTHER_AMOUNT));
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andReturn(" + dequeryize(ATM.AMOUNT_QUERY_OPTIONS, Messages.OTHER_AMOUNT) + ")");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.3.c.1";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                //fullState.testMethod.addStatement("expect(screen.displayChoice(Messages.AMOUNT_QUERY, ATM.AMOUNT_QUERY_OPTIONS, Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException())");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }));

                put("6.1.b.3.c.1", Collections.<IRecordProvider>singletonList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                screen.displayMessage(Messages.CHOOSE_AMOUNT);
                                fullState.currentAmount = 0;
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.3.c.LOOP";
                    }
                }));

                put("6.1.b.3.c.LOOP", new Iterable<IRecordProvider>() {
                    private final int[] interestingAmountLengths = new int[]{0, 1, 2, 6, 13};

                    List<IRecordProvider> result = new ArrayList<>();

                    private boolean wantCorrect() {
                        return random.nextInt(10) < 4;
                    }

                    @Override
                    public Iterator<IRecordProvider> iterator() {
                        for (final int amountLength : interestingAmountLengths) {
                            result.add(new IRecordProvider() {
                                @Override
                                public IMocksRecord getMocksRecord(DecisionState branchingState) {
                                    return new IMocksRecord() {
                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CORRECT);
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CORRECT);
                                            makeSomeNumber(fullState, keyPad, screen, amountLength);
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CANCEL);
                                        }
                                    };
                                }

                                @Override
                                public String nextChunk(DecisionState branchingState) {
                                    return "7"; // CANCEL
                                }
                            });
                            result.add(new IRecordProvider() {
                                @Override
                                public IMocksRecord getMocksRecord(DecisionState branchingState) {
                                    return new IMocksRecord() {
                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CORRECT);
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CORRECT);
                                            makeSomeNumber(fullState, keyPad, screen, amountLength);
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.OK);
                                            fullState.amount = fullState.currentAmount;
                                        }
                                    };
                                }

                                @Override
                                public String nextChunk(DecisionState branchingState) {
                                    return "6.1.b.4";   // OK
                                }
                            });
                            result.add(new IRecordProvider() {
                                @Override
                                public IMocksRecord getMocksRecord(DecisionState branchingState) {
                                    return new IMocksRecord() {
                                        @Override
                                        public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                            makeSomeNumber(fullState, keyPad, screen, amountLength);
                                            expectCurrentAmount(fullState, screen);
                                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andThrow(new TimeoutException());
                                        }
                                    };
                                }

                                @Override
                                public String nextChunk(DecisionState branchingState) {
                                    return "7";
                                }
                            });
                        }

                        return result.iterator();
                    }

                    private void expectCurrentAmount(DataState fullState, Screen screen) {
                        screen.displayMessage(String.format(Messages.CURRENT_AMOUNT_FORMAT, fullState.currentAmount));
                    }

                    private void makeSomeNumber(DataState fullState, KeyPad keyPad, Screen screen, int amountLength) throws TimeoutException {
                        final Key[] digits = new Key[]{Key.DIGIT0, Key.DIGIT1, Key.DIGIT2, Key.DIGIT3, Key.DIGIT4, Key.DIGIT5, Key.DIGIT6, Key.DIGIT7, Key.DIGIT8, Key.DIGIT9};
                        for (int i = 0; i < amountLength; ++i) {
                            int key = random.nextInt(10);
                            expectCurrentAmount(fullState, screen);
                            expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(digits[key]);
                            fullState.currentAmount = fullState.currentAmount * 10 + key;
                            if (wantCorrect()) {
                                expectCurrentAmount(fullState, screen);
                                expect(keyPad.getKey(Config.CHOICE_TIMEOUT)).andReturn(Key.CORRECT);
                                i -= 1;
                                fullState.currentAmount /= 10;
                            }
                        }
                    }
                });

                put("6.1.b.4", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        final boolean print = (boolean) branchingState.print;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                int amount = fullState.amount;
                                expect(bankService.withdraw(accountId, amount)).andThrow(new TransactionRefusedException());
                                //fullState.testMethod.addStatement("expect(bankService.withdraw(\"" + accountId + "\", " + amount + ")).andThrow(new TransactionRefusedException())");
                                screen.displayMessage(Messages.TRANSACTION_REFUSED);
                                //fullState.testMethod.addStatement("screen.displayMessage(Messages.TRANSACTION_REFUSED)");
                                if (print) {
                                    printer.print(Messages.TRANSACTION_REFUSED);
                                    //fullState.testMethod.addStatement("printer.print(Messages.TRANSACTION_REFUSED)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttemptsW;
                        if (attempts % 2 == 1)
                            return ABORT_CHUNK;
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(final DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttemptsW;
                        final int finalAttempts = attempts + 1;
                        branchingState.numberOfAttemptsW = finalAttempts;
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                int amount = fullState.amount;
                                expect(bankService.withdraw(accountId, amount)).andThrow(new ConnectException());
                                //fullState.testMethod.addStatement("expect(bankService.withdraw(\"" + accountId + "\", " + amount + ")).andThrow(new ConnectException())");
                                if (finalAttempts >= Config.CONNECT_ATTEMPTS) {
                                    screen.displayMessage(Messages.CONNECTION_ERROR);
                                    //fullState.testMethod.addStatement("screen.displayMessage(Messages.CONNECTION_ERROR)");
                                }
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttemptsW;
                        attempts += 1;
                        if (attempts < Config.CONNECT_ATTEMPTS) {
                            return "6.1.b.4";
                        } else {
                            return "7";
                        }
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                String accountId = makeAccount(fullState);
                                int amount = fullState.amount;
                                Transaction transaction = mocksControl.createMock(Transaction.class);
                                //fullState.testMethod.addStatement("Transaction transaction = mocksControl.createMock(Transaction.class)");
                                fullState.transaction = transaction;
                                expect(bankService.withdraw(accountId, amount)).andReturn(transaction);
                                //fullState.testMethod.addStatement("expect(bankService.withdraw(\"" + accountId + "\", " + amount + ")).andReturn(transaction)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        int attempts = branchingState.numberOfAttemptsW;
                        if (attempts % 2 == 1)
                            return ABORT_CHUNK;
                        return "6.1.b.5";
                    }
                }));

                put("6.1.b.5", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                int amount = fullState.amount;
                                expect(dispenser.prepareBanknotes(amount)).andThrow(new NotEnoughMoneyException());
                                //fullState.testMethod.addStatement("expect(dispenser.prepareBanknotes(" + amount + ")).andThrow(new NotEnoughMoneyException())");
                                Transaction transaction = fullState.transaction;
                                transaction.rollback();
                                //fullState.testMethod.addStatement("transaction.rollback()");
                                screen.displayMessage(Messages.OUT_OF_CASH);
                                //fullState.testMethod.addStatement("screen.displayMessage(Messages.OUT_OF_CASH)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                Map<Nominal, Integer> banknotesCollection = mocksControl.createMock(Map.class);
                                fullState.banknotesCollection = banknotesCollection;
                                //fullState.testMethod.addStatement("Map<Nominal, Integer> banknotesCollection = mocksControl.createMock(Map.class)");
                                int amount = fullState.amount;
                                expect(dispenser.prepareBanknotes(amount)).andReturn(banknotesCollection);
                                //fullState.testMethod.addStatement("expect(dispenser.prepareBanknotes(" + amount + ")).andReturn(banknotesCollection)");
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.6";
                    }
                }));

                put("6.1.b.6", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                cardReader.ejectCard(Config.EJECT_TIMEOUT);
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.7";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                cardReader.ejectCard(Config.EJECT_TIMEOUT);
                                expectLastCall().andThrow(new TimeoutException());
                                fullState.transaction.rollback();
                                cardReader.holdCard();
                                screen.displayMessage(Messages.CARD_HELD);
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }));

                put("6.1.b.7", Arrays.<IRecordProvider>asList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                dispenser.dispense(fullState.banknotesCollection);
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.8";
                    }
                }, new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                dispenser.dispense(fullState.banknotesCollection);
                                expectLastCall().andThrow(new TimeoutException());
                                fullState.transaction.rollback();
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }));

                put("6.1.b.8", Collections.<IRecordProvider>singletonList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                fullState.transaction.commit();
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "6.1.b.9";
                    }
                }));

                put("6.1.b.9", Collections.<IRecordProvider>singletonList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                // FALL THROUGH
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        if (branchingState.print) {
                            return "6.1.b.10";
                        } else {
                            return "8";
                        }
                    }
                }));

                put("6.1.b.10", Collections.<IRecordProvider>singletonList(new IRecordProvider() {
                    @Override
                    public IMocksRecord getMocksRecord(DecisionState branchingState) {
                        return new IMocksRecord() {
                            @Override
                            public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                                printer.print(String.format(Messages.RECEIPT_FORMAT, fullState.amount));
                            }
                        };
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }));

                put("7", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        cardReader.ejectCard(Config.EJECT_TIMEOUT);
                        //fullState.testMethod.addStatement("cardReader.ejectCard(Config.EJECT_TIMEOUT)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }, new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        cardReader.ejectCard(Config.EJECT_TIMEOUT);
                        //fullState.testMethod.addStatement("cardReader.ejectCard(Config.EJECT_TIMEOUT)");
                        expectLastCall().andThrow(new TimeoutException());
                        //fullState.testMethod.addStatement("expectLastCall().andThrow(new TimeoutException())");
                        cardReader.holdCard();
                        //fullState.testMethod.addStatement("cardReader.holdCard()");
                        screen.displayMessage(Messages.CARD_HELD);
                        //fullState.testMethod.addStatement("screen.displayMessage(Messages.CARD_HELD)");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return "8";
                    }
                }));

                put("8", Arrays.<IRecordProvider>asList(new Action() {
                    @Override
                    public void record(DataState fullState, IMocksControl mocksControl, BankService bankService, CardReader cardReader, KeyPad keyPad, Screen screen, Printer printer, Dispenser dispenser) throws Exception {
                        //fullState.testMethod.addStatement("mocksControl.replay()");
                    }

                    @Override
                    public String nextChunk(DecisionState branchingState) {
                        return null;
                    }
                }));
            }


        };

        final Iterable<Iterable<IMocksRecord>> testsRecords = generateMocksForTests(code);

        return new Iterable<Object[]>() {
            @Override
            public Iterator<Object[]> iterator() {
                final Iterator<Iterable<IMocksRecord>> testsIterator = testsRecords.iterator();
                return new Iterator<Object[]>() {
                    //                        PrintStream printStream = new PrintStream(new FileOutputStream("ATMImplTest.java")) {
//                            {
//                                String contents = new String(Files.readAllBytes(Paths.get("testH.txt")), StandardCharsets.UTF_8);
//                                println(contents);
//                            }
//                        };
                    private int size = 0;

                    @Override
                    public boolean hasNext() {
                        if (testsIterator.hasNext())
                            return true;
                        System.err.println("Wygenerowano testów: " + size);
//                            printStream.println('}');
//                            printStream.close();
                        return false;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }

                    @Override
                    public Object[] next() {
                        IMocksControl mocksControl = createStrictControl();
                        BankService bankService = mocksControl.createMock(BankService.class);
                        CardReader cardReader = mocksControl.createMock(CardReader.class);
                        KeyPad keyPad = mocksControl.createMock(KeyPad.class);
                        Screen screen = mocksControl.createMock(Screen.class);
                        Printer printer = mocksControl.createMock(Printer.class);
                        Dispenser dispenser = mocksControl.createMock(Dispenser.class);
                        DataState fullState = new DataState(size);
                        for (IMocksRecord mockRecord : testsIterator.next()) {
                            try {
                                mockRecord.record(fullState, mocksControl, bankService, cardReader, keyPad, screen, printer, dispenser);
                            } catch (Exception e) {
                                throw new AssertionError("Wygląda na to, że poleciał wyjątek z mock-a w fazie nagrywania...", e);
                            }
                        }
                        size++;
                        //fullState.testMethod.dump(printStream);
                        return new Object[]{mocksControl, bankService, cardReader, keyPad, screen, printer, dispenser};
                    }
                };
            }
        };
    }

    private static int dequeryize(String[] options, String selectedOption) {
        for (int i = 0; i < options.length; ++i) {
            if (options[i].equals(selectedOption))
                return i;
        }
        throw new AssertionError("Nie ma " + selectedOption + " w " + options);
    }

    private static Iterable<Iterable<IMocksRecord>> generateMocksForTests(final Map<String, Iterable<IRecordProvider>> code) {

        Callable<Iterable<Iterable<IMocksRecord>>> result = new Callable<Iterable<Iterable<IMocksRecord>>>() {
            final Collection<Iterable<IMocksRecord>> result = new ArrayList<>();
            final List<IMocksRecord> records = new ArrayList<>();
            final Map<String, Integer> reenterLevel = new HashMap<>();
            final Set<String> forbiddenChunks = new HashSet<>(Arrays.asList("6.1.a"));
            final Set<String> oneTrackChunks = new HashSet<>(Arrays.asList("6", "6.1.a"));

            private void generateMocksForTests1(String currentChunk, DecisionState branchingState, int depth) {
                if (currentChunk == null) {
                    result.add(new ArrayList<>(records));
                    return;
                } else if (ABORT_CHUNK.equals(currentChunk)) {
                    return;
                } else if (forbiddenChunks.contains(currentChunk)) {
                    return;
                }
                final int currentReenterLevel = (reenterLevel.containsKey(currentChunk) ? reenterLevel.get(currentChunk) : 0) + 1;
                reenterLevel.put(currentChunk, currentReenterLevel);
                Iterable<IRecordProvider> actions = code.get(currentChunk);
                if (actions == null) {
                    System.err.println("Niekompletna ścieżka wykonania: nie istnieje fragment " + currentChunk);
                } else {
                    for (IRecordProvider action : actions) {
                        DecisionState newState = new DecisionState(branchingState);
                        String nextChunk = action.nextChunk(newState);
                        records.add(action.getMocksRecord(newState));
                        generateMocksForTests1(nextChunk, newState, depth + 1);
                        records.remove(records.size() - 1);
                    }
                }
                if (oneTrackChunks.contains(currentChunk) && currentReenterLevel == 1) {
                    forbiddenChunks.add(currentChunk);
                }
                reenterLevel.put(currentChunk, currentReenterLevel - 1);
            }

            @Override
            public Iterable<Iterable<IMocksRecord>> call() throws Exception {
                generateMocksForTests1("1", new DecisionState(), 0);
                return result;
            }
        };

        try {
            return result.call();
        } catch (Exception e) {
            throw new AssertionError("Wyjątek podczas generowania testów (backtracking)", e);
        }
    }

    private static String makeRandomString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            char n = (char) random.nextInt(256);
            if (!Character.isLetterOrDigit(n)) {
                i -= 1;
                continue;
            }
            sb.append(n);
        }
        return sb.toString();
    }

    @Before
    public void setUp() throws Exception {
        mocksControl.replay();
    }

    @After
    public void tearDown() throws Exception {
        mocksControl.verify();
    }

    @Test
    public void testSingleSession() throws Exception {
        ATMImpl atmImpl = new ATMImpl(bankService, cardReader, keyPad, screen, printer, dispenser);
        atmImpl.singleSession();
    }
}