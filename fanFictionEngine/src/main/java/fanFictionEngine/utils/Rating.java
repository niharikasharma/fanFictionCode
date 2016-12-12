package fanFictionEngine.utils;

public enum Rating {
	Unknown(0), K(1), Kplus(2), M(3), T(4);

	private int code;

	private Rating(int code) {
		this.code = code;
	}

	public String getDescription(int code) {
		if (code == 1) {
			return "Content Suitable for most ages";
		} else if (code == 2) {
			return "Some content may not be suitable for young children";
		} else if (code == 3) {
			return "Contains content not suitable for children";
		} else if (code == 4) {
			return "Conatins content suitable for Mature Teens and older";
		} else {
			return "unknown";
		}

	}
}
