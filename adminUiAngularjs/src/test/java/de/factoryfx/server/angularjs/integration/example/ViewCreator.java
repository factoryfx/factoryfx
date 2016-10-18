package de.factoryfx.server.angularjs.integration.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.server.angularjs.model.view.GuiView;
import de.factoryfx.server.angularjs.model.view.WebGuiFactoryHeader;
import de.factoryfx.data.util.LanguageText;

public class ViewCreator {

    public List<GuiView<ExampleFactoryA>> create(){
        ArrayList<GuiView<ExampleFactoryA>> result = new ArrayList<>();
        result.add(new GuiView<>("dfhgrdtrst", new LanguageText().en("view1").de("view1"),
                exampleFactoryA -> exampleFactoryA.referenceListAttribute.get().stream().map(WebGuiFactoryHeader::new).collect(Collectors.toList())));
        return result;
    }
}
