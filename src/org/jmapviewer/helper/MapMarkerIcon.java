package org.jmapviewer.helper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.ImageIcon;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A simple implementation of the {@link MapMarker} interface. Each map marker
 * is painted as an icon. Using Nicolas Mollet's scuba diving icon downloaded
 * from WikiMedia Commons: 
 * https://commons.wikimedia.org/wiki/File:Map_marker_icon_%E2%80%93_Nicolas_Mollet_%E2%80%93_Scuba_Diving_%E2%80%93_Sports_%E2%80%93_Classic.png
 *
 * @author Alexander von Lünen
 * @since 16/08/2020
 *
 */
public class MapMarkerIcon extends MapObjectImpl implements MapMarker {

   private Coordinate coord;
   private Integer zoom_level;

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param coord  Coordinates of the map marker
    */
   public MapMarkerIcon(Coordinate coord) {
      this(null, null, coord, 0);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param name   Name of the map marker
    * @param coord  Coordinates of the map marker
    */
   public MapMarkerIcon(String name, Coordinate coord) {
      this(null, name, coord, 0);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param layer  Layer of the map marker
    * @param coord  Coordinates of the map marker
    */
   public MapMarkerIcon(Layer layer, Coordinate coord) {
      this(layer, null, coord, 0);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    */
   public MapMarkerIcon(double lat, double lon) {
      this(null, null, new Coordinate(lat, lon), 0);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param layer  Layer of the map marker
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    */
   public MapMarkerIcon(Layer layer, double lat, double lon) {
      this(layer, null, new Coordinate(lat, lon), 0);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param layer  Layer of the map marker
    * @param name   Name of the map marker
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    */
   public MapMarkerIcon(Layer layer, String name, double lat, double lon, int zooml) {
      this(layer, name, new Coordinate(lat, lon), zooml);
   }

   /**
    * Constructs a new {@code MapMarkerIcon}.
    * 
    * @param layer       Layer of the map marker
    * @param name        Name of the map marker
    * @param coord       Coordinates of the map marker
    */
   public MapMarkerIcon(Layer layer, String name, Coordinate coord, int lzoom) {
      super(layer, name, null);
      this.coord = coord;
      this.zoom_level = Integer.valueOf(lzoom);
   }

   @Override
   public Coordinate getCoordinate() {
      return coord;
   }

   @Override
   public double getLat() {
      return coord.getLat();
   }

   @Override
   public double getLon() {
      return coord.getLon();
   }

   public void setZoomLevel(int zooml) {
      this.zoom_level = zooml;
   }
   
   @Override
   public void paint(Graphics g, Point position, int radius) {
      String filename = "/net/sf/jdivelog/gui/resources/icons_32x32/Map_marker_icon_–_Nicolas_Mollet_–_Scuba_Diving_–_Sports_–_Dark.png";
      java.net.URL imgURL = getClass().getResource(filename);
      //System.out.println(this.zoom_level);
      
      ImageIcon x = new ImageIcon(imgURL, this.getName());
      x.paintIcon(null, g, position.x-16, position.y-37);
      
      if ((getLayer() == null || getLayer().isVisibleTexts()) && this.zoom_level > 10) paintText(g, position);
   }

   public static Style getDefaultStyle() {
      return new Style(Color.ORANGE, new Color(200, 200, 200, 200), null, getDefaultFont());
   }

   @Override
   public String toString() {
      return "MapMarker at " + getLat() + ' ' + getLon();
   }

   @Override
   public void setLat(double lat) {
      if (coord == null)
         coord = new Coordinate(lat, 0);
      else
         coord.setLat(lat);
   }

   @Override
   public void setLon(double lon) {
      if (coord == null)
         coord = new Coordinate(0, lon);
      else
         coord.setLon(lon);
   }

   @Override
   public double getRadius() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public STYLE getMarkerStyle() {
      // TODO Auto-generated method stub
      return null;
   }
}