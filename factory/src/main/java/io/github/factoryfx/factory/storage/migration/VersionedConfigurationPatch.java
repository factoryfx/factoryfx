package io.github.factoryfx.factory.storage.migration;

/**
 * a {@link ConfigurationPatch} gated by the configuration schema version.<br>
 * the patch is applied only when the stored configuration's schema version equals fromVersion, afterwards the version is toVersion (patches chain in version order).<br>
 * fromVersion==null and toVersion==null means the patch is applied on every load regardless of version.
 */
public class VersionedConfigurationPatch {
    public final Integer fromVersion;
    public final Integer toVersion;
    public final ConfigurationPatch patch;

    public VersionedConfigurationPatch(Integer fromVersion, Integer toVersion, ConfigurationPatch patch) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.patch = patch;
    }

    public boolean isEveryTimePatch() {
        return fromVersion == null && toVersion == null;
    }
}
