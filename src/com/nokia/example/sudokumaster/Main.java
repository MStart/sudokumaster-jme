/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import com.nokia.mid.ui.VirtualKeyboard;

/**
 * Main class that handles starting the application, pausing it and closing.
 */
public class Main
    extends MIDlet {
    // Members
    private SudokuCanvas sudokuCanvas = null;
    private final boolean hasOneKeyBack;
    private boolean closed = false;

    /**
     * Constructor. Determines whether the device has hardware back key and
     * stores the value so that it can be queried later.
     */
    public Main()
    {
        String keyboardType = System.getProperty("com.nokia.keyboard.type");
        
        if (keyboardType  != null && keyboardType.equalsIgnoreCase("OnekeyBack")) {
            hasOneKeyBack = true;
        }
        else {
            hasOneKeyBack = false;
        }
    }

    /**
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    public void startApp() {

        if (sudokuCanvas == null) {
            sudokuCanvas = new SudokuCanvas(this);
            
            if (hasOneKeyBack) {
                // The device has a virtual keyboard which is not needed here
                VirtualKeyboard.hideOpenKeypadCommand(true);
                VirtualKeyboard.suppressSizeChanged(true);
            }
            
            Display.getDisplay(this).setCurrent(sudokuCanvas);
        }
    }

    /**
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    public void pauseApp() {
        // No implementation required
    }

    /**
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    public void destroyApp(boolean unconditional) {
        closed = true;
        
        if (sudokuCanvas != null) {
            sudokuCanvas.saveGameState();
        }
    }

    /**
     * Quits the app.
     */
    public void close() {
        destroyApp(true);
        notifyDestroyed();
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean hasOneKeyBack() {
        return hasOneKeyBack;
    }
}
