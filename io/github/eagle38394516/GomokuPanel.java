package io.github.eagle38394516;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * The main panel of the game.
 *
 * @author Chen Wang
 */
public final class GomokuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final GomokuPanel instance = new GomokuPanel();

	/**
	 * The spacing of the grid.
	 */
	private static final int GRID_SPACING = 50;

	/**
	 * The radius of pieces.
	 */
	private static final int PIECE_RADIUS = 18;

	/**
	 * The diameter of pieces.
	 */
	private static final int PIECE_DIAMETER = PIECE_RADIUS * 2;

	/**
	 * The half of the cross marking the positions that breaks the rules.
	 */
	private static final int CROSS_LENGTH_HALF = 10;

	/**
	 * The boundary of judging when clicking the board.
	 */
	private static final double JUDGING_BOUNDARY = 0.45;

	/**
	 * The radius of the marks placed at some specific positions.
	 */
	private static final int MARKS_RADIUS = 4;

	/**
	 * The diameter of the marks placed at some specific positions.
	 */
	private static final int MARKS_DIAMETER = MARKS_RADIUS * 2;

	/**
	 * The horizontal position of the prompt string.
	 */
	private static final int STRING_POSITIONX = 60;

	/**
	 * The vertical position of the prompt string.
	 */
	private static final int STRING_POSITIONY = 785;

	/**
	 * The chess tool box class.
	 */
	private static final Chess chess = Chess.getInstance();

	/**
	 * Initializations when create the chess board.
	 */
	private GomokuPanel() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// If game is not over.
				if (!chess.isGameOver()) {
					// Check whether the click is in the valid area.
					if (e.getX() > GRID_SPACING - PIECE_RADIUS
							&& e.getX() < GRID_SPACING * Chess.BOARD_SIZE + PIECE_RADIUS
							&& e.getY() > GRID_SPACING - PIECE_RADIUS
							&& e.getY() < GRID_SPACING * Chess.BOARD_SIZE + PIECE_RADIUS) {
						int x, y;

						// Keep the fractional part to compare with boundary.
						if ((double) e.getX() / GRID_SPACING
								- Math.floor((double) e.getX() / GRID_SPACING)
								< JUDGING_BOUNDARY) {
							x = e.getX() / GRID_SPACING;
						} else if ((double) e.getX() / GRID_SPACING
								- Math.floor((double) e.getX() / GRID_SPACING)
								> 1 - JUDGING_BOUNDARY) {
							x = e.getX() / GRID_SPACING + 1;
						} else {
							// Clicks the central part of one grid.
							return;
						}
						// Same as above.
						if ((double) e.getY() / GRID_SPACING
								- Math.floor((double) e.getY() / GRID_SPACING)
								< JUDGING_BOUNDARY) {
							y = e.getY() / GRID_SPACING;
						} else if ((double) e.getY() / GRID_SPACING
								- Math.floor((double) e.getY() / GRID_SPACING)
								> 1 - JUDGING_BOUNDARY) {
							y = e.getY() / GRID_SPACING + 1;
						} else {
							return;
						}

						chess.place(PiecePosition.get(x, y));

						// Refresh the panel.
						repaint();
					}
				}
			}
		});
	}

	public static GomokuPanel getInstance() {
		return instance;
	}

	/**
	 * The actions when pressing the undo button.
	 */
	public void undo() {
		// Take actions only when the game is not over.
		// if (!chess.isGameOver()) {
		chess.undo();
		repaint();
		// }
	}

	/**
	 * The actions when pressing the reset button.
	 */
	public void resetAll() {
		chess.resetAll();
		repaint();
	}

	public void randomPlacingOneStep() {
		if (chess.isGameOver()) {
			resetAll();
		}
		placeByRandom();
	}

	/**
	 * The actions when pressing the random placing button
	 */
	public void placeByRandom() {
		chess.placeByRandom();
		repaint();
	}

	/**
	 * Refreshing actions.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		if (g2d.getRenderingHints() == null) {
			g2d.setRenderingHints(new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON));
		} else {
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g2d.setFont(new Font("Calibri", Font.BOLD, 25));
		drawChessBoard(g2d);
		drawPromptStrings(g2d);
		drawPieces(g2d);
	}

	/**
	 * Draw the chess board.
	 */
	private void drawChessBoard(Graphics2D g2d) {
		// Downcast in order to set the thickness of the lines.
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setColor(Color.BLACK);

		// Horizontal lines.
		for (int i = 1; i <= Chess.BOARD_SIZE; i++) {
			g2d.drawLine(GRID_SPACING * i, GRID_SPACING,
					GRID_SPACING * i, GRID_SPACING * Chess.BOARD_SIZE);
		}

		// Vertical lines.
		for (int i = 1; i <= Chess.BOARD_SIZE; i++) {
			g2d.drawLine(GRID_SPACING, GRID_SPACING * i,
					GRID_SPACING * Chess.BOARD_SIZE, GRID_SPACING * i);
		}

		// Specific positions markers.
		for (int x = 1; x <= 3; x++) {
			for (int y = 1; y <= 3; y++) {
				g2d.fillRoundRect(x * GRID_SPACING * MARKS_RADIUS - MARKS_RADIUS,
						y * GRID_SPACING * MARKS_RADIUS - MARKS_RADIUS,
						MARKS_DIAMETER, MARKS_DIAMETER, 3, 3);
			}
		}

		// Indexes
		for (int i = 1; i <= 15; i++) {
			g2d.drawString(String.format("%2d", i), 15, i * GRID_SPACING + 7);
			// g2d.drawString(String.format("%2d", i), i * GRID_SPACING - 10,
			// 40);
			g2d.drawString((char) ('A' + i - 1) + "", i * GRID_SPACING - 8, 40);
		}
	}

	/**
	 * Draw the prompt strings to show the status the game.
	 */
	private void drawPromptStrings(Graphics2D g2d) {
		if (chess.isClickedForbidPoint()) {
			// When the player clicked on an invalid position.
			g2d.setColor(Color.pink);
			g2d.drawString("You cannot place here according to the rules! " + chess.getBreaksWhichRule(), STRING_POSITIONX, STRING_POSITIONY);
		} else {
			// Perform different actions according to the game status.
			switch (chess.getGameStatus()) {
				case 0: // Normal
					if (chess.getWhosTurn()) {
						g2d.setColor(Color.black);
						g2d.drawString("Now Black's Turn...", STRING_POSITIONX, STRING_POSITIONY);
					} else {
						g2d.setColor(Color.white);
						g2d.drawString("Now White's Turn...", STRING_POSITIONX, STRING_POSITIONY);
					}
					break;
				case 1: // Black wins
					g2d.setColor(Color.yellow);
					g2d.drawString("Game over! Black wins!", STRING_POSITIONX, STRING_POSITIONY);
					break;
				case 2: // White wins.
					g2d.setColor(Color.yellow);
					g2d.drawString("Game over! White wins!", STRING_POSITIONX, STRING_POSITIONY);
					break;
				case 3: // Tie
					g2d.setColor(Color.red);
					g2d.drawString("Tied! The chess board is full!", STRING_POSITIONX, STRING_POSITIONY);
					break;
				default:
					throw new IllegalArgumentException("Invalid gameStatus: " + chess.getGameStatus());
			}
		}
	}

	/**
	 * Draw the pieces.
	 */
	private void drawPieces(Graphics2D g2d) {
		// Draw black pieces.
		g2d.setColor(Color.black);
		for (int i = 0; i < chess.blackPos.size(); i++) {
			g2d.fillOval(chess.blackPos.get(i).getX() * GRID_SPACING - PIECE_RADIUS,
					chess.blackPos.get(i).getY() * GRID_SPACING - PIECE_RADIUS,
					PIECE_DIAMETER, PIECE_DIAMETER);
		}

		// Draw white pieces.
		g2d.setColor(Color.white);
		for (int i = 0; i < chess.whitePos.size(); i++) {
			g2d.fillOval(chess.whitePos.get(i).getX() * GRID_SPACING - PIECE_RADIUS,
					chess.whitePos.get(i).getY() * GRID_SPACING - PIECE_RADIUS,
					PIECE_DIAMETER, PIECE_DIAMETER);
		}

		// If game is not over, then draw a cross to mark which one is placed
		// last. If game is already over, then draw five crosses to mark which
		// five pieces forming an unbroken chain.
		switch (chess.getGameStatus()) {
			case 0: // Normal
				// If the advanced rules enabled, then draw crosses to mark the
				// invalid positions.
				if (chess.isUsingAdvancedRules()) {
					g2d.setColor(Color.red);
					for (int i = 0; i < chess.breakingRulesPos.size(); i++) {
						g2d.drawLine(chess.breakingRulesPos.get(i).getX() * GRID_SPACING - (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getY() * GRID_SPACING - (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getX() * GRID_SPACING + (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getY() * GRID_SPACING + (int) (CROSS_LENGTH_HALF / Math.sqrt(2)));
						g2d.drawLine(chess.breakingRulesPos.get(i).getX() * GRID_SPACING - (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getY() * GRID_SPACING + (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getX() * GRID_SPACING + (int) (CROSS_LENGTH_HALF / Math.sqrt(2)),
								chess.breakingRulesPos.get(i).getY() * GRID_SPACING - (int) (CROSS_LENGTH_HALF / Math.sqrt(2)));
					}
				}
				// No break here!!! Same actions when normal gaming and tie.
			case 3: // Tie
				// If the board is not empty, there's always a black piece on
				// the board, then draw a cross in the opposite color to the
				// piece to mark which one is the last piece placed on the
				// board. Now draw five plus signs to mark which five pieces
				// form an unbroken chain.
				if (!chess.blackPos.isEmpty()) {
					if (chess.getWhosTurn()) {
						// Now is black's turn. Last is a white.
						g2d.setColor(Color.black);
						g2d.drawLine(chess.whitePos.get(chess.whitePos.size() - 1).getX() * GRID_SPACING - CROSS_LENGTH_HALF,
								chess.whitePos.get(chess.whitePos.size() - 1).getY() * GRID_SPACING,
								chess.whitePos.get(chess.whitePos.size() - 1).getX() * GRID_SPACING + CROSS_LENGTH_HALF,
								chess.whitePos.get(chess.whitePos.size() - 1).getY() * GRID_SPACING);
						g2d.drawLine(chess.whitePos.get(chess.whitePos.size() - 1).getX() * GRID_SPACING,
								chess.whitePos.get(chess.whitePos.size() - 1).getY() * GRID_SPACING - CROSS_LENGTH_HALF,
								chess.whitePos.get(chess.whitePos.size() - 1).getX() * GRID_SPACING,
								chess.whitePos.get(chess.whitePos.size() - 1).getY() * GRID_SPACING + CROSS_LENGTH_HALF);
					} else {
						// Now is white's turn. Last is a black.
						g2d.setColor(Color.white);
						g2d.drawLine(chess.blackPos.get(chess.blackPos.size() - 1).getX() * GRID_SPACING - CROSS_LENGTH_HALF,
								chess.blackPos.get(chess.blackPos.size() - 1).getY() * GRID_SPACING,
								chess.blackPos.get(chess.blackPos.size() - 1).getX() * GRID_SPACING + CROSS_LENGTH_HALF,
								chess.blackPos.get(chess.blackPos.size() - 1).getY() * GRID_SPACING);
						g2d.drawLine(chess.blackPos.get(chess.blackPos.size() - 1).getX() * GRID_SPACING,
								chess.blackPos.get(chess.blackPos.size() - 1).getY() * GRID_SPACING - CROSS_LENGTH_HALF,
								chess.blackPos.get(chess.blackPos.size() - 1).getX() * GRID_SPACING,
								chess.blackPos.get(chess.blackPos.size() - 1).getY() * GRID_SPACING + CROSS_LENGTH_HALF);
					}
				}
				break;
			case 1: // Black wins
			case 2: // White wins.
				if (chess.winnerPos.size() < 5) {
					throw new IllegalArgumentException("Invalid winnerPos's length: " + chess.winnerPos.size());
				}
				g2d.setColor(Color.red);
				for (int i = 0; i < chess.winnerPos.size(); i++) {
					g2d.drawLine(chess.winnerPos.get(i).getX() * GRID_SPACING - CROSS_LENGTH_HALF,
							chess.winnerPos.get(i).getY() * GRID_SPACING,
							chess.winnerPos.get(i).getX() * GRID_SPACING + CROSS_LENGTH_HALF,
							chess.winnerPos.get(i).getY() * GRID_SPACING);
					g2d.drawLine(chess.winnerPos.get(i).getX() * GRID_SPACING,
							chess.winnerPos.get(i).getY() * GRID_SPACING - CROSS_LENGTH_HALF,
							chess.winnerPos.get(i).getX() * GRID_SPACING,
							chess.winnerPos.get(i).getY() * GRID_SPACING + CROSS_LENGTH_HALF);
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected gameStatus value: " + chess.getGameStatus());
		}
	}
}
