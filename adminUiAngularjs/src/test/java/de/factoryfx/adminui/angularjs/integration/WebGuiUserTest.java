package de.factoryfx.adminui.angularjs.integration;

import java.util.Arrays;
import java.util.Locale;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.adminui.SinglePrecessInstanceUtil;
import de.factoryfx.user.InMemoryUserManagement;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;
import javafx.application.Application;
import org.slf4j.LoggerFactory;

public class WebGuiUserTest extends WebGuiTest{

    @Override
    protected UserManagement getUserManagement() {
        return new InMemoryUserManagement(Arrays.asList(new User("test","p", Locale.GERMAN,Permissions.PERMISSON_X)));
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch(args);
    }

}