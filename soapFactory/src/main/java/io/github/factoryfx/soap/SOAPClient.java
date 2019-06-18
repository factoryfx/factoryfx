package io.github.factoryfx.soap;


import java.net.URI;

public class SOAPClient<T> {

//    private static final Logger logger = LoggerFactory.getLogger(SOAPClient.class);

//    private final Class<T> soapInterface;
//    private final URI endpoint;
////    private final Client client;
////    private final SOAPProtocol soapProtocol;
//    private final PROTOCOL_VERSION version;
//    private final Object interceptor;
//    private final Function<Object, Object> filterFunction;

    public enum PROTOCOL_VERSION {
        _1_1, _1_2,;
    }

    public SOAPClient(Class<T> soapInterface, URI endpoint, PROTOCOL_VERSION version, Integer connectTimeout, Integer readTimeout, Object interceptor) {
//        this.soapInterface = soapInterface;
//        this.endpoint = endpoint;
//        ClientConfig configuration = new ClientConfig();
//        configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
//        configuration.property(ClientProperties.READ_TIMEOUT, readTimeout);
//        this.client = ClientBuilder.newBuilder().withConfig(configuration).build().register(new Soap12Provider()).register(new Soap11Provider());
////        this.soapProtocol = new SOAPProtocol(soapInterface);
//        this.version = version;
//        this.interceptor = interceptor;
//        this.filterFunction = interceptor == null ? Function.identity() : this::filterRequest;

    }
//
//    @SuppressWarnings("unchecked")
//    public T getClient() {
//        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { soapInterface }, (proxy, method, args) -> {
//            if (method.getName().equals("toString")) { return "ProxyInstance " + soapInterface.getSimpleName(); }
//            return call(args[0]);
//        });
//    }
//
//    private Object call(Object param) throws GeneratedJAXBException {
//        param = filterFunction.apply(param);
////        Logging.logRequest(soapInterface.getSimpleName(), param, LoggerFactory.getLogger("webservice.out." + soapInterface.getSimpleName()));
//        boolean is12 = version == PROTOCOL_VERSION._1_2;
//        SOAPMessage messageOut = is12 ? soapProtocol.createSoap12Message(param) : soapProtocol.createSoap11Message(param);
//        Response response = client.target(endpoint).request().post(Entity.entity(messageOut, is12 ? "application/soap+xml" : "text/xml"));
//        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
//            SOAPMessage messageIn = response.readEntity(SOAPMessage.class);
//            final Object r = soapProtocol.unboxSOAPBody(messageIn);
////            Logging.logRequest(soapInterface.getSimpleName(), r, LoggerFactory.getLogger("webservice.out." + soapInterface.getSimpleName()));
//            return r;
//        }
//        throw new RuntimeException("Bad http return code: " + response.getStatus() + "\n" + response.readEntity(String.class));
//    }
//
//    private Object filterRequest(Object object) {
//        if (interceptor == null) {
//            return object;
//        }
//
//        Class<?> clazz = object.getClass();
//
//        Optional<Method> filterMethodOpt = Arrays.stream(interceptor.getClass().getMethods())
//                                                 .filter(method -> Stream.of(method.getParameterTypes()).collect(Collectors.toList()).contains(clazz))
//                                                 .findAny();
//
//        if (!filterMethodOpt.isPresent()) {
//            return object;
//        }
//        Method filterMethod = filterMethodOpt.get();
//
//        try {
//            return filterMethod.invoke(interceptor, object);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            logger.error("Error while filtering outbound request ({}.{})", soapInterface.getSimpleName(), filterMethod.getName(), e);
//        }
//
//        return object;
//    }
//
//    public Object unboxSOAPBody(SOAPMessage messageIn) throws GeneratedJAXBException {
//        try {
//
//            Unmarshaller unmarshaller = createJxbContext().createUnmarshaller();
//            SOAPFault soapFault = messageIn.getSOAPBody().getFault();
//            if (soapFault != null) {
//                DetailEntry next = soapFault.getDetail().getDetailEntries().next();
//                Object unmarshalledExceptionElement = unmarshaller.unmarshal(soapFault.getDetail().getDetailEntries().next());
//
//                Class<?> elementClass;
//                Object exceptionElement;
//
//                if (unmarshalledExceptionElement instanceof JAXBElement) {
//                    JAXBElement jaxbElement = (JAXBElement) unmarshalledExceptionElement;
//                    elementClass = jaxbElement.getDeclaredType();
//                    exceptionElement = jaxbElement.getValue();
//                } else {
//                    elementClass = unmarshalledExceptionElement.getClass();
//                    exceptionElement = unmarshalledExceptionElement;
//                }
//
    //                throw (GeneratedJAXBException) soapExceptionMap.get(elementClass)
//                        .getConstructor(String.class, elementClass)
//                        .newInstance(soapFault.getFaultString(), elementClass.cast(exceptionElement));
//            } else {
//
//                return unmarshaller.unmarshal(messageIn.getSOAPBody().getFirstChild());
//            }
//
//        } catch (JAXBException | SOAPException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//
//            throw new RuntimeException(e);
//        }
//
//    }

//    private JAXBContext createJxbContext() {
//        JAXBContext fromCache = jxbCache.get(resourceClass);
//        if (fromCache != null)
//            return fromCache;
//        Set<Class<?>> classes = new HashSet<>();
//        dispatchMap.forEach((c,m)->{
//            classes.add(m.getReturnType());
//            classes.add(m.getParameterTypes()[0]);
//            for (Class<?> exClass : m.getExceptionTypes()) {
//                if (exClass.getAnnotation(WebFault.class) != null) {
//                    try {
//                        classes.add(exClass.getMethod("getFaultInfo").getReturnType());
//                    } catch (NoSuchMethodException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        });
//        try {
//            JAXBContext jxb = JAXBContext.newInstance(classes.toArray(new Class[0]));
//            jxbCache.put(resourceClass,jxb);
//            return jxb;
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public SOAPMessage createSoap11Message(Object param) {
//        return createSoapMessage(param, messageFactory11);
//    }
//
//    public SOAPMessage createSoap12Message(Object param) {
//        return createSoapMessage(param, messageFactory12);
//    }
//
//    private SOAPMessage createSoapMessage(Object param, MessageFactory messageFactory) {
//        try {
//            SOAPMessage converted = messageFactory.createMessage();
//
//            final Object o;
//            Class<?> clazz = param.getClass();
//            XmlRootElement r = clazz.getAnnotation(XmlRootElement.class);
//            if (r == null) {
//                // we need to infer the errorName
//                o = new JAXBElement(new QName(Introspector.decapitalize(clazz.getSimpleName())), clazz, param);
//            } else {
//                o = param;
//            }
//            Marshaller marshaller = jxb.createMarshaller();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            org.w3c.dom.Document document = db.newDocument();
//            marshaller.marshal(o, document);
//            org.w3c.dom.Node importedNode = converted.getSOAPBody().getOwnerDocument().importNode(document.getChildNodes().item(0), true);
//            converted.getSOAPBody().appendChild(importedNode);
//            return converted;
//        } catch (SOAPException | JAXBException | ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        }
//    }
}

