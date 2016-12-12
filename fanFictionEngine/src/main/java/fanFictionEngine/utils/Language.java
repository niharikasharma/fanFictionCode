package fanFictionEngine.utils;

public enum Language {

	NoLanguage(0), English(1), Spanish(2), French(3), Portuguese(4), Chinese(5), Dutch(6), Polish(7), Indonesian(
			8), Czech(9), German(10), Latin(11), Russian(12), Hungarian(13), Japanese(14), Malay(
					15), Filipino(16), Hebrew(17), Serbian(18), Esperanto(19), Catalan(20), Italian(21), Vietnamese(
							22), Arabic(23), Afrikaans(24), Swedish(25), Finnish(26), Estonian(27), Greek(28), Romanian(
									29), Slovak(30), Bulgarian(31), Korean(32), Hindi(33), Turkish(34), Norwegian(
											35), Danish(36), Punjabi(37), Farsi(38), Croatian(39), Albanian(
													40), Icelandic(41), Thai(42), Devanagari(43), Ukrainian(44);

	private int code;

	private Language(int code) {
		this.code = code;
	}

}
