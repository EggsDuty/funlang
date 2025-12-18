package edu.ktu.funlang.semantics.rules;

import java.util.*;

public class SymbolTable {
    private Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public void enterScope() { scopes.push(new HashMap<>()); }
    public void exitScope() { scopes.pop(); }

    public void define(Symbol s) { if (!scopes.isEmpty()) scopes.peek().put(s.getName(), s); }
    public Optional<Symbol> resolve(String name) {
        for (Map<String, Symbol> m : scopes) {
            if (m.containsKey(name)) return Optional.of(m.get(name));
        }
        return Optional.empty();
    }
    public Optional<Symbol> resolveInCurrentScope(String name) {
        if (scopes.isEmpty()) return Optional.empty();
        Map<String, Symbol> top = scopes.peek();
        return Optional.ofNullable(top.get(name));
    }
}


