package edu.ktu.funlang.syntax;

public record Diagnostic(Severity severity, String message, int line, int col) {}
