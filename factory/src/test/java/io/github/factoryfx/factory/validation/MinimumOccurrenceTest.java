package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinimumOccurrenceTest {

    @Test
    public final void test(){
        StringListAttribute stringListAttribute = new StringListAttribute();
        stringListAttribute.validation(new MinimumOccurrence<>(2));

        {
            List<ValidationError> errors = stringListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 1);
        }

        stringListAttribute.add("1");
        stringListAttribute.add("2");
        {
            List<ValidationError> errors = stringListAttribute.internal_validate(null, "blub");
            assertEquals(errors.size(), 0);
        }
    }

}