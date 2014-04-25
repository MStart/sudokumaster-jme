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

/**
 * Implements the options dialog, which holds "Restart", "New Game" and "Exit"
 * options.
 */
public class OptionsDialog
    extends ImageView {

    private static final String[] ITEMS = {"Restart", "New Game", "Exit"};
    private static final int TEXT_COLOR = 0x00000000;
    private static final int HIGHLIGHT_COLOR = 0x00ffff99;
    private static final int ANIMATION_STEPS = 5;
    private int[] itemYs = null;
    private final Listener listener;
    private int highlighted = -1;
    private int highlightAnimationCounter = 0;

    public OptionsDialog(Image backgroundImage, Listener listener) {
        super(backgroundImage);
        this.listener = listener;
    }

    /**
     * Updates the dialog.
     * @see com.nokia.example.sudokumaster.View#update()
     */
    public void update() {
        if (highlighted < 0) {
            highlightAnimationCounter = 0;
        }
        
        if (highlightAnimationCounter > 0) {
            int h = highlightHeight();
            highlightAnimationCounter = Math.max(highlightAnimationCounter - 1, 0);
            int newH = highlightHeight();
            
            if (h != newH) {
                invalidate();
            }
        }
    }

    public void setTop(int top) {
        super.setTop(top);
        itemYs = null;
    }

    public void setBottom(int bottom) {
        super.setBottom(bottom);
        itemYs = null;
    }

    public void setHeight(int height) {
        super.setHeight(height);
        itemYs = null;
    }

    private int highlightHeight() {
        return (height / 6 - height * highlightAnimationCounter / ANIMATION_STEPS / 12) / 2 * 2;
    }

    /**
     * Paints the options dialog.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        super.paint(g);
        
        if (itemYs == null) {
            itemYs = new int[ITEMS.length];
            int padding = height / 10;
            int itemHeight = (height - padding) / ITEMS.length;
            int itemCenter = top + (padding + itemHeight) / 2;
            
            for (int i = 0; i < ITEMS.length; i++) {
                itemYs[i] = itemCenter + i * itemHeight;
            }
        }
        
        if (highlighted >= 0) {
            int w = width - (width > 128 ? 8 : 6);
            int h = highlightHeight();
            int x = left + (width - w) / 2;
            int y = itemYs[highlighted] - h / 2;
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRect(x, y, w, h);
        }
        
        int x, y;
        x = left + width / 2;
        g.setColor(TEXT_COLOR);
        
        for (int i = 0; i < ITEMS.length; i++) {
            y = itemYs[i] - g.getFont().getHeight() / 2;
            g.drawString(ITEMS[i], x, y, Graphics.HCENTER | Graphics.TOP);
        }
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        highlighted = -1;
        highlightAnimationCounter = 0;
    }

    /**
     * Handles the pointer events sent from SudokuCanvas.
     * @param type pointer press, release or drag
     * @param x coordinate of event
     * @param y coordinate of event
     * @return ret 
     */
    public boolean handlePointerEvent(int type, int x, int y) {
        boolean ret = false;
        
        if (!isVisible()) {
            return ret;
        }
        
        final int oldHighlighted = highlighted;
        
        if (hits(x, y)) {
            highlighted = getItemIndex(x, y);
            
            if (type == View.POINTER_RELEASED && highlighted >= 0) {
                listener.onItemClick(highlighted);
            }
            
            ret = true;
        }
        else {
            highlighted = -1;
        }
        
        if (oldHighlighted != highlighted) {
            highlightAnimationCounter = ANIMATION_STEPS;
            invalidate();
        }
        return ret;
    }

    /**
     * Handle key events sent by SudokuCanvas.
     * Navigate or select options item based on key events.
     * @param type key press, release or repeat
     * @param key key code
     */
    public void keyEvent(int type, int key) {
        if (type == KEY_RELEASED) {
            return;
        }
        
        switch (key) {
            case KEY_UP:
                highlightItem(highlighted < 0 ? 0 : (highlighted - 1 + ITEMS.length) % ITEMS.length);
                break;
            case KEY_DOWN:
                highlightItem(highlighted < 0 ? 0 : (highlighted + 1) % ITEMS.length);
                break;
            case KEY_SELECT:
                if (highlighted < 0) {
                    highlightItem(0);
                }
                else if (type == KEY_PRESSED) {
                    listener.onItemClick(highlighted);
                }
                break;
            default:
                break;
        }
    }

    private int getItemIndex(int x, int y) {
        if (x < left + width / 6 || x > left + width * 5 / 6) {
            return -1;
        }
        
        return Math.min((y - top) / (height / 3), ITEMS.length - 1);
    }

    public void highlightItem(int i) {
        highlighted = i;
        highlightAnimationCounter = ANIMATION_STEPS;
        invalidate();
    }

    public static interface Listener {
        void onItemClick(int itemIndex);
    }
}
