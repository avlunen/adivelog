/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SuuntoComputerType.java
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

import static net.sf.jdivelog.comm.SerialPort.DataBits;
import static net.sf.jdivelog.comm.SerialPort.Parity;
import static net.sf.jdivelog.comm.SerialPort.StopBits;

/**
 * Description: Enumeration for Suunto computer models.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 548 $
 */
public enum SuuntoComputerType {
    UNKNOWN     (0x00, "unknown",       0,    DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    STINGER     (0x03, "Stinger",       2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    MOSQUITO    (0x04, "Mosquito",      2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    NEW_VYPER   (0x0a, "new Vyper",     2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    VYTEC       (0x0b, "VyTec",         2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    VYPER_COBRA (0x0c, "Vyper / Cobra", 2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    GEKKO       (0x0d, "Gekko",         2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false),
    D9          (0x0e, "D9",            9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1, true),
    D6          (0x0f, "D6",            9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1, false),
    VYPER2      (0x10, "Vyper2",        9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1, false),
    D4          (0x12, "D4",            9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1, false),
    VYPER_AIR   (0x13, "Vyper Air",     9600, DataBits.DataBits_8, Parity.NONE, StopBits.StopBits_1, true),
    ZOOP        (0x16, "Zoop",          2400, DataBits.DataBits_8, Parity.ODD,  StopBits.StopBits_1, false);

    private final int identifier;

    private final String label;

    private final int baudRate;

    private final DataBits numDataBits;

    private final Parity parity;

    private final StopBits numStopBits;

    private final boolean isAirIntegrated;

    SuuntoComputerType(int identifier, String label, int baudRate,
        DataBits numDataBits, Parity parity, StopBits numStopBits,
        boolean isAirIntegrated) {
        this.identifier = identifier;
        this.label = label;
        this.baudRate = baudRate;
        this.numDataBits = numDataBits;
        this.parity = parity;
        this.numStopBits = numStopBits;
        this.isAirIntegrated = isAirIntegrated;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getIdentifier() {
        return identifier;
    }

    public DataBits getNumDataBits() {
        return numDataBits;
    }

    public StopBits getNumStopBits() {
        return numStopBits;
    }

    public Parity getParity() {
        return parity;
    }

    public boolean isAirIntegrated() {
        return isAirIntegrated;
    }

    public String toString() {
        return label;
    }

    public static SuuntoComputerType getFromOrdinal(int ordinal) throws InvalidConfigurationException {
        for (SuuntoComputerType e : SuuntoComputerType.values()) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        throw new InvalidConfigurationException("no Suunto computer with ordinal " + ordinal
            + " found");
    }

    public static SuuntoComputerType getFromIdentifier(int identifier)
        throws InvalidConfigurationException {
        for (SuuntoComputerType e : SuuntoComputerType.values()) {
            if (e.identifier == identifier) {
                return e;
            }
        }
        throw new InvalidConfigurationException(
            "no Suunto computer with identifier " + identifier + " found");
    }
}
