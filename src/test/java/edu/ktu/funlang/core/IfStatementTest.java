package edu.ktu.funlang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfStatementTest {

    @Test
    void execute_ifWithTrueCondition_executesThenBranch() {
        String src = """
            let x = 1;
            if (x) print(42);
            """;

        String expected = """
            42
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifWithFalseCondition_skipsThenBranch() {
        String src = """
            let x = 0;
            if (x) print(42);
            """;

        String expected = """
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifElse_true_executesThenNotElse() {
        String src = """
            let x = 7;
            if (x) print(1); else print(2);
            """;

        String expected = """
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifElse_false_executesElse() {
        String src = """
            let x = 0;
            if (x) print(1); else print(2);
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_nestedIfs_threeVars_selectsThenElseThen_branch() {
        String src = """
            let a = 1;
            let b = 0;
            let c = 2;
            if (a)
                if (b)
                    if (c)
                        print(111);
                    else
                        print(112);
                else if (c)
                        print(121);
                     else
                        print(122);
            """;

        // a=1 (true) -> take THEN
        //   b=0 (false) -> take ELSE
        //     c=2 (true) -> take THEN -> print(121)
        String expected = """
            121
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}

