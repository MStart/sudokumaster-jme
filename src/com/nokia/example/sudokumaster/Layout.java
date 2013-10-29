/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * View that groups all the other views.
 */
public class Layout extends View {

    private static final int BACKGROUND_COLOR = 0x00622415;
    private Vector views;

    public Layout(int width, int height) {
        super(0, 0, width, height);
        views = new Vector();
    }

    public void addView(View view) {
        views.addElement(view);
    }

    /**
     * Update the layout.
     * @see com.nokia.example.sudokumaster.View#update()
     */
    public void update() {
        for (Enumeration e = views.elements(); e.hasMoreElements();) {
            ((View) e.nextElement()).update();
        }
    }

    /**
     * Paint the layout.
     * @see com.nokia.example.sudokumaster.View#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     */
    protected void paint(Graphics g) {
        renderBackground(g);
        renderViews(g);
    }

    public boolean needsRendering() {
        return super.needsRendering() || anyViewNeedsRendering();
    }

    private boolean anyViewNeedsRendering() {
        for (Enumeration e = views.elements(); e.hasMoreElements();) {
            if (((View) e.nextElement()).needsRendering()) {
                return true;
            }
        }
        return false;
    }

    private void renderBackground(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(left, top, width, height);
    }

    private void renderViews(Graphics g) {
        for (Enumeration e = views.elements(); e.hasMoreElements();) {
            ((View) e.nextElement()).render(g);
        }
    }
}
