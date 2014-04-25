/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import java.util.Random;
import java.util.Vector;

/**
 * Generates a sudoku puzzle. There are three puzzles to randomly choose from.
 */
public class SudokuGenerator {
    private static final int[][] PUZZLE_0 = {
        {4,0,5,0,0,0,0,3,0},
        {0,0,9,7,5,0,0,0,0},
        {2,1,0,9,0,0,7,4,0},
        {0,8,0,5,3,0,4,2,7},
        {0,0,7,0,0,0,3,0,0},
        {3,4,2,0,7,8,0,9,0},
        {0,5,8,0,0,7,0,1,4},
        {0,0,0,0,6,1,2,0,0},
        {0,2,0,0,0,0,6,0,3}};
    private static final int[][] PUZZLE_1 = {
        {9,5,0,0,3,0,0,1,2},
        {0,2,0,5,0,0,3,0,0},
        {0,0,8,2,6,0,0,0,0},
        {0,7,1,0,9,0,8,0,0},
        {6,9,0,0,8,0,0,2,3},
        {0,0,3,0,5,0,6,7,0},
        {0,0,0,0,2,6,5,0,0},
        {0,0,7,0,0,5,0,3,0},
        {5,6,0,0,1,0,0,4,7}};
    private static final int[][] PUZZLE_2 = {
        {0,1,9,0,0,2,8,0,3},
        {8,3,0,5,0,6,0,0,1},
        {0,0,4,0,3,0,0,0,6},
        {0,2,0,0,0,0,5,6,4},
        {1,0,0,0,0,0,0,0,7},
        {7,9,6,0,0,0,0,3,0},
        {4,0,0,0,8,0,6,0,0},
        {5,0,0,4,0,9,0,7,8},
        {9,0,1,6,0,0,3,4,0}};

    public static int[][] newPuzzle() {
        return randomPuzzle();
    }

    private static int[][] randomPuzzle() {
        Random r = new Random(System.currentTimeMillis());
        int[][] template;
        switch(r.nextInt(3)) {
            case 0:
                template = PUZZLE_0;
                break;
            case 1:
                template = PUZZLE_1;
                break;
            default:
                template = PUZZLE_2;
                break;
        }
        Vector numbera = new Vector(9);
        for(int n = 1; n <= 9; n++) {
            numbera.addElement(new Integer(n));
        }
        int[] p = new int[10];
        p[0] = 0;
        for(int i = 1; i < p.length; i++) {
            int j = r.nextInt(numbera.size());
            p[i] = ((Integer) numbera.elementAt(j)).intValue();
            numbera.removeElementAt(j);
        }
        int[][] puzzle = new int[9][9];
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                puzzle[i][j] = p[template[i][j]];
            }
        }
        return puzzle;
    }
}
