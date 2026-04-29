package net.sf.jdivelog.model.garmin;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

import com.garmin.fit.DiveAlert;

import net.sf.jdivelog.gui.resources.Messages;
//import net.sf.jdivelog.model.Equipment;
import net.sf.jdivelog.model.JDive;
//import net.sf.jdivelog.model.Tank;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;

/**
 * Class to convert a list of Garmin FIT files into JDive objects
 * 
 * @author Alexander von L&uuml;nen
 * @version 0.5
 * @since 25 Apr 2026
 */
public class GarminFitAdapter extends TreeSet<JDive> {
   private static final long serialVersionUID = 7074146879793307452L;

   /**
    * Constructor
    * 
    * @param dpe   profile with all data points from a dive
    * @param index number of last dive
    */
   public GarminFitAdapter(List<DepthProfileEntries> dpel, Long index) {
      if (dpel == null) {
         throw new IllegalArgumentException("DepthProfile is null");
      }

      for (DepthProfileEntries dpe : dpel) {
         JDive dive = new JDive();
         Gas gas = new Gas(); // TODO check gas and add
         //Tank tank = new Tank();
         //Equipment equipment = new Equipment();

         Dive depthProfile = convertDepthProfile(dpe, gas);
         depthProfile.addGas(gas);
         depthProfile.setDate(dpe.getDate());

         dive.setDate(dpe.getDate());
         dive.setDepth(Math.floor(dpe.maxDepth() * 100) / 100);
         dive.setDiveNumber(index + 1L);
         dive.setDuration(dpe.duration());

         dive.setAverageDepth(dpe.avgDepth());
         // result.setSurfaceTemperature(profileEntries.getSurfaceTemperature());
         dive.setTemperature(dpe.minTemperature());
         dive.setDive(depthProfile);

         add(dive);
      }
   }

   /**
    * Method convert the depth profile into a Dive object
    * 
    * @param profileEntries
    * @param startingGas
    * @return Dive
    */
   private Dive convertDepthProfile(DepthProfileEntries profileEntries, Gas startingGas) {
      Dive result = new Dive();
      long start = profileEntries.startPoint()*1000l;
      
      result.setSurfaceinterval("");
      result.setDensity(profileEntries.getM_water_density());
      result.setAltitude(0d); // BUBU
      if (!Messages.getString("default_mixname").equals(startingGas.getName())) {  //$NON-NLS-1$
         result.addSwitch(startingGas.getName());
      }
      result.addDepth("0");
      result.setTemperature("0");
      result.addTime("0");

      for(depthProfileEntry de : profileEntries.getDepthProfileEntries()) {
         result.addDepth(de.getM_depth().toString());
         result.addTemperature(de.getM_temperature().toString());
         //addAlarms(result, profileEntries.getM_alerts(), de.getM_timestamp()); // TODO add alerts
         
         // compute time point
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
         calendar.setTimeInMillis(de.getM_timestamp().toEpochMilli()-start);
         
         String tim = convertTime(calendar.getTimeInMillis()/1000);
         result.addTime(tim);
         result.addCns(de.getM_cns_load().doubleValue());
         result.addPPO2("pO2", de.getM_po2().toString()); // BUBU
         result.addDecoInfo(de.getM_depth().doubleValue(), Double.valueOf(de.getM_tts()), Double.valueOf(de.getM_ndl()));
      }
      
      result.setTimeDepthMode();

      return result;
   }

   /**
    * Convert a time from timestamp format (seconds) into JDiveLog format
    * (minutes).
    * 
    * @param seconds time in seconds
    * @return time in minutes
    */
   private String convertTime(long seconds) {
      return Double.valueOf(seconds / 60d).toString();
   }

   /**
    * Add alarms to a dive.
    * 
    * @param dive   current dive
    * @param alarms list of alarms to add
    */
   private void addAlarms(Dive dive, Map<Instant, Integer> alarms, Instant in) {
      if ((dive != null) && (alarms != null) && (in != null)) {
         // for (Map.Entry<Instant, Integer> alarm : alarms.entrySet()) {
         String messageString;
         Integer alarm = alarms.get(in);
         if (alarm == null) {
            dive.addAlarm(null);
            return;
         }
         DiveAlert da = DiveAlert.getByValue(alarm.shortValue());

         if (da.getValue() == DiveAlert.NDL_REACHED.getValue())
            messageString = "garmin.alert.ndl_reached";
         else if (da.getValue() == DiveAlert.GAS_SWITCH_PROMPTED.getValue())
            messageString = "garmin.alert.gas_switch_prompted";
         else if (da.getValue() == DiveAlert.NEAR_SURFACE.getValue())
            messageString = "garmin.alert.near_surface";
         else if (da.getValue() == DiveAlert.APPROACHING_NDL.getValue())
            messageString = "garmin.alert.approaching_ndl";
         else if (da.getValue() == DiveAlert.PO2_WARN.getValue())
            messageString = "garmin.alert.po2_warn";
         else if (da.getValue() == DiveAlert.PO2_CRIT_HIGH.getValue())
            messageString = "garmin.alert.po2_crit_high";
         else if (da.getValue() == DiveAlert.PO2_CRIT_LOW.getValue())
            messageString = "garmin.alert.po2_crit_low";
         else if (da.getValue() == DiveAlert.TIME_ALERT.getValue())
            messageString = "garmin.alert.time_alert";
         else if (da.getValue() == DiveAlert.DEPTH_ALERT.getValue())
            messageString = "garmin.alert.depth_alert";
         else if (da.getValue() == DiveAlert.DECO_CEILING_BROKEN.getValue())
            messageString = "garmin.alert.deco_ceiling_broken";
         else if (da.getValue() == DiveAlert.DECO_COMPLETE.getValue())
            messageString = "garmin.alert.deco_complete";
         else if (da.getValue() == DiveAlert.SAFETY_STOP_BROKEN.getValue())
            messageString = "garmin.alert.safety_stop_broken";
         else if (da.getValue() == DiveAlert.SAFETY_STOP_COMPLETE.getValue())
            messageString = "garmin.alert.safety_stop_complete";
         else if (da.getValue() == DiveAlert.CNS_WARNING.getValue())
            messageString = "garmin.alert.cns_warning";
         else if (da.getValue() == DiveAlert.CNS_CRITICAL.getValue())
            messageString = "garmin.alert.cns_critical";
         else if (da.getValue() == DiveAlert.OTU_WARNING.getValue())
            messageString = "garmin.alert.otu_warning";
         else if (da.getValue() == DiveAlert.OTU_CRITICAL.getValue())
            messageString = "garmin.alert.otu_critical";
         else if (da.getValue() == DiveAlert.ASCENT_CRITICAL.getValue())
            messageString = "garmin.alert.ascent_critical";
         else if (da.getValue() == DiveAlert.ALERT_DISMISSED_BY_KEY.getValue())
            messageString = "garmin.alert.alert_dismissed_by_key";
         else if (da.getValue() == DiveAlert.ALERT_DISMISSED_BY_TIMEOUT.getValue())
            messageString = "garmin.alert.alert_dismissed_by_timeout";
         else if (da.getValue() == DiveAlert.BATTERY_LOW.getValue())
            messageString = "garmin.alert.battery_low";
         else if (da.getValue() == DiveAlert.BATTERY_CRITICAL.getValue())
            messageString = "garmin.alert.battery_critical";
         else if (da.getValue() == DiveAlert.SAFETY_STOP_STARTED.getValue())
            messageString = "garmin.alert.safety_stop_started";
         else if (da.getValue() == DiveAlert.APPROACHING_FIRST_DECO_STOP.getValue())
            messageString = "garmin.alert.approaching_first_deco_stop";
         else if (da.getValue() == DiveAlert.SETPOINT_SWITCH_AUTO_LOW.getValue())
            messageString = "garmin.alert.setpoint_switch_auto_low";
         else if (da.getValue() == DiveAlert.SETPOINT_SWITCH_AUTO_HIGH.getValue())
            messageString = "garmin.alert.setpoint_switch_auto_high";
         else if (da.getValue() == DiveAlert.SETPOINT_SWITCH_MANUAL_LOW.getValue())
            messageString = "garmin.alert.setpoint_switch_manual_low";
         else if (da.getValue() == DiveAlert.SETPOINT_SWITCH_MANUAL_HIGH.getValue())
            messageString = "garmin.alert.setpoint_switch_manual_high";
         else if (da.getValue() == DiveAlert.AUTO_SETPOINT_SWITCH_IGNORED.getValue())
            messageString = "garmin.alert.auto_setpoint_switch_ignored";
         else if (da.getValue() == DiveAlert.SWITCHED_TO_OPEN_CIRCUIT.getValue())
            messageString = "garmin.alert.switched_to_open_circuit";
         else if (da.getValue() == DiveAlert.SWITCHED_TO_CLOSED_CIRCUIT.getValue())
            messageString = "garmin.alert.switched_to_closed_circuit";
         else if (da.getValue() == DiveAlert.TANK_BATTERY_LOW.getValue())
            messageString = "garmin.alert.tank_battery_low";
         else if (da.getValue() == DiveAlert.PO2_CCR_DIL_LOW.getValue())
            messageString = "garmin.alert.po2_ccr_dil_low";
         else if (da.getValue() == DiveAlert.DECO_STOP_CLEARED.getValue())
            messageString = "garmin.alert.deco_stop_cleared";
         else if (da.getValue() == DiveAlert.APNEA_NEUTRAL_BUOYANCY.getValue())
            messageString = "garmin.alert.apnea_neutral_buoyancy";
         else if (da.getValue() == DiveAlert.APNEA_TARGET_DEPTH.getValue())
            messageString = "garmin.alert.apnea_target_depth";
         else if (da.getValue() == DiveAlert.APNEA_SURFACE.getValue())
            messageString = "garmin.alert.apnea_surface";
         else if (da.getValue() == DiveAlert.APNEA_HIGH_SPEED.getValue())
            messageString = "garmin.alert.apnea_high_speed";
         else if (da.getValue() == DiveAlert.APNEA_LOW_SPEED.getValue())
            messageString = "garmin.alert.apnea_low_speed";
         else if (da.getValue() == DiveAlert.INVALID.getValue())
            messageString = "garmin.alert.invalid";
         else
            messageString = "";

         dive.addAlarm(Messages.getString(messageString));
         // }
      }
   }
}
