/**********************************************************************/
/*                                                                    */
/* Copyright 2012, GT Software, Inc.                                  */
/* All rights reserved.                                               */
/*                                                                    */
/* #Description#                                                      */
/*                                                                    */
/*     Sample servlet to execute the Ivory resource adapter           */
/*     installation verification program. The installation            */
/*     verfication program will return an HTML document which may     */
/*     be displayed in a web browser.                                 */
/*                                                                    */
/**********************************************************************/
package gtsoft.ivory.samples.jca;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.resource.cci.*;

import gtsoft.ivory.server.jca.cci.IvoryInteractionSpec;
import gtsoft.ivory.server.jca.cci.IvoryStringRecord;
import gtsoft.ivory.server.jca.cci.IvoryLocalConnectionSpec;

/**
 * <code>InstallationVerificationServlet</code>
 * is a sample servlet which will execute the Ivory JCA resource
 * adapter installation verification functionality
 *
 * @author  GT Software
 */
public class InstallationVerificationServlet
    extends HttpServlet
{
    private static final String CONTENT_TYPE_HTML =
        "text/html; charset=utf-8";

    /**
     * Implementation of servlet init method. 
     *
     * @param  config servlet config object
     * @throws ServletException
     */
    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
    }

    /**
     * Implementation of servlet doGet method. 
     *
     * @param  request  request object
     * @param  response respons object
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException,IOException
    {
        doInstallationVerification(request,response);
    }

    /**
     * Implementation of servlet doPost method. 
     *
     * @param  request  request object
     * @param  response respons object
     * @throws ServletException
     */
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
        throws ServletException,IOException
    {
        doInstallationVerification(request,response);
    }

    /**
     * Perform the actual installation verification.
     *
     * @param  request  request object
     * @param  response respons object
     * @throws ServletException
     */
    public void doInstallationVerification(HttpServletRequest request,
                                           HttpServletResponse response)
        throws ServletException,IOException
    {
        ConnectionFactory cf    = null;
        StringBuffer errBuff    = new StringBuffer();
        String retHtml          = null;
        String jndiName         = getServletContext().getInitParameter("IvoryConnectionFactoryName");
        
        do
        {
            // Make sure a <context-param> was supplied for IvoryConnectionFactoryName
            if ((jndiName == null) || (jndiName.length() == 0))
            {
                errBuff.append("<p>Mandatory web application context parameter IvoryConnectionFactoryName was not supplied. Please add to web.xml.</p>");
                break;
            }

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
                errBuff.append("<p>Context.lookup() failed, cannot allocate ConnectionFactory with name " + jndiName + ". Exception occurred: " + e.getMessage() + "</p>");
                break;
            }

            // An exception did not occur but make sure we actually did get a
            // ConnectionFactory
            if (cf == null)
            {
                errBuff.append("<p>Could not get connection factory</p>");
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
                errBuff.append("<p>Unable to create a Connection. Exception occurred: " + e.getMessage() + "</p>");
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
                errBuff.append("<p>Unable to create an Interaction. Exception occurred: " + e.getMessage() + "</p>");
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
                errBuff.append("<p>Error during execution of Ivory Server resource adapter. Exception occurred: " + e.getMessage() + "</p>");
                break;
            }

            // Installation verification will return an HTML document. It is stored 
            // inside the output Record. Get the response and close both the
            // Interaction and the Connection.            
            try
            {
                retHtml = ivRecord.getString();
                ivInteraction.close();
                ivConnection.close();
            }

            // Something went wrong
            catch (Exception e)
            {
                errBuff.append("<p>Error while attempting to close either the Interaction or Connection. Exception occurred: " + e.getMessage() + "</p>");
                break;
            }

        } while (false);

        // Write out the HTML response
        response.setContentType(CONTENT_TYPE_HTML);
        response.setHeader("Expires","-1");
        response.setHeader("Pragma","no-cache");
        PrintWriter out = response.getWriter();

        // If there was an error generate our own HTML document
        if (errBuff.length() > 0)
        {
            out.println("<html>");
            out.println("<head><title>Ivory Server Resource Adapter Installation Verification Results</title></head>");
            out.println("<body>");
            out.println(errBuff.toString());
            out.println("</body></html>");
        }

        // No error and the installation verification returned HTML. Write all the
        // HTML out
        else if (retHtml != null)
        {
            out.println(retHtml);
        }
        out.close();
    }
}
