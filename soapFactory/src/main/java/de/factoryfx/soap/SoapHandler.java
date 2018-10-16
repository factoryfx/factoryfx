package de.factoryfx.soap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringTokenizer;

public class SoapHandler<S> extends AbstractHandler {
    private final WebServiceRequestDispatcher dispatcher;
    private final SOAPMessageUtil soapMessageUtil;

    public SoapHandler(WebServiceRequestDispatcher dispatcher, SOAPMessageUtil soapXmlParser) {
        this.dispatcher = dispatcher;
        this.soapMessageUtil = soapXmlParser;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        try {
            MessageFactory messageFactory;
            if (Objects.equals(request.getHeader("Content-Type"),"text/xml")){
                messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            } else {
                //"application/soap+xml"
                messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            }

            StreamSource messageSource = new StreamSource(request.getInputStream());
            SOAPMessage message = messageFactory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();
            soapPart.setContent(messageSource);
            message.saveChanges();

            Object requestData = dispatcher.execute(soapMessageUtil.parseRequest(message));
            SOAPMessage responseMessage = soapMessageUtil.wrapResponse(requestData, messageFactory);

            if (responseMessage.saveRequired()) {
                responseMessage.saveChanges();
            }
            response.setStatus(HttpServletResponse.SC_OK);
            putHeaders(responseMessage.getMimeHeaders(), response);

            OutputStream os = response.getOutputStream();
            responseMessage.writeTo(os);
            os.flush();

            baseRequest.setHandled(true);

        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }

    }

    private MimeHeaders getHeaders(HttpServletRequest req) {

        Enumeration headerNames = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();

        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            String headerValue = req.getHeader(headerName);

            StringTokenizer values = new StringTokenizer(headerValue, ",");
            while (values.hasMoreTokens()) {
                headers.addHeader(headerName, values.nextToken().trim());
            }
        }
        return headers;
    }
    private void putHeaders(MimeHeaders headers, HttpServletResponse res) {

        Iterator it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader)it.next();

            String[] values = headers.getHeader(header.getName());
            if (values.length == 1)
                res.setHeader(header.getName(), header.getValue());
            else {
                StringBuffer concat = new StringBuffer();
                int i = 0;
                while (i < values.length) {
                    if (i != 0) {
                        concat.append(',');
                    }
                    concat.append(values[i++]);
                }
                res.setHeader(header.getName(), concat.toString());
            }
        }
    }


}
