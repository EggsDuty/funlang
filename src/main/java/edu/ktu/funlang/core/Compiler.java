package edu.ktu.funlang.core;

import java.io.*;
import java.nio.file.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import edu.ktu.funlang.visitors.FunLangToJavaVisitor;
import edu.ktu.funlang.visitors.FunLangSemanticAnalyzer;
import edu.ktu.funlang.syntax.FunLangLexer;
import edu.ktu.funlang.syntax.FunLangParser;

public class Compiler {
    public static void compile(Path inputFile, Path outputDir) throws Exception {
        String source = Files.readString(inputFile);
        CharStream cs = CharStreams.fromString(source);
        FunLangLexer lexer = new FunLangLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunLangParser parser = new FunLangParser(tokens);
        ParseTree tree = parser.program();

        // 1) Semantic pass (collect symbols + basic checks)
        FunLangSemanticAnalyzer analyzer = new FunLangSemanticAnalyzer();
        analyzer.visit(tree);
        if (analyzer.hasErrors()) {
            System.err.println("Semantic errors found:");
            analyzer.getErrors().forEach(System.err::println);
            System.exit(2);
        }

        // 2) Generation pass
        FunLangToJavaVisitor visitor = new FunLangToJavaVisitor(tokens, analyzer.getSymbolTable());
        String javaOutput = visitor.visit(tree);

        // ensure out dir
        Files.createDirectories(outputDir);
        Path outFile = outputDir.resolve("FunProgram.java");
        Files.writeString(outFile, javaOutput);
        System.out.println("Wrote Java source to: " + outFile.toAbsolutePath());
    }
}


