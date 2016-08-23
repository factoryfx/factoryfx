package de.factoryfx.adminui.angularjs.model.view;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.util.LanguageText;

public class GuiView<T extends FactoryBase> {
    public final String id;
    public final LanguageText title;
    public final Function<T,List<WebGuiFactoryHeader>> viewDataFromRootProvider;

    public GuiView(String id, LanguageText title, Function<T, List<WebGuiFactoryHeader>> viewDataFromRootProvider) {
        this.id = id;
        this.title = title;
        this.viewDataFromRootProvider = viewDataFromRootProvider;
    }

    @SuppressWarnings("unchecked")//TODO fix generics
    public WebGuiView createWebGuiView(FactoryBase root, Locale locale){
        return new WebGuiView(id,title.getPreferred(locale),viewDataFromRootProvider.apply((T)root));
    }

    public ViewHeader createHeader(Locale locale){
        return new ViewHeader(id,title.getPreferred(locale));
    }
}
