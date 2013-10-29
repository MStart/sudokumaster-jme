/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.sudokumaster;

import java.io.InputStream;
import javax.microedition.lcdui.Image;

/**
 * Class for loading images.
 */
public class ImageLoader {
    // Constants
    private static final String SMALL_ASSETS_PREFIX = "/small/";
    private static final String MEDIUM_ASSETS_PREFIX = "/medium/";

    // Members
    private static ImageLoader self;

    /** 
     * @return The singleton instance of this class.
     */
    public static ImageLoader getInstance() {
        if (self == null) {
            self = new ImageLoader();
        }
        
        return self;
    }

    /**
     * Private constructor.
     */
    private ImageLoader() {
        // Nothing to do here
    }

    /**
     * For convenience.
     * @param fileName The file name of the image to load.
     * @param isSmallScreen Determines from which image set to load the image from.
     * @return The loaded image or null in case of an error.
     */
    public static Image loadImage(String fileName, boolean isSmallScreen) {
        Image image = null;
        
        if (isSmallScreen) {
            image = getInstance().loadImage(SMALL_ASSETS_PREFIX + fileName);
        }
        
        if (image == null) {
            image = getInstance().loadImage(MEDIUM_ASSETS_PREFIX + fileName);
        }
        
        return image;
    }

    /**
     * Load image through InputStream.
     * @param imagepath path to the image file
     * @return created image or null if creation failed
     */
    public Image loadImage(String imagepath) throws RuntimeException {
        try {
            InputStream in = getClass().getResourceAsStream(imagepath);
            return Image.createImage(in);
        }
        catch (Exception e) {
            // Empty implementation
        }
        return null;
    }

    /**
     * Scales the image to the desired width and height.
     * @param original original image
     * @param newWidth new width for the image
     * @param newHeight new height for the image
     * @return scaled image
     */
    public static Image scaleImage(Image original, int newWidth, int newHeight) {
        int[] rawInput = new int[original.getHeight() * original.getWidth()];
        original.getRGB(rawInput, 0, original.getWidth(), 0, 0,
                        original.getWidth(), original.getHeight());

        int[] rawOutput = new int[newWidth * newHeight];

        // YD compensates for the x loop by subtracting the width back out
        int YD = (original.getHeight() / newHeight) * original.getWidth() - original.getWidth();
        int YR = original.getHeight() % newHeight;
        int XD = original.getWidth() / newWidth;
        int XR = original.getWidth() % newWidth;
        int outOffset = 0;
        int inOffset = 0;

        for (int y = newHeight, YE = 0; y > 0; y--) {
            for (int x = newWidth, XE = 0; x > 0; x--) {
                rawOutput[outOffset++] = rawInput[inOffset];
                inOffset += XD;
                XE += XR;
                if (XE >= newWidth) {
                    XE -= newWidth;
                    inOffset++;
                }
            }
            inOffset += YD;
            YE += YR;
            if (YE >= newHeight) {
                YE -= newHeight;
                inOffset += original.getWidth();
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, false);
    }
}
