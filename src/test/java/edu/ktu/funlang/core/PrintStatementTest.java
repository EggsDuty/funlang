package edu.ktu.funlang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrintStatementTest {

    @org.junit.Test
    void execute_singlePrintStatement_printsNumber() {
        String src = """
            print(42);
            """;

        String expected = """
            42
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_multiplePrintStatementsWithComments_printsNumbers() {
        String src = """
            //print one
            print(1);
            /* print two */
            print(2);
            print(3); //print three
            """;

        String expected = """
            1
            2
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    // NOTE - this should go to another tests class
    @Test
    void execute_letThenPrintVariable_printsValue() {
        String src = """
            let x = 42;
            print(x);
            """;
        String expected = """
            42
            """;
        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_letThenReassign_printsNewValue() {
        String src = """
            let x = 1;
            x = 5;
            print(x);
            """;

        String expected = """
            5
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_letThenAssign_printsValue() {
        String src = """
            let x;
            x = 5;
            print(x);
            """;

        String expected = """
            5
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_letOnly_printsDefaultValue() {
        String src = """
            let x;
            print(x);
            """;

        String expected = """
            0
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    // NOTE - this should definitely go to another tests class
    @Test
    void execute_variableInitializer_printsValue() {
        String src = """
            let x = 1;
            let y = x;
            print(y);
            """;

        String expected = """
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
