package edu.ktu.funlang.visitors;

import edu.ktu.funlang.syntax.FunLangBaseVisitor;
import edu.ktu.funlang.syntax.FunLangParser;
import edu.ktu.funlang.semantics.rules.*;
import java.util.*;

/**
 * A light-weight semantic pass that:
 * - collects function signatures (names + arity)
 * - collects top-level variable declarations
 * - checks for obvious errors like duplicate function names and undefined variables in simple cases
 * This is intentionally conservative (no full type inference yet).
 */
public class FunLangSemanticAnalyzer extends FunLangBaseVisitor<Void> {

    private SymbolTable symbols = new SymbolTable();
    private List<String> errors = new ArrayList<>();

    public FunLangSemanticAnalyzer() {
        symbols.enterScope();
        symbols.define(new FunctionSymbol("MIN"));
        symbols.define(new FunctionSymbol("SQRT"));
    }

    public SymbolTable getSymbolTable() { return symbols; }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public List<String> getErrors() { return errors; }

    @Override
    public Void visitProgram(FunLangParser.ProgramContext ctx) {
        // first pass: collect function names
        for (var s : ctx.statement()) {
            if (s.funcDef() != null) {
                String name = s.funcDef().ID().getText();
                if (symbols.resolve(name).isPresent()) {
                    errors.add("Duplicate function name: " + name);
                } else {
                    symbols.define(new FunctionSymbol(name));
                }
            }
        }
        // second pass: deeper checks
        for (var s : ctx.statement()) visit(s);
        return null;
    }

    @Override
    public Void visitVarDecl(FunLangParser.VarDeclContext ctx) {
        String name = ctx.ID().getText();
        if (symbols.resolveInCurrentScope(name).isPresent()) {
            errors.add("Variable already declared in this scope: " + name);
        } else {
            symbols.define(new VarSymbol(name, ctx.type().getText()));
        }
        return null;
    }

    @Override
    public Void visitAssignment(FunLangParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        if (!symbols.resolve(name).isPresent()) {
            errors.add("Assignment to undeclared variable: " + name);
        }
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitForStmt(FunLangParser.ForStmtContext ctx) {
        symbols.enterScope();

        String name = ctx.ID().getText();
        if (symbols.resolveInCurrentScope(name).isPresent()) {
            errors.add("Loop variable already declared: " + name);
        } else {
            symbols.define(new VarSymbol(name, "integer"));
        }

        visit(ctx.expr(0));
        visit(ctx.expr(1));
        if (ctx.expr().size() > 2) visit(ctx.expr(2));

        visit(ctx.block());

        symbols.exitScope();
        return null;
    }

    @Override
    public Void visitFuncDef(FunLangParser.FuncDefContext ctx) {
        symbols.enterScope();
        if (ctx.paramList() != null) {
            for (var p : ctx.paramList().param()) {
                String pname = p.ID().getText();
                symbols.define(new VarSymbol(pname, p.type().getText()));
            }
        }
        visit(ctx.block());
        symbols.exitScope();
        return null;
    }

    @Override
    public Void visitFunctionCall(FunLangParser.FunctionCallContext ctx) {
        String name = ctx.ID().getText();

        if (!symbols.resolve(name).isPresent()) {
            errors.add("Call to undefined function: " + name);
        }

        if (ctx.argList() != null) {
            for (var a : ctx.argList().expr()) visit(a);
        }

        return null;
    }


    @Override
    public Void visitBasicExpr(FunLangParser.BasicExprContext ctx) {
        if (ctx.ID() != null) {
            String id = ctx.ID().getText();
            if (!symbols.resolve(id).isPresent()) {
                errors.add("Use of undeclared variable: " + id);
            }
        }
        return visitChildren(ctx);
    }
}

