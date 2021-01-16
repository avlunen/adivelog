package org.jmapviewer.helper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A simple implementation of the {@link MapMarker} interface. Each map marker
 * is painted as a star without border.
 *
 * @author Jan Peter Stotz
 * 
 * @author Alexander von Lünen
 * @since 2020
 *
 */
public class MapMarkerStar extends MapObjectImpl implements MapMarker {

   private Coordinate coord;
   //private double radius;
   private STYLE markerStyle;

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param coord  Coordinates of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(Coordinate coord) {
      this(null, null, coord);
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param name   Name of the map marker
    * @param coord  Coordinates of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(String name, Coordinate coord) {
      this(null, name, coord);
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param layer  Layer of the map marker
    * @param coord  Coordinates of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(Layer layer, Coordinate coord) {
      this(layer, null, coord);
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(double lat, double lon) {
      this(null, null, new Coordinate(lat, lon));
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param layer  Layer of the map marker
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(Layer layer, double lat, double lon) {
      this(layer, null, new Coordinate(lat, lon));
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param layer  Layer of the map marker
    * @param lat    Latitude of the map marker
    * @param lon    Longitude of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(Layer layer, String name, double lat, double lon) {
      this(layer, name, new Coordinate(lat, lon));
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param layer  Layer of the map marker
    * @param name   Name of the map marker
    * @param coord  Coordinates of the map marker
    * @param radius Radius of the map marker position
    */
   public MapMarkerStar(Layer layer, String name, Coordinate coord) {
      this(layer, name, coord, STYLE.VARIABLE, getDefaultStyle());
   }

   /**
    * Constructs a new {@code MapMarkerStar}.
    * 
    * @param layer       Layer of the map marker
    * @param name        Name of the map marker
    * @param coord       Coordinates of the map marker
    * @param radius      Radius of the map marker position
    * @param markerStyle Marker style (fixed or variable)
    * @param style       Graphical style
    */
   public MapMarkerStar(Layer layer, String name, Coordinate coord, STYLE markerStyle, Style style) {
      super(layer, name, style);
      this.markerStyle = markerStyle;
      this.coord = coord;
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

   @Override
   public STYLE getMarkerStyle() {
      return markerStyle;
   }

   @Override
   public void paint(Graphics g, Point position, int radius) {
      int xPoints[] = { 9, 15, 0, 18, 3 };
      int yPoints[] = { 0, 18, 6, 6, 18 };

      Graphics2D g2d = (Graphics2D) g;
      GeneralPath star = new GeneralPath();

      star.moveTo(xPoints[0] + position.x, yPoints[0] + position.y);
      for (int i = 1; i < xPoints.length; i++) {
         star.lineTo(xPoints[i] + position.x, yPoints[i] + position.y);
      }
      star.closePath();

      g2d.setColor(Color.YELLOW);
      g2d.fill(star);

      if (getLayer() == null || getLayer().isVisibleTexts())
         paintText(g, position);
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
}