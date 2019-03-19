module io.github.factoryfx.copperBridge {
    requires transitive io.github.factoryfx.factory;
    requires transitive io.github.factoryfx.data;
    requires org.copperengine.core;
    requires org.copperengine.ext;

    opens io.github.factoryfx.copperbridge;
    opens io.github.factoryfx.copperbridge.db;
}