package agent;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.*;

public class DependencyScanner implements ClassFileTransformer {

    private static final Method adjustStackTraceMethod;
    private static final Method storeThrowableMethod;

    static {
        try {
            adjustStackTraceMethod = DependencyScanner.class.getDeclaredMethod("adjustStackTrace", Throwable.class);
            storeThrowableMethod = DependencyScanner.class.getDeclaredMethod("storeThrowable", Throwable.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    private static final Map<Throwable, Void> throwablesInCatchClause = Collections.synchronizedMap(new WeakHashMap<Throwable, Void>());

    public static void storeThrowable(Throwable throwable) {
        throwablesInCatchClause.put(throwable, null);
    }

    public static void adjustStackTrace(Throwable throwable) {
        if (throwablesInCatchClause.containsKey(throwable))
            return;
        throwable.fillInStackTrace();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        throwable.setStackTrace(Arrays.copyOfRange(stackTrace, 1, stackTrace.length));
    }

    @Override
    public byte[] transform(ClassLoader loader, final String name,
                            Class<?> clazz, ProtectionDomain domain, byte[] bytecode)
            throws IllegalClassFormatException {
        try {
            if (name.contains("asm")) {
                System.err.println(name);
                return null;
            }
            ClassReader classReader = new ClassReader(bytecode);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new ClassVisitor(Opcodes.ASM5, classWriter) {

                @Override
                public MethodVisitor visitMethod(int access, String name,
                                                 String desc, String signature, String[] exceptions) {
                    return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {

                        Map<Label, Label> catchTranslation = new HashMap<>();

                        @Override
                        public void visitLabel(Label label) {
                            if (label == null)
                                throw new AssertionError("label is null");
                            if (catchTranslation.containsKey(label)) {
                                super.visitLabel(catchTranslation.get(label));
                                catchTranslation.remove(label);
                                super.visitInsn(Opcodes.DUP);
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(DependencyScanner.class), storeThrowableMethod.getName(), Type.getMethodDescriptor(storeThrowableMethod), false);
                            }
                            super.visitLabel(label);
                        }

                        @Override
                        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                            Label newLabel = new Label();
                            catchTranslation.put(handler, newLabel);
                            super.visitTryCatchBlock(start, end, newLabel, type);
                        }

                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.ATHROW) {
                                super.visitInsn(Opcodes.DUP);
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(DependencyScanner.class), adjustStackTraceMethod.getName(), Type.getMethodDescriptor(adjustStackTraceMethod), false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }

            }, 0);
            return classWriter.toByteArray();
            // ClassReader
            // visit API
            // LocalVariableSorter
            // Verifier (Visitor)
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

}
