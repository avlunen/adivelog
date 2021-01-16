/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: MemoMouseAdapter.java
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

import java.util.List;
import java.util.TreeSet;

import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.aladin.DepthProfile;
import net.sf.jdivelog.model.aladin.DepthProfileEntry;
import net.sf.jdivelog.model.memomouse.LogEntry;
import net.sf.jdivelog.model.memomouse.MemoMouseData;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;

/**
 * Description: adapts the MemoMouse data structure to JDiveLog format
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 548 $
 */
public class MemoMouseAdapter extends TreeSet<JDive> {
    private static final long serialVersionUID = -3281921774933767710L;

    private int diveTime = 0;
    private boolean deco_stop = false;
    private boolean rbt_alarm = false;

    public MemoMouseAdapter(MemoMouseData memoMouseData) {
        if (memoMouseData == null) {
            throw new IllegalArgumentException(
                    "parameter memoMouseData is null");
        }

        int index = 1;

        for (LogEntry logEntry : memoMouseData.getLogbook()) {
            Dive depthProfile = convertDepthProfile(logEntry.getDepthProfile());
            JDive dive = new JDive();

            dive.setDate(logEntry.entryTime);
            dive.setDepth(logEntry.maximumDepth.toString());
            dive.setDiveNumber(new Long(index++));
            dive.setDuration(new Double(logEntry.bottomTime));
            dive.setTemperature(logEntry.waterTemperature.toString());

            Gas gas = AladinAdapter.getGas(logEntry.airConsumption);
            Tank tank = new Tank();
            Equipment equipment = new Equipment();

            tank.setGas(gas);
            equipment.addTank(tank);
            dive.setEquipment(equipment);

            if (depthProfile != null) {
                depthProfile.addGas(gas);
                depthProfile.setDate(logEntry.entryTime);
                depthProfile.setSurfaceinterval(String
                        .valueOf(logEntry.surfaceTime));
                dive.setDate(logEntry.entryTime);
                dive.setDive(depthProfile);
                dive.setAverageDepth(depthProfile.getAverageDepth());
            }
            add(dive);
        }
    }

    /**
     * Add alarms to the current dive.
     * 
     * @param dive
     *            the dive
     * @param warnings
     *            the warnings to be added
     */
    private void addAlarms(Dive dive, int warnings) {
        if (dive != null) {
            if ((warnings & 16) > 0) {
                dive.addAlarm(Messages.getString("workalarm"));
            }
            if ((warnings & 8) > 0) {
                dive.addAlarm(Messages.getString("decoceilingalarm"));
            }
            if ((warnings & 4) > 0) {
                dive.addAlarm(Messages.getString("ascentalarm"));
            }
            if ((warnings & 2) > 0 && !rbt_alarm) {
                dive.addAlarm(Messages.getString("rbtalarm"));
                rbt_alarm = true;
            }
            if ((warnings & 1) > 0 && !deco_stop) {
                dive.addAlarm(Messages.getString("decostop"));
                deco_stop = true;
            }
        }
    }

    /**
     * Convert the depth profile from MemoMouse format into JDiveLog format.
     * 
     * @param profile
     *            profile in MemoMouse format
     * @return profile in JDiveLog format
     */
    private Dive convertDepthProfile(DepthProfile profile) {
        Dive result = null;

        if (profile != null) {
            result = new Dive();
            resetDiveTime();
            result.setSurfaceinterval("");
            result.setDensity(new Double(0));
            result.setAltitude(new Double(0));
            result.addTime(getNextDiveTime());
            result.addDepth("0");

            List<DepthProfileEntry> profileEntries = profile
                    .getProfileEntries();

            for (int entryIndex = 0; entryIndex < profileEntries.size(); entryIndex++) {
                final DepthProfileEntry profileEntry = profileEntries
                        .get(entryIndex);

                addAlarms(result, profileEntry.warnings20);
                result.addDepth(profileEntry.depth20.toString());
                result.addTime(getNextDiveTime());

                addAlarms(result, profileEntry.warnings40);
                result.addDepth(profileEntry.depth40.toString());
                result.addTime(getNextDiveTime());

                addAlarms(result, profileEntry.warnings00);
                result.addDepth(profileEntry.depth00.toString());
                result.addTime(getNextDiveTime());
            }

            // set the last depth to 0 meters for the profile
            result.addDepth(new Double(0).toString());
            result.addTime(getNextDiveTime());

            result.setTimeDepthMode();
        }
        return result;
    }

    /**
     * Get the next dive time (20 seconds later than before).
     * 
     * @return the next dive time
     */
    private String getNextDiveTime() {
        String result = new Double(diveTime / 60.0).toString();

        diveTime += 20;
        return result;
    }

    /**
     * Reset the dive time to the starting point.
     */
    private void resetDiveTime() {
        diveTime = 0;
    }
}
