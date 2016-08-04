package de.factoryfx.development.angularjs.model;

import java.util.ArrayList;
import java.util.List;

public class WebGuiDataType {

    public final String dataType;
    public final List<String> enumValues;

    public WebGuiDataType(Class<?> dataType) {

        if (dataType!=null && Enum.class.isAssignableFrom(dataType)){
            this.dataType="Enum";
        } else {
            if (dataType!=null) {
                this.dataType=dataType.getSimpleName();
            } else {
                this.dataType=null;
            }
        }

        if (dataType!=null && dataType.getEnumConstants()!=null){
            enumValues=new ArrayList<>();
            for (Object item: dataType.getEnumConstants()){
                enumValues.add(item.toString());
            }
        }else {
            enumValues=null;
        }
    }
}
