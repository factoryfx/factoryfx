package de.factoryfx.jetty.soap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class AbstractSoapProvider implements MessageBodyWriter<SOAPMessage>, MessageBodyReader<SOAPMessage>
{

    @Override
    public boolean isWriteable(Class<?> aClass, Type genericType,
                               Annotation[] annotations, MediaType mediaType)
    {
        return SOAPMessage.class.isAssignableFrom( aClass );
    }

    @Override
    public long getSize( SOAPMessage soapMessage, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo(SOAPMessage soapMessage, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream ) throws IOException, WebApplicationException
    {
        try
        {
            soapMessage.writeTo( outputStream );
        }
        catch( SOAPException e )
        {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isReadable( Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType )
    {
        return aClass.isAssignableFrom( SOAPMessage.class );
    }

    protected SOAPMessage modifyMessage( String soapProtocol, InputStream inputStream ) throws SOAPException
    {
        MessageFactory messageFactory = MessageFactory.newInstance( soapProtocol );
        StreamSource messageSource = new StreamSource( inputStream );
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        soapPart.setContent( messageSource );
        message.saveChanges();
        return message;
    }
}