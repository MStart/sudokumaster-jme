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
 * View that holds images.
 */
public class ImageView
    extends View {

    private Image image;
    private Listener listener;

    public ImageView(Image image) {
        this(image, null);
    }

    public ImageView(Image image, Listener listener) {
        super(0, 0, image.getWidth(), image.getHeight());
        this.image = image;
        this.listener = listener;
    }

    /**
     * Paints the image view.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        g.drawImage(image, left + width / 2, top + height / 2,
                    Graphics.HCENTER | Graphics.VCENTER);
    }

    /**
     * Handles pointer events sent by SudokuCanvas.
     * Inform if pointer event release hit the image view.
     * @param type pointer press, release or drag
     * @param x coordinate of event
     * @param y coordinate of event
     * @return true or false depending on whether the pointer event hit the image.
     */
    public boolean handlePointerEvent(int type, int x, int y) {
        if (!isVisible() || listener == null) {
            return false;
        }
        
        if (hits(x, y)) {
            if (type == POINTER_RELEASED) {
                listener.onClick();
            }
            
            return true;
        }
        
        return false;
    }

    public static interface Listener {
        void onClick();
    }
}
