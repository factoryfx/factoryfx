package de.factoryfx.server.angularjs.integration;

import java.util.Arrays;
import java.util.Locale;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.server.SinglePrecessInstanceUtil;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.persistent.PersistentUserManagement;
import javafx.application.Application;
import org.slf4j.LoggerFactory;

public class WebGuiUserTest extends WebGuiTest{

    @Override
    protected UserManagement getUserManagement() {
        return new PersistentUserManagement(Arrays.asList(new User("test","p", Locale.GERMAN,Permissions.PERMISSON_X)));
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch(args);
    }

}