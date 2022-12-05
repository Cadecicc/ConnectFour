/**
 * Implements a game of Connect 4, where players take turns
 * dropping checkers down columns.  The first player to get
 * 4 in a row is the winner.
 * @author Norm Krumpe
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ConnectFour extends JFrame {
	
	public static void main(String[] args) {
		new ConnectFour(6, 7);
	}

	private int rows, columns;
	private JPanel cellPanel, buttonPanel;
	private JLabel status;
	private Cell[][] cells;
	private JButton[] columnButtons;
	private static int CELL_SIZE = 70; // adjust this for larger or smaller cells
	private int lastPlayer;

	/**
	 * Constructs a Connect 4 grid with a specified number of rows and columns.
	 * 
	 * @param rows
	 *            the number of rows of squares
	 * @param columns
	 *            the number of columns of squares
	 */
	public ConnectFour(int rows, int columns) {
		super("Let's play Connect 4!");
		this.rows = rows;
		this.columns = columns;
		
		lastPlayer = 2;

		frameSetup();
		cellPanelSetup();
		buttonPanelSetup();
		statusSetup();
		pack();
		setVisible(true);
	}

	/**
	 * Sets up the details for the frame.
	 */
	private void frameSetup() {
		setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	/**
	 * Sets up the details for the square panel itself.
	 */
	private void cellPanelSetup() {
		// The layout is based on the number of rows
		cellPanel = new JPanel(new GridLayout(rows, 0));
		cellPanel.setPreferredSize(new Dimension(CELL_SIZE * columns, CELL_SIZE * rows));

		// Each square in the grid is a Square object
		cells = new Cell[rows][columns];
		for (int row = 0; row < cells.length; row++) {
			for (int cell = 0; cell < cells[0].length; cell++) {
				cells[row][cell] = new Cell();
				cellPanel.add(cells[row][cell]);
			}
		}
		add(cellPanel, BorderLayout.CENTER);
	}

	/**
	 * Sets up the buttons at the top of each column
	 */
	private void buttonPanelSetup() {
		buttonPanel = new JPanel(new GridLayout(1, 0));
		columnButtons = new JButton[columns];
		for (int i = 0; i < columns; i++) {
			columnButtons[i] = new JButton("" + i);
			
			ButtonClicked ear = new ButtonClicked();
			columnButtons[i].addActionListener(ear);
			
			columnButtons[i].setToolTipText("Move in column " + i);
			buttonPanel.add(columnButtons[i]);
		}
		add(buttonPanel, BorderLayout.NORTH);
	}

	/**
	 * Sets up the status label at the bottom of the frame, which keeps track of
	 * which player has the move.
	 */
	private void statusSetup() {
		
		status = new JLabel("Player 1's turn...", JLabel.CENTER);
		
		add(status, BorderLayout.SOUTH);
	}

	/**
	 * Updates a specified square by indicating which player should now occupy
	 * that square
	 * 
	 * @param row
	 *            the row of the square
	 * @param col
	 *            the column of the square
	 * @param player
	 *            which player should occupy that square (0=empty, 1=player1,
	 *            2=player2)
	 */
	public void updateCell(int row, int col, int player) {
		cells[row][col].setPlayer(player);
		
		status.setText("Player " + lastPlayer + "'s turn...");
		
		lastPlayer = player;
	}
	
	/**
	 * Listener class that responds to the buttons being clicked, in order to
	 * play the game
	 * @author Cade Ciccone
	 *
	 */
	class ButtonClicked implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton pressed = (JButton)e.getSource();
			int col = Integer.parseInt(pressed.getText());
			int row = rows - 1;
			
			//Getting which row the next cell should be filled in
			for (int i = row; i >= 0; i--) {
				if (cells[i][col].getPlayer() == 0) {
					row = i;
					break;
				}
			}
			
			//Updating the cell for which player is playing and if that column hasn't been
			//filled up yet
			if (lastPlayer == 1) {
				updateCell(row, col, 2);
			} else if (lastPlayer == 2) {
				updateCell(row, col, 1);
			}
			
			//Disabling the column button if that column is totally filled up
			zeroSpace(col, pressed);
			
			//Changing the label, disabling the buttons, and highlighting the winning
			//cells if one of the players won the game, or if it resulted in a tie
			wonOrTied();
		}
	}
	
	private void zeroSpace(int col, JButton b) {
		int zeroSpace = 0;
		for (int i = 0; i < rows; i++) {
			if (cells[i][col].getPlayer() == 0) {
				zeroSpace++;
			}
		}
		if (zeroSpace == 0) {
			b.setEnabled(false);
		}
	}
	
	private void wonOrTied() {
		//Changing the label and  disabling the buttons if someone won the game
		if (gameWon() == 1) {
			status.setText("Player 1 Wins!!!");
			for (JButton b : columnButtons) {
				b.setEnabled(false);
			}
		} else if (gameWon() == 2) {
			status.setText("Player 2 Wins!!!");
			for (JButton b : columnButtons) {
				b.setEnabled(false);
			}
		}
		
		//Changing the label and disabling the buttons if the game was a tie
		if (gameTied()) {
			status.setText("Tie Game");
			for (JButton b : columnButtons) {
				b.setEnabled(false);
			}
		}
	}
	
	/**
	 * Method to check if the game was won by player 1 or player 2, or no one
	 * @return 1 if player 1 won the game, 2 if player 2 won the game, or 0 if no
	 * one has won yet
	 * @return 1 if player 1 won, 2 if player 2 won, or 0 if no one has won yet
	 */
	private int gameWon() {
		
		//Checking row by row
		if (gameWonRowbyRow() == 1) {
			return 1;
		} else if (gameWonRowbyRow() == 2) {
			return 2;
		}
		
		//Checking column by column
		if (gameWonColumnbyColumn() == 1) {
			return 1;
		} else if (gameWonColumnbyColumn() == 2) {
			return 2;
		}
		
		//Checking diagonally
		if (gameWonDiagonally() == 1) {
			return 1;
		} else if (gameWonDiagonally() == 2) {
			return 2;
		}
		
		return 0;
	}
	
	/**
	 * Method to check if anyone won row by row
	 * @return 1 if player 1 won in a row, 2 if player 2 won in a row, or 0 if nobody
	 * won in a row
	 */
	private int gameWonRowbyRow() {
		for (int i = 0; i < rows; i++) {
					
			for (int j = 0; j < columns - 3; j++) {
				if (cells[i][j].getPlayer() == 1 && cells[i][j + 1].getPlayer() == 1
						&& cells[i][j + 2].getPlayer() == 1 && cells[i][j + 3].getPlayer() == 1) {
					cells[i][j].setBackground(Color.green);
					cells[i][j + 1].setBackground(Color.green);
					cells[i][j + 2].setBackground(Color.green);
					cells[i][j + 3].setBackground(Color.green);
					return 1;
				}
				if (cells[i][j].getPlayer() == 2 && cells[i][j + 1].getPlayer() == 2
						&& cells[i][j + 2].getPlayer() == 2 && cells[i][j + 3].getPlayer() == 2) {
					cells[i][j].setBackground(Color.green);
					cells[i][j + 1].setBackground(Color.green);
					cells[i][j + 2].setBackground(Color.green);
					cells[i][j + 3].setBackground(Color.green);
					return 2;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Method to check if anyone won column by column
	 * @return 1 if player 1 won in a column, 2 if player 2 won in a column, or 0 if nobody
	 * won in a column
	 */
	private int gameWonColumnbyColumn() {
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows - 3; j++) {
				if (cells[j][i].getPlayer() == 1 && cells[j + 1][i].getPlayer() == 1
						&& cells[j + 2][i].getPlayer() == 1 && cells[j + 3][i].getPlayer() == 1) {
					cells[j][i].setBackground(Color.green);
					cells[j + 1][i].setBackground(Color.green);
					cells[j + 2][i].setBackground(Color.green);
					cells[j + 3][i].setBackground(Color.green);
					return 1;
				}
				if (cells[j][i].getPlayer() == 2 && cells[j + 1][i].getPlayer() == 2
						&& cells[j + 2][i].getPlayer() == 2 && cells[j + 3][i].getPlayer() == 2) {
					cells[j][i].setBackground(Color.green);
					cells[j + 1][i].setBackground(Color.green);
					cells[j + 2][i].setBackground(Color.green);
					cells[j + 3][i].setBackground(Color.green);
					return 2;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Method to check if anyone won diagonally
	 * @return 1 if player 1 won diagonally, 2 if player 2 won diagonally, or 0 if nobody
	 * won diagonally
	 */
	private int gameWonDiagonally() {
		//Checking diagonally down to the right
		if (gameWonDiagonallyD() == 1) {
			return 1;
		} else if (gameWonDiagonallyD() == 2) {
			return 2;
		}
				
		//Checking diagonally up to the right
		if (gameWonDiagonallyU() == 1) {
			return 1;
		} else if (gameWonDiagonallyU() == 2) {
			return 2;
		}
			
		return 0;
	}
	
	private int gameWonDiagonallyD() {
		for (int i = 0; i < rows - 3; i++) {
			
			for (int j = 0; j < columns - 3; j++) {
				if (cells[i][j].getPlayer() == 1 && cells[i + 1][j + 1].getPlayer() == 1
						&& cells[i + 2][j + 2].getPlayer() == 1 && cells[i + 3][j + 3].getPlayer() == 1) {
					cells[i][j].setBackground(Color.green);
					cells[i + 1][j + 1].setBackground(Color.green);
					cells[i + 2][j + 2].setBackground(Color.green);
					cells[i + 3][j + 3].setBackground(Color.green);
					return 1;
				}
				if (cells[i][j].getPlayer() == 2 && cells[i + 1][j + 1].getPlayer() == 2
						&& cells[i + 2][j + 2].getPlayer() == 2 && cells[i + 3][j + 3].getPlayer() == 2) {
					cells[i][j].setBackground(Color.green);
					cells[i + 1][j + 1].setBackground(Color.green);
					cells[i + 2][j + 2].setBackground(Color.green);
					cells[i + 3][j + 3].setBackground(Color.green);
					return 2;
				}
			}
		}
		return 0;
	}
	
	private int gameWonDiagonallyU() {
		for (int i = rows - 1; i > 3; i--) {
			
			for (int j = 0; j < columns - 3; j++) {
				if (cells[i][j].getPlayer() == 1 && cells[i - 1][j + 1].getPlayer() == 1
						&& cells[i - 2][j + 2].getPlayer() == 1 && cells[i - 3][j + 3].getPlayer() == 1) {
					cells[i][j].setBackground(Color.green);
					cells[i - 1][j + 1].setBackground(Color.green);
					cells[i - 2][j + 2].setBackground(Color.green);
					cells[i - 3][j + 3].setBackground(Color.green);
					return  1;
				}
				if (cells[i][j].getPlayer() == 2 && cells[i - 1][j + 1].getPlayer() == 2
						&& cells[i - 2][j + 2].getPlayer() == 2 && cells[i - 3][j + 3].getPlayer() == 2) {
					cells[i][j].setBackground(Color.green);
					cells[i - 1][j + 1].setBackground(Color.green);
					cells[i - 2][j + 2].setBackground(Color.green);
					cells[i - 3][j + 3].setBackground(Color.green);
					return  2;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Method to check if the game resulted in a tie game
	 * @return true if the game ended in a tie and false otherwise
	 */
	private boolean gameTied() {
		
		//Checking to see if all of the spaces have been filled up or not
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (cells[i][j].getPlayer() == 0) {
					return false;
				}
			}
		}
		
		//Making sure no one has won the game and if not, returning true
		if (gameWon() == 1 || gameWon() == 2) {
			return false;
		} else {
			return true;
		}
	}
	
}
