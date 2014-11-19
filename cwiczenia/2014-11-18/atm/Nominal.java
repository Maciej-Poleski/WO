package atm;

public enum Nominal {

	PLN200(200) {
	},

	PLN100(100) {
	},

	PLN50(50) {
	},

	PLN20(20) {
	};

	private int value;

	private Nominal(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
