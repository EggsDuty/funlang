package edu.ktu.funlang.semantics;

import edu.ktu.funlang.semantics.rules.AbstractSemanticRule;
import edu.ktu.funlang.semantics.rules.Rule;
import edu.ktu.funlang.syntax.DiagnosticReporter;
import edu.ktu.funlang.syntax.FunLangBaseVisitor;
import edu.ktu.funlang.syntax.FunLangParser;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;

public class SemanticEngine {

    private final List<Rule> rules;
    private final SemanticContext sharedCtx;

    public SemanticEngine(List<Rule> rules, SemanticContext sharedCtx) {
        this.rules = rules;
        this.sharedCtx = sharedCtx;
    }

    public void analyze(FunLangParser.ProgramContext program,
                        DiagnosticReporter reporter,
                        TokenStream tokens) {
        for (Rule r : rules) {
            r.init(reporter, tokens);
            if (r instanceof AbstractSemanticRule tr) {
                tr.setContext(sharedCtx);
                tr.visit(program);
            } else {
                ((FunLangBaseVisitor<Void>) r).visit(program);
            }
            r.finish();
        }
    }
}
