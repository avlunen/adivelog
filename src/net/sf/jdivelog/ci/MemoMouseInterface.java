/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: MemoMouseInterface.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jdivelog.comm.CommPortIdentifier;
import net.sf.jdivelog.comm.CommUtil;
import net.sf.jdivelog.comm.PortInUseException;
import net.sf.jdivelog.comm.PortNotFoundException;
import net.sf.jdivelog.comm.SerialPort;
import net.sf.jdivelog.comm.SerialPort.DataBits;
import net.sf.jdivelog.comm.SerialPort.Parity;
import net.sf.jdivelog.comm.SerialPort.StopBits;
import net.sf.jdivelog.comm.UnsupportedCommOperationException;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.gui.statusbar.StatusInterface;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.JDiveLog;
import net.sf.jdivelog.model.MemoMouseAdapter;
import net.sf.jdivelog.model.memomouse.MemoMouseData;

/**
 * Description: Class for data transfer with the Uwatec MemoMouse
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class MemoMouseInterface implements ComputerInterface {
    public static final String DRIVER_NAME = "Uwatec MemoMouse";

    private static final String[] PROPERTY_NAMES = { "memomouse.commport", "memomouse.download_all",
            "memomouse.timeadjustment" };

    private static final Logger LOGGER = Logger.getLogger(MemoMouseInterface.class.getName());

    private static final byte ACK = 0x06;

    private static final byte NAK = 0x15;

    private static final byte SEND_LOGBOOK_DATA = 0x55;

    private SerialPort commPort = null;

    private MemoMouseConfigurationPanel configurationPanel = null;

    private SortedSet<JDive> dives = null;

    private Properties properties = new Properties();

    private InputStream input = null;

    private OutputStream output = null;

    private StatusInterface status = null;

    //
    // inner classes
    //
    private static class MemoMouseConfigurationPanel extends JPanel {
        private static final long serialVersionUID = -1129502966312505305L;

        private JCheckBox downloadAllCheckbox = null;

        private JComboBox<String> portList = null;

        private JFormattedTextField timeAdjustmentField = null;

        private MemoMouseConfigurationPanel() {
            JLabel labelCommport = new JLabel(Messages.getString("memomouse.commport"));
            JLabel labelDownloadAll = new JLabel(Messages.getString("memomouse.download_all"));
            JLabel labelTimeAdjustment = new JLabel(Messages.getString("memomouse.timeadjustment"));

            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.anchor = GridBagConstraints.NORTHWEST;
            gc.gridy = 0;
            gc.gridx = 0;
            add(labelCommport, gc);
            gc.gridx = 1;
            add(getPortList(), gc);
            gc.gridy++;
            gc.gridx = 0;
            add(labelDownloadAll, gc);
            gc.gridx = 1;
            add(getDownloadAllCheckbox(), gc);
            gc.gridy++;
            gc.gridx = 0;
            add(labelTimeAdjustment, gc);
            gc.gridx = 1;
            add(getTimeAdjustment(), gc);
        }

        /**
         * Get the comm port the user has selected.
         * 
         * @return comm port
         */
        private String getCommPort() {
            return String.valueOf(getPortList().getSelectedItem());
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
         * Get a combo box with all available comm ports.
         * 
         * @return comm port list
         */
        private JComboBox<String> getPortList() {
            if (portList == null) {
                Vector<String> availablePorts = new Vector<String>();
                Iterator<CommPortIdentifier> it = CommUtil.getInstance().getPortIdentifiers();

                while (it.hasNext()) {
                    availablePorts.add(it.next().getName());
                }
                portList = new JComboBox<String>(availablePorts);
            }
            return portList;
        }

        /**
         * Get an input field for the "time adjustment" value.
         * 
         * This value will be added to the entry time of every dive to correct
         * discrepancy with the internal clock of the dive computer.
         * 
         * @return "time adjustment" input field
         */
        private JFormattedTextField getTimeAdjustment() {
            if (timeAdjustmentField == null) {
                timeAdjustmentField = new JFormattedTextField(NumberFormat.getIntegerInstance());
                timeAdjustmentField.setColumns(10);
            }
            return timeAdjustmentField;
        }

        /**
         * Is the checkbox "download all" selected?
         * 
         * @return checkbox state
         */
        private boolean isDownloadAllSelected() {
            return getDownloadAllCheckbox().isSelected();
        }

        /**
         * Set the comm port.
         * 
         * @param commPort
         *            comm port
         */
        private void setCommPort(String commPort) {
            getPortList().setSelectedItem(commPort);
        }

        /**
         * Set the checkbox "download all".
         * 
         * @param downloadAll
         *            checkbox state
         */
        private void setDownloadAll(boolean downloadAll) {
            getDownloadAllCheckbox().setSelected(downloadAll);
        }

        /**
         * Set the time adjustment value.
         * 
         * @param timeAdjustment
         *            time adjustment
         */
        private void setTimeAdjustment(int timeAdjustment) {
            getTimeAdjustment().setValue(timeAdjustment);
        }
    }

    /**
     * Return the panel to configure the dive computer.
     * 
     * @return ConfigurationPanel
     */
    public MemoMouseConfigurationPanel getConfigurationPanel() {
        if (configurationPanel == null) {
            configurationPanel = new MemoMouseConfigurationPanel();
        }
        return configurationPanel;
    }

    /**
     * Get a list of all downloaded dives.
     * 
     * @return dive list
     */
    public Set<JDive> getDives() {
        return dives;
    }

    /**
     * Return the name of the MemoMouse driver.
     * 
     * @return the name of the driver
     */
    public String getDriverName() {
        return DRIVER_NAME;
    }

    /**
     * Get the properties for MemoMouse interface.
     * 
     * @return the properties
     */
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    /**
     * Get the time adjustment value.
     * 
     * @return time adjustment
     */
    public int getTimeAdjustment() {
        int result = 0;

        try {
            result = Integer.parseInt(properties.getProperty(PROPERTY_NAMES[2]));
        } catch (Exception e) {
        }
        return result;
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

        String dlAllProp = this.properties.getProperty(PROPERTY_NAMES[1]);

        if (dlAllProp != null && !"".equals(dlAllProp)) {
            getConfigurationPanel().setDownloadAll(Boolean.valueOf(dlAllProp));
        } else {
            getConfigurationPanel().setDownloadAll(false);
        }

        getConfigurationPanel().setTimeAdjustment(getTimeAdjustment());
    }

    /**
     * Save the configuration.
     * 
     * @return Properties
     */
    public Properties saveConfiguration() {
        Properties result = new Properties();

        result.setProperty(PROPERTY_NAMES[0], getConfigurationPanel().getCommPort());
        result.setProperty(PROPERTY_NAMES[1], String.valueOf(getConfigurationPanel().isDownloadAllSelected()));
        result.setProperty(PROPERTY_NAMES[2], String.valueOf(getConfigurationPanel().getTimeAdjustment().getValue()));
        return result;
    }

    /**
     * Download dives from the dive computer.
     * 
     * @param status
     *            status object to show progress bar and status text
     * @param logbook
     *            list of all dives already present in JDiveLog
     */
    public void transfer(StatusInterface status, JDiveLog logbook) throws TransferException, NotInitializedException,
            InvalidConfigurationException {
        this.status = status;
        status.messageInfo(Messages.getString("memomouse.initializing"));

        JDive lastDive = logbook.getLastDive();
        boolean downloadAll = true;

        if (properties.get(PROPERTY_NAMES[1]) != null) {
            String dlAllStr = properties.getProperty(PROPERTY_NAMES[1]);

            if (dlAllStr.length() > 0) {
                downloadAll = Boolean.valueOf(dlAllStr);
            }
        }

        try {
            String portName = properties.getProperty(PROPERTY_NAMES[0]);

            if (portName == null) {
                throw new InvalidConfigurationException(Messages.getString("memomouse.comport_not_set"));
            }
            commPort = openPort(portName);

            status.messageInfo(Messages.getString("memomouse.reading"));
            status.infiniteProgressbarStart();

            byte[] read_data = readLog(new byte[] { SEND_LOGBOOK_DATA, 0x00, 0x00, 0x00, 0x00 });

            status.infiniteProgressbarEnd();

            // save MemoMouse log file to disk
            saveToFile("memoMouse.data", read_data);

            // create the dive objects
            int timeAdjustment = getTimeAdjustment();

            dives = new MemoMouseAdapter(new MemoMouseData(read_data, timeAdjustment));

            // remove old dives
            if (!downloadAll) {
                for (Iterator<JDive> it = dives.iterator(); it.hasNext();) {
                    JDive dive = it.next();

                    if (lastDive != null && !lastDive.before(dive)) {
                        it.remove();
                    }
                }
            }
        } catch (PortInUseException piuex) {
            LOGGER.log(Level.SEVERE, "transfer failed", piuex);
            throw new TransferException(Messages.getString("memomouse.comport_in_use"));
        } catch (UnsupportedCommOperationException ucoex) {
            LOGGER.log(Level.SEVERE, "transfer failed", ucoex);
            throw new TransferException(Messages.getString("memomouse.could_not_set_comparams"));
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "transfer failed", ioe);
            throw new TransferException(Messages.getString("memomouse.ioexception"));
        } catch (InterruptedException e) {
        } catch (ChecksumException cse) {
            LOGGER.log(Level.SEVERE, "transfer failed", cse);
            throw new TransferException(Messages.getString("memomouse.checksum_error"));
        } catch (CommunicationTimeoutException cte) {
            LOGGER.log(Level.SEVERE, "transfer failed", cte);
            throw new TransferException(Messages.getString("memomouse.comm_timeout"));
        } catch (PortNotFoundException pnfe) {
            LOGGER.log(Level.SEVERE, "transfer failed", pnfe);
            throw new TransferException(Messages.getString("memomouse.comport_not_found"));
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
     * Open the comm port with the given name.
     * 
     * @param portName
     *            comm port name
     * 
     * @return comm port object
     * @throws InterruptedException
     * @throws IOException
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     * @throws PortNotFoundException
     * @throws InvalidConfigurationException
     */
    private SerialPort openPort(String portName) throws InterruptedException, IOException, PortInUseException,
            UnsupportedCommOperationException, PortNotFoundException, InvalidConfigurationException {
        SerialPort result = null;
        CommPortIdentifier portId = findPort(portName);

        if (portId == null) {
            throw new InvalidConfigurationException(Messages.getString("memomouse.comport_not_found"));
        }
        result = CommUtil.getInstance().open(portId);
        result.setSerialPortParams(9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1);
        result.enableReceiveTimeout(1000);
        result.setDTR(true);
        result.setRTS(false);
        input = result.getInputStream();
        output = result.getOutputStream();
        return result;
    }

    /**
     * Pack the given bytes into a packet, add length and checksum.
     * 
     * @param bytes
     *            the bytes to be packes
     * @param withInnerChecksum
     *            whether or not a checksum has to be added
     * 
     * @return packet containing the given bytes plus some additional info
     * @throws IOException
     *             if an I/O error occurs.
     */
    private byte[] pack(byte[] bytes, boolean withInnerChecksum) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        if (withInnerChecksum) {
            result.write((byte) (bytes.length + 3));
        } else {
            result.write((byte) (bytes.length + 2));
        }
        result.write((byte) bytes.length);
        result.write((byte) 0);
        result.write(bytes);
        if (withInnerChecksum) {
            result.write((byte) ((byte) bytes.length ^ xor(bytes)));
        }
        result.write(xor(result.toByteArray()));
        return result.toByteArray();
    }

    /**
     * Read the outer packet from MemoMouse and return the inner packet
     * including length and checksum.
     * 
     * @return inner packet including length and checksum
     * @throws InterruptedException
     *             if another thread has interrupted the current thread.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws ChecksumException
     *             wrong checksum
     */
    private byte[] readInnerPacket() throws InterruptedException, IOException, ChecksumException {
        byte[] result = null;
        int length = receive();

        if (length > 0) {
            result = new byte[length];
            for (int index = 0; index < result.length; index++) {
                result[index] = (byte) receive();
            }

            int checksum = receive();
            int test = length ^ xor(result);

            if (checksum != test) {
                throw new ChecksumException();
            }
            send(ACK);
        }
        return result;
    }

    /**
     * Read all log data from the MemoMouse.
     * 
     * @param data
     *            byte sequence that triggers sending of the log data
     * @return all log data from the MemoMouse
     * @throws InterruptedException
     *             if another thread has interrupted the current thread.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws ChecksumException
     *             wrong checksum
     * @throws CommunicationTimeoutException
     *             if there was no answer from the MemoMouse.
     */
    private byte[] readLog(byte[] data) throws InterruptedException, IOException, ChecksumException,
            CommunicationTimeoutException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        // read ID string
        readPacket();

        // send command
        send(data, false);

        // read data
        result.write(readPacket());

        return result.toByteArray();
    }

    /**
     * Read all outer packets from the MemoMouse responded to one command.
     * 
     * @return all inner packets for this command
     * @throws InterruptedException
     *             if another thread has interrupted the current thread.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws ChecksumException
     *             wrong checksum
     */
    private byte[] readPacket() throws InterruptedException, IOException, ChecksumException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] bytes = null;

        while (bytes == null) {
            bytes = readInnerPacket();
            Thread.sleep(300);
        }
        result.write(bytes, 2, bytes.length - 2);

        int total = bytes[0] + (bytes[1] << 8) + 3 - bytes.length;

        while (total > 0) {
            bytes = readInnerPacket();
            total -= bytes.length;
            result.write(bytes, 0, bytes.length);
        }
        return result.toByteArray();
    }

    /**
     * Receive one byte from the MemoMouse.
     * 
     * @return one byte from the MemoMouse
     * @throws IOException
     *             if an I/O error occurs.
     */
    private int receive() throws IOException {
        int result = -1;

        result = input.read();
        if (result != -1) {
            status.commReceive();
            result = reorder(result);
        }
        return result;
    }

    /**
     * Reorder the byte.
     * 
     * @param b
     *            the byte
     * 
     * @return given byte in reverse bit order
     */
    private byte reorder(int b) {
        int result;

        result = (b & 0x01) << 7;
        result += (b & 0x02) << 5;
        result += (b & 0x04) << 3;
        result += (b & 0x08) << 1;
        result += (b & 0x10) >> 1;
        result += (b & 0x20) >> 3;
        result += (b & 0x40) >> 5;
        result += (b & 0x80) >> 7;
        return (byte) result;
    }

    /**
     * Save the given bytes to a file.
     * 
     * @param name
     *            file name
     * @param bytes
     *            the bytes
     * 
     * @throws IOException
     *             if an I/O error occurs.
     */
    private void saveToFile(String name, byte[] bytes) throws IOException {
        OutputStream file = null;

        try {
            file = new FileOutputStream(new java.io.File(System.getProperty("java.io.tmpdir"), name));
            for (int index = 0; index < bytes.length; index++) {
                file.write(bytes[index]);
            }
        } finally {
            if (file != null) {
                file.close();
            }
        }
    }

    /**
     * Reorder the bytes.
     * 
     * @param bytes
     *            the bytes
     * 
     * @return given bytes in reverse bit order
     */
    private byte[] reorder(byte[] bytes) {
        byte[] result = new byte[bytes.length];

        for (int index = 0; index < bytes.length; index++) {
            result[index] = reorder(bytes[index]);
        }
        return result;
    }

    /**
     * Send one byte to the MemoMouse.
     * 
     * @param b
     *            the byte to be sent
     * 
     * @throws InterruptedException
     *             if another thread has interrupted the current thread.
     * @throws IOException
     *             if an I/O error occurs.
     */
    private void send(int b) throws InterruptedException, IOException {
        Thread.sleep(50);
        status.commSend();
        output.write(reorder(b));
        output.flush();
    }

    /**
     * Send some bytes to the MemoMouse.
     * 
     * @param bytes
     *            the bytes to be sent
     * @param withInnerChecksum
     *            whether or not a checksum has to be added
     * 
     * @throws InterruptedException
     *             if another thread has interrupted the current thread.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws CommunicationTimeoutException
     *             if there was no answer from the MemoMouse.
     */
    private void send(byte[] bytes, boolean withInnerChecksum) throws InterruptedException, IOException,
            CommunicationTimeoutException {
        byte[] b = reorder(pack(bytes, withInnerChecksum));

        for (int index = 0; index < 50; index++) {
            Thread.sleep(50);
            status.commSend();
            output.write(b);
            output.flush();

            int ch = NAK;
            int retries = 1;

            while ((ch != ACK) && (retries < 2)) {
                ch = receive();
                if (ch == ACK) {
                    return;
                }
                retries++;
            }
            if (ch == ACK) {
                return;
            }
            Thread.sleep(300);
        }
        throw new CommunicationTimeoutException();
    }

    /**
     * Calculate a checksum by XORing the given bytes. Stop after "count" bytes.
     * 
     * @param bytes
     *            the bytes for checksum calculation
     * @param count
     *            stop after "count" bytes
     * 
     * @return checksum for the given bytes
     */
    private byte xor(byte[] bytes, int count) {
        byte result = 0;

        for (int index = 0; index < count; index++) {
            result ^= bytes[index];
        }
        return result;
    }

    /**
     * Calculate a checksum by XORing the given bytes.
     * 
     * @param bytes
     *            the bytes for checksum calculation
     * @return checksum for the given bytes
     */
    private byte xor(byte[] bytes) {
        return xor(bytes, bytes.length);
    }
}
