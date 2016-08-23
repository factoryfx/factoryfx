package de.factoryfx.adminui.angularjs.model.view;

import java.util.List;

public class WebGuiView {
    public final String id;
    public final String title;
    public final List<WebGuiFactoryHeader> factories;

    public WebGuiView(String id, String title, List<WebGuiFactoryHeader> factories) {
        this.id = id;
        this.title = title;
        this.factories = factories;
    }
}
