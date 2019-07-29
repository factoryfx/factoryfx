package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;

import java.util.List;

public class UpdateSummary {
    public final List<AttributeDiffInfo> changed;

    @JsonCreator
    public UpdateSummary(@JsonProperty("changed") List<AttributeDiffInfo> changed) {
        this.changed = changed;
    }
}
