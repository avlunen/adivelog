package net.sf.jdivelog.model.garmin;

import java.time.Instant;

/**
 * Class for holding a single data point in the diveprofile
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 */
public class depthProfileEntry {
   private Instant m_timestamp;
   private Long m_pressure;
   private Double m_altitude;
   private Float m_depth;
   private Byte m_temperature;
   
   
   public Instant getM_timestamp() {
      return m_timestamp;
   }
   public void setM_timestamp(Instant m_timestamp) {
      this.m_timestamp = m_timestamp;
   }
   public Long getM_pressure() {
      return m_pressure;
   }
   public void setM_pressure(Long m_pressure) {
      this.m_pressure = m_pressure;
   }
   public Double getM_altitude() {
      return m_altitude;
   }
   public void setM_altitude(Double m_altitude) {
      this.m_altitude = m_altitude;
   }
   public Float getM_depth() {
      return m_depth;
   }
   public void setM_depth(Float m_depth) {
      this.m_depth = m_depth;
   }
   public Byte getM_temperature() {
      return m_temperature;
   }
   public void setM_temperature(Byte m_temperature) {
      this.m_temperature = m_temperature;
   }

}
