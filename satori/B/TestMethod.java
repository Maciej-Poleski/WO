package atm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Przechowuje "kod źródłowy" pojedyńczego przypadku testowegoł
 */
class TestMethod {
    int name;
    List<String> body = new ArrayList<>();

    TestMethod(int name) {
        this.name = name;
    }

    void addStatement(String stmt) {
        body.add(stmt);
    }

    void dump(PrintStream printStream) {
        printStream.println("@Test");
        printStream.println("public void test" + name + "() throws Exception {");
        for (String stmt : body) {
            printStream.print('\t');
            printStream.print(stmt);
            printStream.println(';');
        }
        printStream.println('}');
        printStream.println();
    }
}
