/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5SettingsStorage.java
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
package net.sf.jdivelog.model.dr5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Dr5SettingsStorage {

    private static final Logger LOGGER = Logger.getLogger(Dr5SettingsStorage.class.getName());
    private static final int FILE_CREATION_TIMEOUT_SECS = 180;

    private final File dr5Home;

    private Dr5SettingsStorage(File dr5Home) {
        this.dr5Home = dr5Home;
    }

    public Dr5Settings readSettings() {
        assertDrxConfig();
        byte[] dump = readDrxConfig();
        Dr5SettingsWriter w = new Dr5SettingsWriter(dump);
        return w.read();
    }
    
    public void writeSettings(Dr5Settings s) {
        byte[] dump = readDrxConfig();
        Dr5SettingsWriter w = new Dr5SettingsWriter(dump);
        w.write(s);
        writeDrxConfig(dump);
    }
    
    public void setDate() {
        execSetdate();
    }

    private byte[] readDrxConfig() {
        File file = getConfigFile(dr5Home);
        try {
            InputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                // File is too large
            }

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // close input stream
            is.close();
            
            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            return bytes;
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not load config file", e);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not load config file", e);
        }
    }

    private void assertDrxConfig() {
        LOGGER.fine("asssertDrxConfig");
        File configFile = getConfigFile(dr5Home);
//        if (configFile.exists()) {
//            LOGGER.finer(configFile + " already exists! Removing old one!");
//            configFile.delete();
//            sync();
//            sleep(2000);
//        }
        execShowconfig();
        waitUntilExists(configFile);
    }
    
    private void writeDrxConfig(byte[] dump) {
        LOGGER.fine("writeDrxConfig");
        File configFile = getConfigFile(dr5Home);
        try {
            FileOutputStream fos = new FileOutputStream(configFile);
            fos.write(dump);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not write config file", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not write config file", e);
        }
        execWriteconfig();
    }

    private void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }

    private void waitUntilExists(File configFile) {
        LOGGER.fine("waitUntilExists " + configFile);
        long timeout = System.currentTimeMillis() + FILE_CREATION_TIMEOUT_SECS * 60000;
        while (System.currentTimeMillis() < timeout) {
            if (configFile.exists()) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new RuntimeException("Operation aborted by interrupt!");
            }
        }
        throw new RuntimeException("config file didn't appear");
    }

    private void execShowconfig() {
        LOGGER.fine("execShowconfig");
        File showconfig = new File(dr5Home, "showconfig");
        if (!showconfig.exists()) {
            LOGGER.finer(showconfig + " does not exist yet, create it");
            if (!showconfig.mkdir()) {
                throw new RuntimeException("Could not invoke showconfig!");
            }
            sync();
            sleep(3000);
        } else {
            LOGGER.finer(showconfig + " already exists");
        }
    }
    
    private void execWriteconfig() {
        LOGGER.fine("execWriteconfig");
        File writeconfig = new File(dr5Home, "writeconfig");
        if (writeconfig.exists() ) {
            LOGGER.finer(writeconfig + " already exists. Deleting.");
            writeconfig.delete();
            sync();
        }
        LOGGER.finer("create "+writeconfig);
        if (!writeconfig.mkdir()) {
            throw new RuntimeException("Could not invoke writeconfig!");
        }
        sync();
        sleep(1000);
    }
    
    private void execSetdate() {
        LOGGER.fine("execSetdate");
        File setdate = new File(dr5Home, "setdate");
        if (setdate.exists()) {
            setdate.delete();
            sync();
        }
        if (!setdate.mkdir()) {
            throw new RuntimeException("Could not invoke setdate!");
        }
        sync();
    }


    private void sync() {
        try {
            Runtime.getRuntime().exec("sync");
        } catch (Throwable t) {
        }
    }

    public static Dr5SettingsStorage detect(File dr5Path) {
        if (!dr5Path.exists()) {
            throw new IllegalArgumentException("Path " + dr5Path + " does not exist!");
        }
        if (!dr5Path.isDirectory()) {
            throw new IllegalArgumentException("Path " + dr5Path + " is not a directory!");
        }
        if (!dr5Path.canRead()) {
            throw new IllegalArgumentException("Can not read path " + dr5Path + "!");
        }
        File dr5Home = null;
        if (hasCorrectSubdirectories(dr5Path)) {
            dr5Home = dr5Path;
        } else if (hasCorrectSubdirectories(dr5Path.getParentFile())) {
            dr5Home = dr5Path.getParentFile();
        }
        if (dr5Home == null) {
            throw new IllegalArgumentException("Could not detect DR5 at " + dr5Path + "!");
        }
        return new Dr5SettingsStorage(dr5Home);
    }

    private static boolean hasCorrectSubdirectories(File dr5Root) {
        File logdir = getLogbookDir(dr5Root);
        File cfgdir = getConfigDir(dr5Root);
        return logdir.exists() && logdir.isDirectory() && cfgdir.exists() && cfgdir.isDirectory() && cfgdir.canRead();
    }

    private static File getLogbookDir(File dr5Root) {
        return new File(dr5Root, "logbook");
    }

    private static File getConfigDir(File dr5Root) {
        return new File(dr5Root, ".system" + File.separator + "cfg");
    }

    private static File getConfigFile(File dr5Root) {
        return new File(getConfigDir(dr5Root), "config.drx");
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        Dr5SettingsStorage storage = Dr5SettingsStorage.detect(f);
        Dr5Settings settings = storage.readSettings();
        System.out.println(settings);
    }
}
