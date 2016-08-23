package de.factoryfx.adminui.angularjs.integration.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.adminui.angularjs.model.view.WebGuiFactoryHeader;
import de.factoryfx.factory.util.LanguageText;

public class ViewCreator {

    public List<GuiView<ExampleFactoryA>> create(){
        ArrayList<GuiView<ExampleFactoryA>> result = new ArrayList<>();
        result.add(new GuiView<>("dfhgrdtrst", new LanguageText().en("view1").de("view1"),
                exampleFactoryA -> exampleFactoryA.referenceListAttribute.get().stream().map(WebGuiFactoryHeader::new).collect(Collectors.toList())));
        return result;
    }
}
