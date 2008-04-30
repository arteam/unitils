/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.mock.core;

import org.objectweb.asm.ClassReader;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Type.getMethodDescriptor;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.ArgumentMatcher;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import org.unitils.util.ReflectionUtils;
import static org.unitils.util.ReflectionUtils.getClassWithName;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * todo javadoc
 */
public class ArgumentMatcherPositionFinder {

    public static void main(String[] args) throws Exception {
        //finder.getMethod(ReflectionUtils.getMethod(TestClass.class, "someMethod", false, Integer.TYPE, Integer.TYPE, Integer.class), 46, 1);
        //Method testMethod = ReflectionUtils.getMethod(TestClass.class, "test", false);
        Method mockMethod = ReflectionUtils.getMethod(TestClass.class, "someMethod", false, String.class, String.class, String.class);
        List<Integer> indexes = ArgumentMatcherPositionFinder.getArgumentMatcherIndexes("test", TestClass.class, mockMethod, 54, 1);

        System.out.println("" + indexes);
    }


    public class TestClass {

        public void test() {
            someMethod("1000", not0(String.class), "");
            someMethod(not0(String.class), "new Integer(0)", not0(String.class));
            someMethod("1000", not0(String.class), "new Integer(0)");
        }

        public void someMethod(String value1, String value2, String value3) {
        }
    }

    @ArgumentMatcher
    public static <T> T not0(Class<T> clazz) {
        return null;
    }


    @SuppressWarnings({"unchecked"})
    public static List<Integer> getArgumentMatcherIndexes(String invokerMethodName, Class<?> invokerClass, Method mockMethod, int methodLineNr, int methodInvocationIndex) {
        // get the test method info
        String mockMethodName = mockMethod.getName();
        String mockMethodDescriptor = getMethodDescriptor(mockMethod);

        InputStream inputStream = null;
        try {
            inputStream = invokerClass.getClassLoader().getResourceAsStream(invokerClass.getName().replace('.', '/') + ".class");

            ClassReader cr = new ClassReader(inputStream);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);

            List<MethodNode> methods = cn.methods;
            for (final MethodNode methodNode : methods) {
                if (invokerMethodName.equals(methodNode.name)) {
                    MethodInterpreter methodInterpreter = new MethodInterpreter(mockMethodName, mockMethodDescriptor, methodLineNr, methodInvocationIndex);
                    Analyzer analyzer = new MethodAnalyzer(methodNode, methodInterpreter);
                    analyzer.analyze(cn.name, methodNode);
                    return methodInterpreter.getResultArgumentMatcherIndexes();
                }
            }
            return null;

        } catch (Exception e) {
            throw new UnitilsException("Unable to read class file for method: " + invokerMethodName, e);
        } finally {
            closeQuietly(inputStream);
        }
    }

    protected static class MethodAnalyzer extends Analyzer {

        private MethodNode methodNode;

        private MethodInterpreter methodInterpreter;

        public MethodAnalyzer(MethodNode methodNode, MethodInterpreter methodInterpreter) {
            super(methodInterpreter);
            this.methodNode = methodNode;
            this.methodInterpreter = methodInterpreter;
        }


        protected void newControlFlowEdge(int insn, int successor) {
            AbstractInsnNode insnNode = methodNode.instructions.get(insn);
            if (insnNode instanceof LineNumberNode) {
                LineNumberNode lineNumberNode = (LineNumberNode) insnNode;
                methodInterpreter.setCurrentLineNr(lineNumberNode.line);
            }
        }

    }


    protected static class MethodInterpreter implements Interpreter {

        protected final Value REGULAR_VALUE = BasicValue.UNINITIALIZED_VALUE;

        protected final Value ARGUMENT_MATCHER = BasicValue.REFERENCE_VALUE;

        private String methodName;
        private String methodDescriptor;

        private int methodLineNr;
        private int methodInvocationIndex;

        private int currentLineNr = 0;
        private int currentInvocationIndex = 0;

        private List<Integer> resultArgumentMatcherIndexes;


        public MethodInterpreter(String methodName, String methodDescriptor, int methodLineNr, int methodInvocationIndex) {
            this.methodName = methodName;
            this.methodDescriptor = methodDescriptor;
            this.methodLineNr = methodLineNr;
            this.methodInvocationIndex = methodInvocationIndex;
        }


        public List<Integer> getResultArgumentMatcherIndexes() {
            return resultArgumentMatcherIndexes;
        }

        public void setCurrentLineNr(int currentLineNr) {
            this.currentLineNr = currentLineNr;
        }


        public Value newValue(Type type) {
            return REGULAR_VALUE;
        }

        public Value newOperation(AbstractInsnNode insn) throws AnalyzerException {
            return REGULAR_VALUE;
        }

        public Value copyOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
            return REGULAR_VALUE;
        }

        public Value unaryOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
            return REGULAR_VALUE;
        }

        public Value binaryOperation(AbstractInsnNode insn, Value value1, Value value2) throws AnalyzerException {
            return REGULAR_VALUE;
        }

        public Value ternaryOperation(AbstractInsnNode insn, Value value1, Value value2, Value value3) throws AnalyzerException {
            return REGULAR_VALUE;
        }

        public Value naryOperation(AbstractInsnNode insn, List values) throws AnalyzerException {
            if (currentLineNr != methodLineNr) {
                return REGULAR_VALUE;
            }
            if (!(insn instanceof MethodInsnNode)) {
                return REGULAR_VALUE;
            }

            MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
            if (methodName.equals(methodInsnNode.name) && methodDescriptor.equals(methodInsnNode.desc)) {
                currentInvocationIndex++;
                if (currentInvocationIndex != methodInvocationIndex) {
                    return REGULAR_VALUE;
                }

                boolean isStatic = methodInsnNode.getOpcode() == INVOKESTATIC;
                resultArgumentMatcherIndexes = new ArrayList<Integer>();
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) == BasicValue.REFERENCE_VALUE) {
                        resultArgumentMatcherIndexes.add(isStatic ? i - 1 : i);
                    }
                }
                return REGULAR_VALUE;
            }

            Method matcherMethod = getMethod(methodInsnNode);
            if (matcherMethod != null) {
                if (matcherMethod.getAnnotation(ArgumentMatcher.class) != null) {
                    return ARGUMENT_MATCHER;
                }
            }
            return REGULAR_VALUE;
        }


        public Value merge(Value v, Value w) {
            if (!v.equals(w)) {
                return REGULAR_VALUE;
            }
            return v;
        }


        protected Method getMethod(MethodInsnNode methodNode) {
            String internalClassName = methodNode.owner;
            String className = internalClassName.replace('/', '.');
            String methodName = methodNode.name;
            String methodDescriptor = methodNode.desc;

            Class<?> clazz = getClassWithName(className);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName()) && methodDescriptor.equals(getMethodDescriptor(method))) {
                    return method;
                }
            }
            return null;
        }
    }


}

