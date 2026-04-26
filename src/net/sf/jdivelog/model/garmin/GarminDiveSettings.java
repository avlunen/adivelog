package net.sf.jdivelog.model.garmin;

/**
 * Class to hold the dive setting information found in a Garmin FIT file
 * 
 * @author Alexander von L&uuml;nen
 * @version 0.5
 * @since 26 Apr 2026
 */
public class GarminDiveSettings {
   /**
    * Water density in kg/m³
    */
   private Float m_water_density = 1000.0f;

   public Float getM_water_density() {
      return m_water_density;
   }

   public void setM_water_density(Float m_water_density) {
      this.m_water_density = m_water_density;
   }
}
