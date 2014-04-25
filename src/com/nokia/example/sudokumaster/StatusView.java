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
 * Holds the number of views, the time elapsed, and
 * the number or numbers inserted on board.
 */
public class StatusView
    extends View {

    private static final int TEXT_COLOR = 0x00ffffff;
    private Image image;
    private String text = "";

    public StatusView(Image image) {
        super();
        this.image = image;
    }

    /**
     * Paints the status view.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        g.drawImage(image, left, top + height / 2,
                    Graphics.LEFT | Graphics.VCENTER);
        g.setColor(TEXT_COLOR);
        g.drawString(text, left + image.getWidth() + 3,
                     top + height / 2 - g.getFont().getHeight() / 2,
                     Graphics.LEFT | Graphics.TOP);
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            invalidate();
        }
        
        this.text = text;
    }
}
