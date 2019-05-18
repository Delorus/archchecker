package ru.sherb.archchecker.report;

import java.util.Set;

/**
 * @author maksim
 * @since 18.05.19
 */
public final class ReportSetting {
    final Set<String> includeModules;
    final int dependencyDepth;

    //region Builder

    private ReportSetting(Set<String> includeModules, int dependencyDepth) {
        this.includeModules = includeModules;
        this.dependencyDepth = dependencyDepth;
    }

    public static SerializeSettingBuilder builder() {
        return new SerializeSettingBuilder();
    }

    public static final class SerializeSettingBuilder {
        Set<String> includeModules;
        int dependencyDepth;

        private SerializeSettingBuilder() {
        }

        public SerializeSettingBuilder includeModules(Set<String> includeModules) {
            this.includeModules = includeModules;
            return this;
        }

        public SerializeSettingBuilder dependencyDepth(int dependencyDepth) {
            this.dependencyDepth = dependencyDepth;
            return this;
        }

        public ReportSetting build() {
            return new ReportSetting(includeModules, dependencyDepth);
        }
    }
    //endregion
}
