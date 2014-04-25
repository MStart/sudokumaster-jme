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

/**
 * Generic View-class, which is subclassed by all
 * game widgets.
 */
public abstract class View {

    public static final int POINTER_PRESSED = 0;
    public static final int POINTER_DRAGGED = 1;
    public static final int POINTER_RELEASED = 2;
    public static final int KEY_PRESSED = 0;
    public static final int KEY_REPEAT = 1;
    public static final int KEY_RELEASED = 2;
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;
    public static final int KEY_SELECT = 4;
    public static final int KEY_UNKNOWN = -1;
    public int left, right, top, bottom, width, height;
    private volatile boolean needsRendering = true;
    private volatile boolean visible = true;

    protected View() {
        // View with no parameters.
    }

    /**
     * View constructor that sets the position and size of view.
     * @param left left margin in pixels
     * @param top top margin in pixels
     * @param width width of view in pixels
     * @param height height of view in pixels
     */
    protected View(int left, int top, int width, int height) {
        this.left = left;
        this.right = left + width;
        this.top = top;
        this.bottom = top + height;
        this.width = width;
        this.height = height;
    }

    public void setLeft(int left) {
        this.left = left;
        right = left + width;
    }

    public void setRight(int right) {
        this.right = right;
        left = right - width;
    }

    public void setWidth(int width) {
        this.width = width;
        right = left + width;
    }

    public void setTop(int top) {
        this.top = top;
        bottom = top + height;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
        top = bottom - height;
    }

    public void setHeight(int height) {
        this.height = height;
        bottom = top + height;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setVisible(boolean visible) {
        final boolean oldVisible = this.visible;
        this.visible = visible;
        if (this.visible != oldVisible) {
            needsRendering = true;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void update() {
        // Empty implementation.
    }

    public void render(Graphics g) {
        needsRendering = false;
        if (visible) {
            paint(g);
        }
    }

    public boolean needsRendering() {
        return needsRendering;
    }

    public void invalidate() {
        needsRendering = true;
    }

    protected boolean hits(int x, int y) {
        return left <= x && x < right && top <= y && y < bottom;
    }

    protected abstract void paint(Graphics g);
}
