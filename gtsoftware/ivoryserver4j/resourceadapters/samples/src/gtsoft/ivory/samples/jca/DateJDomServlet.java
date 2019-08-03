/**********************************************************************/
/*                                                                    */
/* Copyright 2012, GT Software, Inc.                                  */
/* All rights reserved.                                               */
/*                                                                    */
/* #Description#                                                      */
/*                                                                    */
/*     Sample servlet to execute an Ivory request which returns       */
/*     the current date. This sample uses an IvoryJDomDocumentRecord  */
/*     to pass data from/to the Ivory resource adapter.               */
/*                                                                    */
/**********************************************************************/
package gtsoft.ivory.samples.jca;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.resource.cci.*;

import gtsoft.ivory.server.jca.cci.IvoryInteractionSpec;
import gtsoft.ivory.server.jca.cci.IvoryJDomDocumentRecord;
import gtsoft.ivory.server.jca.cci.IvoryLocalConnectionSpec;

/**
 * <code>DateJDomServlet</code>
 * is a sample servlet which will make a request to Ivory via
 * the JCA resource adapter to get the current date
 *
 * @author  GT Software
 */
public class DateJDomServlet
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
        ConnectionFactory cf                = null;
        StringBuffer errBuff                = new StringBuffer();
        String jndiName                     = getServletContext().getInitParameter("IvoryConnectionFactoryName");
        org.jdom.Document jdomDocument      = null;
        
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

            // Create the request jdom document
            jdomDocument = createRequestJDomDocument(errBuff);
            if (jdomDocument == null)
                break;

            // IvoryJDomDocumentRecord is an implementation of Record to hold
            // a jdom document request and/or response.
            IvoryJDomDocumentRecord ivRecord = new IvoryJDomDocumentRecord();
            try
            {
                ivRecord.setJDomDocument(jdomDocument);
                ivInteraction.execute(ivInteractionSpec,ivRecord,ivRecord);
            }

            // Something went wrong during the execution
            catch (Exception e)
            {
                errBuff.append("<p>Error during execution of Ivory Server resource adapter. Exception occurred: " + e.getMessage() + "</p>");
                break;
            }

            // Since the output Record is an IvoryJDomDocumentRecord the response 
            // will be a jdom document. Get the response and close both the
            // Interaction and the Connection.            
            try
            {
                jdomDocument = ivRecord.getJDomDocument();
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

        // No error and Ivory returned a jdom document, convert to string XML
        // and write it out
        if ((errBuff.length() == 0) && (jdomDocument != null))
        {
            String retXml = createResponseXml(jdomDocument,errBuff);
            if (errBuff.length() == 0)
            {
                response.setContentType(CONTENT_TYPE_XML);
                out.println(retXml);
            }
        }

        // If there was an error generate our own HTML document
        if (errBuff.length() > 0)
        {
            response.setContentType(CONTENT_TYPE_HTML);
            out.println("<html>");
            out.println("<head><title>DateJDomServlet</title></head>");
            out.println("<body>");
            out.println(errBuff.toString());
            out.println("</body></html>");
        }
        out.close();
    }

    /**
     * Create a jdom document and populate it with the request data
     *
     * @param  errBuff  buffer to store error information if need be
     * @return          a jdom document with the request data
     */
    private org.jdom.Document createRequestJDomDocument(StringBuffer errBuff)
    {
        org.jdom.Document jdomDocument = null;
        
        try
        {
            byte[] request = new String("<yyyymmdd/>").getBytes("UTF-8");
            ByteArrayInputStream bais = new ByteArrayInputStream(request);
            InputStreamReader reader = new InputStreamReader(bais,"UTF-8");
            org.jdom.input.SAXBuilder sb = new org.jdom.input.SAXBuilder();
            jdomDocument = sb.build(reader);
        }
        catch (Exception e)
        {
            errBuff.append("<p>Exception occurred: " + e.getMessage() + "</p>");
        }
        
        return (jdomDocument);
    }

    /**
     * Create the response XML
     *
     * @param  jdomDocument the jdom document containing the response data
     * @param  errBuff      buffer to store error information if need be
     * @return              XML string response
     */
    private String createResponseXml(org.jdom.Document jdomDocument,StringBuffer errBuff)
    {
        String retXml = null;
        org.jdom.output.Format format = org.jdom.output.Format.getRawFormat();
        format.setOmitDeclaration(true);
        format.setTextMode(org.jdom.output.Format.TextMode.PRESERVE);
        org.jdom.output.XMLOutputter outputter = new org.jdom.output.XMLOutputter(format);
        StringWriter sw = new StringWriter();
        try
        {
            outputter.output(jdomDocument,sw);
            retXml = sw.toString();
        }
        catch (Exception e)
        {
            errBuff.append("<p>Exception occurred: " + e.getMessage() + "</p>");
        }

        return (retXml);
    }
}
