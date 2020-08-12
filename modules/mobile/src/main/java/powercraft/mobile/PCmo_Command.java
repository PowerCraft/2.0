package powercraft.mobile;

import java.util.Random;

import powercraft.launcher.PC_Logger;

/**
 * Class for miner's direct program parsing
 * 
 * @author MightyPork
 */
public class PCmo_Command {

	private static final int COUNT = 20;

	private static String[] names = new String[COUNT];
	private static char[] chars = new char[COUNT];

	private static Random random = new Random();

	static {
		names[0] = "FORWARD";
		names[1] = "TURN_LEFT";
		names[2] = "TURN_RIGHT";
		names[3] = "BACKWARD";

		names[4] = "FORWARD";
		names[5] = "TURN_LEFT";
		names[6] = "TURN_RIGHT";
		names[7] = "BACKWARD";

		names[8] = "MINE_DOWN";
		names[9] = "MINE_UP";

		names[10] = "DEPOSIT";
		names[11] = "TO_BLOCKS";

		names[12] = "MINING_ENABLE";
		names[13] = "MINING_DISABLE";

		names[14] = "BRIDGE_ENABLE";
		names[15] = "BRIDGE_DISABLE";

		names[16] = "LAVA_ENABLE";
		names[17] = "LAVA_DISABLE";

		names[18] = "WATER_ENABLE";
		names[19] = "WATER_DISABLE";

		// free
		// ACHKMTVYZ/+_$#
		//
		// used
		// FLRB SNEW DUQX*%-123456789@:=() OP []IJ

		chars[0] = 'F';
		chars[1] = 'L';
		chars[2] = 'R';
		chars[3] = 'B';

		chars[4] = 'S';
		chars[5] = 'N';
		chars[6] = 'E';
		chars[7] = 'W';

		chars[8] = 'D';
		chars[9] = 'U';
		chars[10] = 'Q';
		chars[11] = 'X';

		chars[12] = '*';
		chars[13] = '%';

		chars[14] = 'O';
		chars[15] = 'P';

		chars[16] = 'I';
		chars[17] = 'J';

		chars[18] = 'Y';
		chars[19] = 'Z';

	}

	@SuppressWarnings("javadoc")
	public static final int FORWARD = 0, LEFT = 1, RIGHT = 2, BACKWARD = 3, SOUTH = 4, NORTH = 5, EAST = 6, WEST = 7,
			DOWN = 8, UP = 9, DEPOSIT = 10, DISASSEMBLY = 11, MINING_ENABLE = 12, MINING_DISABLE = 13,
			BRIDGE_ENABLE = 14, BRIDGE_DISABLE = 15, LAVA_ENABLE = 16, LAVA_DISABLE = 17, WATER_ENABLE = 18,
			WATER_DISABLE = 19;

	// direct commands
	@SuppressWarnings("javadoc")
	public static final int RESET = -2, RUN_PROGRAM = -3;

	/**
	 * is command with this id turning commans?
	 * 
	 * @param i id of the command
	 * @return is turning
	 */
	public static boolean isCommandTurn(int i) {
		return i == LEFT || i == RIGHT;
	}

	/**
	 * is command with this id movement commans?
	 * 
	 * @param i id of the command
	 * @return is movement
	 */
	public static boolean isCommandMove(int i) {
		return i == FORWARD || i == BACKWARD;
	}

	/**
	 * is command with this id up/down commans?
	 * 
	 * @param i id of the command
	 * @return is up/down
	 */
	public static boolean isCommandVertical(int i) {
		return i == UP || i == DOWN;
	}

	/**
	 * is command with this id turn do world's direction (SNEW)?
	 * 
	 * @param i id of the command
	 * @return is world side turn
	 */
	public static boolean isCommandCompass(int i) {
		return i == SOUTH || i == NORTH || i == EAST || i == WEST;
	}

	/**
	 * Is the id a flag change command?
	 * 
	 * @param i command id
	 * @return is flag command
	 */
	public static boolean isCommandOption(int i) {
		return i >= 12;
	}

//	/**
//	 * Convert int to string representation of the command.
//	 * 
//	 * @param num command id
//	 * @return command as string (one character long)
//	 */
//	private static String getNameFromInt(int num) {
//		if (num < 0 || num >= COUNT) {
//			return "BAD_CMD";
//		}
//		return names[num];
//	}

	/**
	 * Convert the command int to a character
	 * 
	 * @param num command id
	 * @return as character
	 */
	public static char getCharFromInt(int num) {
		if (num < 0 || num >= COUNT) {
			return '?';
		}
		return chars[num];
	}

	/**
	 * Get id of a command
	 * 
	 * @param chr command as Character
	 * @return command id
	 */
	public static int getIntFromChar(char chr) {
		for (int i = 0; i < COUNT; i++) {
			if (chars[i] == chr) {
				return i;
			}
		}
		return -1;
	}

	private static String parseEvaluateVars(String input) throws ParseException {
		if (input.indexOf("<") == -1) {
			if (input.indexOf(">") != -1) {
				throw new ParseException("Invalid var field syntax.");
			}

			return input; // no vars found, plain code.
		}

		if (input.indexOf(">") == -1) {
			throw new ParseException("Invalid var field syntax.");
		}

		// find and evaluate all pieces.

		int pos = 0;
		int pos2 = 0;
		int lastVarEnd = 0;
		String output = "";

		String piece = "";

		while (true) {
			pos = input.indexOf("<", lastVarEnd);
			pos2 = input.indexOf(">", pos);

			if (pos < 0 || pos2 < 0) {
				output += input.substring(lastVarEnd, input.length());
				break;
			}
			output += input.substring(lastVarEnd, pos); // add normal code to
														// output

			piece = input.substring(pos, pos2 + 1);
			lastVarEnd = pos2 + 1; // first normal letter

			// throws exception if invalid var code
			int var = parseSingleVar(piece, false);
			if (var > 0) {
				output += "," + Integer.toString(var) + ",";
			}
		}

		return output;

	}

	private static int parseLoopCount(String code) throws ParseException {
		try {
			if (!Character.isDigit(Character.valueOf(code.charAt(0))) && code.charAt(0) != '-') {
				throw new NumberFormatException();
			}
			int n = Integer.valueOf(code);
			if (n > 0) {
				return n;
			}
			throw new ParseException("Invalid loop count.");
		} catch (NumberFormatException nfe) {
			try {
				return parseSingleVar(code, true);
			} catch (ParseException ce) {
				throw ce;
			}
		}
	}

	private static int parseSingleVar(String code, boolean requireValue) throws ParseException {
		String local = new String(code);

		if (code == null) {
			throw new ParseException("parseSingleVar: NullPointer");
		}
		if (code.charAt(0) == '<' && code.charAt(code.length() - 1) == '>') {
			local = local.substring(1, local.length() - 1); // only the insides
		}

		// -123
		if (local.matches("[\\-]?[0-9]+")) {
			return readInt(local, 0);
		}

		// RND*15
		if (local.matches("RND\\*[\\-]?[0-9]+")) {
			try {
				return random.nextInt(readInt(local, 4));
			} catch (IllegalArgumentException iae) {
				throw new ParseException("Negative random.");
			}
		}

		// RND*15+16 only plus!
		if (local.matches("RND\\*[0-9]+\\+[0-9]+")) {
			try {
				return readInt(local, local.indexOf("+") + 1) + random.nextInt(readInt(local, 4));
			} catch (IllegalArgumentException iae) {
				throw new ParseException("Negative random.");
			}
		}

		if (requireValue) {
			throw new ParseException("Loop count needs value!");
		}

		throw new ParseException("Invalid miner calculation syntax.");
	}

	private static boolean parseGetEquationResult(String eq) throws ParseException {
		String local = eq.replace("==", "=");
		local = local.replace("=>", ">=");
		local = local.replace("=<", "<=");

		if (local.matches(".+=.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf("=")), true);
			int b = parseSingleVar(local.substring(local.indexOf("=") + 1, local.length()), true);

			return a == b;
		}

		if (local.matches(".+!=.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf("!=")), true);
			int b = parseSingleVar(local.substring(local.indexOf("!=") + 2, local.length()), true);

			return a != b;
		}

		if (local.matches(".+>.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf(">")), true);
			int b = parseSingleVar(local.substring(local.indexOf(">") + 1, local.length()), true);

			return a > b;
		}

		if (local.matches(".+<.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf("<")), true);
			int b = parseSingleVar(local.substring(local.indexOf("<") + 1, local.length()), true);

			return a < b;
		}

		if (local.matches(".+>=.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf(">=")), true);
			int b = parseSingleVar(local.substring(local.indexOf(">=") + 2, local.length()), true);

			return a >= b;
		}

		if (local.matches(".+<=.+")) {
			int a = parseSingleVar(local.substring(0, local.indexOf("<=")), true);
			int b = parseSingleVar(local.substring(local.indexOf("<=") + 2, local.length()), true);

			return a <= b;
		}

		if (local.equals("RND")) {
			return random.nextBoolean();
		}

		throw new ParseException("Invalid condition syntax.");
	}

	private static int readInt(String str, int pos) throws ParseException {
		if (str.length() <= pos) {
			throw new ParseException("Number expected.");
		}

		Character chr = str.charAt(pos);

		if (Character.isDigit(chr) || chr.equals('-')) {
			String numbuff = Character.toString(chr);
			pos++;
			while (pos < str.length()) {
				chr = Character.valueOf(str.charAt(pos));

				if (Character.isDigit(chr)) {
					numbuff += chr.toString();
				} else {
					break;
				}
				pos++;
			}

			try {
				return Integer.valueOf(numbuff);
			} catch (NumberFormatException nfe) {
				throw new ParseException("Number expected.");
			}
		} else {
			throw new ParseException("Number expected.");
		}
	}

	/**
	 * Parse miner's program to direct commands and numbers. Expand loops, process
	 * variables etc.
	 * 
	 * @param str program to parse
	 * @return parsed command sequence
	 * @throws ParseException if there was an error in the program
	 */
	public static String parseCode(String str) throws ParseException {
		try {
			PC_Logger.fine("Parsing Miner's code:\n" + str);
			PC_Logger.finer("Erasing vars.");

			String local = new String(str); // copy + to uppercase

			PC_Logger.finer("Removing spaces, endlines, converting brackets.");
			local = parseBatchReplace(local); // remove spaces, fix brackets..

			PC_Logger.finer("Replacing aliases with one-letter commands.");
			local = parseReplaceAliases(local);

			PC_Logger.finer("Checking loops consistency.");
			if (!parseLoopConsistencyCheck(local)) {

				PC_Logger.warning("Loop consistency check failed, broken loops found!");
				throw new ParseException("Unclosed or broken loops.");
			}

			PC_Logger.finer("Expanding loops, if-else statements, variables.");
			local = parseExpandLoops(local);

			PC_Logger.finer("Removing unknown commands from the output code.");
			local = parseRemoveUnknown(local);

			PC_Logger.finer("Output code:\n" + local);

			PC_Logger.fine("Parsing completed.");

			return local;
		} catch (ParseException e) {
			throw e;
		} catch (Throwable t) {
			PC_Logger.finer("Unexpected throwable!");
			throw new ParseException("Parse error.");
		}
	}

	private static String parseBatchReplace(String str) {
		String tmp = str.toUpperCase();
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace("[", "(");
		tmp = tmp.replaceAll("[#']{1}.+$", ""); // comment at the end of line
		tmp = tmp.replace("\n", "");
		tmp = tmp.replace("\r", "");
		tmp = tmp.replace("\t", "");
		tmp = tmp.replace("]", ")");
		tmp = tmp.replace("!", "/");
		tmp = tmp.replace("(LOOP", "(@");
		return tmp;
	}

	private static String parseReplaceAliases(String str) {
		String tmp = new String(str);
		tmp = tmp.replace("(MINING:ON)", "*");
		tmp = tmp.replace("(MINING)", "*");
		tmp = tmp.replace("(MINE)", "*");
		tmp = tmp.replace("(MI)", "*");
		tmp = tmp.replace("(M)", "*");
		tmp = tmp.replace("(MINE:ON)", "*");
		tmp = tmp.replace("(MI:ON)", "*");
		tmp = tmp.replace("(M:ON)", "*");

		tmp = tmp.replace("(MINING:OFF)", "%");
		tmp = tmp.replace("(/MINING)", "%");
		tmp = tmp.replace("(/MINE)", "%");
		tmp = tmp.replace("(/MI)", "%");
		tmp = tmp.replace("(/M)", "%");
		tmp = tmp.replace("(MINE:OFF)", "%");
		tmp = tmp.replace("(MI:OFF)", "%");
		tmp = tmp.replace("(M:OFF)", "%");

		tmp = tmp.replace("(BRIDGE:OFF)", "P");
		tmp = tmp.replace("(/BRIDGE)", "P");
		tmp = tmp.replace("(/BR)", "P");
		tmp = tmp.replace("(/B)", "P");
		tmp = tmp.replace("(BR:OFF)", "P");
		tmp = tmp.replace("(B:OFF)", "P");

		tmp = tmp.replace("(BRIDGE:ON)", "O");
		tmp = tmp.replace("(BRIDGE)", "O");
		tmp = tmp.replace("(BR)", "O");
		tmp = tmp.replace("(B)", "O");
		tmp = tmp.replace("(BR:ON)", "O");
		tmp = tmp.replace("(B:ON)", "O");

		tmp = tmp.replace("(LAVA:ON)", "I");
		tmp = tmp.replace("(LAVA)", "I");
		tmp = tmp.replace("(L)", "I");
		tmp = tmp.replace("(L:ON)", "I");

		tmp = tmp.replace("(LAVA:OFF)", "J");
		tmp = tmp.replace("(/LAVA)", "J");
		tmp = tmp.replace("(/L)", "J");
		tmp = tmp.replace("(L:OFF)", "J");

		tmp = tmp.replace("(WATER:ON)", "Y");
		tmp = tmp.replace("(WATER)", "Y");
		tmp = tmp.replace("(W)", "Y");
		tmp = tmp.replace("(W:ON)", "Y");

		tmp = tmp.replace("(WATER:OFF)", "Z");
		tmp = tmp.replace("(/WATER)", "Z");
		tmp = tmp.replace("(/W)", "Z");
		tmp = tmp.replace("(W:OFF)", "Z");

		tmp = tmp.replace("(DROP)", "Q");
		tmp = tmp.replace("(DEPOSIT)", "Q");
		tmp = tmp.replace("(EJECT)", "Q");
		tmp = tmp.replace("(STORE)", "Q");

		tmp = tmp.replace("(DIE)", "X");
		tmp = tmp.replace("(HALT)", "X");
		tmp = tmp.replace("(TOBLOCKS)", "X");
		tmp = tmp.replace("(BLOCKS)", "X");

		return tmp;
	}

	private static boolean parseLoopConsistencyCheck(String source) {
		int pos = 0;
		int openLoops = 0;

		while (pos < source.length()) {
			Character chr = Character.valueOf(source.charAt(pos));

			if (Character.valueOf(chr).equals('(')) {
				openLoops++;
			}

			if (Character.valueOf(chr).equals(')')) {
				openLoops--;
			}

			pos++;
		}

		return openLoops == 0;
	}

	private static String parseIfElse(String source) throws ParseException {
		PC_Logger.finer("Parsing IF-ELSE " + source);
		String local = new String(source);
		if (!Character.valueOf(local.charAt(0)).equals('(')) {
			PC_Logger.warning("error 1: " + local);
			throw new ParseException("Bad if-else syntax.");
		}

		if (!Character.valueOf(local.charAt(local.length() - 1)).equals(')')) {
			PC_Logger.warning("error 2: " + local);
			throw new ParseException("Bad if-else syntax.");
		}

		local = local.substring(1, local.length() - 1); // remove brackets.

		int qmark = local.indexOf("?");
		int ddot = local.indexOf(":");

		// no question mark
		if (qmark == -1 || (ddot > -1 && ddot < qmark)) {
			PC_Logger.warning("error 3: " + local);
			throw new ParseException("Bad if-else syntax.");
		}

		String equation = local.substring(0, qmark);

		if (parseGetEquationResult(equation)) {
			PC_Logger.finest("true, qmark=" + qmark + ", ddot=" + ddot);
			PC_Logger.finest("parsing->" + local.substring(qmark + 1, ddot > qmark ? ddot : local.length()));
			return parseExpandLoops(local.substring(qmark + 1, ddot > qmark ? ddot : local.length()));
		} else {
			PC_Logger.finest("false, qmark=" + qmark + ", ddot=" + ddot);
			if (ddot != -1) {
				return parseExpandLoops(local.substring(ddot + 1, local.length()));
			}
			return "";
		}
	}

	private static String parseExpandLoops(String source) throws ParseException {
		return parseExpandLoops(source, 0);
	}

	private static String parseExpandLoops(String source, int recursion) throws ParseException {
		recursion++;
		if (recursion > 5000) {
			PC_Logger.warning("Recursion limit exceeded.");
			throw new ParseException("Recursion limit exceeded.");
		}

		String output = new String("");

		int openLoops = 0;

		int posOpen = 0;
		int posClosed = 0;
		int repeatCount = 0;

		int pos = 0;
		boolean ifElse = false;

		// return without parsing
		// if no loops are found

		if (source.indexOf("(") == -1) {
			return parseEvaluateVars(source);
		}

		if (source.indexOf("@") == -1) {
			ifElse = true;
			// return parseEvaluateVars(parseIfElse(source));
		}

		if (source.indexOf(":") == -1) {
			PC_Logger.warning(": not found");
			if (!ifElse) {
				throw new ParseException("Invalid loop syntax.");
			}
		}

		if (source.indexOf(")") == -1) {
			PC_Logger.warning(") not found");
			throw new ParseException("Unclosed brackets.");
		}

		outer: while (pos < source.length()) {

			Character chr = Character.valueOf(source.charAt(pos));

			pos = source.indexOf("(", posClosed);

			// write code between loops into output directly.
			if (pos > 0) {
				output += parseEvaluateVars(source.substring(posClosed, pos)); // write code before loop into output
			}

			if (pos == -1) {
				output += parseEvaluateVars(source.substring(posClosed));
				break outer;
			}

			if (Character.valueOf(source.charAt(pos + 1)).equals('@')) {
				pos++; // now at @
				int posLimiter = source.indexOf(":", posClosed);

				pos++; // now at first number.
				String numbuf;
				try {
					numbuf = source.substring(pos, posLimiter);
				} catch (StringIndexOutOfBoundsException ee) {
					throw new ParseException("Loop syntax error.");
				}

				try {
					repeatCount = parseLoopCount(numbuf);
				} catch (ParseException ce) {
					throw ce;
				}

				pos = posLimiter + 1; // now at start of inner code.

				posOpen = pos; // save start index of inner code

				openLoops = 1; // started new loop, waiting for end bracket.

				while (pos < source.length()) {

					chr = Character.valueOf(source.charAt(pos));

					if (Character.valueOf(chr).equals('(')) {
						openLoops++;
					}

					if (Character.valueOf(chr).equals(')')) {
						openLoops--;
					}

					if (openLoops == 0) {
						// reached end of main loop, everything is closed;

						String repeated = source.substring(posOpen, pos);
						boolean novars = false;
						String parsedRep = "";

						if (repeated.indexOf("<") == -1 && repeated.indexOf("?") == -1) {
							novars = true;
						}

						if (novars) {
							parsedRep = parseExpandLoops(repeated);
						}

						for (; repeatCount > 0; repeatCount--) {
							if (!novars) {
								parsedRep = parseExpandLoops(repeated);
							}

							output += "," + parsedRep + ",";
						}

						openLoops = 0;

						posClosed = pos + 1; // continue one char after last
												// bracket

						continue outer;

					}

					pos++;
				}

				if (openLoops > 0) {
					throw new ParseException("Unclosed loops.");
				}

				// if not a loop - its an if-else statement
			} else {

				int ifElseStart = pos;

				pos++; // now at first char after start bracket

				openLoops = 1;

				while (pos < source.length()) {

					chr = Character.valueOf(source.charAt(pos));

					if (Character.valueOf(chr).equals('(')) {
						openLoops++;
					}

					if (Character.valueOf(chr).equals(')')) {
						openLoops--;
					}

					if (openLoops == 0) {

						// reached end of main statement, everything is closed;

						output += parseIfElse(source.substring(ifElseStart, pos + 1));

						openLoops = 0;

						posClosed = pos + 1; // continue one char after last
												// bracket

						continue outer;

					}

					pos++;

				}
			}
		}

		return output;
	}

	private static String parseRemoveUnknown(String code) throws ParseException {
		String str = new String(code);

		str = str.replaceAll(",+", ",");

		str = str.replaceAll("([^0-9]),", "$1");
		str = str.replaceAll(",([^0-9])", "$1");

		str = str.replaceAll("^,", "");
		str = str.replaceAll(",$", "");

		String codeBuffer = "";

		while (str.length() > 0) {
			Character chr = Character.valueOf(str.charAt(0));

			// accept commands, digits and "-"
			if ((getIntFromChar(chr) != -1) || Character.isDigit(chr) || chr.equals('-') || chr.equals(',')) {
				codeBuffer += chr.toString();
			} else {
				throw new ParseException("'" + chr.toString() + "' is not a valid command.");
			}

			str = str.substring(1);
		}

		return codeBuffer;
	}

	public static class ParseException extends Exception {
		private static final long serialVersionUID = 8169613785780477459L;

		/**
		 * The following constructors are for use by you for whatever purpose you can
		 * think of. Constructing the exception in this manner makes the exception
		 * behave in the normal way - i.e., as documented in the class "Throwable". The
		 * fields "errorToken", "expectedTokenSequences", and "tokenImage" do not
		 * contain relevant information. The JavaCC generated code does not use these
		 * constructors.
		 */

		public ParseException() {
			super();
		}

		public ParseException(String message) {
			super(message);
		}

	}

}