/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: ProfileUtil.java
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
package net.sf.jdivelog.printing.report;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.batik.ext.awt.image.codec.jpeg.JPEGImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

import net.sf.jdivelog.gui.DiveProfile;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.JDiveLog;


public class ProfileUtil {
    
    private static JDiveLog logbook;
    
    public static void setLogbook(JDiveLog lb) {
        logbook = lb;
    }
    
    public static boolean hasProfile(long num) {
        JDive dive = getDive(num);
        return dive != null && dive.getDive() != null;
    }
    
    public static String getProfile(long num, int width, int height) {
        JDive dive = getDive(num);
        if (dive == null) {
            throw new IllegalArgumentException("Dive "+num+" not found!");
        }
        DiveProfile p = new DiveProfile(logbook.getProfileSettings(), dive);
        p.setSize(width, height);
        return writeProfile(p, width, height).getAbsolutePath();
    }
    
    private static JDive getDive(long num) {
        if (logbook == null) {
            throw new IllegalStateException("Logbook not set!");
        }
        for (JDive dive : logbook.getDives()) {
            if (Long.valueOf(num).equals(dive.getDiveNumber())) {
                return dive;
            }
        }
        return null;
    }
    
    private static File writeProfile(DiveProfile p, int width, int height) {
        File file;
        try {
            file = File.createTempFile("prof", "jpg");
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        p.paint(img.createGraphics());
        float quality = 90;
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        JPEGImageWriter w = new JPEGImageWriter();
        ImageWriterParams params = new ImageWriterParams();
        params.setJPEGQuality(quality / 100.0f, false);
        w.writeImage(img, out);
        out.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing profile!", e);
        }
        return file;

    }

}
