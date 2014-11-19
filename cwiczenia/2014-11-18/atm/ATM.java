package atm;

import java.util.ArrayList;

public interface ATM {

	public static final int[] AVAILABLE_AMOUNTS = { 20, 50, 100, 200, 300 };

	public static final String[] MAIN_MENU_OPTIONS = { //
	Messages.OPTION_GET_BALANCE, Messages.OPTION_WITHDRAW_CASH, Messages.OPTION_RETURN_CARD };

	@SuppressWarnings("serial")
	public static final String[] AMOUNT_QUERY_OPTIONS = new ArrayList<String>() {
		{
			add(Messages.CANCEL);
			for (int amount : AVAILABLE_AMOUNTS) {
				add(String.format(Messages.AMOUNT_FORMAT, amount));
			}
			add(Messages.OTHER_AMOUNT);

		}
	}.toArray(new String[] {});

	public abstract void singleSession();

}