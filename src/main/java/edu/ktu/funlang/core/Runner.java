package edu.ktu.funlang.core;

import edu.ktu.funlang.semantics.SemanticContext;
import edu.ktu.funlang.semantics.SemanticEngine;
import edu.ktu.funlang.semantics.rules.Rule;
import edu.ktu.funlang.semantics.rules.UndeclaredAssignRule;
import edu.ktu.funlang.semantics.rules.UndeclaredUseRule;
import edu.ktu.funlang.syntax.DiagnosticReporter;
import edu.ktu.funlang.syntax.FunLangLexer;
import edu.ktu.funlang.syntax.FunLangParser;
import edu.ktu.funlang.syntax.SyntaxErrorCollector;
import edu.ktu.funlang.visitors.FunLangVisitor;
import org.antlr.v4.runtime.*;

import org.antlr.v4.runtime.tree.ParseTree;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public final class Runner {

    private Runner() {}

    /** Parse + execute from a CharStream (used by Main). */
    public static void run(CharStream input, PrintStream out) {
        FunLangLexer lexer = new FunLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunLangParser parser = new FunLangParser(tokens);

        // Collect syntax errors instead of printing to stderr
        SyntaxErrorCollector err = new SyntaxErrorCollector();
        parser.removeErrorListeners();
        parser.addErrorListener(err);

        ParseTree tree = parser.program();

        List<String> errors = err.getErrors();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Syntax errors: " + errors);
        }

        var reporter = new DiagnosticReporter();
        var sharedCtx = new SemanticContext(reporter, tokens);

        List<Rule> rules = List.of(
                new UndeclaredAssignRule(),
                new UndeclaredUseRule()
        );

        var engine = new SemanticEngine(rules, sharedCtx);
        engine.analyze((FunLangParser.ProgramContext) tree, reporter, tokens);

        if (reporter.hasErrors()) {
            var msgs = reporter.all().stream()
                    .map(d -> "%s at %d:%d".formatted(d.message(), d.line(), d.col()))
                    .toList();
            throw new IllegalArgumentException("Semantic errors:\n" + String.join("\n", msgs));
        }

        new FunLangVisitor(out).visit(tree);
    }

    /** Parse + execute from a raw source string. */
    public static void run(String source, PrintStream out) {
        run(CharStreams.fromString(source), out);
    }

    /** Convenience for tests: returns captured stdout as a String. */
    public static String runToString(String source) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf);
        run(source, ps);
        return buf.toString().replace("\r\n", "\n"); // normalize newlines on Windows
    }
}

