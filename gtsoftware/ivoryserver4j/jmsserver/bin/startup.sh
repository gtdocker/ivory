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
# path to the optional j2ee.jar. JMS classes contained in j2ee.jar
# are usually supplied in provider libraries. If not uncomment the
# the following line and delete the line after.
# J2EE_JAR="$LIB_HOME"/j2ee.jar
J2EE_JAR=
#
# path to the conf directory containing IvoryJmsServer configuration files
#
CONFIG_HOME="$INSTALL_HOME"/conf
#
# path to the IvoryJmsServer configuration file
#
CONFIG_FILE="$CONFIG_HOME"/jmsserver.xml
#
# classpath to the Java Messaging Service Provider libraries
#
PROVIDER_LIB=
#
# (Optional) location of the Java keystore file containing the collection of CA certificates trusted by this application process (trust store)
#
TRUST_STORE=
#
"$JAVA_HOME"/bin/java -classpath "$PROVIDER_LIB":"$LIB_HOME"/log4j-1.2.16.jar:"$LIB_HOME"/ivoryjmsserver.jar:"$J2EE_JAR" -Djavax.net.ssl.trustStore="$TRUST_STORE" gtsoft.ivory.jmsserver.IvoryJmsServer -start -cf "$CONFIG_FILE" &

