/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Holds a reference to the Layout to draw all the views.
 */
public class SudokuCanvas
    extends GameCanvas
    implements CommandListener {

    private static final int LEFT_SOFTKEY = -6;
    private static final int RIGHT_SOFTKEY = -7;
    private Main main;
    private Timer timer;
    private Layout layout;
    private ImageView title;
    private Button exit;
    private Button back;
    private Button options;
    private ImageView backX;
    private SudokuView sudoku;
    private StatusView empty;
    private StatusView moves;
    private StatusView elapsed;
    private NumberSelector numberSelector;
    private VictoryDialog victoryDialog;
    private OptionsDialog optionsDialog;

    private Command backCommand;

    public SudokuCanvas(final Main main) {
        super(false);
        setFullScreenMode(true);
        this.main = main;

        backCommand = new Command("Back", Command.BACK, 0);
        addCommand(backCommand);
        setCommandListener(this);
    }

    /**
     * @see javax.microedition.lcdui.CommandListener#commandAction(
     * javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
            if (numberSelector.isVisible()
                || optionsDialog.isVisible()
                || victoryDialog.isVisible())
            {
                back();
            }
            else {
                main.close();
            }
        }
    }

    /**
     * Called when canvas is shown.
     * @see javax.microedition.lcdui.Canvas#showNotify()
     */
    protected void showNotify() {
        if (sudoku == null) {
            generateLayout();
        }
        
        loadGameState();
        updateEmptyAndMoves();
        startTimer();
    }

    /**
     * Called when canvas is hidden.
     * @see javax.microedition.lcdui.Canvas#hideNotify()
     */
    protected void hideNotify() {
        stopTimer();
        
        if (!main.isClosed()) {
            saveGameState();
        }
    }

    /**
     * Called when the drawable area of the Canvas has been changed.
     * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h the new height in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        if (timer != null) {
            stopTimer();
            updateLayout(w, h);
            startTimer();
        }
    }

    private void startTimer() {
        final Graphics g = getGraphics();
        
        if (isSmallScreen(getWidth(), getHeight())) {
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                                   Font.SIZE_SMALL));
        }
        
        timer = new Timer();
        
        timer.schedule(new TimerTask() {
            public void run() {
                render(g);
            }
        }, 0, 20);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateElapsed();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        timer.cancel();
        timer = null;
    }

    /**
     * Saves the current state of the game using RecordStore.
     * @see javax.microedition.rms.RecordStore
     */
    public void saveGameState() {
        if (sudoku == null) {
            return;
        }
        
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState", true);
            
            if (gameState.getNumRecords() == 0) {
                gameState.addRecord(null, 0, 0);
            }
            
            byte[] data = sudoku.getState();
            gameState.setRecord(1, data, 0, data.length);
        }
        catch (RecordStoreException e) {
            // Empty implementation
        }
    }

    /**
     * Load the saved state of the game using RecordStore.
     * @see javax.microedition.rms.RecordStore
     */
    public void loadGameState() {
        if (sudoku == null) {
            return;
        }
        
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState", true);
            
            if (gameState.getNumRecords() == 0) {
                sudoku.newGame(SudokuGenerator.newPuzzle());
            }
            else {
                sudoku.setState(gameState.getRecord(1));
                
                if (sudoku.isComplete()) {
                    showVictoryDialog();
                }
            }
        }
        catch (RecordStoreException e) {
            // Empty implementation
        }
    }

    private void render(Graphics g) {
        if (layout.needsRendering()) {
            layout.render(g);
            flushGraphics();
        }
        
        layout.update();
    }

    private void updateElapsed() {
        long s = sudoku.getElapsedSeconds();
        long m = s / 60;
        s = s % 60;
        StringBuffer text = new StringBuffer(5);
        
        if (m < 10) {
            text.append('0');
        }
        
        text.append(m);
        text.append(':');
        
        if (s < 10) {
            text.append('0');
        }
        
        text.append(s);
        elapsed.setText(text.toString());
    }

    private void updateEmptyAndMoves() {
        empty.setText(String.valueOf(sudoku.getEmpty()));
        moves.setText(String.valueOf(sudoku.getMoves()));
    }

    /**
     * Handle pointer press by redirecting.
     * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
     * @param x coordinate of press
     * @param y coordinate of press
     */
    protected void pointerPressed(int x, int y) {
        handlePointerEvent(View.POINTER_PRESSED, x, y);
    }

    /**
     * Handle pointer drag by redirecting.
     * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
     * @param x coordinate of press
     * @param y coordinate of press
     */
    protected void pointerDragged(int x, int y) {
        handlePointerEvent(View.POINTER_DRAGGED, x, y);
    }

    /**
     * Handle pointer release by redirecting.
     * @see javax.microedition.lcdui.Canvas#pointerRelease(int, int)
     * @param x coordinate of press
     * @param y coordinate of press
     */
    protected void pointerReleased(int x, int y) {
        handlePointerEvent(View.POINTER_RELEASED, x, y);
    }

    /**
     * Each view handles the pointer event the way it suits them.
     * @param type pointer press, drag or release
     * @param x coordinate of event
     * @param y coordinate of event
     */
    private void handlePointerEvent(int type, int x, int y) {
        options.handlePointerEvent(type, x, y);
        
        if (exit != null && back != null) {
            exit.handlePointerEvent(type, x, y);
            back.handlePointerEvent(type, x, y);
        }
        
        if (optionsDialog.isVisible()) {
            if (backX == null || !backX.handlePointerEvent(type, x, y)) {
                optionsDialog.handlePointerEvent(type, x, y);
            }
        }
        else if (victoryDialog.isVisible()) {
            return;
        }
        else if (numberSelector.isVisible()) {
            numberSelector.handlePointerEvent(type, x, y);
        }
        else {
            sudoku.handlePointerEvent(type, x, y);
        }
    }

    /**
     * Redirect key press event.
     * @see javax.microedition.lcdui.Canvas#keyPressed(int)
     * @param i key code
     */
    protected void keyPressed(int i) {
        handleKeyEvent(View.KEY_PRESSED, i);
    }

    /**
     * Redirect key repeated event.
     * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
     * @param i key code
     */
    protected void keyRepeated(int i) {
        handleKeyEvent(View.KEY_REPEAT, i);
    }

    /**
     * Redirect key released event.
     * @see javax.microedition.lcdui.Canvas#keyReleased(int)
     * @param i key code
     */
    protected void keyReleased(int i) {
        handleKeyEvent(View.KEY_RELEASED, i);
    }

    /**
     * Each view handles the key event the way it suits them.
     * @param type key press, release or repeated
     * @param i key code
     */
    private void handleKeyEvent(int type, int i) {
        if (i == LEFT_SOFTKEY) {
            options.keyEvent(type);
        }
        else if (i == RIGHT_SOFTKEY) {
            if (exit.isVisible()) {
                exit.keyEvent(type);
            }
            else if (back != null && back.isVisible()) {
                back.keyEvent(type);
            }
        }
        else if (optionsDialog.isVisible()) {
            if (getNumber(i) < 0) {
                optionsDialog.keyEvent(type, getViewKey(i));
            }
        }
        else if (victoryDialog.isVisible()) {
            return;
        }
        else {
            int n = getNumber(i);
            
            if (n >= 0) {
                if (type == View.KEY_PRESSED) {
                    sudoku.setNumber(n);
                }
            }
            else if (numberSelector.isVisible()) {
                numberSelector.keyEvent(type, getViewKey(i));
            }
            else {
                sudoku.keyEvent(type, getViewKey(i));
            }
        }
    }

    public int getNumber(int keyCode) {
        switch (keyCode) {
            case KEY_NUM0:
                return 0;
            case KEY_NUM1:
                return 1;
            case KEY_NUM2:
                return 2;
            case KEY_NUM3:
                return 3;
            case KEY_NUM4:
                return 4;
            case KEY_NUM5:
                return 5;
            case KEY_NUM6:
                return 6;
            case KEY_NUM7:
                return 7;
            case KEY_NUM8:
                return 8;
            case KEY_NUM9:
                return 9;
            default:
                return -1;
        }
    }

    public int getViewKey(int keyCode) {
        switch (getGameAction(keyCode)) {
            case UP:
                return View.KEY_UP;
            case DOWN:
                return View.KEY_DOWN;
            case LEFT:
                return View.KEY_LEFT;
            case RIGHT:
                return View.KEY_RIGHT;
            case FIRE:
                return View.KEY_SELECT;
            default:
                return View.KEY_UNKNOWN;
        }
    }

    private boolean isPortrait(int w, int h) {
        return w < h;
    }

    private boolean isSmallScreen(int w, int h) {
        final int min = Math.min(w, h);
        final int max = Math.max(w, h);
        
        if (min < 240 || max < 320) {
            return true;
        }
        
        return false;
    }

    private void generateLayout() {
        final int w = getWidth();
        final int h = getHeight();
        layout = new Layout(w, h);
        generateTitle(w, h);
        generateButtons();
        generateBoard(w, h);
        generateStatusViews(w, h);
        generateNumberSelector(w, h);
        generateVictoryDialog(w, h);
        generateOptionsDialog(w, h);
        updateLayout(w, h);
    }

    private synchronized void updateLayout(int w, int h) {
        updateButtons(w, h);
        updateBoard(w, h);
        updateTitle(w, h);
        updateStatusViews(w, h);
        updateNumberSelector(w, h);
        updateVictoryDialog(w, h);
        updateOptionsDialog(w, h);
        layout.setSize(w, h);
        layout.invalidate();
    }

    private void generateTitle(int w, int h) {
        title = new ImageView(ImageLoader.loadImage("title.png", isSmallScreen(w, h)));
        layout.addView(title);
    }

    private void updateTitle(int w, int h) {
        title.setLeft(0);
        title.setTop(0);
        title.setWidth(w);
        title.setHeight(sudoku.top);
        title.invalidate();
    }

    private int getMinTitleHeight(int w, int h) {
        return h / 10;
    }

    /**
     * Generates the menu, exit and back buttons. Exit and back buttons are not
     * created if the phone has a physical back key.
     */
    private void generateButtons() {
        final Button.Listener buttonListener = new Button.Listener() {
            public void onClick(Button b) {
                if (exit != null && b == exit) {
                    main.close();
                }
                else if (back != null && b == back) {
                    back();
                }
                else if (b == options) {
                    showOptionsDialog();
                }
            }
        };
        
        if (!main.hasOneKeyBack()) {
            // Exit and back buttons are not needed if the phone has a
            // physical back button
            exit = new Button("Exit", buttonListener);
            layout.addView(exit);
            back = new Button("Back", buttonListener);
            back.setVisible(false);
            layout.addView(back);
        }
        
        options = new Button("Menu", buttonListener);
        layout.addView(options);
    }

    private void back() {
        if (optionsDialog.isVisible()) {
            hideOptionsDialog();
        }
        else {
            hideNumberSelector();
        }
    }

    private int getButtonsHeight(int w, int h) {
        return isPortrait(w, h) ? h / 13 : h / 10;
    }

    /**
     * Updates the button sizes and positions based on the given dimensions.
     * @param w The width of the content area.
     * @param h The height of the content area.
     */
    private void updateButtons(int w, int h) {
        final int width = isPortrait(w, h) ? w / 3 : w / 4;
        final int height = getButtonsHeight(w, h);
        
        if (exit != null) {
            exit.setSize(width, height);
            exit.setRight(w);
            exit.setBottom(h);
            exit.invalidate();
        }
        
        if (back != null) {
            back.setSize(width, height);
            back.setRight(w);
            back.setBottom(h);
            back.invalidate();
        }
        
        options.setSize(width, height);
        options.setLeft(0);
        options.setBottom(h);
        options.invalidate();
    }

    private int getStatusItemHeight(int w, int h) {
        return h / 10 - 4;
    }

    private void generateBoard(int w, int h) {
        sudoku = new SudokuView(new SudokuView.Listener() {
            public void onCellSelected() {
                showNumberSelector();
            }

            public void onSetNumber() {
                if (numberSelector.isVisible()) {
                    hideNumberSelector();
                }
                updateEmptyAndMoves();
                if (sudoku.isComplete()) {
                    showVictoryDialog();
                }
            }
        });
        
        layout.addView(sudoku);
    }

    private void updateBoard(int w, int h) {
        final int horizontalPadding = 4;
        Image image = ImageLoader.loadImage("board_tiles.png", isSmallScreen(w, h));
        final int maxBoardWidth = isPortrait(w, h) ? w : w * 3 / 4 - horizontalPadding;
        final int maxBoardHeight = h - getMinTitleHeight(w, h) - getButtonsHeight(
                w, h) - (isPortrait(w, h) ? getStatusItemHeight(w, h) : 0);
        final int maxBoardSize = Math.min(maxBoardWidth, maxBoardHeight);
        int boardSize = image.getHeight() * 9 + 1;
        
        if (boardSize > maxBoardSize || boardSize < maxBoardSize) {
            int newHeight = image.getHeight() * maxBoardSize / boardSize;
            
            if (newHeight % 2 == 0) {
                newHeight -= 1;
            }
            
            if (newHeight != image.getHeight()) {
                int newWidth = image.getWidth() / image.getHeight() * newHeight;
                image = ImageLoader.scaleImage(image, newWidth, newHeight);
                boardSize = image.getHeight() * 9 + 1;
            }
        }
        
        sudoku.setLeft(
                isPortrait(w, h) ? (w - boardSize) / 2 : (w - boardSize * 4 / 3) / 2);
        sudoku.setTop(getMinTitleHeight(w, h) + (maxBoardHeight - boardSize) / 2);
        sudoku.setBoardSize(boardSize);
        sudoku.setBoardImage(image);
    }

    private void generateStatusViews(int w, int h) {
        final boolean smallScreen = isSmallScreen(w, h);
        
        empty = new StatusView(ImageLoader.loadImage("empty.png", smallScreen));
        moves = new StatusView(ImageLoader.loadImage("moves.png", smallScreen));
        elapsed = new StatusView(ImageLoader.loadImage("time.png", smallScreen));
        
        layout.addView(empty);
        layout.addView(moves);
        layout.addView(elapsed);
    }

    private void updateStatusViews(int w, int h) {
        int width = sudoku.width / 3;
        int height = getStatusItemHeight(w, h);
        empty.setSize(width, height);
        moves.setSize(width, height);
        elapsed.setSize(width, height);
        
        if (isPortrait(w, h)) {
            empty.setLeft(sudoku.left);
            moves.setLeft(empty.right);
            elapsed.setLeft(moves.right);
            empty.setTop(sudoku.bottom);
            moves.setTop(sudoku.bottom);
            elapsed.setTop(sudoku.bottom);
        }
        else {
            int left = sudoku.right + 2;
            int top = sudoku.top + sudoku.height / 2 - height * 3 / 2;
            empty.setLeft(left);
            moves.setLeft(left);
            elapsed.setLeft(left);
            empty.setTop(top);
            moves.setTop(empty.bottom);
            elapsed.setTop(moves.bottom);
        }
        
        empty.invalidate();
        moves.invalidate();
        elapsed.invalidate();
    }

    /**
     * Generate the number selector view, where user can choose the number to be
     * inserted on the board.
     * @param w The width of the screen
     * @param h The height of the screen
     */
    private void generateNumberSelector(int w, int h) {
        final Image nButtonImage = ImageLoader.loadImage("dial.png", isSmallScreen(w, h));
        final Image cButtonImage = ImageLoader.loadImage("dial_c.png", isSmallScreen(w, h));
        final int selectorWidth = nButtonImage.getWidth() / 2 * 3;
        final int selectorHeight = nButtonImage.getHeight() * 3 + cButtonImage.getHeight();
        
        numberSelector = new NumberSelector(selectorWidth, selectorHeight,
                                            nButtonImage, cButtonImage,
                                            new NumberSelector.Listener() {

            public void numberSelected(int n) {
                sudoku.setNumber(n);
            }
        });
        
        numberSelector.setVisible(false);
        layout.addView(numberSelector);
    }

    private void updateNumberSelector(int w, int h) {
        numberSelector.setLeft((w - numberSelector.width) / 2);
        numberSelector.setTop(
                sudoku.top + (sudoku.height - numberSelector.height) / 2);
        numberSelector.invalidate();
    }

    private void generateVictoryDialog(int w, int h) {
        victoryDialog = new VictoryDialog(
            ImageLoader.loadImage("victory.png", isSmallScreen(w, h)));
        victoryDialog.setVisible(false);
        layout.addView(victoryDialog);
    }

    private void updateVictoryDialog(int w, int h) {
        victoryDialog.setLeft((w - victoryDialog.width) / 2);
        victoryDialog.setTop(
                sudoku.top + (sudoku.height - victoryDialog.height) / 2);
        victoryDialog.invalidate();
    }

    /**
     * Generate the options dialog.
     * @param w width of the screen
     * @param h height of the screen
     */
    private void generateOptionsDialog(int w, int h) {
        optionsDialog = new OptionsDialog(
            ImageLoader.loadImage("panel.png", isSmallScreen(w, h)),
            new OptionsDialog.Listener() {
                public void onItemClick(int itemIndex) {
                    hideOptionsDialog();
                    
                    switch (itemIndex) {
                        case 0:
                            sudoku.restart();
                            hideVictoryDialog();
                            updateEmptyAndMoves();
                            updateElapsed();
                            break;
                        case 1:
                            sudoku.newGame(SudokuGenerator.newPuzzle());
                            hideVictoryDialog();
                            updateEmptyAndMoves();
                            updateElapsed();
                            break;
                        case 2:
                            main.close();
                        break;
                        default:
                            break;
                    }
                }
            });
        
        optionsDialog.setVisible(false);
        layout.addView(optionsDialog);
        
        if (!main.hasOneKeyBack()) {
            backX = new ImageView(
                ImageLoader.loadImage("close.png", isSmallScreen(w, h)),
                new ImageView.Listener() {
                    public void onClick() {
                        back();
                    }
                });
            
            backX.setVisible(false);
            layout.addView(backX);
        }
    }

    private void updateOptionsDialog(int w, int h) {
        optionsDialog.setLeft((w - optionsDialog.width) / 2);
        optionsDialog.setTop(
                sudoku.top + (sudoku.height - optionsDialog.height) / 2);
        optionsDialog.invalidate();
        
        if (backX != null) {
            backX.setLeft(optionsDialog.right - backX.width / 2);
            backX.setTop(optionsDialog.top - backX.height / 2);
            backX.invalidate();
        }
    }

    private void refreshBackButton() {
        if (exit == null || back == null) {
            return;
        }
        
        if (numberSelector.isVisible() || optionsDialog.isVisible()) {
            exit.setVisible(false);
            back.setVisible(true);
        }
        else {
            back.setVisible(false);
            exit.setVisible(true);
        }
    }

    private void showNumberSelector() {
        numberSelector.setVisible(true);
        
        if (!hasPointerEvents()) {
            numberSelector.setKeyPressed(5);
        }
        
        refreshBackButton();
    }

    private void hideNumberSelector() {
        numberSelector.setVisible(false);
        refreshBackButton();
    }

    private void showVictoryDialog() {
        if (numberSelector.isVisible()) {
            hideNumberSelector();
        }
        
        victoryDialog.setVisible(true);
        victoryDialog.moves = sudoku.getMoves();
        victoryDialog.seconds = sudoku.getElapsedSeconds();
    }

    private void hideVictoryDialog() {
        victoryDialog.setVisible(false);
    }

    private void showOptionsDialog() {
        if (numberSelector.isVisible()) {
            hideNumberSelector();
        }
        optionsDialog.setVisible(true);
        
        if (!hasPointerEvents()) {
            optionsDialog.highlightItem(0);
        }
        else if (backX != null) {
            backX.setVisible(true);
        }
        
        refreshBackButton();
    }

    private void hideOptionsDialog() {
        optionsDialog.setVisible(false);
        
        if (backX != null) {
            backX.setVisible(false);
        }
        
        refreshBackButton();
    }
}
