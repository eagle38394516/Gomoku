package io.github.eagle38394516;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The main frame of Gomoku game for EEC 289A WQ 2019.
 * 
 * @author Chen Wang
 * @version 4.0
 */
// "There are two advanced rules:\n" +
// "1. The black side cannot have two valid concatenate chains with three or four unbroken pieces, but there¡¯s no restriction to the white side.\n"
// +
// "2. Each player cannot form an unbroken chain with over five pieces.\n\n" +
// "Current bugs:\n1. Cannot check the broken rule correctly.",
public final class MainBody extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * The expected panel width to show in the frame.
	 */
	public static final int PANEL_WIDTH = 800;

	/**
	 * The expected panel height to show in the frame.
	 */
	public static final int PANEL_HEIGHT = 800;

	/**
	 * The enabled color for the advanced rules switch button's background.
	 */
	public static final Color ADV_RULES_DISABLED_COLOR = new Color(244, 177, 131);

	/**
	 * The disabled color for the advanced rules switch button's background.
	 */
	public static final Color ADV_RULES_ENABLED_COLOR = new Color(168, 208, 141);

	/**
	 * The prefix of the title of the frame.
	 */
	private static final String VERSION_STRING = "v4.0";

	/**
	 * The prefix of the title of the frame.
	 */
	private static final String FRAME_TITLE = "Gomoku for EEC 289A WQ 2019 " + VERSION_STRING;

	/**
	 * The button panel of the frame.
	 */
	private final JPanel buttonsPanel = new JPanel();

	/**
	 * The reset button.
	 */
	private final JButton resetBtn = new JButton("Reset");

	/**
	 * The undo button.
	 */
	private final JButton undoBtn = new JButton("Undo");

	/**
	 * The random placing button.
	 */
	private final JButton randomBtn = new JButton("Random Placing");

	/**
	 * The advanced rules enable switch button.
	 */
	private final JButton advancedRulesBtn = new JButton("Enable Advanced Rules");

	private final JButton selfDefeatBtn = new JButton("Self Defeat");

	/**
	 * The debug function button.
	 */
	private final JButton debugBtn = new JButton("Debug Function");

	private final String AUTO_BLACK_STRING = "Auto: Black";
	private final Color AUTO_BLACK_BGCOLOR = Color.black;
	private final Color AUTO_BLACK_STRCOLOR = Color.white;
	private final String AUTO_WHITE_STRING = "Auto: White";
	private final Color AUTO_WHITE_BGCOLOR = Color.white;
	private final Color AUTO_WHITE_STRCOLOR = Color.black;
	private final String AUTO_OFF_STRING = "Auto: Off";
	private final Color AUTO_OFF_BGCOLOR = Color.gray;
	private final Color AUTO_OFF_STRCOLOR = Color.white;

	private final JButton autoBtn = new JButton(AUTO_OFF_STRING);

	/**
	 * The gaming panel. (Main panel)
	 */
	public final GomokuPanel paintingPanel = GomokuPanel.getInstance();

	public static MainBody instance;

	/**
	 * The chess tool box class.
	 */
	private final Chess chess = Chess.getInstance();

	/**
	 * To mark whether the random placer module is running.
	 */
	private boolean isRunning = false;

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		instance = new MainBody();
	}

	/**
	 * Initialize the main frame.
	 */
	public MainBody() {
		buttonsInitialization();
		frameInitialization();
	}

	/**
	 * Add action listener for each button and add them to the button panel.
	 */
	private void buttonsInitialization() {
		advancedRulesBtn.setBackground(ADV_RULES_ENABLED_COLOR);
		autoBtn.setBackground(AUTO_OFF_BGCOLOR);
		autoBtn.setForeground(AUTO_OFF_STRCOLOR);

		resetBtn.addActionListener(e -> {
			paintingPanel.resetAll();
		});

		undoBtn.addActionListener(e -> {
			paintingPanel.undo();
		});

		advancedRulesBtn.addActionListener(e -> {
			if (chess.whitePos.isEmpty() || chess.blackPos.isEmpty()) {
				// Changing the rules only when the board is empty.
				if (chess.isUsingAdvancedRules()) {
					chess.setUsingAdvancedRules(false);
					advancedRulesBtn.setBackground(ADV_RULES_ENABLED_COLOR);
					advancedRulesBtn.setText("Enable Advanced Rules");
					setTitle(FRAME_TITLE + " - Advanced Rules DISABLED");
				} else {
					chess.setUsingAdvancedRules(true);
					advancedRulesBtn.setBackground(ADV_RULES_DISABLED_COLOR);
					advancedRulesBtn.setText("Disable Advanced Rules");
					setTitle(FRAME_TITLE + " - Advanced Rules ENABLED");
				}
			}
		});

		selfDefeatBtn.addActionListener(e -> {
			paintingPanel.resetAll();
			if (chess.getAutoPlacing() == Chess.AUTO_PLACING_BLACK) {
				// This: white
				// That: black
				// int blackWin = 0, whiteWin = 0;
				// for (int repeat = 0; repeat < 1000; repeat++) {
				while (chess.getGameStatus() != 1 && chess.getGameStatus() != 2) {
					chess.place(chess.calcBestPos(chess.whitePos, chess.blackPos));
					paintingPanel.repaint();
				}
				// if (chess.getGameStatus() == 1) {
				// blackWin++;
				// } else {
				// whiteWin++;
				// }
				// paintingPanel.resetAll();
				// }
				// JOptionPane.showMessageDialog(null,
				// String.format("Black wins %d rounds, white wins %d rounds.\nThe black winning rate: %f\n",
				// blackWin, whiteWin, ((float) blackWin) / (blackWin +
				// whiteWin)));
			}
		});

		randomBtn.addActionListener(e -> {
			if (isRunning) {
				isRunning = false;
			} else {
				isRunning = true;
				new Thread() {
					@Override
					public void run() {
						while (isRunning) {
							if (chess.isGameOver()) {
								paintingPanel.resetAll();
								try {
									Thread.sleep(50);
								} catch (Exception exception) {
									exception.printStackTrace();
								}
							}
							paintingPanel.placeByRandom();
							try {
								Thread.sleep(5);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
							if (chess.isGameOver()) {
								isRunning = false;
							}
						}
					}
				}.start();
			}
		});

		debugBtn.addActionListener(e -> {
			paintingPanel.randomPlacingOneStep();
		});

		autoBtn.addActionListener(e -> {
			chess.changingAutoPlacing();
			switch (chess.getAutoPlacing()) {
				case Chess.AUTO_PLACING_BLACK:
					autoBtn.setText(AUTO_BLACK_STRING);
					autoBtn.setBackground(AUTO_BLACK_BGCOLOR);
					autoBtn.setForeground(AUTO_BLACK_STRCOLOR);
					break;
				case Chess.AUTO_PLACING_OFF:
					autoBtn.setText(AUTO_OFF_STRING);
					autoBtn.setBackground(AUTO_OFF_BGCOLOR);
					autoBtn.setForeground(AUTO_OFF_STRCOLOR);
					break;
				case Chess.AUTO_PLACING_WHITE:
					autoBtn.setText(AUTO_WHITE_STRING);
					autoBtn.setBackground(AUTO_WHITE_BGCOLOR);
					autoBtn.setForeground(AUTO_WHITE_STRCOLOR);
					break;
				default:
					throw new RuntimeException("Invalid autoPlacing - " + chess.getAutoPlacing());
			}
		});

		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(resetBtn);
		buttonsPanel.add(undoBtn);
		buttonsPanel.add(advancedRulesBtn);
		buttonsPanel.add(selfDefeatBtn);
		buttonsPanel.add(randomBtn);
		buttonsPanel.add(debugBtn);
		buttonsPanel.add(autoBtn);
	}

	/**
	 * Add the components to the frame and set the basic information.
	 */
	private void frameInitialization() {
		paintingPanel.setBackground(Color.GRAY);

		this.setLayout(new BorderLayout());
		this.add(buttonsPanel, BorderLayout.NORTH);
		this.add(paintingPanel, BorderLayout.CENTER);

		this.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		this.setTitle(FRAME_TITLE + " - Advanced Rules DISABLED");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize((int) (getWidth() - paintingPanel.getWidth())
				+ PANEL_WIDTH, (int) (getHeight() - paintingPanel.getHeight())
				+ PANEL_HEIGHT);
	}
}
