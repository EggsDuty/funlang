package edu.ktu.funlang.semantics.rules;

/**
 * A simple wrapper to allow ref semantics for primitive types.
 * Functions can modify the contained value, simulating pass-by-reference.
 */
public class Value<T> {
    private T value;
    public Value(T value) { this.value = value; }
    public T get() { return value; }
    public void set(T value) { this.value = value; }
}

