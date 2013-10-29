/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * View for the sudoku game board, which is implemented
 * with TiledLayer.
 */
public class SudokuView
    extends View {

    private static final int BG0 = 1;
    private static final int BG1 = 2;
    private static final int BG0_HIGHLIGHTED = 3;
    private static final int BG1_HIGHLIGHTED = 4;
    private static final int BG_ERROR = 5;
    private static final int ANIMATION_DURATION = 20;
    private static final int NUMBER_COLOR = 0x000000ff;
    private static final int PUZZLE_NUMBER_COLOR = 0x00000000;
    private static final int BACKGROUND_COLOR = 0x004e2316;
    private int[][] numbers = new int[9][9];
    private int[][] puzzle = new int[9][9];
    private int[][] animate = new int[9][9];
    private TiledLayer board;
    private Image boardImage;
    private volatile boolean refreshBoard = true;
    private Listener listener;
    private int tileSize;
    private int selectedCol = 4, selectedRow = 4;
    private int moves = 0;
    private long startTime;
    private long victoryTimeSeconds = -1;

    public SudokuView(Listener listener) {
        super();
        this.listener = listener;
    }

    public void setBoardImage(Image image) {
        board = null;
        boardImage = image;
        refreshBoard = true;
    }

    public void setBoardSize(int boardSize) {
        setSize(boardSize, boardSize);
        tileSize = boardSize / 9;
    }

    /**
     * Update the view.
     * @see com.nokia.example.sudokumaster.View#update()
     */
    public void update() {
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                if (animate[col][row] > 0) {
                    boolean old = drawError(col, row);
                    animate[col][row]--;
                    if (old != drawError(col, row)) {
                        refreshBoard = true;
                        invalidate();
                    }
                }
            }
        }
    }

    private boolean drawError(int col, int row) {
        return 4 * animate[col][row] / ANIMATION_DURATION % 2 == 1;
    }

    /**
     * Draws the board.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        drawBoard(g);
        drawNumbers(g);
    }

    /**
     * Draw the board with the help of a TiledLayer.
     * @see javax.microedition.lcdui.game.TiledLayer
     * @param g Graphics object
     */
    private void drawBoard(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(left, top, width, height);
        if (boardImage == null) {
            return;
        }
        if (board == null) {
            board = new TiledLayer(9, 9, boardImage, boardImage.getWidth() / 5,
                                   boardImage.getHeight());
            board.setPosition(left, top);
        }
        if (refreshBoard) {
            refreshBoard();
        }
        board.paint(g);
    }

    private void refreshBoard() {
        refreshBoard = false;
        board.fillCells(0, 0, 9, 9, BG0);
        board.fillCells(0, 3, 3, 3, BG1);
        board.fillCells(3, 0, 3, 3, BG1);
        board.fillCells(3, 6, 3, 3, BG1);
        board.fillCells(6, 3, 3, 3, BG1);
        // If the user selected a cell, hightlight  the other cells
        // in the row and column the selected cell is in.
        if (selectedCol >= 0 && selectedRow >= 0) {
            for (int col = 0; col < 9; col++) {
                if (col != selectedCol) {
                    final int bg = board.getCell(col, selectedRow) == 
                            BG0 ? BG0_HIGHLIGHTED : BG1_HIGHLIGHTED;
                    board.setCell(col, selectedRow, bg);
                }
            }
            for (int row = 0; row < 9; row++) {
                if (row != selectedRow) {
                    final int bg = board.getCell(selectedCol, row) == 
                            BG0 ? BG0_HIGHLIGHTED : BG1_HIGHLIGHTED;
                    board.setCell(selectedCol, row, bg);
                }
            }
        }
        // If the user selects a number that is next to or on the same
        // row or column the same number, the conflicting numbers will
        // be flickered.
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                if (animate[col][row] > 0 && drawError(col, row)) {
                    board.setCell(col, row, BG_ERROR);
                }
            }
        }
    }

    /**
     * Position and draw the numbers on the board.
     * @param g Graphics object
     */
    private void drawNumbers(Graphics g) {
        // Draw the puzzle numbers. The 'puzzle' array
        // holds the places and the numbers to be drawn.
        g.setColor(PUZZLE_NUMBER_COLOR);
        final int xOffset = left + (tileSize + 1) / 2;
        final int yOffset = top + (tileSize + 1) / 2 - g.getFont().getHeight() / 2;
        int x, y, n;
        for (int col = 0; col < 9; col++) {
            x = xOffset + col * tileSize;
            for (int row = 0; row < 9; row++) {
                y = yOffset + row * tileSize;
                n = puzzle[col][row];
                if (n > 0) {
                    g.drawString(String.valueOf(n), x, y,
                                 Graphics.HCENTER | Graphics.TOP);
                }
            }
        }
        // Draw the numbers, which are the result of user input.
        // 'numbers' array holds the numbers and places where to draw.
        g.setColor(NUMBER_COLOR);
        for (int col = 0; col < 9; col++) {
            x = xOffset + col * tileSize;
            for (int row = 0; row < 9; row++) {
                y = yOffset + row * tileSize;
                n = numbers[col][row];
                if (n > 0) {
                    g.drawString(String.valueOf(n), x, y,
                                 Graphics.HCENTER | Graphics.TOP);
                }
            }
        }
    }

    public void handlePointerEvent(int type, int x, int y) {
        if (!isVisible() || !hits(x, y)) {
            return;
        }
        if (tileSize > 0) {
            selectCell(Math.min((x - left) / tileSize, 8), Math.min(
                    (y - top) / tileSize, 8));
        }
        if (type == View.POINTER_RELEASED && puzzle[selectedCol][selectedRow] <= 0) {
            listener.onCellSelected();
        }
    }

    private void selectCell(int col, int row) {
        selectedCol = col;
        selectedRow = row;
        refreshBoard = true;
        invalidate();
    }

    public void keyEvent(final int type, final int key) {
        if (type == KEY_RELEASED) {
            return;
        }
        switch (key) {
            case KEY_UP:
                selectCell(selectedCol, (selectedRow - 1 + 9) % 9);
                break;
            case KEY_DOWN:
                selectCell(selectedCol, (selectedRow + 1 + 9) % 9);
                break;
            case KEY_LEFT:
                selectCell((selectedCol - 1 + 9) % 9, selectedRow);
                break;
            case KEY_RIGHT:
                selectCell((selectedCol + 1 + 9) % 9, selectedRow);
                break;
            case KEY_SELECT:
                if (type == KEY_PRESSED && puzzle[selectedCol][selectedRow] <= 0) {
                    listener.onCellSelected();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Checks that the user input number is valid according to game logic,
     * if so, update the number of moves, check if the whole board is filled
     * and notify listeners that a new number has been set.
     * @param n the new number
     */
    public void setNumber(final int n) {
        if (cellSelected() && puzzle[selectedCol][selectedRow] < 1) {
            final int newNumber = n > 9 || n < 1 ? 0 : n;
            if (validateNumber(n) && numbers[selectedCol][selectedRow] != newNumber) {
                numbers[selectedCol][selectedRow] = newNumber;
                moves++;
                if (isComplete()) {
                    victoryTimeSeconds = getElapsedSeconds();
                }
                invalidate();
            }
        }
        listener.onSetNumber();
    }

    private boolean cellSelected() {
        return selectedCol >= 0 && selectedCol < 9 && selectedRow >= 0 && selectedCol < 9;
    }

    private int getNumber(final int col, final int row) {
        if (puzzle[col][row] > 0) {
            return puzzle[col][row];
        }
        return numbers[col][row];
    }

    /**
     * Validate the number by checking that the same number is not next to the
     * selected cell, or in the same row or column.
     * @param n the number to be validated.
     * @return true or false depending on the validation result
     */
    private boolean validateNumber(final int n) {
        return n == 0 || (validateRow(n) & validateCol(n) & validateBlock(n));
    }

    private boolean validateRow(final int n) {
        for (int col = 0; col < 9; col++) {
            if (getNumber(col, selectedRow) == n && col != selectedCol) {
                animateCell(col, selectedRow);
                return false;
            }
        }
        return true;
    }

    private boolean validateCol(final int n) {
        for (int row = 0; row < 9; row++) {
            if (getNumber(selectedCol, row) == n && row != selectedRow) {
                animateCell(selectedCol, row);
                return false;
            }
        }
        return true;
    }

    private boolean validateBlock(final int n) {
        final int colOffset = selectedCol / 3 * 3;
        final int rowOffset = selectedRow / 3 * 3;
        int col, row;
        for (int c = 0; c < 3; c++) {
            col = colOffset + c;
            for (int r = 0; r < 3; r++) {
                row = rowOffset + r;
                if (getNumber(col, row) == n && !(col == selectedCol && row == selectedRow)) {
                    animateCell(col, row);
                    return false;
                }
            }
        }
        return true;
    }

    private void animateCell(int col, int row) {
        animate[col][row] = ANIMATION_DURATION;
        refreshBoard = true;
        invalidate();
    }

    /**
     * Checks if the board is completed by checking that
     * there are no empty cells.
     * @return true or false depending on if an empty cell was found
     */
    public boolean isComplete() {
        int n;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                n = getNumber(i, j);
                if (n <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static interface Listener {
        void onCellSelected();
        void onSetNumber();
    }

    public void restart() {
        numbers = new int[9][9];
        moves = 0;
        startTime = System.currentTimeMillis();
        victoryTimeSeconds = -1;
        selectCell(4, 4);
    }

    public void newGame(int[][] puzzle) {
        this.puzzle = puzzle;
        restart();
    }

    public int getMoves() {
        return moves;
    }

    public int getEmpty() {
        int empty = 0;
        int n;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                n = getNumber(i, j);
                if (n <= 0) {
                    empty++;
                }
            }
        }
        return empty;
    }

    public long getElapsedSeconds() {
        if (victoryTimeSeconds > -1) {
            return victoryTimeSeconds;
        }
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    private void setElapsedSeconds(long elapsedSeconds) {
        if (isComplete()) {
            victoryTimeSeconds = elapsedSeconds;
        }
        startTime = System.currentTimeMillis() - elapsedSeconds * 1000;
    }

    /**
     * Gets the saved state of the game.
     * @return the saved state as a byte array
     */
    public byte[] getState() {
        ByteArrayOutputStream bout = null;
        try {
            bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    dout.writeByte(puzzle[i][j]);
                    dout.writeByte(numbers[i][j]);
                }
            }
            dout.writeByte(selectedCol);
            dout.writeByte(selectedRow);
            dout.writeInt(moves);
            dout.writeLong(getElapsedSeconds());
            return bout.toByteArray();
        }
        catch (IOException e) {
            return new byte[0];
        }
        finally {
            try {
                if (bout != null) {
                    bout.close();
                }
            }
            catch (IOException e) {
                // Empty implementation.
            }
        }
    }

    /**
     * Sets the state of the game.
     * @param state state in byte array
     */
    public void setState(byte[] state) {
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(state));
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    puzzle[i][j] = din.readByte();
                    numbers[i][j] = din.readByte();
                }
            }
            selectedCol = din.readByte();
            selectedRow = din.readByte();
            moves = din.readInt();
            setElapsedSeconds(din.readLong());
        }
        catch (IOException e) {
            // Empty implementation.
        }
    }
}
