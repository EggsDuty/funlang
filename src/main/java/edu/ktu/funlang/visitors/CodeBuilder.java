package edu.ktu.funlang.visitors;

import java.util.*;

public class CodeBuilder {
    private StringBuilder sb = new StringBuilder();
    private int indent = 0;
    private String ind = "    ";

    public void indent() { indent++; }
    public void outdent() { if (indent>0) indent--; }

    public void wl(String line) {
        for (int i=0;i<indent;i++) sb.append(ind);
        sb.append(line).append(System.lineSeparator());
    }

    @Override
    public String toString() { return sb.toString(); }
}

