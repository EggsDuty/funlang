package edu.ktu.funlang.visitors;

import edu.ktu.funlang.syntax.FunLangBaseVisitor;
import edu.ktu.funlang.syntax.FunLangParser;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class FunLangVisitor extends FunLangBaseVisitor<Object> {

    private final PrintStream out;
    private final Map<String, Integer> symbolTable = new HashMap<>();

    private final IfVisitor ifVisitor; // delegate for if/else

    public FunLangVisitor(PrintStream out) {
        this.out = out;

        this.ifVisitor = new IfVisitor(this);
    }

    @Override
    public Object visitProgram(FunLangParser.ProgramContext ctx) {
        // visit every statement in order
        for (FunLangParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    @Override
    public Object visitStatement(FunLangParser.StatementContext ctx) {
        // just delegate to child
        return visitChildren(ctx);
    }

    @Override
    public Object visitLetDecl(FunLangParser.LetDeclContext ctx) {
        String name = ctx.ID().getText();
        int value = 0; // NOTE - default if no initializer
        if (ctx.expr() != null) {
            value = (Integer) visit(ctx.expr());
        }
        symbolTable.put(name, value); // NOTE - we allow re-declaring for now
        return null;
    }

    @Override
    public Object visitAssignment(FunLangParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        int value = (Integer) visit(ctx.expr());
        symbolTable.put(name, value); // NOTE - we assume the variable was declared
        return null;
    }

    @Override
    public Object visitPrintStmt(FunLangParser.PrintStmtContext ctx) {
        int value = (Integer) visit(ctx.expr());
        out.println(value);
        return null;
    }

    @Override
    public Object visitIfStmt(FunLangParser.IfStmtContext ctx) {
        return this.ifVisitor.visitIfStmt(ctx); // NOTE - we delegate here
    }

    @Override
    public Object visitExpr(FunLangParser.ExprContext ctx) {
        if (ctx.INT() != null) {
            return Integer.parseInt(ctx.INT().getText());
        } else {
            String name = ctx.ID().getText();
            return symbolTable.get(name); // NOTE - we assume the variable was declared
        }
        // NOTE - this method return value, instead of null
    }
}
