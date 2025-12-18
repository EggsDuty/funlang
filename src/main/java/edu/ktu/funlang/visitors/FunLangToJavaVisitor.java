package edu.ktu.funlang.visitors;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ParseTree;
import edu.ktu.funlang.syntax.FunLangBaseVisitor;
import edu.ktu.funlang.syntax.FunLangParser;
import edu.ktu.funlang.semantics.rules.*;
import java.util.*;

/**
 * A visitor that generates Java source code from the FunLang parse tree.
 * Strategy (two-pass): semantic analyzer run separately; this visitor focuses on emission.
 */
public class FunLangToJavaVisitor extends FunLangBaseVisitor<String> {
    private CodeBuilder cb = new CodeBuilder();
    private SymbolTable symbols;
    private TokenStream tokens;
    private int tmpCounter = 0;

    public FunLangToJavaVisitor(TokenStream tokens, SymbolTable symbols) {
        this.tokens = tokens;
        this.symbols = symbols;
    }

    @Override
    public String visitProgram(FunLangParser.ProgramContext ctx) {
        cb.wl("public class FunProgram {");
        cb.indent();
        cb.wl("public static void main(String[] args) {");
        cb.indent();

        symbols.enterScope();
        // emit top level statements (functions will be emitted after main)
        List<FunLangParser.StatementContext> funcs = new ArrayList<>();
        for (var s : ctx.statement()) {
            if (s.funcDef() != null) funcs.add(s);
            else visit(s);
        }

        cb.wl("// end of main");
        cb.outdent();
        cb.wl("}");

        // emit functions
        for (var f : funcs) cb.wl(visit(f));

        symbols.exitScope();
        cb.outdent();
        cb.wl("}");
        return cb.toString();
    }

    // Statement dispatch
    @Override
    public String visitStatement(FunLangParser.StatementContext ctx) {
        if (ctx.varDecl() != null) return visit(ctx.varDecl());
        if (ctx.assignment() != null) return visit(ctx.assignment());
        if (ctx.exprStmt() != null) return visit(ctx.exprStmt());
        if (ctx.systemCall() != null) return visit(ctx.systemCall());
        if (ctx.ifStmt() != null) return visit(ctx.ifStmt());
        if (ctx.whileStmt() != null) return visit(ctx.whileStmt());
        if (ctx.forStmt() != null) return visit(ctx.forStmt());
        if (ctx.funcDef() != null) return visit(ctx.funcDef());
        if (ctx.returnStmt() != null) return visit(ctx.returnStmt());
        if (ctx.block() != null) return visit(ctx.block());
        throw new RuntimeException("Unhandled statement: " + ctx.getText());
    }

    @Override
    public String visitBlock(FunLangParser.BlockContext ctx) {
        cb.wl("{");
        cb.indent();
        symbols.enterScope();
        for (var s : ctx.statement()) visit(s);
        symbols.exitScope();
        cb.outdent();
        cb.wl("}");
        return "";
    }

    @Override
    public String visitVarDecl(FunLangParser.VarDeclContext ctx) {
        String jType = mapType(ctx.type().getText());
        String name = ctx.ID().getText();
        String init = "";
        if (ctx.expr() != null) init = " = " + visit(ctx.expr());
        cb.wl(jType + " " + name + init + ";");
        return "";
    }

    @Override
    public String visitAssignment(FunLangParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        String val = visit(ctx.expr());
        cb.wl(name + " = " + val + ";");
        return "";
    }

    @Override
    public String visitExprStmt(FunLangParser.ExprStmtContext ctx) {
        String e = visit(ctx.expr());
        cb.wl(e + ";");
        return "";
    }

    @Override
    public String visitSystemCall(FunLangParser.SystemCallContext ctx) {
        String value = visit(ctx.expr());
        cb.wl("System.out.println(" + value + ");");
        return "";
    }


    @Override
    public String visitIfStmt(FunLangParser.IfStmtContext ctx) {

        // IF
        cb.wl("if (" + visit(ctx.expr(0)) + ") ");
        visit(ctx.block(0));

        int exprCount = ctx.expr().size();
        int blockCount = ctx.block().size();

        // ELSE IFs
        for (int i = 1; i < exprCount; i++) {
            cb.wl("else if (" + visit(ctx.expr(i)) + ") ");
            visit(ctx.block(i));
        }

        // ELSE
        if (blockCount > exprCount) {
            cb.wl("else ");
            visit(ctx.block(blockCount - 1));
        }

        return "";
    }


    private boolean hasFinalElse(FunLangParser.IfStmtContext ctx) {
        // our rule places ELSE block at the end if present
        return ctx.block().size() > 1 && ctx.getText().contains("ELSE") && !ctx.getText().contains("ELSEIF");
    }

    @Override
    public String visitWhileStmt(FunLangParser.WhileStmtContext ctx) {
        cb.wl("while (" + visit(ctx.expr()) + ") ");
        visit(ctx.block());
        return "";
    }

    @Override
    public String visitForStmt(FunLangParser.ForStmtContext ctx) {
        String id = ctx.ID().getText();
        String start = visit(ctx.expr(0));
        String end = visit(ctx.expr(1));
        String step = ctx.expr().size() > 2 ? visit(ctx.expr(2)) : "1";

        cb.wl("for (int " + id + " = " + start +
                "; " + id + " <= " + end +
                "; " + id + " += " + step + ") ");

        visit(ctx.block());
        return "";
    }



    @Override
    public String visitFuncDef(FunLangParser.FuncDefContext ctx) {
        String fname = ctx.ID().getText();
        StringBuilder sig = new StringBuilder();
        // return type omitted in grammar — default to double for now
        sig.append("public static double "+ fname +"(");
        List<String> params = new ArrayList<>();
        if (ctx.paramList() != null) {
            for (var p : ctx.paramList().param()) {
                String t = mapType(p.type().getText());
                String n = p.ID().getText();
                params.add(t + " " + n);
            }
        }
        sig.append(String.join(", ", params));
        sig.append(") ");
        cb.wl(sig.toString());
        visit(ctx.block());
        return "";
    }

    @Override
    public String visitReturnStmt(FunLangParser.ReturnStmtContext ctx) {
        cb.wl("return " + visit(ctx.expr()) + ";");
        return "";
    }

    // Expressions: improved handling
    @Override
    public String visitExpr(FunLangParser.ExprContext ctx) {
        if (ctx.chainExpr() != null) {
            return visit(ctx.chainExpr());
        }
        return "";
    }


    /*@Override
    public String visitChainExpr(FunLangParser.ChainExprContext ctx) {
        String left = visit(ctx.binaryExpr(0));
        for (int i = 1; i < ctx.binaryExpr().size(); i++) {
            String op = ctx.chainOp(i-1).getText();
            String rightExpr = visit(ctx.binaryExpr(i));
            if (op.equals("<=>")) {
                // wrap left in Value to simulate ref
                String tmp = makeTmp();
                cb.wl("Value<Double> " + tmp + " = new Value<>((double)" + left + ");");
                left = rightExpr.replaceFirst("\\)", tmp + ")"); // inject Value as param
            } else if (rightExpr.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\(.*\\)$")) {
                int idx = rightExpr.indexOf('(');
                String name = rightExpr.substring(0, idx);
                String inside = rightExpr.substring(idx+1, rightExpr.length()-1).trim();
                String newArgs = left + (inside.isEmpty() ? "" : ", " + inside);
                left = name + "(" + newArgs + ")";
            } else {
                String tmp = makeTmp();
                cb.wl("double " + tmp + " = " + left + ";");
                left = rightExpr;
            }
        }
        return left;
    }*/

    @Override
    public String visitChainExpr(FunLangParser.ChainExprContext ctx) {
        if (ctx.binaryExpr().size() == 1) {
            return visit(ctx.binaryExpr(0));
        }
        // handle chaining properly later
        String result = visit(ctx.binaryExpr(0));
        for (int i = 1; i < ctx.binaryExpr().size(); i++) {
            String op = ctx.chainOp(i-1).getText();
            String right = visit(ctx.binaryExpr(i));
            result = "(" + result + " " + mapOp(op) + " " + right + ")";
        }
        return result;
    }

    @Override
    public String visitBinaryExpr(FunLangParser.BinaryExprContext ctx) {
        if (ctx.basicExpr() != null) return visit(ctx.basicExpr());
        if (ctx.binaryExpr().size() == 2) {
            String left = visit(ctx.binaryExpr(0));
            String right = visit(ctx.binaryExpr(1));
            String op = ctx.getChild(1).getText();
            return "(" + left + " " + mapOp(op) + " " + right + ")";
        }
        return "";
    }


    @Override
    public String visitBasicExpr(FunLangParser.BasicExprContext ctx) {
        if (ctx.literal() != null) return visit(ctx.literal());
        if (ctx.ID() != null) return ctx.ID().getText();
        if (ctx.functionCall() != null) return visit(ctx.functionCall());
        if (ctx.expr() != null) return "(" + visit(ctx.expr()) + ")";
        return "";
    }

    @Override
    public String visitFunctionCall(FunLangParser.FunctionCallContext ctx) {
        String name = ctx.ID().getText();
        List<String> args = new ArrayList<>();

        if (ctx.argList() != null) {
            for (var a : ctx.argList().expr()) {
                args.add(visit(a));
            }
        }

        switch (name) {
            case "MIN":
                return "Math.min(" + args.get(0) + ", " + args.get(1) + ")";
            case "SQRT":
                return "Math.sqrt(" + args.get(0) + ")";
            default:
                return name + "(" + String.join(", ", args) + ")";
        }
    }


    @Override
    public String visitLiteral(FunLangParser.LiteralContext ctx) {
        if (ctx.INT() != null) return ctx.INT().getText();
        if (ctx.DECIMAL() != null) return ctx.DECIMAL().getText();
        if (ctx.STRING() != null) return ctx.STRING().getText();
        if (ctx.TRUE() != null) return "true";
        if (ctx.FALSE() != null) return "false";
        if (ctx.listLiteral() != null) {
            List<String> elems = new ArrayList<>();
            var el = ctx.listLiteral().expr();
            for (var e : el) elems.add(visit(e));
            return "new double[] {" + String.join(", ", elems) + "}";
        }
        return "0";
    }

    // Helpers
    private String visitChildrenAsString(ParseTree t) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < t.getChildCount(); i++) tmp.append(visit(t.getChild(i)));
        return tmp.toString();
    }
    private String mapType(String funType) {
        switch (funType) {
            case "integer": return "int";
            case "decimal": return "double";
            case "text": return "String";
            case "boolean": return "boolean";
            default:
                if (funType.startsWith("list")) return "double[]";
                return "double";
        }
    }
    private String mapOp(String op) {
        switch (op) {
            case "AND": return "&&";
            case "OR": return "||";
            case "<": return "<";
            case ">": return ">";
            case "<=": return "<=";
            case ">=": return ">=";
            case "==": return "==";
            case "!=": return "!=";
            default:
                return op;
        }
    }

    private String makeTmp() { return "__tmp" + (tmpCounter++); }
}

