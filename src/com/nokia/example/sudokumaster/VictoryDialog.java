/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
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
 * Dialog for notifying of victory.
 */
public class VictoryDialog
    extends ImageView {

    private static final int TITLE_COLOR = 0x00ffffff;
    private static final int TITLE_SHADOW_COLOR = 0x00000000;
    private static final int TEXT_COLOR = 0x00000000;
    private static final String TITLE_TEXT = "Well done!";
    private static final String MOVES_TEXT = " moves";
    private static final String ELAPSED_TEXT = " seconds";
    public volatile int moves;
    public volatile long seconds;

    public VictoryDialog(Image backgroundImage) {
        super(backgroundImage);
    }

    /**
     * Paints the dialog.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        super.paint(g);
        int x, y;
        x = left + width / 2;
        y = top + height / 4 - g.getFont().getHeight() / 2;
        g.setColor(TITLE_SHADOW_COLOR);
        int shadowOffset = width > 128 ? 2 : 1;
        g.drawString(TITLE_TEXT, x + shadowOffset, y + shadowOffset,
                     Graphics.HCENTER | Graphics.TOP);
        g.setColor(TITLE_COLOR);
        g.drawString(TITLE_TEXT, x, y, Graphics.HCENTER | Graphics.TOP);
        x = left + width / 3;
        g.setColor(TEXT_COLOR);
        y += height / 4;
        g.drawString(MOVES_TEXT, x, y, Graphics.LEFT | Graphics.TOP);
        g.drawString(String.valueOf(moves), x, y, Graphics.RIGHT | Graphics.TOP);
        y += height / 4;
        g.drawString(ELAPSED_TEXT, x, y, Graphics.LEFT | Graphics.TOP);
        g.drawString(String.valueOf(seconds), x, y,
                     Graphics.RIGHT | Graphics.TOP);
    }
}
