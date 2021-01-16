/*
 * Project: ADiveLog: A Dive Logbook written in Java
 * File: MapPanel.java
 * 
 * @author Alexander von Lünen <avl1@gmx.de>
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
package net.sf.jdivelog.gui;

import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.gui.statusbar.StatusInterface;
import net.sf.jdivelog.model.DiveSite;
import net.sf.jdivelog.model.JDiveLog;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.TreeSet;

import org.jmapviewer.helper.MapMarkerIcon;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.LayerGroup;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

/**
 * Displaying the dive sites on a map
 * 
 * @author Alexander von Lünen <avl1@gmx.de>
 *
 */
public class MapPanel extends JPanel implements JMapViewerEventListener {
   private static final long serialVersionUID = 1L;
   private MainWindow mainWindow;
   private StatusInterface status;
   private boolean initialized = false;
   private JDiveLog divelog = null;
   private JMapViewerTree treeMap;

   /**
    * Default Constructor for GUI Builder, do not use!
    */
   @Deprecated
   public MapPanel() {
      super();
      init();
   }

   private void init() {
      if (!initialized) {
         this.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
         this.setLayout(new BorderLayout());
      }
   }

   public MapPanel(MainWindow mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.status = mainWindow.getStatusBar();
      init();
      map();
   }

   private void map() {
      status.messageInfo(Messages.getString("creating_map"));
      //status.infiniteProgressbarStart();

      treeMap = new JMapViewerTree("Zones");

      JPanel helpPanel = new JPanel();
      add(helpPanel, BorderLayout.SOUTH);
      JLabel helpLabel = new JLabel("Use right mouse button to move,\n " + "left double click or mouse wheel to zoom.");
      helpPanel.add(helpLabel);

      treeMap.getViewer().setTileLoader(new OsmTileLoader(treeMap.getViewer()));
      treeMap.getViewer().setTileSource(new OsmTileSource.Mapnik());

      add(treeMap, BorderLayout.CENTER);
      addMarkers();

      // Listen to the map viewer for user operations so components will
      // receive events and update
      treeMap.getViewer().addJMVListener(this);
      setVisible(true);

      treeMap.getViewer().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
               treeMap.getViewer().getAttribution().handleAttribution(e.getPoint(), true);
            }
         }
      });

      treeMap.getViewer().addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();
            boolean cursorHand = treeMap.getViewer().getAttribution().handleAttributionCursor(p);
            if (cursorHand) {
               treeMap.getViewer().setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
               treeMap.getViewer().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
         }
      });
   }

   private void addMarkers() {
      divelog = this.mainWindow.getLogBook();
      LayerGroup sitesGroup = new LayerGroup("Dive Sites");
      Layer sitesLayer = sitesGroup.addLayer("Dive Sites");

      TreeSet<DiveSite> sites = divelog.getMasterdata().getDiveSites();
      status.countingProgressbarStart(sites.size(), true);
      try {
         Iterator<DiveSite> it = sites.iterator();
         while (it.hasNext()) {
            DiveSite site = it.next();
            if (site != null) {
               MapMarkerIcon dsite = new MapMarkerIcon(sitesLayer,
                     site.getSpot(),
                     Double.parseDouble(site.getLatitude()),
                     Double.parseDouble(site.getLongitude()),
                     treeMap.getViewer().getZoom());
               treeMap.getViewer().addMapMarker(dsite);
            }
            status.countingProgressbarIncrement();
         }
      } 
      finally {
         status.countingProgressbarEnd();
         status.messageClear();
         treeMap.addLayer(sitesLayer);
      }

   }

   private void updateZoomParameters() {
      java.util.List<MapMarker> ml = treeMap.getViewer().getMapMarkerList();
      
      Iterator<MapMarker> it = ml.iterator();
      while(it.hasNext()) {
         MapMarkerIcon mi = (MapMarkerIcon)it.next();
         mi.setZoomLevel(treeMap.getViewer().getZoom());
      }
   }
   
   @Override
   public void processCommand(JMVCommandEvent command) {
      if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)) {
        updateZoomParameters();
    }

   }
}
