/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: TransferDataResponse.java
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
package net.sf.jdivelog.ci.sri.comm;

/**
 * A response for {@link TransferDataMessage}.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
public class TransferDataResponse implements ResponseDatagram {

    private static final int SERVICE_MESSAGE_ID = 0x76;
    private int _blockCounter;
    private byte[] _data;

    TransferDataResponse(int blockCounter, byte[] data) {
        _blockCounter = blockCounter;
        _data = data;
    }

    public int getBlockCounter() {
        return _blockCounter;
    }

    public byte[] getData() {
        return _data;
    }

    @Override
    public byte[] getDatagram() {
        int dataGramLength = 2 + (_data != null ? _data.length : 0);
        byte[] dataGram = new byte[dataGramLength];

        dataGram[0] = SERVICE_MESSAGE_ID;
        dataGram[1] = (byte) _blockCounter;

        if (_data != null) {
            System.arraycopy(_data, 0, dataGram, 2, dataGramLength);
        }

        return dataGram;
    }

    public static TransferDataResponse Parse(byte[] dataGram) {
        int blockCounter = dataGram[1] & 0xFF;
        byte[] data = new byte[dataGram.length - 2];

        System.arraycopy(dataGram, 2, data, 0, data.length);

        return new TransferDataResponse(blockCounter, data);
    }
}