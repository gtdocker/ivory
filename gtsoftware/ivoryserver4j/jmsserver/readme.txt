              Ivory JMS Server readme.txt


Ivory JMS Server sits between a Messaging Services provider and an 
Ivory Server. It allows clients to write to queues and read from 
queues. Ivory JMS Server does the listening and passes Web service 
requests and responses between the queues and the Ivory Server via 
HTTP/HTTPS. Please consult "Ivory JMS Server User's Guide" for a
complete description. The User's Guide includes a reference for the
server configuration file jmsserver.xml.

You will need to edit the startup and shutdown scripts in the bin
directory. The Windows scripts are named startup.bat and shutdown.bat.
The Linux and zLinux scripts are named startup.sh and shutdown.sh.
Edit these scripts to change install location specific information.
Also required is a Java 6.0 JDK or JRE. The location of the JDK or
JRE is specified in these scripts via variable JAVA_HOME.
Provider libraries (or .jar files) are specified via the variable
PROVIDER_LIB.


