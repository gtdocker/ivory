/**********************************************************************/
/*                                                                    */
/* Copyright 2012, GT Software, Inc.                                  */
/* All rights reserved.                                               */
/*                                                                    */
/* #Description#                                                      */
/*                                                                    */
/*     Sample servlet to execute an Ivory request which returns       */
/*     the current date. This sample uses an IvoryStringRecord        */
/*     to pass data from/to the Ivory resource adapter.               */
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
 * <code>DateStringServlet</code>
 * is a sample servlet which will make a request to Ivory via
 * the JCA resource adapter to get the current date
 *
 * @author  GT Software
 */
public class DateStringServlet
    extends HttpServlet
{
    private static final String CONTENT_TYPE_HTML =
        "text/html; charset=utf-8";

    private static final String CONTENT_TYPE_XML =
        "text/xml; charset=utf-8";

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
        doDate(request,response);
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
        doDate(request,response);
    }

    /**
     * Execute Ivory sample project ivorydateTime using the Ivory resource adapter
     *
     * @param  request  request object
     * @param  response respons object
     * @throws ServletException
     */
    public void doDate(HttpServletRequest request,HttpServletResponse response)
        throws ServletException,IOException
    {
        ConnectionFactory cf    = null;
        StringBuffer errBuff    = new StringBuffer();
        String retXml           = null;
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

            // Create an IvoryInteractionSpec. Set the endpoint to the deployed
            // directory of the ivorydateTime project sample. Set the function name
            // to one of the operations.
            IvoryInteractionSpec ivInteractionSpec = new IvoryInteractionSpec();
            ivInteractionSpec.setRequestID("DateServlet");
            ivInteractionSpec.setEndpoint("/soap/General/DateTime/DateTime.ashx");
            ivInteractionSpec.setFunctionName("yyyymmdd");

            // IvoryStringRecord is a generalized implementation of Record to hold
            // a String request and/or a String response.
            IvoryStringRecord ivRecord = new IvoryStringRecord();
            try
            {
                ivRecord.setString("<yyyymmdd/>");
                ivInteraction.execute(ivInteractionSpec,ivRecord,ivRecord);
            }

            // Something went wrong during the execution
            catch (Exception e)
            {
                errBuff.append("<p>Error during execution of Ivory Server resource adapter. Exception occurred: " + e.getMessage() + "</p>");
                break;
            }

            // Since the output Record is an IvoryStringRecord the response will be
            // an XML string. Get the response and close both the
            // Interaction and the Connection.            
            try
            {
                retXml = ivRecord.getString();
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

        // Write out the response
        response.setHeader("Expires","-1");
        response.setHeader("Pragma","no-cache");
        PrintWriter out = response.getWriter();

        // If there was an error generate our own HTML document
        if (errBuff.length() > 0)
        {
            response.setContentType(CONTENT_TYPE_HTML);
            out.println("<html>");
            out.println("<head><title>DateStringServlet</title></head>");
            out.println("<body>");
            out.println(errBuff.toString());
            out.println("</body></html>");
        }

        // No error and Ivory returned XML, write it out
        else if (retXml != null)
        {
            response.setContentType(CONTENT_TYPE_XML);
            out.println(retXml);
        }
        out.close();
    }
}
