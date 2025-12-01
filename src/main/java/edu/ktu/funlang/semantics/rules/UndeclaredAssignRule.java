package edu.ktu.funlang.semantics.rules;

import edu.ktu.funlang.syntax.FunLangParser;

import java.util.HashSet;
import java.util.Set;

public class UndeclaredAssignRule extends AbstractSemanticRule {
    private final Set<String> declared = new HashSet<>();

    @Override public String id() { return "undeclared-assign"; }
    @Override public String description() { return "Assignment to undeclared variable."; }

    @Override
    protected void onVisitLetDecl(FunLangParser.LetDeclContext ctx) {
        declared.add(ctx.ID().getText());
    }

    @Override
    protected void onVisitAssignment(FunLangParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        if (!declared.contains(name)) {
            error(ctx, "Assignment to undeclared variable '" + name + "'.");
        }
    }
}
