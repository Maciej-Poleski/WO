package atm;

public class Config {

	public static final int CARD_ACCEPT_TIMEOUT = 1 << 2;
	public static final int PIN_TIMEOUT = 1 << 3;
	public static final int CHOICE_TIMEOUT = 1 << 4;
	public static final int CONNECT_TIMEOUT = 1 << 5;
	public static final int DISPENSE_TIMEOUT = 1 << 6;
	public static final int EJECT_TIMEOUT = 1 << 7;

	public static int PIN_ATTEMPTS = 3;
	public static int CONNECT_ATTEMPTS = 5;

	public static int PIN_MIN_LENGTH = 4;
	public static int PIN_MAX_LENGTH = 6;



}
