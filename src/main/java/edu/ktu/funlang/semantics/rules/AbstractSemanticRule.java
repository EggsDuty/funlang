package edu.ktu.funlang.semantics.rules;

import edu.ktu.funlang.semantics.SemanticContext;
import edu.ktu.funlang.syntax.DiagnosticReporter;
import edu.ktu.funlang.syntax.FunLangBaseVisitor;
import edu.ktu.funlang.syntax.FunLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

public abstract class AbstractSemanticRule extends FunLangBaseVisitor<Void> implements Rule {
    protected DiagnosticReporter reporter;
    protected TokenStream tokens;
    protected SemanticContext ctx; // optional shared context (symbol table, etc.)

    @Override
    public void init(DiagnosticReporter reporter, TokenStream tokens) {
        this.reporter = reporter;
        this.tokens = tokens;
    }
    public void setContext(SemanticContext ctx) { this.ctx = ctx; }

    /* ---------- traverse tree ---------- */

    @Override
    public Void visitProgram(FunLangParser.ProgramContext ctx) {
        onVisitProgram(ctx);
        for (var s : ctx.statement()) visit(s);
        return null;
    }

    @Override
    public Void visitStatement(FunLangParser.StatementContext ctx) {
        onVisitStatement(ctx);
        return visitChildren(ctx);
    }

    @Override
    public Void visitLetDecl(FunLangParser.LetDeclContext ctx) {
        onVisitLetDecl(ctx);
        if (ctx.expr() != null) visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitAssignment(FunLangParser.AssignmentContext ctx) {
        onVisitAssignment(ctx);
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitPrintStmt(FunLangParser.PrintStmtContext ctx) {
        onVisitPrintStmt(ctx);
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitIfStmt(FunLangParser.IfStmtContext ctx) {
        onVisitIfStmt(ctx);
        visit(ctx.expr());
        visit(ctx.statement(0));
        if (ctx.statement().size() > 1) visit(ctx.statement(1));
        return null;
    }

    @Override
    public Void visitExpr(FunLangParser.ExprContext ctx) {
        onVisitExpr(ctx);
        return null; // expr: INT | ID – no children
    }

    /* ---------- helpers ---------- */

    protected void error(ParserRuleContext n, String msg) {
        var t = n.getStart();
        reporter.error(msg, t.getLine(), t.getCharPositionInLine());
    }
    protected void warn(ParserRuleContext n, String msg) {
        var t = n.getStart();
        reporter.warn(msg, t.getLine(), t.getCharPositionInLine());
    }

    /* ---------- hooks  ---------- */

    protected void onVisitProgram(FunLangParser.ProgramContext ctx) {}
    protected void onVisitStatement(FunLangParser.StatementContext ctx) {}
    protected void onVisitLetDecl(FunLangParser.LetDeclContext ctx) {}
    protected void onVisitAssignment(FunLangParser.AssignmentContext ctx) {}
    protected void onVisitPrintStmt(FunLangParser.PrintStmtContext ctx) {}
    protected void onVisitIfStmt(FunLangParser.IfStmtContext ctx) {}
    protected void onVisitExpr(FunLangParser.ExprContext ctx) {}
}