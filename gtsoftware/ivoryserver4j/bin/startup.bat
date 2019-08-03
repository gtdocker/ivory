@echo off
REM
REM path to a Java 6.0 or higher JDK or JRE installation directory
REM
SET JAVA_HOME=

REM
REM path to the IvoryJmsServer installation directory, modify if need be
REM
SET INSTALL_HOME="C:\Program Files (x86)\GT_Software\Ivory_Server\version44\jmsserver"

REM
REM path to the lib directory containing IvoryJmsServer jar files
REM
SET LIB_HOME=%INSTALL_HOME%\lib

REM
REM path to the optional j2ee.jar. JMS classes contained in j2ee.jar
REM are usually supplied in provider libraries. If not uncomment the
REM the following line and delete the line after.
REM SET J2EE_JAR=%LIB_HOME%\j2ee.jar
SET J2EE_JAR=

REM
REM path to the conf directory containing IvoryJmsServer configuration files
REM
SET CONFIG_HOME=%INSTALL_HOME%\conf

REM
REM path to the IvoryJmsServer configuration file
REM
SET CONFIG_FILE=%CONFIG_HOME%\jmsserver.xml

REM
REM classpath to the Java Messaging Service Provider libraries
REM
SET PROVIDER_LIB=

REM
REM (Optional) location of the Java keystore file containing the collection of CA certificates trusted by this application process (trust store)
REM
SET TRUST_STORE=

%JAVA_HOME%\bin\java.exe -classpath %PROVIDER_LIB%;%LIB_HOME%\log4j-1.2.16.jar;%LIB_HOME%\ivoryjmsserver.jar;%J2EE_JAR% -Djavax.net.ssl.trustStore=%TRUST_STORE% gtsoft.ivory.jmsserver.IvoryJmsServer -start -cf %CONFIG_FILE%
