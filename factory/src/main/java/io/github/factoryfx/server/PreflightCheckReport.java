package io.github.factoryfx.server;

import java.util.Collections;
import java.util.List;

/**
 * result of {@link Microservice#preflightCheck}: problems that would prevent or degrade a start of the
 * application with the stored configuration. empty problems means the configuration is safe to start with.
 */
public class PreflightCheckReport {
    public final List<String> problems;

    public PreflightCheckReport(List<String> problems) {
        this.problems = Collections.unmodifiableList(problems);
    }

    public boolean isOk() {
        return problems.isEmpty();
    }

    /**
     * @return human-readable report, e.g. for deployment logs
     */
    public String report() {
        if (isOk()) {
            return "preflight check ok";
        }
        StringBuilder result = new StringBuilder("preflight check found ").append(problems.size()).append(" problem(s):");
        for (String problem : problems) {
            result.append("\n  * ").append(problem);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return report();
    }
}
