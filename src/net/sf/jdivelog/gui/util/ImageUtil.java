/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: ImageUtil.java
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 * 
 * This file is part of JDiveLog.
 * JDiveLog is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * JDiveLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JDiveLog; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jdivelog.gui.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.*;

import org.jfree.util.WaitingImageObserver;

public class ImageUtil {

    /*
     * Grabbed from:
     * http://today.java.net/pub/a/today/2007/04/03/perils-of-image
     * -getscaledinstance.html and
     * http://www.rgagnon.com/javadetails/java-0243.html
     */
    private static BufferedImage getScaledInstance(
        BufferedImage img, int targetWidth, int targetHeight, Object hint) {
        int type =
            (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g = ret.createGraphics();
        AffineTransform at =
            AffineTransform.getScaleInstance(
                (double) targetWidth / img.getWidth(), (double) targetHeight
                    / img.getHeight());

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
        g.drawRenderedImage(img, at);
        return ret;
    }

    public static void transform(
        Image sourceImg, BufferedImage targetImg, int targetWidth,
        int targetHeight, int rotation, ImageObserver observer) {
        WaitingImageObserver o = new WaitingImageObserver(sourceImg);
        int sizeX = sourceImg.getWidth(o);
        int sizeY = sourceImg.getHeight(o);

        double factor =
            getFactor(sizeX, sizeY, targetWidth, targetHeight, rotation);
        double scaledX = sizeX * factor;
        double scaledY = sizeY * factor;

        AffineTransform at = new AffineTransform();

        BufferedImage temp =
            new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d_temp = temp.createGraphics();
        g2d_temp.drawImage(sourceImg, at, new WaitingImageObserver(sourceImg));

        BufferedImage scaledImage =
            getScaledInstance(temp, (int) scaledX, (int) scaledY,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        Graphics2D g2d = targetImg.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, targetWidth, targetHeight);
        at = new AffineTransform();
        at.translate((targetWidth - scaledX) / 2, (targetHeight - scaledY) / 2);
        at.rotate(Math.toRadians(rotation * 90), scaledX / 2, scaledY / 2);
        g2d.drawImage(scaledImage, at, observer);
        g2d.dispose();
    }

    public static BufferedImage transform(
        Image sourceImg, int maxWidth, int maxHeight, int rotation) {
        WaitingImageObserver o = new WaitingImageObserver(sourceImg);
        o.waitImageLoaded();
        int sizeX = sourceImg.getWidth(o);
        int sizeY = sourceImg.getHeight(o);

        double factor = getFactor(sizeX, sizeY, maxWidth, maxHeight, rotation);
        int scaledX;
        int scaledY;
        if (rotation == 0 || rotation == 2) {
            scaledX = (int) (sizeX * factor);
            scaledY = (int) (sizeY * factor);
        }
        else {
            scaledX = (int) (sizeY * factor);
            scaledY = (int) (sizeX * factor);
        }
        BufferedImage buf =
            new BufferedImage(scaledX, scaledY, BufferedImage.TYPE_INT_RGB);
        WaitingImageObserver obs = new WaitingImageObserver(buf);
        transform(sourceImg, buf, scaledX, scaledY, rotation, obs);
        obs.waitImageLoaded();
        return buf;
    }

    private static double getFactor(
        int sourceWidth, int sourceHeight, int maxWidth, int maxHeight,
        int rotation) {
        float factorX;
        float factorY;
        if (rotation == 0 || rotation == 2) {
            factorX = getScaleFactor(maxWidth, sourceWidth);
            factorY = getScaleFactor(maxHeight, sourceHeight);
        }
        else {
            factorX = getScaleFactor(maxWidth, sourceHeight);
            factorY = getScaleFactor(maxHeight, sourceWidth);
        }
        if (factorX < factorY) {
            return factorX;
        }
        return factorY;
    }

    private static float getScaleFactor(double preferredSize, int size) {
        return (float) preferredSize / size;
    }
}
