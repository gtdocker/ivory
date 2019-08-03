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
REM path to the conf directory containing IvoryJmsServer configuration files
REM
SET CONFIG_HOME=%INSTALL_HOME%\conf

REM
REM path to the IvoryJmsServer configuration file
REM
SET CONFIG_FILE=%CONFIG_HOME%\jmsserver.xml

%JAVA_HOME%\bin\java.exe -classpath %LIB_HOME%\log4j-1.2.16.jar;%LIB_HOME%\ivoryjmsserver.jar gtsoft.ivory.jmsserver.IvoryJmsServer -stop -cf %CONFIG_FILE%
