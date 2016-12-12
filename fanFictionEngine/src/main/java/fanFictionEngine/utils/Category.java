package fanFictionEngine.utils;

public enum Category {

	nocategory(0), anime(1), book(2), cartoon(3), comic(4), game(5), misc(6), play(7), movie(8) , tv(9);

	private int code;

	private Category(int code) {
		this.code = code;
	}

}
