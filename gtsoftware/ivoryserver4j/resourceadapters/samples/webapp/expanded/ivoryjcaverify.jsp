<%@ page import= "java.io.IOException,javax.resource.cci.*,gtsoft.ivory.server.jca.cci.IvoryInteractionSpec,gtsoft.ivory.server.jca.cci.IvoryStringRecord,gtsoft.ivory.server.jca.cci.IvoryLocalConnectionSpec" %>
<%

        ConnectionFactory cf    = null;
        StringBuffer outBuff    = new StringBuffer("<html><body><h1>Ivory Server Resource Adapter Installation Verification Results</h1>");
        String jndiName         = request.getParameter("IvoryConnectionFactoryName");
        
        do
        {
            // Make sure a <context-param> was supplied for IvoryConnectionFactoryName
            if ((jndiName == null) || (jndiName.length() == 0))
            {
                outBuff.append("<p>Mandatory query parameter IvoryConnectionFactoryName was not supplied. ");
                outBuff.append("<p>Format is: <h3> jcaverify.jsp?IvoryConnectionFactoryName=jndiname</h3></p></body></html>");
                break;
            }else
            		outBuff.append("<h2>IvoryConnectionFactoryName name input: " + jndiName + "</h2>");

            // Get a ConnectionFactory object
            try
            {
                javax.naming.Context ic = new javax.naming.InitialContext();
                cf = (ConnectionFactory) ic.lookup(jndiName);
            }

            // The lookup did not work, cannot find a ConnectionFactory object with the
            // supplied jndi name
            catch (Exception e)
            {
                outBuff.append("<h4>Context.lookup() failed, cannot allocate ConnectionFactory with name " + jndiName + ". Exception occurred: <p> " + e.getMessage() + "</p></h4></body></html>");
                break;
            }

            // An exception did not occur but make sure we actually did get a
            // ConnectionFactory
            if (cf == null)
            {
                outBuff.append("<h4>Could not get connection factory</h4></body></html>");
                break;
            }

            // We have a ConnectionFactory, from it get a Connection object
            Connection ivConnection;    
            try
            {
                // Create a local connection spec since the installation
                // verification is going to be performed locally
                IvoryLocalConnectionSpec cSpec = new IvoryLocalConnectionSpec();
                ivConnection =  (Connection)cf.getConnection(cSpec);
            }

            // Unable to create a Connection
            catch (Exception e)
            {
                outBuff.append("<h4>Unable to create a Connection. Exception occurred:<p> " + e.getMessage() + "</p></h4></body></html>");
                break;
            }

            // We have a Connection, from it get an Interaction object
            Interaction ivInteraction;
            try
            {
                ivInteraction = (Interaction)ivConnection.createInteraction();
            }

            // Unable to create an Interaction
            catch (Exception e)
            {
                outBuff.append("<h4>Unable to create an Interaction. Exception occurred:<p> " + e.getMessage() + "</p></h4></body></html>");
                break;
            }

            // Create an IvoryInteractionSpec and most importantly set the endpoint
            // to ivp.srv. This will tell the JCA adapter to perform an installation
            // verification
            IvoryInteractionSpec ivInteractionSpec = new IvoryInteractionSpec();
            ivInteractionSpec.setRequestID("InstallationVerificationServlet");
            ivInteractionSpec.setEndpoint("ivp.srv");
            ivInteractionSpec.setFunctionName("InstallationVerification");

            // IvoryStringRecord is a generalized implementation of Record to hold
            // a String request and/or String response. Since installation verification
            // does not require any real input, reuse the same IvoryStringRecord for
            // both input and output
            IvoryStringRecord ivRecord = new IvoryStringRecord();
            try
            {
                // Perform the installation verification
                ivInteraction.execute(ivInteractionSpec,ivRecord,ivRecord);
            }

            // Something went wrong during the execution
            catch (Exception e)
            {
                outBuff.append("<h4>Error during execution of Ivory Server resource adapter. Exception occurred: <h4><p>" + e.getMessage() + "</p></body></html>");
                break;
            }

            // Installation verification will return an HTML document. It is stored 
            // inside the output Record. Get the response and close both the
            // Interaction and the Connection.            
            try
            {
            	outBuff.setLength(0);
                outBuff.append(ivRecord.getString());
                ivInteraction.close();
                ivConnection.close();
            }

            // Something went wrong
            catch (Exception e)
            {
                outBuff.append("<h4>Error while attempting to close either the Interaction or Connection. Exception occurred: <h4><p> " + e.getMessage() + "</p></body></html>");
                outBuff.append("");
                break;
            }

        } while (false);
      
%>


<%= outBuff %>		
