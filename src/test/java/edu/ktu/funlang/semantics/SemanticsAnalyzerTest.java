package edu.ktu.funlang.semantics;

import edu.ktu.funlang.core.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticsAnalyzerTest {

    @Test
    void semantic_assignAfterLet_noError() {
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
    void semantic_assignBeforeLet_throwsError() {
        String src = """
            x = 10;
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Assignment to undeclared variable"));
    }

    @Test
    void semantic_assignMultipleUndeclaredVariables_throwsErrorForFirst() {
        String src = """
            x = 1;
            y = 2;
            print(x);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Assignment to undeclared variable 'x'"));
    }

    @Test
    void semantic_mixedDeclaredAndUndeclaredAssignments_detectsUndeclared() {
        String src = """
            let x = 1;
            y = 2;
            print(x);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Assignment to undeclared variable 'y'"));
    }

    @Test
    void semantic_printUndeclaredVariable_throwsError() {
        String src = """
            print(x);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Use of undeclared variable 'x'"));
    }

    @Test
    void semantic_ifConditionUsesUndeclaredVariable_throwsError() {
        String src = """
            if (x) print(1);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Use of undeclared variable 'x'"));
    }

    @Test
    void semantic_expressionReferencesUndeclaredVariable_throwsError() {
        String src = """
            let a = x;
            print(a);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Use of undeclared variable 'x'"));
    }

    @Test
    void semantic_undeclaredVariableInElseBranch_throwsError() {
        String src = """
            let a = 1;
            if (a) print(a); else print(b);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Use of undeclared variable 'b'"));
    }

    @Test
    void semantic_declaredAndUndeclaredMix_detectsUndeclaredOnly() {
        String src = """
            let a = 10;
            print(a);
            print(b);
            """;

        Exception ex = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(ex.getMessage().contains("Use of undeclared variable 'b'"));
    }
}
