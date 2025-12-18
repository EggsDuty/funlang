package edu.ktu.funlang.semantics.rules;

public class VarSymbol extends Symbol {
    private final String type;
    public VarSymbol(String name, String type) { super(name); this.type = type; }
    public String getType() { return type; }
}

