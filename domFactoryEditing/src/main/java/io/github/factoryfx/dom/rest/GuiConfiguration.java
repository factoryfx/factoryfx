package io.github.factoryfx.dom.rest;

import java.util.List;

public class GuiConfiguration {
    public final String projectName;
    public final List<GuiNavbarItem> navBarItems;

    public GuiConfiguration(String projectName, List<GuiNavbarItem> navBarItems) {
        this.projectName = projectName;
        this.navBarItems = navBarItems;
    }
}
