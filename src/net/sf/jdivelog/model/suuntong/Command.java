/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Command.java
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
package net.sf.jdivelog.model.suuntong;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import net.sf.jdivelog.ci.ChecksumException;
import net.sf.jdivelog.comm.SerialPort;

/**
 * Description: general object to encapsulate the serial communication with the
 * Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.14 $
 */
public abstract class Command {
    private final SerialPort port;

    private final InputStream input;

    private final OutputStream output;

    private final CommandType commandType;

    /**
     * Create a new object for serial communication.
     * 
     * @param commandType
     *            type of the command
     * @param port
     *            serial port
     * 
     * @throws IOException
     *             Thrown in case of a communication error.
     */
    public Command(CommandType commandType, SerialPort port) throws IOException {
        this.commandType = commandType;
        this.port = port;
        if (port != null) {
            input = port.getInputStream();
            output = port.getOutputStream();
        } else {
            input = null;
            output = null;
        }
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);

        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Calculate a CRC of the given bytes.
     * 
     * @param bytes
     *            the bytes
     * 
     * @return CRC
     */
    private byte crc(byte[] bytes) {
        byte result = 0x00;

        for (int i = 0; i < bytes.length; ++i) {
            result ^= bytes[i];
        }
        return result;
    }

    /**
     * Read some bytes from the Suunto D6.
     * 
     * @return the read bytes
     * 
     * @throws IOException
     *             Thrown in case of a communication error.
     */
    private byte[] read(int count) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int chr = 0;
        int firstByte = -1;

        while (result.size() < count && (chr = input.read()) != -1) {
            if (firstByte == -1) {
                firstByte = chr;
            }
            result.write((byte) chr);
        }
        return result.toByteArray();
    }

    /**
     * Write some bytes to the Suunto D6.
     * 
     * @param data
     *            the bytes
     * 
     * @throws IOException
     *             Thrown in case of a communication error.
     */
    private void write(byte[] data) throws IOException, InterruptedException {
        byte[] request = new byte[data != null ? data.length + 4 : 4];

        request[0] = commandType.getCommandByte();
        if (data != null) {
            request[1] = (byte) (data.length / 0xFF);
            request[2] = (byte) (data.length % 0xFF);
            System.arraycopy(data, 0, request, 3, data.length);
        } else {
            request[1] = 0;
            request[2] = 0;
        }
        request[request.length - 1] = crc(request);
        output.write(request);
        output.flush();
    }

    /**
     * Execute a command by sending the given data and receiving the answer from
     * the Suunto D6.
     * 
     * @param data
     *            the bytes
     * 
     * @return the answer from the Suunto
     * 
     * @throws IOException
     *             Thrown in case of a communication error.
     * @throws ChecksumException
     *             Thrown if the CRC was wrong.
     */
    protected byte[] execute(byte[] data) throws IOException,
            ChecksumException, InterruptedException {
        byte[] result = null;

        for (int retries = 1; retries < 5
                && (result == null || result.length == 0); retries++) {

            port.setRTS(false);

            // send command
            write(data);

            port.setRTS(true);

            // receive echo
            read(data.length + 4);

            // receive response length
            byte[] length = read(4);

            if (length.length == 4) {
                if (length[0] != commandType.getCommandByte()) {
                    System.out.println("Unexpected command in response: "
                            + Arrays.toString(length));
                }

                int responseLength = length[1] & 0xFF << 8 | length[2] & 0xFF;

                // receive answer
                byte[] response = concat(length, read(responseLength));

                if (response.length > 4) {
                    // check CRC
                    byte[] buffer = new byte[response.length - 1];
                    int crc = response[response.length - 1];

                    System.arraycopy(response, 0, buffer, 0, buffer.length);

                    if (crc != crc(buffer)) {
                        throw new ChecksumException();
                    }

                    // command byte + packet length + data length + CRC byte
                    int offset = 1 + 2 + data.length + 1;

                    result = new byte[response.length - offset];
                    System.arraycopy(response, (offset - 1), result, 0,
                            result.length);
                    break;
                } else {
                    System.out
                            .println("response too short, expected at least 4 bytes but got only "
                                    + response.length);
                }
            } else {
                System.out
                        .println("response too short, expected 4 bytes but got only "
                                + length.length);
            }

        }
        return result;
    }
}
