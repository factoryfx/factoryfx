module io.github.factoryfx.copperBridge {
    requires transitive io.github.factoryfx.factory;
    requires org.copperengine.core;
    requires org.copperengine.ext;
    requires org.yaml.snakeyaml;

    opens io.github.factoryfx.copperbridge;
    opens io.github.factoryfx.copperbridge.db;
}