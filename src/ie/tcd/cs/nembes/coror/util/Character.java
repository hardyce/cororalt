package ie.tcd.cs.nembes.coror.util;

public class Character {
	
	public static boolean isWhitespace(char ch){
		switch(ch){
                    case ' ':
		case '\u00A0':
		case '\u2007':
		case '\u202F':
		case '\u0009':
		case '\u000B':
		case '\u000C':
		case '\u001C':
		case '\u001D':
		case '\u001E':
		case '\u001F':
			return true;
		}
		return false;
	}

	public static boolean isDigit(char charAt) {
		return java.lang.Character.isDigit(charAt);
	}
}
