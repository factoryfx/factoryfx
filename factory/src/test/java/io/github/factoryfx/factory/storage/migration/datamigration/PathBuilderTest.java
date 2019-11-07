package io.github.factoryfx.factory.storage.migration.datamigration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathBuilderTest {

    @Test
    public void test_parse(){
        AttributePathTarget<String> parsed = PathBuilder.of("referenceAttribute.stringAttribute");
        AttributePathTarget<String> programmatic = new PathBuilder<String>().pathElement("referenceAttribute").attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_parse_reflist(){
        AttributePathTarget<String> parsed = PathBuilder.of("referenceListAttribute[1].stringAttribute");
        AttributePathTarget<String> programmatic = new PathBuilder<String>().pathElement("referenceListAttribute",1).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_parse_reflist_multi_digit_index(){
        AttributePathTarget<String> parsed = PathBuilder.of("referenceListAttribute[123].stringAttribute");
        AttributePathTarget<String> programmatic = new PathBuilder<String>().pathElement("referenceListAttribute",123).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

    @Test
    public void test_mixed_path(){
        AttributePathTarget<String> parsed = PathBuilder.of("referenceAttribute.referenceListAttribute[123].stringAttribute");
        AttributePathTarget<String> programmatic = new PathBuilder<String>().pathElement("referenceAttribute").pathElement("referenceListAttribute",123).attribute("stringAttribute");

        assertTrue(parsed.match(programmatic));
    }

}