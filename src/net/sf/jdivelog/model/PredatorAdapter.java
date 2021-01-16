/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SuuntoNGAdapter.java
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
package net.sf.jdivelog.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import net.sf.jdivelog.ci.sri.format.DiveLog;
import net.sf.jdivelog.ci.sri.format.DiveLogRecord;
import net.sf.jdivelog.ci.sri.format.constants.UnitSystem;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;
import net.sf.jdivelog.util.UnitConverter;

/**
 * Description: adapts the Shearwater Predator data structure to JDiveLog format
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.9 $
 */
public class PredatorAdapter extends TreeSet<JDive> {
    private static final long serialVersionUID = -2298111157745722955L;

    /**
     * Create a new adapter object.
     * 
     * @param diveLogs
     *            log book containing all dives from dive computer
     * @param lastDive
     *            last dive in JDiveLog's log book
     */
    public PredatorAdapter(List<DiveLog> diveLogs, JDive lastDive) {
        if (diveLogs == null) {
            throw new IllegalArgumentException("parameter diveLogs is null");
        }
        for (DiveLog diveLog : diveLogs) {
            final JDive dive = createDive(diveLog);

            if (lastDive != null && !lastDive.before(dive)) {
                break;
            }
            add(dive);
        }
    }

    private JDive createDive(DiveLog predLog) {
        Dive dive = new Dive();

        Gas gas = new Gas();
        gas.setName("DIVE");
        dive.addGas(gas);

        // init stuff required for beginnning of dive
        Calendar cal = Calendar.getInstance();
        long time = ((long) predLog.getDiveLogHeader().getTimestamp()) & 0x00000000FFFFFFFFL;
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(time * 1000L);

        Calendar adjustedCal = new GregorianCalendar(
        		cal.get(Calendar.YEAR),
        		cal.get(Calendar.MONTH),
        		cal.get(Calendar.DAY_OF_MONTH),
        		cal.get(Calendar.HOUR_OF_DAY),
        		cal.get(Calendar.MINUTE),
        		cal.get(Calendar.SECOND));

        dive.setDate(adjustedCal.getTime());
        dive.addSwitch(gas.getName());
        dive.addDelta("10");

        UnitSystem unitSystem = UnitSystem.values()[predLog.getDiveLogHeader()
                .getUnitSystem()];

        UnitConverter converter;
        switch (unitSystem) {
        case IMPERIAL:
            converter = new UnitConverter(UnitConverter.SYSTEM_IMPERIAL,
                    UnitConverter.SYSTEM_SI);
            break;
        case METRIC:
            converter = new UnitConverter(UnitConverter.SYSTEM_METRIC,
                    UnitConverter.SYSTEM_SI);
            break;
        default:
            throw new IllegalStateException(
                    "Unrecognized unit system encountered: " + unitSystem);
        }

        for (DiveLogRecord record : predLog.getDiveLogRecords()) {
            dive.addDepth(converter.convertAltitude(record.getDepth())
                    .toString());
            double ppo2 = record.getAveragePPO2() * 101325; // atm -> Pa
            dive.addPPO2("PPO2", String.valueOf(ppo2));
            dive.addTemperature(converter.convertTemperature(
                    (double) record.getWaterTemperature()).toString());
            dive.addDecoInfo(
                    record.getNextStopDepth() != null ? ""
                            + record.getNextStopDepth() : null, null, null);
        }

        JDive jDive = new JDive("si", dive);
        return jDive;
    }
}
