/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Implements buttons.
 */
public class Button
    extends View {
    // Constants
    public static final int BUTTON_NORMAL = 0;
    public static final int BUTTON_PRESSED = 1;
    private static final int TEXT_COLOR = 0x00CCCCCC;
    private static final int TEXT_PRESSED_COLOR = 0x00FFFFFF;
    private static final int BACKGROUND_COLOR = 0x004e2316;

    // Members
    private String text = "";
    private volatile int state = BUTTON_NORMAL;
    private final Listener listener;

    public Button(String text, Listener listener) {
        super();
        this.text = text;
        this.listener = listener;
    }

    /**
     * Paint button.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRoundRect(left, top, width, height, width / 6, width / 6);
        int c = TEXT_COLOR;
        switch (state) {
            case BUTTON_PRESSED:
                c = TEXT_PRESSED_COLOR;
                break;
            default:
                break;
        }
        g.setColor(c);
        final Font f = g.getFont();
        g.drawString(text, left + width / 2,
                     top + height / 2 - f.getHeight() / 2,
                     Graphics.HCENTER | Graphics.TOP);
    }

    /**
     * Handles pointer events received from SudokuCanvas.
     * Informs if a button was clicked.
     * @param type pointer press, release or drag
     * @param x coordinate of event
     * @param y coordinate of event 
     */
    public void handlePointerEvent(int type, int x, int y) {
        if (!isVisible()) {
            return;
        }
        if (hits(x, y)) {
            changeState(type == View.POINTER_RELEASED ? BUTTON_NORMAL : BUTTON_PRESSED);
            if (type == View.POINTER_RELEASED) {
                listener.onClick(this);
            }
        }
        else if (type == View.POINTER_DRAGGED) {
            changeState(BUTTON_NORMAL);
        }
    }

    private void changeState(int newState) {
        if (state != newState) {
            invalidate();
        }
        state = newState;
    }

    /**
     * Handles the key events received from SudokuCanvas.
     * @param type key press, release or repeat
     */
    public void keyEvent(int type) {
        if (type == KEY_RELEASED) {
            changeState(BUTTON_NORMAL);
            listener.onClick(this);
        }
        else {
            changeState(BUTTON_PRESSED);
        }
    }

    public static interface Listener {
        void onClick(Button b);
    }
}
