package de.factoryfx.server.angularjs.factory;

import javax.servlet.http.HttpServletRequest;

import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.server.angularjs.factory.server.AuthorizationRequestFilter;
import de.factoryfx.user.AuthorizedUser;

public class SessionStorage {

    private static final String CURRENT_EDITING_FACTORY_SESSION_KEY = "CurrentEditingFactory";
    private static final String USER = "USER_";

    public AuthorizedUser getUser(HttpServletRequest request) {
        return (AuthorizedUser) getAttribute(request,USER);
    }

    public void loginUser(HttpServletRequest request,AuthorizedUser user){
        setAttribute(request,AuthorizationRequestFilter.LOGIN_SESSION_KEY,true);
        setAttribute(request,USER,user);
    }

    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<?> getCurrentEditingFactory(HttpServletRequest request){
        return (FactoryAndStorageMetadata<?>) getAttribute(request,CURRENT_EDITING_FACTORY_SESSION_KEY);
    }

    public void setCurrentEditingFactory(HttpServletRequest request, FactoryAndStorageMetadata<?> factory){
        setAttribute(request,CURRENT_EDITING_FACTORY_SESSION_KEY, factory);
    }

    public boolean  hasCurrentEditingFactory(HttpServletRequest request){
        return getAttribute(request,CURRENT_EDITING_FACTORY_SESSION_KEY)!=null;
    }


    protected Object getAttribute(HttpServletRequest request,String key){
        return request.getSession(true).getAttribute(key);
    }
    protected void setAttribute(HttpServletRequest request,String key,Object object){
        request.getSession(true).setAttribute(key,object);
    }



}
