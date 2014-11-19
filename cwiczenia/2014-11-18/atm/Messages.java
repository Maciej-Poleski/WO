package atm;

public class Messages {
	public static final String INSERT_CARD = "Proszę włożyć kartę";
	public static final String INVALID_PIN = "Nieprawidłowy kod PIN";
	public static final String CARD_HELD = "Karta została zatrzymana. Prosimy skontaktować się z operatorem.";

	public static final String MAIN_MENU_QUERY = "Wybierz jedną z operacji";
	public static final String OPTION_GET_BALANCE = "Sprawdzenie stanu konta";
	public static final String OPTION_WITHDRAW_CASH = "Wypłata gotówki";
	public static final String OPTION_RETURN_CARD = "Zwrot karty";

	public static final String CONTINUE_QUERY = "Czy chcesz kontynuować?";
	public static final String CONTINUE_QUERY_WITHOUT_PAPER = "Skończył się papier w drukarce. Czy chcesz kontynuować?";
	public static final String NO_RECEIPTS = "Drukowanie niedostępne. Czy chcesz kontynuować?";

	public static final String YES = "Tak";
	public static final String NO = "Nie";
	public static final String CANCEL = "Anuluj";

	public static final String AMOUNT_QUERY = "Wybierz żądaną kwotę:";
	public static final String AMOUNT_FORMAT = "%d PLN";
	public static final String OTHER_AMOUNT = "Inna kwota";
	public static final String CURRENT_AMOUNT_FORMAT = "Wybrana kwota: %d PLN";
	public static final String RECEIPT_FORMAT = "Wypłacono kwotę %d PLN.";

	public static final String BALANCE_FORMAT = "Stan Twojego konta to %d PLN.";
	public static final String BALANCE_NOT_SUPPORTED = "Nie można sprawdzić stanu konta: operacja nieobsługiwana przez bank.";

	public static final String CONNECTION_ERROR = "System bankowy niedostępny. Przepraszamy.";
	public static final String TRANSACTION_REFUSED = "Transakcja odrzucona przez system bankowy.";
	public static final String OUT_OF_CASH = "Brak wystarczających środków w bankomacie";

	public static final String PRINT_QUERY = "Czy wydrukować potwierdzenie transakcji?";

	public static final String[] YES_NO = { YES, NO };
	public static final String CHOOSE_AMOUNT = "Wprowadź żądaną kwotę.";
	public static final String INVALID_CARD = "Nieobsługiwany typ karty";
	public static final String PLEASE_ENTER_PIN = "Podaj kod PIN";
	public static final String CONTACT_ATM_OPERATOR = "Wystąpił problem - skontaktuj się z operatorem bankomatu";
}
