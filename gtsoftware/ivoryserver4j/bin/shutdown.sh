#!/bin/bash
#
# path to a Java 6.0 or higher JDK or JRE installation directory
#
JAVA_HOME=
#
# path to the IvoryJmsServer installation directory, modify if need be
#
INSTALL_HOME=/opt/gtsoftware/ivoryserver4j/version44/jmsserver
#
# path to the lib directory containing IvoryJmsServer jar files
#
LIB_HOME="$INSTALL_HOME"/lib
#
# path to the conf directory containing IvoryJmsServer configuration files
#
CONFIG_HOME="$INSTALL_HOME"/conf
#
# path to the IvoryJmsServer configuration file
#
CONFIG_FILE="$CONFIG_HOME"/jmsserver.xml
#
"$JAVA_HOME"/bin/java -classpath "$LIB_HOME"/log4j-1.2.16.jar:"$LIB_HOME"/ivoryjmsserver.jar gtsoft.ivory.jmsserver.IvoryJmsServer -stop -cf "$CONFIG_FILE"

