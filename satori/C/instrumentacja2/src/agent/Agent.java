package agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        try {
            instrumentation.addTransformer(new DependencyScanner(), true);
            // gather previously loaded (modifiable) classes
            ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (instrumentation.isModifiableClass(clazz))
                    classes.add(clazz);
            }
            // transform them all at once
            try {
                instrumentation.retransformClasses(classes.toArray(new Class<?>[0]));
            } catch (UnmodifiableClassException e) {
                // this should NOT happen
                throw new RuntimeException(e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

}
