package io.github.eagle38394516;

import java.util.ArrayList;

/**
 * A separately class to check whether the position to be placed breaks the
 * advanced rules.
 *
 * (The program refers to Hao Tian's algorithm.
 * http://blog.csdn.net/JkSparkle/article/details/822873)
 *
 * Direction representations: Eight directions: \|/ 701 -.- 6.2 /|\ 543
 *
 * Four directions: \|/ 301 ..- ..2
 *
 * Symbols in comments: 0: Black +: Empty Q: White or boundary ?: White, empty
 * or boundary
 *
 * @author Chen Wang
 */
public final class CheckForbids {

	/**
	 * Private the constructor in order to prevent users from creating
	 * instances.
	 */
	private CheckForbids() {
	}

	/**
	 * No forbidden.
	 */
	public static final int NO_FORBIDDEN = 0;

	/**
	 * Forbidden of two threes.
	 */
	public static final int THREE_THREE_FORBIDDEN = 1;

	/**
	 * Forbidden of two fours.
	 */
	public static final int FOUR_FOUR_FORBIDDEN = 2;

	/**
	 * Forbidden of long chain.
	 */
	public static final int LONG_FORBIDDEN = 3;

	/**
	 * The chess tool box class.
	 */
	private static final Chess chess = Chess.getInstance();

	private static final ArrayList<PiecePosition> blackPos = chess.blackPos;
	private static final ArrayList<PiecePosition> whitePos = chess.whitePos;
	private static final ArrayList<PiecePosition> winnerPos = chess.winnerPos;
	// private static final ArrayList<PiecePosition> breakingRulesPos =
	// chess.breakingRulesPos;
	// private static final ArrayList<Integer> breakingRulesPosReason =
	// chess.breakingRulesPosReason;

	// \|/ 321
	// -.- 4.0
	// /|\ 567
	//
	// l = 0 --> x + 1 ; y
	// l = 1 --> x + 1 ; y - 1
	// l = 2 --> x ....; y - 1
	// l = 3 --> x - 1 ; y - 1
	// l = 4 --> x - 1 ; y
	// l = 5 --> x - 1 ; y + 1
	// l = 6 --> x ....; y + 1
	// l = 7 --> x + 1 ; y + 1

	/**
	 * Stores how the x coordinates change when finding the chains.
	 */
	private static final int[] DIRECTION_SIGN_X = { 1, 1, 0, -1, -1, -1, 0, 1 };

	/**
	 * Stores how the y coordinates change when finding the chains.
	 */
	private static final int[] DIRECTION_SIGN_Y = { 0, -1, -1, -1, 0, 1, 1, 1 };

	public static int[][] chessboardSearch(ArrayList<PiecePosition> whichPos,
			PiecePosition originPos, boolean checkWins, ArrayList<PiecePosition> anotherPos) {
		// 如果是checkWins则后四个数组失效

		int[][] result = new int[5][8];

		// // 8 directions.
		// // 记录与(x, y)相邻的连续黑色棋子数
		// int[] adjsame = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		// // 记录adjsame后相邻的连续空位数
		// int[] adjempty = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		// // 记录adjempty后的连续黑色棋子数
		// int[] jumpsame = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		// // 记录jumpsame后的空位数
		// int[] jumpempty = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		// // 记录jumpempty后的连续黑色棋子数
		// int[] jumpjumpsame = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };

		// Search whole chess board.
		int x, y; // The searching position

		for (int direction = 0; direction < 8; direction++) {
			x = originPos.getX() + DIRECTION_SIGN_X[direction];
			y = originPos.getY() + DIRECTION_SIGN_Y[direction];
			for (int index = 1; index <= (checkWins ? 1 : 5); index++) {
				while ((x >= 1 && x <= 15 && y >= 1 && y <= 15) &&
						((index % 2 != 0) ?
								whichPos.contains(PiecePosition.get(x, y)) :
								(checkWins ?
										(!whichPos.contains(PiecePosition.get(x, y))) :
										(!(whichPos.contains(PiecePosition.get(x, y)) ||
										anotherPos.contains(PiecePosition.get(x, y))))))) {
					result[index - 1][direction]++;
					x += DIRECTION_SIGN_X[direction];
					y += DIRECTION_SIGN_Y[direction];
				}
			}
		}

		// try {
		// BufferedWriter bw = new BufferedWriter(new FileWriter("v3.0.txt",
		// true));
		// bw.write(originPos.toString());
		// bw.newLine();
		// bw.write(Arrays.toString(result[0]));
		// bw.newLine();
		// bw.write(Arrays.toString(result[1]));
		// bw.newLine();
		// bw.write(Arrays.toString(result[2]));
		// bw.newLine();
		// bw.write(Arrays.toString(result[3]));
		// bw.newLine();
		// bw.write(Arrays.toString(result[4]));
		// bw.newLine();
		// bw.newLine();
		// bw.flush();
		// bw.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return result;
	}

	public static boolean checkIfWins() {
		ArrayList<PiecePosition> whichPos = chess.getWhosTurn() ? blackPos : whitePos;
		int[][] searchResults = chessboardSearch(whichPos, whichPos.get(whichPos.size() - 1), true, null);
		for (int direction = 0; direction < 4; direction++) {
			if (searchResults[0][direction] + searchResults[0][direction + 4] >= 4) {
				winnerPos.clear();
				for (int index = -searchResults[0][direction + 4]; index <= searchResults[0][direction]; index++) {
					winnerPos.add(PiecePosition.get(
							whichPos.get(whichPos.size() - 1).getX() + index * DIRECTION_SIGN_X[direction],
							whichPos.get(whichPos.size() - 1).getY() + index * DIRECTION_SIGN_Y[direction]));
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the certain position breaks the advanced rules.
	 *
	 * @param pos
	 *            The coordinate to be checked.
	 * @return Whether the certain position breaks the advanced rules and which
	 *         rule breaks.
	 */
	public static int checkForbids(PiecePosition pos) {
		if (pos.getX() < 1 || pos.getX() > 15 || pos.getY() < 1 || pos.getY() > 15) {
			throw new IllegalArgumentException(String.format("Invalid pos (%d, %d)", pos.getX(), pos.getY()));
		}

		int[][] searchResults = chessboardSearch(blackPos, pos, false, whitePos);

		// Check whether black wins
		for (int i = 0; i < 4; i++) {
			if (searchResults[0][i] + searchResults[0][i + 4] == 4) {
				return NO_FORBIDDEN;
			}
		}

		// Analyze whether breaks the rules
		int threeCounter = 0, fourCounter = 0; // Accumulator

		for (int i = 0; i < 4; i++) {
			if (searchResults[0][i] + searchResults[0][i + 4] >= 5) {
				// Long chain
				return LONG_FORBIDDEN;
			} else if (searchResults[0][i] + searchResults[0][i + 4] == 3) {
				// ?0000?
				// Check whether it is a valid four
				boolean isFour = false;
				if (searchResults[1][i] > 0) {
					// ?+0000?
					// Check if the key point can be placed by recursive.
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						isFour = true;
					}
				}
				if (searchResults[1][i + 4] > 0) {
					// ?0000+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						isFour = true;
					}
				}
				if (isFour) {
					fourCounter++;
				}
			} else if (searchResults[0][i] + searchResults[0][i + 4] == 2) {
				// ?000?
				// Check whether it is a valid four.
				if (searchResults[1][i] == 1 && searchResults[2][i] == 1) {
					// ?0+000?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				if (searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 1) {
					// ?000+0?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				// Check whether it is a valid three.
				boolean isThree = false;
				if ((searchResults[1][i] > 2 || searchResults[1][i] == 2 && searchResults[2][i] == 0) &&
						(searchResults[1][i + 4] > 1 || searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 0)) {
					// ?++000+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						isThree = true;
					}
				}
				if ((searchResults[1][i + 4] > 2 || searchResults[1][i + 4] == 2 && searchResults[2][i + 4] == 0) &&
						(searchResults[1][i] > 1 || searchResults[1][i] == 1 && searchResults[2][i] == 0)) {
					// ?+000++?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						isThree = true;
					}
				}
				if (isThree) {
					threeCounter++;
				}
			} else if (searchResults[0][i] + searchResults[0][i + 4] == 1) {
				// ?00?
				// Check whether it is a valid four.
				if (searchResults[1][i] == 1 && searchResults[2][i] == 2) {
					// ?00+00?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				if (searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 2) {
					// ?00+00?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				// Check whether it is a valid three.
				if (searchResults[1][i] == 1 && searchResults[2][i] == 1 &&
						(searchResults[3][i] > 1 || searchResults[3][i] == 1 && searchResults[4][i] == 0) &&
						(searchResults[1][i + 4] > 1 || searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 0)) {
					// ?+0+00+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						threeCounter++;
					}
				}
				if (searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 1 &&
						(searchResults[3][i + 4] > 1 || searchResults[3][i + 4] == 1 && searchResults[4][i + 4] == 0) &&
						(searchResults[1][i] > 1 || searchResults[1][i] == 1 && searchResults[2][i] == 0)) {
					// ?+00+0+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						threeCounter++;
					}
				}
			} else if (searchResults[0][i] + searchResults[0][i + 4] == 0) {
				// ?0?
				// Check whether it is a valid four.
				if (searchResults[1][i] == 1 && searchResults[2][i] == 3) {
					// ?000+0?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				if (searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 3) {
					// ?0+000?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						fourCounter++;
					}
				}
				// Check whether it is a valid three.
				if (searchResults[1][i] == 1 && searchResults[2][i] == 2 &&
						(searchResults[3][i] > 1 || searchResults[3][i] == 1 && searchResults[4][i] == 0) &&
						(searchResults[1][i + 4] > 1 || searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 0)) {
					// ?+00+0+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i], i) == NO_FORBIDDEN) {
						threeCounter++;
					}
				}
				if (searchResults[1][i + 4] == 1 && searchResults[2][i + 4] == 2 &&
						(searchResults[3][i + 4] > 1 || searchResults[3][i + 4] == 1 && searchResults[4][i + 4] == 0) &&
						(searchResults[1][i] > 1 || searchResults[1][i] == 1 && searchResults[2][i] == 0)) {
					// ?+0+00+?
					if (KeyPointForbiddenCheck(pos, searchResults[0][i + 4], i + 4) == NO_FORBIDDEN) {
						threeCounter++;
					}
				}
			}
		}

		// Return the result.
		if (fourCounter > 1) {
			return FOUR_FOUR_FORBIDDEN;
		}
		if (threeCounter > 1) {
			return THREE_THREE_FORBIDDEN;
		}
		return NO_FORBIDDEN;
	}

	/**
	 * Check whether the key points forming three and four is a forbidden point
	 * or not.
	 *
	 * @param chessboard
	 * @param x
	 *            The horizontal coordinate of the position to be checked.
	 * @param y
	 *            The Vertical coordinate of the position to be checked.
	 * @param adjsame
	 *            待判断禁手点与关键点相隔的点数。
	 * @param direction
	 *            关键点相对禁手点的方向（八方向）。
	 * @return 关键点是否可下，如不可下，则返回禁手类型
	 */
	private static int KeyPointForbiddenCheck(PiecePosition pos, int adjsame, int direction) {
		// if (blackPos.contains(pos)) {
		// throw new
		// IllegalArgumentException("blackPos should not contain pos!");
		// }
		// int i, j; // 关键点坐标(i, j)
		// adjsame++;
		// if (direction >= 4) {
		// adjsame = -adjsame;
		// }

		// 计算关键点坐标
		PiecePosition keyPoint = PiecePosition.get(
				pos.getX() + DIRECTION_SIGN_X[direction] * (adjsame + 1),
				pos.getY() + DIRECTION_SIGN_Y[direction] * (adjsame + 1));

		// if (blackPos.contains(keyPoint)) {
		// throw new
		// IllegalArgumentException("blackPos should not contain keyPoint!");
		// }

		// switch (direction % 4) {
		// case 0:
		// i = pos.getX();
		// j = pos.getY() - adjsame;
		// break;
		// case 1:
		// i = pos.getX() + adjsame;
		// j = pos.getY() - adjsame;
		// break;
		// case 2:
		// i = pos.getX() + adjsame;
		// j = pos.getY();
		// break;
		// case 3:
		// default:
		// i = pos.getX() + adjsame;
		// j = pos.getY() + adjsame;
		// break;
		// }

		// 向棋盘中放入棋子
		blackPos.add(pos);
		blackPos.add(keyPoint);
		// chessboard[x][y] = BLACK;
		// chessboard[i][j] = BLACK;

		// 检查关键点
		int keyPointCheckResult = checkForbids(keyPoint);
		// ForbiddenCheck(chessboard, i, j);

		// 还原棋盘
		// chessboard[i][j] = NONE;
		// chessboard[x][y] = NONE;
		blackPos.remove(keyPoint);
		blackPos.remove(pos);

		return keyPointCheckResult;
	}
}
