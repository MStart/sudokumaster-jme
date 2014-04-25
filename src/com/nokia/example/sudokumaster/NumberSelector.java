/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * Implements the number selection, with which user can input numbers on the
 * sudoku board.
 */
public class NumberSelector
    extends View {

    private static final int TEXT_COLOR = 0x00FFFFFF;
    private final Image buttonImage;
    private final Image largeButtonImage;
    private final Listener listener;
    private TiledLayer numberButtons;
    private Sprite cButton;
    private final int numberButtonWidth, numberButtonHeight;
    private final int cButtonWidth, cButtonHeight;
    private int keyPressed;
    private volatile boolean refreshButtons = true;

    public NumberSelector(int width, int height, Image buttonImage,
            Image largeButtonImage, Listener listener) {
        super(0, 0, width, height);
        this.buttonImage = buttonImage;
        this.largeButtonImage = largeButtonImage;
        this.listener = listener;
        numberButtonWidth = buttonImage.getWidth() / 2;
        numberButtonHeight = buttonImage.getHeight();
        cButtonWidth = largeButtonImage.getWidth() / 2;
        cButtonHeight = largeButtonImage.getHeight();
    }

    public void setLeft(int left) {
        super.setLeft(left);
        refreshButtonPositions();
    }

    public void setRight(int right) {
        super.setRight(right);
        refreshButtonPositions();
    }

    public void setWidth(int width) {
        super.setWidth(width);
        refreshButtonPositions();
    }

    public void setTop(int top) {
        super.setTop(top);
        refreshButtonPositions();
    }

    public void setBottom(int bottom) {
        super.setBottom(bottom);
        refreshButtonPositions();
    }

    public void setHeight(int height) {
        super.setHeight(height);
        refreshButtonPositions();
    }

    private void refreshButtonPositions() {
        if (numberButtons != null) {
            numberButtons.setPosition(left, top);
        }
        if (cButton != null) {
            cButton.setPosition(left, top + numberButtonHeight * 3);
        }
    }

    /**
     * Paints the number selector.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        if (numberButtons == null || cButton == null) {
            numberButtons = new TiledLayer(3, 3, buttonImage, numberButtonWidth,
                                           numberButtonHeight);
            cButton = new Sprite(largeButtonImage, cButtonWidth, cButtonHeight);
            refreshButtonPositions();
        }
        if (refreshButtons) {
            refreshButtons();
        }
        numberButtons.paint(g);
        cButton.paint(g);
        paintNumbers(g);
    }

    private void paintNumbers(Graphics g) {
        g.setColor(TEXT_COLOR);
        final int xOffset = left + numberButtonWidth / 2;
        final int yOffset = top + numberButtonHeight / 2 - g.getFont().getHeight() / 2;
        int x, y, n;
        for (int row = 0; row < 3; row++) {
            y = yOffset + row * numberButtonHeight;
            for (int col = 0; col < 3; col++) {
                x = xOffset + col * numberButtonWidth;
                n = row * 3 + col + 1;
                g.drawString(String.valueOf(n), x, y,
                             Graphics.HCENTER | Graphics.TOP);
            }
        }
        x = cButton.getX() + cButton.getWidth() / 2;
        y = cButton.getY() + cButton.getHeight() / 2 - g.getFont().getHeight() / 2;
        g.drawString("C", x, y, Graphics.HCENTER | Graphics.TOP);
    }

    private void refreshButtons() {
        refreshButtons = false;
        numberButtons.fillCells(0, 0, 3, 3, 1);
        cButton.setFrame(0);
        if (keyPressed > 9) {
            cButton.setFrame(1);
        }
        else if (keyPressed > 0) {
            int col = (keyPressed - 1) % 3;
            int row = (keyPressed - 1) / 3;
            numberButtons.setCell(col, row, 2);
        }
    }

    /**
     * Handle pointer event received from SudokuCanvas.
     * If view is visible and the pointer hits a number, inform
     * listeners of number selection.
     * @param type pointer press, release or drag
     * @param x coordinate of event
     * @param y coordinate of event
     */
    public void handlePointerEvent(int type, int x, int y) {
        if (!isVisible()) {
            return;
        }
        if (type == View.POINTER_RELEASED) {
            if (hits(x, y)) {
                listener.numberSelected(getKey(x, y));
            }
        }
        else {
            setKeyPressed(hits(x, y) ? getKey(x, y) : 0);
        }
    }

    private int getKey(int x, int y) {
        if (y < top + numberButtonHeight * 3) {
            int col = Math.min((x - left) / numberButtonWidth, 2);
            int row = Math.min((y - top) / numberButtonHeight, 2);
            return col + 3 * row + 1;
        }
        else {
            return 10;
        }
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setKeyPressed(0);
    }

    public void setKeyPressed(int n) {
        if (keyPressed != n) {
            keyPressed = n;
            refreshButtons = true;
            invalidate();
        }
    }

    /**
     * Handles key events, which are sent by SudokuCanvas.
     * Navigate view or select number based on key code.
     * @param type key press, release or repeat
     * @param key key code
     */
    public void keyEvent(int type, int key) {
        if (type == KEY_RELEASED) {
            return;
        }
        if (keyPressed > 0) {
            switch (key) {
                case KEY_UP:
                    setKeyPressed((keyPressed - 3 - 1 + 12) % 12 + 1);
                    break;
                case KEY_DOWN:
                    setKeyPressed((keyPressed + 3 - 1) % 12 + 1);
                    break;
                case KEY_LEFT:
                    setKeyPressed(
                            (keyPressed - 1) / 3 * 3 + (keyPressed - 1 + 2) % 3 + 1);
                    break;
                case KEY_RIGHT:
                    setKeyPressed(
                            (keyPressed - 1) / 3 * 3 + (keyPressed + 1 + 2) % 3 + 1);
                    break;
                case KEY_SELECT:
                    if (type == KEY_PRESSED) {
                        listener.numberSelected(keyPressed);
                    }
                    break;
                default:
                    break;
            }
        }
        else {
            setKeyPressed(5);
        }
    }

    public static interface Listener {
        void numberSelected(int n);
    }
}
