/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SRIPredatorInterface.java
 *
 * @author Kasra F (Shearwater Research Inc) <kfaghihi@shearwaterresearch.com>
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
package net.sf.jdivelog.ci;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import net.sf.jdivelog.ci.sri.comm.PredatorCommunicator;
import net.sf.jdivelog.ci.sri.format.DiveLogParser;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.gui.statusbar.StatusInterface;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.JDiveLog;
import net.sf.jdivelog.model.PredatorAdapter;

/**
 * Description: Class for data transfer with Shearwater Predator computer (v37
 * to v42).
 * 
 * @author Kasra F.
 */
public class SRIPredatorInterface implements ComputerInterface {

    public static final String DRIVER_NAME = "Shearwater Predator";

    private static final Logger LOGGER = Logger
            .getLogger(SRIPredatorInterface.class.getName());

    private SRIPredatorInterfaceConfiguration configPanel;
    private TreeSet<JDive> jDives;

    @Override
    public String getDriverName() {
        return DRIVER_NAME;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] {};
    }

    @Override
    public SRIPredatorInterfaceConfiguration getConfigurationPanel() {
        if (configPanel == null) {
            configPanel = new SRIPredatorInterfaceConfiguration();
        }
        return configPanel;
    }

    @Override
    public Properties saveConfiguration() {
        return new Properties();
    }

    /**
     * Save the downloaded data to /tmp for debugging.
     * 
     * @param file
     *            file in /tmp
     * @param data
     *            bytes to save
     * 
     * @throws IOException
     */
    private void saveDataFile(File file, byte data[]) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);

        fileoutputstream.write(data);
        fileoutputstream.close();
    }

    @Override
    public void initialize(Properties properties) {
    }

    @Override
    public void transfer(StatusInterface statusInterface, JDiveLog logbook)
            throws TransferException, NotInitializedException,
            InvalidConfigurationException {
        statusInterface.messageInfo(Messages.getString("sri.connect_predator"));

        PredatorCommunicator pc = null;
        try {
            pc = new PredatorCommunicator();
            pc.connect();

            statusInterface.messageInfo(Messages
                    .getString("sri.request_serial_number"));
            String serialNumber = pc.getSerialNumber();

            LOGGER.info("serial number: " + serialNumber);
            
            statusInterface.messageInfo(Messages
                    .getString("sri.dumping_memory"));
            byte[] dump = pc.getMemoryDump();

            // save Predator log file to disk
            saveDataFile(new File(System.getProperty("java.io.tmpdir"),
                    "predator.swlogdata"), dump);

            statusInterface.messageInfo(Messages
                    .getString("sri.parsing_divelogs"));
            DiveLogParser parser = new DiveLogParser();

            jDives = new PredatorAdapter(parser.getDiveLogs(dump),
                    logbook.getLastDive());

            statusInterface.messageInfo(Messages
                    .getString("sri.divelog_parsed"));
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "transfer failed, IOException", ioe);
            throw new TransferException(Messages.getString("sri.comm_failed")
                    + ": " + ioe.toString());
        } catch (InterruptedException ie) {
            throw new TransferException(
                    Messages.getString("sri.comm_interrupted") + ": "
                            + ie.toString());
        } catch (RuntimeException re) {
            throw new TransferException(Messages.getString("sri.unknown_error")
                    + ": " + re.toString());
        } finally {
            if (pc != null) {
                pc.close();
            }
        }
    }

    @Override
    public TreeSet<JDive> getDives() {
        return jDives;
    }

    private class SRIPredatorInterfaceConfiguration extends JPanel {

        private static final long serialVersionUID = 1L;

    }
}
