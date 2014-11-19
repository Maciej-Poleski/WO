package atm;

public enum Key {
	DIGIT0(0) {
	},
	DIGIT1(1) {
	},
	DIGIT2(2) {
	},
	DIGIT3(3) {
	},
	DIGIT4(4) {
	},
	DIGIT5(5) {
	},
	DIGIT6(6) {
	},
	DIGIT7(7) {
	},
	DIGIT8(8) {
	},
	DIGIT9(9) {
	},
	OK(-1) {
	},
	CORRECT(-2) {
	},
	CANCEL(-3) {
	};

	private Key(int value) {
		this.value = value;
	}

	private final int value;

	public int getValue() {
		return value;
	}

}
