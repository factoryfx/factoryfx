package de.factoryfx.process;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Throwables;
import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.Data;
import de.factoryfx.factory.atrribute.*;

/**
 *
 * @param <P> process
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ProcessExecutor<P extends Process,PR> extends Data {

    private P process;

    public ProcessExecutor() {

    }

    abstract protected P create(PR parameter);




}
