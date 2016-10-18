package de.factoryfx.server.angularjs.factory;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class SessionStorageMock extends SessionStorage {

    private Map<String,Object> sessionSim=new HashMap<>();

    @Override
    protected Object getAttribute(HttpServletRequest request, String key){
        return sessionSim.get(key);
    }
    @Override
    protected void setAttribute(HttpServletRequest request,String key,Object object){
        sessionSim.put(key,object);
    }

}
