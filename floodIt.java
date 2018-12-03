// CS 201 Final Project
// Cater Wang and Eric Leung
// 12/1/2018

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class floodIt extends Applet implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// instance variables
	TileCanvas T;
	Label timesMoved;
	Label maximumMoves;
	Choice gameBoardSize;
	int movesTaken = 0;
	int size = 14; // game board will be 14x14 by default

	// applet initializer
	public void init() {
		Label title = new Label("Flood-It");
		title.setFont(new Font("Courier", Font.BOLD, 32));
		title.setAlignment(Label.CENTER);

		T = new TileCanvas(size, this);
		T.addMouseListener(T);

		this.setLayout(new BorderLayout());
		this.add("North", title);
		this.add("Center", T);
		this.add("South", functionArea());
	}

	// returns a function area that contains
	// (1) number of moves made by player
	// (2) a drop-down list for changing game board size
	// and (3) a reset button for starting a new game
	public Panel functionArea() {
		// number of moves
		timesMoved = new Label("0 ");
		maximumMoves = new Label(calculateMaxMoves(size));
		// size drop-down list
		gameBoardSize = new Choice();
		gameBoardSize.add("2x2");
		gameBoardSize.add("6x6");
		gameBoardSize.add("10x10");
		gameBoardSize.add("14x14");
		gameBoardSize.add("18x18");
		gameBoardSize.add("22x22");
		gameBoardSize.add("26x26");
		gameBoardSize.select(3); // display 14x14 when applet starts
		// to match default configuration
		gameBoardSize.addItemListener(this);
		// reset button
		Button reset = new Button("Reset");
		reset.addActionListener(this);
		
		Panel functionArea = new Panel();
		functionArea.setLayout(new FlowLayout());
		functionArea.add(new Label("Moves:"));
		functionArea.add(timesMoved);
		timesMoved.setFont(new Font("Courier", Font.PLAIN, 15));
		functionArea.add(maximumMoves);
		functionArea.add(gameBoardSize);
		gameBoardSize.setFont(new Font("Courier", Font.PLAIN, 15));
		functionArea.add(reset);
		return functionArea;
	}

	// change the maximum moves allowed based on grid size
	public String calculateMaxMoves(int size) {
		switch (size) {
		case 2:
			return "/ 3";
		case 6:
			return "/ 10";
		case 10:
			return "/ 17";
		case 14:
			return "/ 25";
		case 18:
			return "/ 32";
		case 22:
			return "/ 39";
		default: // case 26
			return "/46";
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			String label = ((Button) event.getSource()).getLabel();
			if (label.equals("Reset")) {
				T.size = this.size;
				T.newGame(size);
				T.repaint();
				movesTaken = 0;
				timesMoved.setText("0 ");
				maximumMoves.setText(calculateMaxMoves(size));
			}
		}
	}

	public void itemStateChanged(ItemEvent event) {
		if (event.getSource() == gameBoardSize) {
			int possibleSizes[] = { 2, 6, 10, 14, 18, 22, 26 };
			size = possibleSizes[gameBoardSize.getSelectedIndex()];
		}
	}
}

class TileCanvas extends Canvas implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// instance variables
	int size;
	int[][] gameBoard;
	floodIt parent;

	// for use in multiple functions
	int tileLength;
	boolean[][] visitedArea;

	// constructor
	public TileCanvas(int size, floodIt parent) {
		this.size = size;
		newGame(size);
		this.parent = parent;
	}

	// generate a new random integer matrix
	public void newGame(int size) {
		gameBoard = new int[size][size];
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				gameBoard[i][j] = (int) (Math.random() * 6);
			}
		}
	}

	public void paint(Graphics g) {
		Dimension d = getSize();
		tileLength = d.width / size;
		for (int j = 0; j < size; j++) {
			int y = j * tileLength;
			int x = 0;
			for (int i = 0; i < size; i++) {
				g.setColor(getColor(gameBoard[i][j]));
				g.fillRect(x, y, tileLength, tileLength);
				x = x + tileLength;
			}
		}
	}

	// create an array of colors which we will refer to by their index
	public Color getColor(int i) {
		Color[] colorTable = {Color.green, Color.cyan,
				Color.red, Color.yellow, Color.magenta, Color.orange };
		return colorTable[i];
	}

	public void mouseClicked(MouseEvent event) {
		Point p = event.getPoint();
		int i = p.x / tileLength;
		int j = p.y / tileLength;
		try { // check if user clicks out of game board
			getColor(gameBoard[i][j]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		// if not, refresh game board by flooding it
		// with color of the clicked point
		// and increase movesTaken by 1;
		int clickedPoint = gameBoard[i][j];
		refreshGameboard(clickedPoint);
		parent.timesMoved.setText(Integer.toString(++parent.movesTaken));
	}

	public void refreshGameboard(int colorIndex) {
		// helper function for flood
		visitedArea = new boolean[size][size];
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				visitedArea[i][j] = false;
			}
		}
		flood(colorIndex, 0, 0);
	}

	public void flood(int colorIndex, int x, int y) {
		// recursively flood the gameboard by a given color
		visitedArea[x][y] = true;
		int currentColorIndex = gameBoard[x][y];
		// check north
		if (y != 0 && !visitedArea[x][y - 1]
				&& gameBoard[x][y - 1] == currentColorIndex)
			flood(colorIndex, x, y - 1);
		// check east
		if (x + 1 < size && !visitedArea[x + 1][y]
				&& gameBoard[x + 1][y] == currentColorIndex)
			flood(colorIndex, x + 1, y);
		// check south
		if (y + 1 < size && !visitedArea[x][y + 1]
				&& gameBoard[x][y + 1] == currentColorIndex)
			flood(colorIndex, x, y + 1);
		// check west
		if (x != 0 && !visitedArea[x - 1][y]
				&& gameBoard[x - 1][y] == currentColorIndex)
			flood(colorIndex, x - 1, y);
		gameBoard[x][y] = colorIndex;
		repaint();
	}

	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	public void mousePressed(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
}
