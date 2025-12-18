package edu.ktu.funlang.app;

import edu.ktu.funlang.core.Compiler;
import java.nio.file.*;

public class CompilerMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java -jar FunLangCompiler.jar <input.fun> <output_dir>");
            System.exit(1);
        }
        Path input = Paths.get(args[0]);
        Path outDir = Paths.get(args[1]);
        Compiler.compile(input, outDir);
    }
}


