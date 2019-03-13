package io.github.eagle38394516;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The tool box class to chess and check if breaks the advanced rules. This
 * class uses single case design pattern to make sure there's only one instance
 * in the main memory.
 *
 * @author Chen Wang
 */
public final class Chess {

	/**
	 * Stores the single instance of this class.
	 */
	private static final Chess chess = new Chess();

	/**
	 * The size of the chess board. (Must be an odd number ranging from 7 to 19)
	 */
	public static final int BOARD_SIZE = 15;

	/**
	 * Marks who's turn next: True for black's turn and false for white's turn.
	 */
	private boolean whosTurn = true;

	/**
	 * When enabled the advanced rules, after chose a place, this variable marks
	 * whether the player has chose a invalid position.
	 */
	private boolean clickedForbidPoint = false;
	private String breaksWhichRule = "";

	/**
	 * Stores the positions of the black pieces. Numbers start from 1.
	 */
	public final ArrayList<PiecePosition> blackPos = new ArrayList<PiecePosition>();

	/**
	 * Stores the positions of the white pieces. Numbers start from 1.
	 */
	public final ArrayList<PiecePosition> whitePos = new ArrayList<PiecePosition>();

	/**
	 * When one player wins, this variable stores the positions of the pieces of
	 * the unbroken chain.
	 *
	 * @see #updateGameStatus()
	 */
	public final ArrayList<PiecePosition> winnerPos = new ArrayList<PiecePosition>();

	// /**
	// * This variable will always be modified by the function
	// * <code>checkUnbrokenChainOfN</code>. It stores the positions of the
	// * unbroken chain pieces.
	// *
	// * @see #checkUnbrokenChainOfN(int, int, ArrayList, int)
	// */
	// public final ArrayList<PiecePosition> unbrokenChainPos = new
	// ArrayList<PiecePosition>();

	/**
	 * Stores the positions of the board that the current player cannot placed
	 * on according to the advanced rules.
	 *
	 * @see #checkForbids()
	 * @see #check_3_3(int, int)
	 * @see #check_4_4(int, int)
	 */
	public final ArrayList<PiecePosition> breakingRulesPos = new ArrayList<PiecePosition>();
	public final ArrayList<Integer> breakingRulesPosReason = new ArrayList<Integer>();

	/**
	 * To mark whether the advanced rules are enabled.
	 */
	private boolean usingAdvancedRules = false;

	/**
	 * Marks the status of the game. 0 for normal gaming, 1 for black wins, 2
	 * for white wins and 3 for tie.
	 */
	private int gameStatus = 0;

	/**
	 * Marks if computer auto placing. 0 for off, 1 for black and 2 for white.
	 */
	private int autoPlacing = 0;
	public static final int AUTO_PLACING_BLACK = 1;
	public static final int AUTO_PLACING_OFF = 0;
	public static final int AUTO_PLACING_WHITE = -1;

	/**
	 * Private the constructor in order to prevent users from creating
	 * instances.
	 */
	private Chess() {
	}

	/**
	 * Get the instance of this class.
	 */
	public static Chess getInstance() {
		return chess;
	}

	/**
	 * Returns whether the advanced rules are enabled.
	 */
	public boolean isUsingAdvancedRules() {
		return usingAdvancedRules;
	}

	/**
	 * Set using the advanced rules or not.
	 */
	public void setUsingAdvancedRules(boolean usingAdvancedRules) {
		// If the game is over.
		if (!(chess.whitePos.isEmpty() || chess.blackPos.isEmpty())) {
			throw new RuntimeException("Invalid game status - the board is not empty!");
		}
		this.usingAdvancedRules = usingAdvancedRules;
	}

	/**
	 * Return whether the game is over.
	 */
	public boolean isGameOver() {
		return gameStatus != 0;
	}

	/**
	 * Return the game status.
	 */
	public int getGameStatus() {
		return gameStatus;
	}

	/**
	 * Return who's turn next.
	 */
	public boolean getWhosTurn() {
		return whosTurn;
	}

	// /**
	// * Return whether the board is empty.
	// */
	// public boolean isBoardEmpty() {
	// return blackPos.isEmpty();
	// }

	/**
	 * Return whether clicked a forbid point
	 *
	 * @return
	 */
	public boolean isClickedForbidPoint() {
		return clickedForbidPoint;
	}

	public String getBreaksWhichRule() {
		return breaksWhichRule;
	}

	/**
	 * Undo the last step.
	 */
	public void undo() {
		// If the board is empty then do nothing.
		if (blackPos.isEmpty() || whitePos.isEmpty() && autoPlacing == AUTO_PLACING_BLACK) {
			return;
		} else {
			whosTurn = !whosTurn;
			if (whosTurn) {
				blackPos.remove(blackPos.size() - 1);
			} else {
				whitePos.remove(whitePos.size() - 1);
			}
			clickedForbidPoint = false;
			breaksWhichRule = "";
			if (usingAdvancedRules && whosTurn) {
				checkForbids();
			} else {
				breakingRulesPos.clear();
			}
			gameStatus = 0;
			if (whosTurn && autoPlacing == AUTO_PLACING_BLACK ||
					!whosTurn && autoPlacing == AUTO_PLACING_WHITE) {
				undo();
			}
		}
	}

	/**
	 * Reset the game.
	 */
	public void resetAll() {
		whosTurn = true;
		clickedForbidPoint = false;
		breaksWhichRule = "";
		blackPos.clear();
		whitePos.clear();
		winnerPos.clear();
		// unbrokenChainPos.clear();
		breakingRulesPos.clear();
		gameStatus = 0;
		autoPlace();
	}

	/**
	 * Place by random.
	 *
	 * @see #place(PiecePosition)
	 */
	public void placeByRandom() {
		// If the game is over.
		if (gameStatus != 0) {
			throw new IllegalArgumentException("Invalid gameStatus: " + gameStatus);
		}

		// Calculate the positions the pieces don't have been placed.
		ArrayList<PiecePosition> restPos = new ArrayList<PiecePosition>();
		PiecePosition posTemp;
		for (int y = 1; y <= BOARD_SIZE; y++) {
			for (int x = 1; x <= BOARD_SIZE; x++) {
				posTemp = PiecePosition.get(x, y);
				if (!blackPos.contains(posTemp) && !whitePos.contains(posTemp)) {
					restPos.add(posTemp);
				}
			}
		}
		// Remove the invalid positions.
		if (usingAdvancedRules) {
			// checkForbids();
			restPos.removeAll(breakingRulesPos);
		}
		// Shuffle the positions, then the first will be chosen to place the
		// next piece.
		Collections.shuffle(restPos);

		// Same as when click the board.
		if (whosTurn) { // Black's turn.
			blackPos.add(restPos.get(0));
		} else { // White's turn.
			whitePos.add(restPos.get(0));
		}
		updateGameStatus();
		// whosTurn = !whosTurn;
		// if (usingAdvancedRules) {
		// checkForbids();
		// } else {
		// breakingRulesPos.clear();
		// clickedForbidPoint = false;
		// breaksWhichRule = "";
		// }
	}

	/**
	 * Place a new piece on the board.
	 *
	 * @param pos
	 *            The position to be placed.
	 * @return Whether the piece placed on the board successfully.
	 */
	public boolean place(PiecePosition pos) {
		// If the position already has an piece.
		if (blackPos.contains(pos) || whitePos.contains(pos)) {
			return false;
		}

		// Calculate the positions that break the rules.
		if (usingAdvancedRules) {
			// If clicked on an invalid position.
			if (breakingRulesPos.contains(pos)) {
				clickedForbidPoint = true;
				switch (breakingRulesPosReason.get(breakingRulesPos.indexOf(pos))) {
					case CheckForbids.LONG_FORBIDDEN:
						breaksWhichRule = "LONG_FORBIDDEN";
						break;
					case CheckForbids.THREE_THREE_FORBIDDEN:
						breaksWhichRule = "THREE_THREE_FORBIDDEN";
						break;
					case CheckForbids.FOUR_FOUR_FORBIDDEN:
						breaksWhichRule = "FOUR_FOUR_FORBIDDEN";
						break;
					default:
						throw new IllegalArgumentException(
								String.format("Invalid breakingRulesPosReason[%d] - %d",
										breakingRulesPos.indexOf(pos),
										breakingRulesPosReason.get(breakingRulesPos.indexOf(pos))));
				}
				return false;
			} else {
				clickedForbidPoint = false;
				breaksWhichRule = "";
			}
		} else {
			breakingRulesPos.clear();
			clickedForbidPoint = false;
			breaksWhichRule = "";
		}

		// Place the piece.
		if (whosTurn) { // Black's turn.
			blackPos.add(pos);
		} else { // White's turn.
			whitePos.add(pos);
		}

		// Check if the current player wins.
		updateGameStatus();
		return true;
	}

	/**
	 * Calculate the invalid position of current player according to the
	 * advanced rules. The variable <code>breakingRulesPos</code> will be
	 * changed.
	 */
	public void checkForbids() {
		breakingRulesPos.clear();
		for (int x = 1; x <= BOARD_SIZE; x++) {
			for (int y = 1; y <= BOARD_SIZE; y++) {
				// Stores the current position.
				PiecePosition pos = PiecePosition.get(x, y);

				// If the position already has a piece on it, passed.
				if (blackPos.contains(pos) || whitePos.contains(pos)) {
					continue;
				}

				int ifForbids = CheckForbids.checkForbids(pos);
				if (ifForbids == CheckForbids.NO_FORBIDDEN) {
					continue;
				}
				breakingRulesPos.add(pos);
				breakingRulesPosReason.add(ifForbids);
			}
		}
	}

	/**
	 * Check if the current player wins with the last move. (Variable
	 * <code>whosTurn</code> marks the current player who makes the last move.)
	 */
	public void updateGameStatus() {
		// Check priority: black / white wins -> tie

		if (CheckForbids.checkIfWins()) {
			// Automatically get the current player (whosTurn), get the
			// chess board (blackPos / whitePos)
			gameStatus = whosTurn ? 1 : 2;
			// Even if the current player wins, we still change the current
			// player in case the player clicks the undo button.
			whosTurn = !whosTurn;
			return;
		}

		// Check if there's no room for the next player to place if the
		// advanced rules enabled.
		ArrayList<PiecePosition> restPos = new ArrayList<PiecePosition>();
		PiecePosition posTemp;
		for (int x = 1; x <= BOARD_SIZE; x++) {
			for (int y = 1; y <= BOARD_SIZE; y++) {
				posTemp = PiecePosition.get(x, y);
				if (!blackPos.contains(posTemp) &&
						!whitePos.contains(posTemp)) {
					restPos.add(posTemp);
				}
			}
		}
		if (restPos.isEmpty()) {
			// If the chess board is full.
			gameStatus = 3;
			// Even if the current player wins, we still change the current
			// player in case the player clicks the undo button.
			whosTurn = !whosTurn;
			return;
		}

		// If the current player doesn't win, change the current player.
		whosTurn = !whosTurn;
		// Calculate the invalid positions of the next player.
		if (usingAdvancedRules && whosTurn) {
			checkForbids();
			restPos.removeAll(breakingRulesPos);
			if (restPos.isEmpty()) {
				// There's no room to place.
				gameStatus = 3;
				return;
			}
		} else {
			breakingRulesPos.clear();
			clickedForbidPoint = false;
			breaksWhichRule = "";
		}
		// Otherwise no one wins.
		gameStatus = 0;
		autoPlace();
	}

	// 【【【【【*******整个游戏的核心*******】】】】】______确定机器落子位置
	// 使用五元组评分算法，该算法参考博客地址：
	// https://blog.csdn.net/u011587401/article/details/50877828
	// 算法思路：对15X15的572个五元组分别评分，一个五元组的得分就是该五元组为其中每个位置贡献的分数，
	// 一个位置的分数就是其所在所有五元组分数之和。所有空位置中分数最高的那个位置就是落子位置。

	public int getAutoPlacing() {
		return autoPlacing;
	}

	public int ratingTable(int computer, int player) {
		if (computer > 0 && player > 0) {
			return 0;
		}
		if (computer == 0 && player == 0) {
			return 7;
		}
		if (computer == 1) {
			return 35;
		}
		if (computer == 2) {
			return 800;
		}
		if (computer == 3) {
			return 15000;
		}
		if (computer == 4) {
			return 800000;
		}
		if (player == 1) {
			return 15;
		}
		if (player == 2) {
			return 400;
		}
		if (player == 3) {
			return 1800;
		}
		if (player == 4) {
			return 100000;
		}
		throw new RuntimeException("Undefined case - Computer(" + computer + "), Player(" + player + ")");
	}

	public PiecePosition calcBestPos(ArrayList<PiecePosition> computerPos, ArrayList<PiecePosition> playerPos) {
		int[][] scores = new int[BOARD_SIZE][BOARD_SIZE];
		int tupleScore = -1, computerPieces = 0, playerPieces = 0;
		int posX = -1, posY = -1;

		// Scan all five-piece tuples
		// Horizontally
		for (int row = 1; row <= BOARD_SIZE; row++) {
			for (int beginningCol = 1; beginningCol <= BOARD_SIZE - 4; beginningCol++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = beginningCol + piece;
					posY = row;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = beginningCol + piece;
					posY = row;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		// Vertically
		for (int col = 1; col <= BOARD_SIZE; col++) {
			for (int beginningRow = 1; beginningRow <= BOARD_SIZE - 4; beginningRow++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = col;
					posY = beginningRow + piece;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = col;
					posY = beginningRow + piece;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		// \ direction (Upper part + Diagonal)
		for (int diagonal = 1; diagonal <= BOARD_SIZE - 4; diagonal++) {
			for (int beginningPiece = 1; beginningPiece <= BOARD_SIZE - 3 - diagonal; beginningPiece++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = diagonal + beginningPiece - 1 + piece;
					posY = beginningPiece + piece;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = diagonal + beginningPiece - 1 + piece;
					posY = beginningPiece + piece;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		// \ direction (Lower part)
		for (int diagonal = 1; diagonal <= BOARD_SIZE - 5; diagonal++) {
			for (int beginningPiece = 1; beginningPiece <= BOARD_SIZE - 4 - diagonal; beginningPiece++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = beginningPiece + piece;
					posY = 1 + diagonal + beginningPiece - 1 + piece;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = beginningPiece + piece;
					posY = 1 + diagonal + beginningPiece - 1 + piece;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		// / direction (Upper part + Diagonal)
		for (int diagonal = 1; diagonal <= BOARD_SIZE - 4; diagonal++) {
			for (int beginningPiece = 1; beginningPiece <= BOARD_SIZE - 3 - diagonal; beginningPiece++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = BOARD_SIZE - (diagonal - 1) - (beginningPiece - 1) - piece;
					posY = beginningPiece + piece;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = BOARD_SIZE - (diagonal - 1) - (beginningPiece - 1) - piece;
					posY = beginningPiece + piece;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		// / direction (Lower part)
		for (int diagonal = 1; diagonal <= BOARD_SIZE - 5; diagonal++) {
			for (int beginningPiece = 1; beginningPiece <= BOARD_SIZE - 4 - diagonal; beginningPiece++) {
				computerPieces = 0;
				playerPieces = 0;
				for (int piece = 0; piece < 5; piece++) {
					posX = BOARD_SIZE - (beginningPiece - 1) - piece;
					posY = 1 + diagonal + beginningPiece - 1 + piece;
					if (computerPos.contains(PiecePosition.get(posX, posY))) {
						computerPieces++;
					} else if (playerPos.contains(PiecePosition.get(posX, posY))) {
						playerPieces++;
					}
				}
				tupleScore = ratingTable(computerPieces, playerPieces);
				for (int piece = 0; piece < 5; piece++) {
					posX = BOARD_SIZE - (beginningPiece - 1) - piece;
					posY = 1 + diagonal + beginningPiece - 1 + piece;
					scores[posX - 1][posY - 1] += tupleScore;
				}
			}
		}

		if (usingAdvancedRules && whosTurn) {
			checkForbids();
			for (int index = 0; index < breakingRulesPos.size(); index++) {
				scores[breakingRulesPos.get(index).getX() - 1][breakingRulesPos.get(index).getY() - 1] = -1;
			}
		}

		int maxScore = -1;
		posX = -1;
		posY = -1;
		for (int col = 0; col < BOARD_SIZE; col++) {
			for (int row = 0; row < BOARD_SIZE; row++) {
				if (scores[col][row] > maxScore && !whitePos.contains(PiecePosition.get(col + 1, row + 1)) && !blackPos.contains(PiecePosition.get(col + 1, row + 1))) {
					maxScore = scores[col][row];
					posX = col + 1;
					posY = row + 1;
				}
			}
		}
		if (maxScore == -1 || posX == -1 || posY == -1) {
			throw new RuntimeException("Unknown case!");
		}
		return PiecePosition.get(posX, posY);
	}

	public void autoPlace() {
		ArrayList<PiecePosition> playerPos, computerPos;
		if (autoPlacing == AUTO_PLACING_BLACK && whosTurn) {
			if (blackPos.isEmpty()) {
				Random r = new Random();
				this.place(PiecePosition.get(r.nextInt(3) + (BOARD_SIZE - 1) / 2, r.nextInt(3) + (BOARD_SIZE - 1) / 2));
				MainBody.instance.paintingPanel.repaint();
				return;
			}
			playerPos = whitePos;
			computerPos = blackPos;
		} else if (autoPlacing == AUTO_PLACING_WHITE && !whosTurn) {
			playerPos = blackPos;
			computerPos = whitePos;
		} else {
			return;
		}

		place(calcBestPos(computerPos, playerPos));
	}

	public void changingAutoPlacing() {
		if ((whitePos.isEmpty() && blackPos.size() == 1 && autoPlacing == AUTO_PLACING_BLACK)
				|| (blackPos.isEmpty() && (autoPlacing == AUTO_PLACING_WHITE || autoPlacing == AUTO_PLACING_OFF))) {
			switch (autoPlacing) {
				case AUTO_PLACING_WHITE:
					autoPlacing = AUTO_PLACING_OFF;
					break;
				case AUTO_PLACING_OFF:
					autoPlacing = AUTO_PLACING_BLACK;
					break;
				case AUTO_PLACING_BLACK:
					autoPlacing = AUTO_PLACING_WHITE;
					break;
				default:
					throw new RuntimeException("Invalid autoPlacing - " + autoPlacing);
			}
			MainBody.instance.paintingPanel.resetAll();
			// autoPlace();
			MainBody.instance.paintingPanel.repaint();
		}
	}
}
