package edu.ktu.funlang.syntax;

import java.util.*;

public class DiagnosticReporter {
    private final List<Diagnostic> list = new ArrayList<>();

    public void error(String msg, int line, int col) {
        list.add(new Diagnostic(Severity.ERROR, msg, line, col));
    }

    public void warn(String msg, int line, int col) {
        list.add(new Diagnostic(Severity.WARNING, msg, line, col));
    }

    public boolean hasErrors() {
        return list.stream().anyMatch(d -> d.severity() == Severity.ERROR);
    }

    public List<Diagnostic> all() {
        return List.copyOf(list);
    }
}
