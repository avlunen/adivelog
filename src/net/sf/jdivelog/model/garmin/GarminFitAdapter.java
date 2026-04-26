package net.sf.jdivelog.model.garmin;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.Equipment;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.Tank;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;

/**
 * Class to convert a list of Garmin FIT files into JDive objects
 * 
 * @author Alexander von L&uuml;nen
 * @version 0.5
 * @since 25 Apr 2026
 */
public class GarminFitAdapter extends TreeSet<JDive>  {
   private static final long serialVersionUID = 7074146879793307452L;

   /**
    * Constructor
    * @param dpe profile with all data points from a dive
    * @param index number of last dive
    */
   public GarminFitAdapter(List<DepthProfileEntries> dpel, Long index) {
      if (dpel == null) {
         throw new IllegalArgumentException("DepthProfile is null");
      }
      
      for(DepthProfileEntries dpe : dpel) {
         JDive dive = new JDive();
         Gas gas = new Gas(); // TODO check gas and add
         Tank tank = new Tank();
         Equipment equipment = new Equipment();
         
         Dive depthProfile = convertDepthProfile(dpe, gas);
         depthProfile.addGas(gas);
         depthProfile.setDate(dpe.getDate());
         
         dive.setDate(dpe.getDate());
         dive.setDepth(dpe.maxDepth());
         dive.setDiveNumber(index + 1L);
         dive.setDuration(dpe.duration());

         dive.setAverageDepth(dpe.avgDepth());
         //result.setSurfaceTemperature(profileEntries.getSurfaceTemperature());
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
      
      result.setSurfaceinterval("");
      result.setDensity(0d);
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
         //addAlarms(result, profileEntry.alarms); // TODO check alarms and add
         
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
         calendar.setTimeInMillis(de.getM_timestamp().toEpochMilli());
         
         result.addTime(convertTime(calendar.getTimeInMillis()/1000));

      }
      
      result.setTimeDepthMode();

      return result;
   }
   
   /**
    * Convert a time from timestamp format (seconds) into JDiveLog format
    * (minutes).
    * 
    * @param seconds
    *            time in seconds
    * @return time in minutes
    */
   private String convertTime(long seconds) {
      return Double.valueOf(seconds / 60d).toString();
   }
}
