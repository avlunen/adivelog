/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SuuntoNGInterface.java
 *
 * @author Andr&eacute; Schenk <andre_schenk@users.sourceforge.net>
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jdivelog.comm.CommPortIdentifier;
import net.sf.jdivelog.comm.CommUtil;
import net.sf.jdivelog.comm.PortInUseException;
import net.sf.jdivelog.comm.PortNotFoundException;
import net.sf.jdivelog.comm.SerialPort;
import net.sf.jdivelog.comm.UnsupportedCommOperationException;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.gui.statusbar.StatusInterface;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.JDiveLog;
import net.sf.jdivelog.model.SuuntoNGAdapter;
import net.sf.jdivelog.model.suuntong.DiveProfileData;
import net.sf.jdivelog.model.suuntong.DiveProfileHeader;
import net.sf.jdivelog.model.suuntong.GetVersion;
import net.sf.jdivelog.model.suuntong.LogBook;
import net.sf.jdivelog.model.suuntong.LogEntry;
import net.sf.jdivelog.model.suuntong.Memory;
import net.sf.jdivelog.model.suuntong.MemoryHeader;
import net.sf.jdivelog.model.suuntong.Version;

/**
 * Description: Class for data transfer with the next generation Suunto
 * computers
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.19 $
 */
public class SuuntoNGInterface implements ComputerInterface {
    public static final String DRIVER_NAME = "Suunto D6, D9, Vyper 2";

    private static final String[] PROPERTY_NAMES = { "suunto.commport", "suunto.computermodel", "suunto.download_all" };

    private static final Logger LOGGER = Logger.getLogger(SuuntoNGInterface.class.getName());

    private SerialPort commPort = null;

    private SuuntoComputerType computerModel = null;

    private SunntoConfigurationPanel configurationPanel = null;

    private TreeSet<JDive> dives = null;

    private Memory memory = null;

    private Properties properties = new Properties();

    //
    // inner classes
    //
    private static class SunntoConfigurationPanel extends JPanel {
        private static final long serialVersionUID = 8983093354040193223L;

        private JCheckBox downloadAllCheckbox = null;

        private JComboBox modelList = null;

        private JComboBox portList = null;

        private List<SuuntoComputerType> computerModelList = new LinkedList<SuuntoComputerType>();

        public SunntoConfigurationPanel() {
            JLabel labelCommport = new JLabel(Messages.getString("suunto.commport"));
            JLabel labelComputermodel = new JLabel(Messages.getString("suunto.computermodel"));
            JLabel labelDownloadAll = new JLabel(Messages.getString("suunto.download_all"));

            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.anchor = GridBagConstraints.NORTHWEST;
            gc.gridy = 0;
            gc.gridx = 0;
            add(labelCommport, gc);
            gc.gridx = 1;
            add(getPortList(), gc);
            gc.gridy = 1;
            gc.gridx = 0;
            add(labelComputermodel, gc);
            gc.gridx = 1;
            add(getComputerModelList(), gc);
            gc.gridy++;
            gc.gridx = 0;
            add(labelDownloadAll, gc);
            gc.gridx = 1;
            add(getDownloadAllCheckbox(), gc);
        }

        /**
         * Get the comm port the user has selected.
         * 
         * @return comm port
         */
        public String getCommPort() {
            return String.valueOf(getPortList().getSelectedItem());
        }

        /**
         * Get a combo box with all Suunto "NG" computer models.
         * 
         * @return model list
         */
        private JComboBox getComputerModelList() {
            if (modelList == null) {
                Map<String, SuuntoComputerType> sortedList = new TreeMap<String, SuuntoComputerType>();

                for (SuuntoComputerType type : SuuntoComputerType.values()) {
                    if (type.getBaudRate() == 9600) {
                        sortedList.put(type.name(), type);
                    }
                }
                computerModelList.addAll(sortedList.values());
                modelList = new JComboBox(sortedList.values().toArray());
            }
            return modelList;
        }

        /**
         * Get a checkbox for the "download all" decision.
         * 
         * @return "download all" checkbox
         */
        private JCheckBox getDownloadAllCheckbox() {
            if (downloadAllCheckbox == null) {
                downloadAllCheckbox = new JCheckBox();
            }
            return downloadAllCheckbox;
        }

        /**
         * Get the computer model the user has selected.
         * 
         * @return computer model
         */
        public String getModel() {
            return String.valueOf(((SuuntoComputerType) getComputerModelList().getSelectedItem()).getIdentifier());
        }

        /**
         * Get a combo box with all available comm ports.
         * 
         * @return comm port list
         */
        private JComboBox getPortList() {
            if (portList == null) {
                Vector<String> availablePorts = new Vector<String>();
                Iterator<CommPortIdentifier> it = CommUtil.getInstance().getPortIdentifiers();

                while (it.hasNext()) {
                    availablePorts.add(it.next().getName());
                }
                portList = new JComboBox(availablePorts);
            }
            return portList;
        }

        /**
         * Is the checkbox "download all" selected?
         * 
         * @return checkbox state
         */
        public boolean isDownloadAllSelected() {
            return getDownloadAllCheckbox().isSelected();
        }

        /**
         * Set the comm port.
         * 
         * @param commPort
         *            comm port
         */
        public void setCommPort(String commPort) {
            getPortList().setSelectedItem(commPort);
            revalidate();
        }

        /**
         * Set the computer model.
         * 
         * @param model
         *            computer model
         */
        public void setModel(int model) {
            try {
                int index = computerModelList.indexOf(SuuntoComputerType.getFromIdentifier(model));

                if ((index >= 0) && (index < getComputerModelList().getItemCount())) {
                    getComputerModelList().setSelectedIndex(index);
                    revalidate();
                }
            } catch (InvalidConfigurationException e) {
            }
        }
    }

    /**
     * Return the panel to configure the dive computer
     * 
     * @return ConfigurationPanel
     */
    public SunntoConfigurationPanel getConfigurationPanel() {
        if (configurationPanel == null) {
            configurationPanel = new SunntoConfigurationPanel();
        }
        return configurationPanel;
    }

    /**
     * Get a list of all downloaded dives.
     * 
     * @return dive list
     */
    public TreeSet<JDive> getDives() {
        return dives;
    }

    /**
     * Return the name of the Suunto driver
     * 
     * @return the name of the driver
     */
    public String getDriverName() {
        return DRIVER_NAME;
    }

    /**
     * Get the properties for Suunto interface
     * 
     * @return the properties
     */
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    /**
     * Initialize this object with the values from the GUI panel.
     * 
     * @param properties
     */
    public void initialize(Properties properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
        getConfigurationPanel().setCommPort(this.properties.getProperty(PROPERTY_NAMES[0]));
        try {
            getConfigurationPanel().setModel(Integer.parseInt(this.properties.getProperty(PROPERTY_NAMES[1])));
        } catch (NumberFormatException nfe) {
        }
        try {
            computerModel = getCurrentModel();
        } catch (InvalidConfigurationException e) {
            LOGGER.log(Level.SEVERE, Messages.getString("suunto.model_invalid"), e);
        }
    }

    /**
     * Save the configuration
     * 
     * @return Properties
     */
    public Properties saveConfiguration() {
        Properties result = new Properties();

        result.setProperty(PROPERTY_NAMES[0], getConfigurationPanel().getCommPort());
        result.setProperty(PROPERTY_NAMES[1], getConfigurationPanel().getModel());
        result.setProperty(PROPERTY_NAMES[2], String.valueOf(getConfigurationPanel().isDownloadAllSelected()));
        return result;
    }

    /**
     * Download dives from the dive computer.
     * 
     * @param status
     *            status object to show progress bar and status text
     * @param logbook
     *            list of all dives already present in JDiveLog
     * 
     * @throws InvalidConfigurationException
     *             Thrown if comm port or computer model is not configured.
     * @throws TransferException
     *             Thrown if the communication to the dive computer failed.
     */
    public void transfer(StatusInterface status, JDiveLog logbook) throws TransferException,
            InvalidConfigurationException {
        status.messageInfo(Messages.getString("suunto.initializing"));

        JDive lastDive = logbook.getLastDive();
        /*
         * DiveLog only saves Hours and Minutes, Computer also reports seconds,
         * to accurately compare the two we zero seconds and milliseconds,
         * otherwise the current seconds / milliseconds would be used.
         */
        Date lastDiveDate = lastDive == null ? null : setSecondsMillisecondsToNull(lastDive.getDate());
        boolean downloadAll = true;

        if (properties.get(PROPERTY_NAMES[2]) != null) {
            String dlAllStr = properties.getProperty(PROPERTY_NAMES[2]);

            if (dlAllStr.length() > 0) {
                downloadAll = Boolean.valueOf(dlAllStr);
            }
        }

        try {
            String portName = properties.getProperty(PROPERTY_NAMES[0]);

            if (portName == null) {
                throw new InvalidConfigurationException(Messages.getString("suunto.comport_not_set"));
            }
            commPort = openPort(portName);

            // read computer model
            SuuntoComputerType currentModel = readComputerModel(status);
            if (computerModel != currentModel) {
                throw new InvalidConfigurationException(Messages.getString("suunto.wrong_computer_set", computerModel,
                        currentModel));
            }

            status.messageInfo(Messages.getString("suunto.reading"));

            // read memory header
            byte[] bytes = readMemory(status, 0, MemoryHeader.SIZE);
            MemoryHeader memoryHeader = new MemoryHeader(bytes);
            LogBook logBook = new LogBook(memoryHeader);

            status.countingProgressbarStart(memoryHeader.numberOfDivesInBuffer, true);

            // read all dive profiles
            int currentDiveOffset = memoryHeader.oldestDiveInBuffer;

            for (int diveNumber = 1; diveNumber <= memoryHeader.numberOfDivesInBuffer; diveNumber++) {
                status.countingProgressbarIncrement();

                // read dive profile header to get offset to next dive
                // profile header
                bytes = readMemory(status, currentDiveOffset, DiveProfileHeader.VYPER_AIR_SIZE);

                DiveProfileHeader diveProfileHeader = new DiveProfileHeader(bytes);

                // read dive profile header with dive profile data
                if (diveProfileHeader.nextDiveOffset > currentDiveOffset) {
                    bytes = readMemory(status, currentDiveOffset, diveProfileHeader.nextDiveOffset - currentDiveOffset);
                } else {
                    bytes = readMemory(status, currentDiveOffset, diveProfileHeader.nextDiveOffset - MemoryHeader.SIZE);
                }
                currentDiveOffset = diveProfileHeader.nextDiveOffset;
                diveProfileHeader = new DiveProfileHeader(bytes);

                LogEntry logEntry = new LogEntry(diveProfileHeader);

                // copy dive profile data into separate array
                byte[] diveProfile = new byte[bytes.length - diveProfileHeader.getSize()];

                System.arraycopy(bytes, diveProfileHeader.getSize(), diveProfile, 0, diveProfile.length);

                DiveProfileData diveProfileData = new DiveProfileData(diveProfile,
                        diveProfileHeader.sampleRecordingInterval, diveProfileHeader.temperatureRecordingInterval,
                        diveProfileHeader.offsetToFirstMarker, diveProfileHeader.hasTankPressureInformation());

                if (downloadAll || (lastDiveDate == null) || logEntry.date.after(lastDiveDate)) {
                    logEntry.setProfile(diveProfileData.getProfile());
                    logBook.addLogEntry(logEntry);
                }
            }

            // save Suunto data to disk
            memory.saveToFile(new File(System.getProperty("java.io.tmpdir"), "suuntoNG.data").getPath());

            // create the dive objects
            dives = new SuuntoNGAdapter(logBook);
        } catch (PortInUseException piuex) {
            LOGGER.log(Level.SEVERE, "transfer failed", piuex);
            throw new TransferException(Messages.getString("suunto.comport_in_use"));
        } catch (UnsupportedCommOperationException ucoex) {
            LOGGER.log(Level.SEVERE, "transfer failed", ucoex);
            throw new TransferException(Messages.getString("suunto.could_not_set_comparams"));
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "transfer failed", ioe);
            throw new TransferException(Messages.getString("suunto.ioexception"));
        } catch (InterruptedException e) {
        } catch (ChecksumException cse) {
            LOGGER.log(Level.SEVERE, "transfer failed", cse);
            throw new TransferException(Messages.getString("suunto.checksum_error"));
        } catch (CommunicationTimeoutException cte) {
            LOGGER.log(Level.SEVERE, "transfer failed", cte);
            throw new TransferException(Messages.getString("suunto.comm_timeout"));
        } catch (PortNotFoundException pnfe) {
            LOGGER.log(Level.SEVERE, "transfer failed", pnfe);
            throw new TransferException(Messages.getString("suunto.comport_not_found"));
        } finally {
            cleanup(status);
        }
    }

    /**
     * Finish progress bar, close comm port.
     * 
     * @param status
     *            status object
     */
    private void cleanup(StatusInterface status) {
        status.messageClear();
        status.infiniteProgressbarEnd();
        if (commPort != null) {
            commPort.close();
            commPort = null;
        }
    }

    /**
     * Find the comm port with the given name.
     * 
     * @param portName
     *            comm port name
     * 
     * @return comm port identifier
     */
    private CommPortIdentifier findPort(String portName) {
        CommPortIdentifier result = null;
        Iterator<CommPortIdentifier> it = CommUtil.getInstance().getPortIdentifiers();

        while (it.hasNext()) {
            final CommPortIdentifier id = it.next();

            if (portName.equals(id.getName())) {
                result = id;
                break;
            }
        }
        return result;
    }

    /**
     * Get the currently configured computer model.
     * 
     * @return computer model
     * 
     * @throws InvalidConfigurationException
     *             Thrown if no computer model is configured.
     */
    public SuuntoComputerType getCurrentModel() throws InvalidConfigurationException {
        SuuntoComputerType result = SuuntoComputerType.UNKNOWN;
        String model = (String) properties.get(PROPERTY_NAMES[1]);

        if (model != null) {
            try {
                result = SuuntoComputerType.getFromIdentifier(Integer.parseInt(model));
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * Get the version information of the dive computer.
     * 
     * @param status
     *            status object
     * 
     * @return version information
     * @throws IOException
     *             Thrown if the communication to the dive computer failed.
     * @throws ChecksumException
     *             Thrown if the checksum was wrong.
     * @throws InterruptedException
     *             Thrown if any thread has interrupted the current thread.
     */
    private Version getVersion(StatusInterface status) throws IOException, ChecksumException, InterruptedException {
        Version result = null;

        status.commSend();
        result = new GetVersion(commPort).execute();
        status.commReceive();
        return result;
    }

    /**
     * Open the comm port with the given name.
     * 
     * @param portName
     *            comm port name
     * 
     * @return comm port object
     * @throws InterruptedException
     *             Thrown if any thread has interrupted the current thread.
     * @throws IOException
     *             Thrown if the communication to the dive computer failed.
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     * @throws PortNotFoundException
     * @throws InvalidConfigurationException
     *             Thrown if comm port is not configured.
     */
    private SerialPort openPort(String portName) throws InterruptedException, IOException, PortInUseException,
            UnsupportedCommOperationException, PortNotFoundException, InvalidConfigurationException {
        SerialPort result = null;
        CommPortIdentifier portId = findPort(portName);

        if (portId == null) {
            throw new InvalidConfigurationException(Messages.getString("suunto.comport_not_found"));
        }
        result = CommUtil.getInstance().open(portId);
        result.setSerialPortParams(computerModel.getBaudRate(), computerModel.getNumDataBits(),
                computerModel.getParity(), computerModel.getNumStopBits());
        result.enableReceiveThreshold(1);
        result.enableReceiveTimeout(3000);
        result.setFlowControlMode(SerialPort.FlowControlMode.FLOWCONTROL_NONE);
        result.setDTR(true);
        Thread.sleep(100);
        memory = new Memory(result);
        return result;
    }

    /**
     * Get the computer model currently connected via comm port.
     * 
     * @param status
     *            status object
     * 
     * @return computer model
     * @throws IOException
     *             Thrown if the communication to the dive computer failed.
     * @throws ChecksumException
     *             Thrown if the checksum was wrong.
     * @throws CommunicationTimeoutException
     * @throws InterruptedException
     *             Thrown if any thread has interrupted the current thread.
     */
    private SuuntoComputerType readComputerModel(StatusInterface status) throws IOException, ChecksumException,
            CommunicationTimeoutException, InterruptedException {
        try {
            return getVersion(status).id;
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    /**
     * Read a memory block from the dive computer.
     * 
     * @param status
     *            status object
     * @param address
     *            start address
     * @param count
     *            number of bytes
     * 
     * @return memory block
     * @throws IOException
     *             Thrown if the communication to the dive computer failed.
     * @throws ChecksumException
     *             Thrown if the checksum was wrong.
     * @throws InterruptedException
     *             Thrown if any thread has interrupted the current thread.
     */
    private byte[] readMemory(StatusInterface status, int address, int count) throws IOException, ChecksumException,
            InterruptedException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] bytes = null;

        status.commSend();
        if (address + count < Memory.SIZE) {
            result.write(memory.read(address, count));
        } else {
            bytes = memory.read(address, Memory.SIZE - address);
            result.write(bytes);
            result.write(memory.read(MemoryHeader.SIZE, count - bytes.length));
        }
        status.commReceive();
        return result.toByteArray();
    }

    private static Date setSecondsMillisecondsToNull(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
