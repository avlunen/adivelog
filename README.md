# ADivelog
This is a customized version of JDivelog (https://sourceforge.net/projects/jdivelog/). Development of JDivelog seems to have ceased, and I found it had some bugs and could do with a feature update.

If you just want to run the app, download 'adivelog.jar' to your computer and double-click on it (JRE needs to be installed on your computer). All dependencies are packaged into the JAR file.

This is in an early stage, and I am not sure how much time I will have to work on this. On the other hand, the main motivation to create this spin-off was that I wanted a dive log app that is less over-blown than others, such as Subsurface, but something nice, small and simple.

In this initial release (call it an alpha version), I have fixed some bugs of JDivelog. I also removed the dive planner/deco module from it; personally, I find "hobby" deco planners questionable, and there are many others out there. Instead, I have replaced it with a mapping feature (although the code is still here, but will be removed in the future); which means you can now create a map with your dive sites, provided you have added coordinates to your list of dive sites.

The software dependencies are as follows (none of these are included in this code repository):

JFreeChart (https://www.jfree.org/)
1) jfreechart-1.0.19.jar
2) jfreechart-1.0.19-swt.jar
3) jcommon-1.0.23.jar

Apache (Commons and XML Graphics Project):
1) commons-beanutils-1.9.3.jar
2) commons-digester-2.1.jar
3) xmlgraphics-commons-2.2.jar
4) commons-lang-2.5.jar
5) commons-io-2.5.jar
6) commons-logging-1.1.1.jar
7) commons-logging-adapters-1.1.1.jar
8) commons-logging-api-1.1.1.jar
9) batik-all-1.7.jar
10) fop.jar

Pixelitor (https://pixelitor.sourceforge.io/):
1) pixelitor_4.0.2.jar

Comm (http://www.java2s.com/Code/Jar/c/Downloadcomm20jar.htm):
1) comm.jar

Bluecove (http://www.java2s.com/Code/Jar/b/bluecove.htm)
1) bluecove-2.1.0.jar

JMapViewer (https://wiki.openstreetmap.org/wiki/JMapViewer):
1) JMapView.jar (2.5)

ANT LaTeX (https://sourceforge.net/projects/antlatex.berlios/)
1) ant_latex_0.0.9_1.jar