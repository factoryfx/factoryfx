package io.github.factoryfx.docu.starter;

import java.lang.Override;

/**
 * Factory for the example resource */
public class ExampleResourceFactory extends ServerBaseFactory<ExampleResource> {
  @Override
  public ExampleResource createImpl() {
    return new ExampleResource();
  }
}
