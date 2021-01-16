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

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.suuntong.AlarmType;
import net.sf.jdivelog.model.suuntong.DepthProfileEntry;
import net.sf.jdivelog.model.suuntong.LogBook;
import net.sf.jdivelog.model.suuntong.LogEntry;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;
import net.sf.jdivelog.model.udcf.Sample;
import net.sf.jdivelog.model.udcf.Temperature;

/**
 * Description: adapts the Suunto D6 data structure to JDiveLog format
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.9 $
 */
public class SuuntoNGAdapter extends TreeSet<JDive> {
	private static final long serialVersionUID = -512047413310641281L;

	/**
	 * Create a new adapter object.
	 * 
	 * @param logBook
	 *            log book containing all dives from dive computer
	 */
	public SuuntoNGAdapter(LogBook logBook) {
		if (logBook == null) {
			throw new IllegalArgumentException("parameter logBook is null");
		}

		List<LogEntry> logEntries = logBook.getLogEntries();

		for (int index = 0; index < logEntries.size(); index++) {
			LogEntry logEntry = logEntries.get(index);
			JDive dive = new JDive();

			dive.setDate(logEntry.date);
			dive.setDepth((double) logEntry.maxDepth / 100);
			dive.setDiveNumber(index + 1L);
			dive.setDuration((double) logEntry.diveTime);

			Gas gas = getGas(logEntry, 0);
			Tank tank = new Tank();
			Equipment equipment = new Equipment();

			tank.setGas(gas);
			tank.setType(Messages.getString("tanktype_aluminium"));
			equipment.addTank(tank);
			dive.setEquipment(equipment);

			Dive depthProfile = convertDepthProfile(logEntry.getProfile(), gas);

			depthProfile.addGas(gas);
			depthProfile.setDate(logEntry.date);

			dive.setAverageDepth(depthProfile.getAverageDepth());
			dive.setSurfaceTemperature(depthProfile.getSurfaceTemperature());
			dive.setTemperature(depthProfile.getTemperature());
			dive.setDive(depthProfile);

			add(dive);
		}
	}

	/**
	 * Add alarms to a dive.
	 * 
	 * @param dive
	 *            current dive
	 * @param alarms
	 *            list of alarms to add
	 */
	private void addAlarms(Dive dive, Collection<AlarmType> alarms) {
		if ((dive != null) && (alarms != null)) {
			for (AlarmType alarm : alarms) {
				String messageString;
				switch (alarm) {
				case MANDATORY_SAFETY_STOP_CEILING_ERROR_BEGIN:
					messageString = "decoceilingalarm";
					break;
				case ASC_WARNING_BEGIN:
					messageString = "ascentalarm";
					break;
				case MANDATORY_SAFETY_STOP_BEGIN:
					messageString = "decostop";
					break;
				case AIR_TIME_WARNING_BEGIN:
					messageString = "";
					break;
				case BELOW_FLOOR_WARNING_BEGIN:
					messageString = "belowflooralarm";
					break;
				case DECO_WARNING_BEGIN:
					messageString = "decoalarm";
					break;
				case DEPTH_ALARM_BEGIN:
					messageString = "depthalarm";
					break;
				case DIVE_TIME_ALARM_BEGIN:
					messageString = "divetimealarm";
					break;
				case HIGH_PP02_WARNING_BEGIN:
					messageString = "highppo2alarm";
					break;
				case RGBM_WARNING_BEGIN:
					messageString = "rgbmalarm";
					break;
				default:
					continue;
				}
				dive.addAlarm(Messages.getString(messageString));
			}
		}
	}

	/**
	 * Convert a depth profile list into a Dive object.
	 * 
	 * @param profileEntries
	 *            depth profile list
	 * @return Dive object
	 */
	private Dive convertDepthProfile(List<DepthProfileEntry> profileEntries, Gas startingGas) {
		Dive result = new Dive();

		result.setSurfaceinterval("");
		result.setDensity(0d);
		result.setAltitude(0d);
		if (!Messages.getString("default_mixname").equals(startingGas.getName())) {  //$NON-NLS-1$
			result.addSwitch(startingGas.getName());
		}
		result.addDepth("0");
		result.setTemperature("0");
		result.addTime("0");

		for (int entryIndex = 0; entryIndex < profileEntries.size(); entryIndex++) {
			final DepthProfileEntry profileEntry = profileEntries
					.get(entryIndex);

			result.addDepth(Double.valueOf(profileEntry.depth / 100d)
					.toString());
			if (profileEntry.temperature != null) {
				result.addTemperature(profileEntry.temperature.toString());
			}
			addAlarms(result, profileEntry.alarms);
			result.addTime(convertTime(profileEntry.time));
		}

		// set the last depth to 0 meters for the profile
		result.addDepth("0");
		if (profileEntries.get(profileEntries.size() - 1).temperature != null) {
			result.addTemperature(profileEntries.get(profileEntries.size() - 1).temperature
					.toString());
		}
		result.addTime(convertTime(profileEntries.get(profileEntries.size() - 1).time + 4));
		result.setSurfaceTemperature(calculateSurfaceTemparature(result));
		result.setTemperature(calculateBottomTemparature(result));
		result.setTimeDepthMode();
		return result;
	}

	private Double calculateBottomTemparature(Dive result) {
		Double bottomTemp = null;
		for (Sample sample : result.getSamples()) {
			if (sample instanceof Temperature) {
				Temperature temp = (Temperature) sample;
				Double value = (Double) temp.getValue();
				if (bottomTemp == null || value.compareTo(bottomTemp) < 0) {
					bottomTemp = value;
				}
			}
		}
		return bottomTemp;
	}

	private Double calculateSurfaceTemparature(Dive result) {
		Double surfaceTemp = null;
		for (Sample sample : result.getSamples()) {
			if (sample instanceof Temperature) {
				Temperature temp = (Temperature) sample;
				Double value = (Double) temp.getValue();
				if (surfaceTemp == null || value.compareTo(surfaceTemp) > 0) {
					surfaceTemp = value;
				}
			}
		}
		return surfaceTemp;
	}

	/**
	 * Convert a time from Suunto format (seconds) into JDiveLog format
	 * (minutes).
	 * 
	 * @param seconds
	 *            time in seconds
	 * @return time in minutes
	 */
	private String convertTime(int seconds) {
		return Double.valueOf(seconds / 60d).toString();
	}

	/**
	 * Create a Gas object from the given LogEntry and air consumption information.
	 * 
	 * @param logEntry 
	 * 			  LogEntry from dive computer
	 * @param airConsumption
	 *            air consumption from dive computer
	 * 
	 * @return Gas object
	 */
	private Gas getGas(LogEntry logEntry, int airConsumption) {
		Gas result = new Gas();

		String nameKey;
		switch (logEntry.gasType) {
		case AIR:
			nameKey = "suuntoConfigurationPanel.model_air";
			break;
		case NITROX:
			nameKey = "suuntoConfigurationPanel.model_nitrox";
			break;
		case GAUGE:
			nameKey = "suuntoConfigurationPanel.model_gauge";
			break;
		default:
			nameKey = "default_mixname";
		}

		result.setName(Messages.getString(nameKey));
		if (logEntry.o2percent1 > 0) {
			result.setMix(new Mix(logEntry.o2percent1, 0));
		} else {
			result.setMix(new Mix(21, 0));
		}
		result.setPstart(Double.valueOf(Messages.getString("standard_pressure")));
		result.setPend(result.getPstart() - airConsumption);
		result.setTankvolume(0.012D);
		return result;
	}
}
