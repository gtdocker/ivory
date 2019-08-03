<%@ page language="java" %><jsp:useBean id="helper" class="gtsoft.ivory.server.servlet.IvoryTraceListHelper"/><%  
    //
    // The helper object has the following methods:
    //
    // void setServletContext(ServletContext inContext);  **Mandatory**
    // void setRequest(HttpServletRequest inRequest);     **Mandatory**
    // void setResponse(HttpServletResponse inResponse);  **Mandatory**
    // void setDateFormat(String inDateFormat); 
    // void init();                                       **Mandatory**
    // boolean isGetTraceRequest();
    // void createTraceList();
    // int getTraceListSize();
    // void resetTraceListIndex();
    // void incrementTraceListIndex();
    // boolean isCurrentEntryHeader();
    // String getCurrentEntryHeader();
    // String getCurrentEntryTraceFileName();
    // String getCurrentEntryTraceLink();
    // String getCurrentEntryDate();
    // String getCurrentEntryDatePadded();
    // long getCurrentEntryFileSize();
    // void getTraceFile();

    helper.setServletContext(application);
    helper.setRequest(request);
    helper.setResponse(response);
    helper.init();
    if (helper.isGetTraceRequest() == false)
    {
        helper.createTraceList();
%>
<html>
<head>
<title>Ivory Server Trace Files</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#FFFFFF">
<h2 align="center">Ivory Server Trace Files</h2><%
        int numEntries = helper.getTraceListSize();
        if (numEntries == 0)
        {
%>
<h3 align="center">No Trace Files Found on Server</h3>
<%
        }
        else
        {
%>
            <pre>
<%
            helper.resetTraceListIndex();
            for (int ii = 0;ii < numEntries;++ii)
            {
                if (helper.isCurrentEntryHeader() == true)
                {
%>
<b> <%= helper.getCurrentEntryHeader() %></b>
<%
                }
                else
                {
%>   <a href="?getfile=<%= helper.getCurrentEntryTraceLink() %>"><%= helper.getCurrentEntryTraceFileName() %></a> <%= helper.getCurrentEntryDatePadded() %>
<%
                }
                helper.incrementTraceListIndex();
            }
%>
            </pre>
<%
        }
%>
</body>
</html>
<%
    }
    else
    {
        helper.getTraceFile();
    }
%>
