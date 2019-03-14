package de.factoryfx.data.storage.migration.datamigration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathBuilderTest {

    @Test
    public void test_parse(){
        AttributePath<String> parsed = PathBuilder.value(String.class).of("referenceAttribute.stringAttribute");
        AttributePath<String> programmatic = PathBuilder.value(String.class).pathElement("referenceAttribute").attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_parse_reflist(){
        AttributePath<String> parsed = PathBuilder.value(String.class).of("referenceListAttribute[1].stringAttribute");
        AttributePath<String> programmatic = PathBuilder.value(String.class).pathElement("referenceListAttribute",1).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_parse_reflist_multidigit_index(){
        AttributePath<String> parsed = PathBuilder.value(String.class).of("referenceListAttribute[123].stringAttribute");
        AttributePath<String> programmatic = PathBuilder.value(String.class).pathElement("referenceListAttribute",123).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_mixed_path(){
        AttributePath<String> parsed = PathBuilder.value(String.class).of("referenceAttribute.referenceListAttribute[123].stringAttribute");
        AttributePath<String> programmatic = PathBuilder.value(String.class).pathElement("referenceAttribute").pathElement("referenceListAttribute",123).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

}