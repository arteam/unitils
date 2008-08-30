/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock.argumentmatcher;

import org.objectweb.asm.ClassReader;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Type.getMethodDescriptor;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.proxy.ProxyInvocation;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.ReflectionUtils.getClassWithName;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for locating argument matchers in method invocations.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class ArgumentMatcherPositionFinder {


    /**
     * Locates the argument matchers for the given proxy method invocation.
     *
     * @param proxyInvocation The method invocation, not null
     * @return The argument indexes, empty if there are no matchers
     */
    public static List<Integer> getArgumentMatcherIndexes(ProxyInvocation proxyInvocation) {
        Class<?> testClass = getClassWithName(proxyInvocation.getInvokedAt().getClassName());
        String testMethodName = proxyInvocation.getInvokedAt().getMethodName();
        Method method = proxyInvocation.getMethod();
        int lineNr = proxyInvocation.getInvokedAt().getLineNumber();

        return getArgumentMatcherIndexes(testClass, testMethodName, method, lineNr, 1);
    }


    /**
     * Locates the argument matchers for the method invocation on the given line.
     * An exception is raised when the given method cannot be found.
     *
     * @param clazz           The class containing the method invocation, not null
     * @param methodName      The method containing the method invocation, not null
     * @param invokedMethod   The invocation to look for, not null
     * @param lineNr          The line nr of the invocation
     * @param invocationIndex The index, in case there is more than one invocation on the same line
     * @return The argument indexes, empty if there are no matchers
     */
    @SuppressWarnings({"unchecked"})
    public static List<Integer> getArgumentMatcherIndexes(Class<?> clazz, String methodName, Method invokedMethod, int lineNr, int invocationIndex) {
        // read the bytecode of the test class
        ClassNode restClassNode = readClass(clazz);

        // find the correct test method
        List<MethodNode> testMethodNodes = restClassNode.methods;
        for (final MethodNode testMethodNode : testMethodNodes) {

            // another method with the same name may exist
            // if no result was found it could be that the line nr was for the other method, so continue with the search
            if (methodName.equals(testMethodNode.name)) {
                List<Integer> result = findArgumentMatcherIndexes(restClassNode, testMethodNode, invokedMethod, lineNr, invocationIndex);
                if (result != null) {
                    return result;
                }
            }
        }
        throw new UnitilsException("Unable to find indexes of argument matcher. Method not found: " + methodName);
    }


    /**
     * Uses ASM to read the byte code of the given class. This will access the class file and create some sort
     * of DOM tree for the structure of the bytecode.
     *
     * @param clazz The class to read, not null
     * @return The structure of the class, not null
     */
    protected static ClassNode readClass(Class<?> clazz) {
        InputStream inputStream = null;
        try {
            inputStream = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class");

            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            return classNode;

        } catch (Exception e) {
            throw new UnitilsException("Unable to read class file for " + clazz, e);
        } finally {
            closeQuietly(inputStream);
        }
    }


    /**
     * Locates the argument matchers for the method invocation on the given line.
     *
     * @param classNode       The class containing the method invocation, not null
     * @param methodNode      The method containing the method invocation, not null
     * @param invokedMethod   The invocation to look for, not null
     * @param lineNr          The line nr of the invocation
     * @param invocationIndex The index, in case there is more than one invocation on the same line
     * @return The argument indexes, null if method was not found, empty if method found but there are no matchers
     */
    protected static List<Integer> findArgumentMatcherIndexes(ClassNode classNode, MethodNode methodNode, Method invokedMethod, int lineNr, int invocationIndex) {
        String invokedMethodName = invokedMethod.getName();
        String invokedMethodDescriptor = getMethodDescriptor(invokedMethod);
        try {
            // analyze the instructions in the method
            MethodInterpreter methodInterpreter = new MethodInterpreter(invokedMethodName, invokedMethodDescriptor, lineNr, invocationIndex);
            Analyzer analyzer = new MethodAnalyzer(methodNode, methodInterpreter);
            analyzer.analyze(classNode.name, methodNode);
            // retrieve the found matcher indexes, if any
            return methodInterpreter.getResultArgumentMatcherIndexes();

        } catch (AnalyzerException e) {
            throw new UnitilsException("Unable to find argument matchers for method invocation. Method name: " + invokedMethodName + ", method description; " + invokedMethodDescriptor + ", line nr; " + lineNr + ", invocation index: " + invocationIndex, e);
        }
    }


    /**
     * Analyzer that passes the line nrs to the given interpreter.
     * By default an analyzer filters out the line number instructions. This analyzer intercepts these instructions and
     * sets the current line nr on the interpreter.
     */
    protected static class MethodAnalyzer extends Analyzer {

        /* The method to analyze */
        protected MethodNode methodNode;

        /* The interpreter to use during the analysis */
        protected MethodInterpreter methodInterpreter;


        /**
         * Creates an analyzer.
         *
         * @param methodNode        The method to analyze, not null
         * @param methodInterpreter The interpreter to use during the analysis, not null
         */
        public MethodAnalyzer(MethodNode methodNode, MethodInterpreter methodInterpreter) {
            super(methodInterpreter);
            this.methodNode = methodNode;
            this.methodInterpreter = methodInterpreter;
        }


        /**
         * Overridden to handle the line number instructions.
         *
         * @param instructionIndex     The current index
         * @param nextInstructionIndex The next index
         */
        protected void newControlFlowEdge(int instructionIndex, int nextInstructionIndex) {
            AbstractInsnNode insnNode = methodNode.instructions.get(instructionIndex);
            if (insnNode instanceof LineNumberNode) {
                LineNumberNode lineNumberNode = (LineNumberNode) insnNode;
                methodInterpreter.setCurrentLineNr(lineNumberNode.line);
            }
        }
    }


    /**
     * Interpreter that implements the argument matcher finder behavior.
     * The analyzer simulates the processing of instructions by the VM and calls methods on this class to determine the
     * result of the processing of an instruction. During this processing, the analyzer simulates the maintenance of
     * the operand stack. For example:
     * <p/>
     * Suppose you have following statement: 1 + 2
     * The analyzer will first simulate the instruction to load constant 1 on the operand stack,
     * then it does the same for constant 2, finally it simulates the sum instruction on both operands, removes both
     * operands from the operand stack and puts the result back on the stack.
     * <p/>
     * All these instructions will pass through this interpreter to determine the result values to put on the
     * operand stack.
     * <p/>
     * This interpreter works as follows to find the argument matchers: if a method call instruction is found that is
     * an argument matcher we always return the {@link #ARGUMENT_MATCHER} value. For other instructions
     * a {@link #REGULAR_VALUE} is returned, unless one of its operands is a an {@link #ARGUMENT_MATCHER} value, in
     * that case an {@link #ARGUMENT_MATCHER} value is returned.
     * <p/>
     * When the actual invoked mehod is found, we then just have to look at the operands: if one of the operands in
     * a {@link #ARGUMENT_MATCHER} value, we've found the index of the argument matcher.
     * <p/>
     * For example:<br>
     * mock.methodCall(0, gt(2))   would give<br>
     * 1) load 0<br>
     * .... stack ( REGULAR_VALUE )<br>
     * 2) load 2<br>
     * .... stack ( REGULAR_VALUE, REGULAR_VALUE)<br>
     * 3) invoke argment matcher (pops last operand)<br>
     * .... stack ( REGULAR_VALUE, ARGUMENT_MATCHER)<br>
     * 4) invoke mock method using last 2 operands => we've found an argument matcher as second operand
     */
    protected static class MethodInterpreter implements Interpreter {

        /* The value to use for non-argument matcher operands */
        protected static final Value REGULAR_VALUE = BasicValue.UNINITIALIZED_VALUE;

        /* The value to use for argument matcher operands */
        protected static final Value ARGUMENT_MATCHER = BasicValue.REFERENCE_VALUE;

        /* The name of the method to look for */
        protected String invokedMethodName;

        /* The signature of the method to look for */
        protected String invokedMethodDescriptor;

        /* The line nr of the invocation */
        protected int lineNr;

        /* The index, in case there is more than one invocation on the same line */
        protected int invocationIndex;

        /* The line that is currently being analyzed */
        protected int currentLineNr = 0;

        /* The current invocation index */
        protected int currentInvocationIndex = 0;

        /* The resulting indexes or null if method was not found */
        protected List<Integer> resultArgumentMatcherIndexes;


        /**
         * Creates an interpreter.
         *
         * @param invokedMethodName       The method to look for, not null
         * @param invokedMethodDescriptor The signature of the method to look for, not null
         * @param lineNr                  The line nr of the invocation
         * @param invocationIndex         The index, in case there is more than one invocation on the same line
         */
        public MethodInterpreter(String invokedMethodName, String invokedMethodDescriptor, int lineNr, int invocationIndex) {
            this.invokedMethodName = invokedMethodName;
            this.invokedMethodDescriptor = invokedMethodDescriptor;
            this.lineNr = lineNr;
            this.invocationIndex = invocationIndex;
        }


        /**
         * Gets the result after the analysis was performed.
         *
         * @return The argument indexes, null if method was not found, empty if method found but there are no matchers
         */
        public List<Integer> getResultArgumentMatcherIndexes() {
            return resultArgumentMatcherIndexes;
        }


        /**
         * Sets the line nr that is being analyzed.
         *
         * @param currentLineNr The line nr
         */
        public void setCurrentLineNr(int currentLineNr) {
            this.currentLineNr = currentLineNr;
        }


        /**
         * Handles new values.
         *
         * @param type The type of the new value
         * @return A {@link #REGULAR_VALUE}
         */
        public Value newValue(Type type) {
            return REGULAR_VALUE;
        }


        /**
         * Handles an instruction without operands.
         *
         * @param instructionNode The instruction
         * @return A {@link #REGULAR_VALUE}
         */
        public Value newOperation(AbstractInsnNode instructionNode) throws AnalyzerException {
            return REGULAR_VALUE;
        }


        /**
         * Handles push or pop value on stack instruction.
         *
         * @param instructionNode The instruction
         * @param value           The operand
         * @return The same value
         */
        public Value copyOperation(AbstractInsnNode instructionNode, Value value) throws AnalyzerException {
            return value;
        }


        /**
         * Handles an instruction with 1 operand.
         *
         * @param instructionNode The instruction
         * @param value           The operand
         * @return The same value
         */
        public Value unaryOperation(AbstractInsnNode instructionNode, Value value) throws AnalyzerException {
            return value;
        }


        /**
         * Handles an instruction with 2 operands.
         *
         * @param instructionNode The instruction
         * @param value1          The first operand
         * @param value2          The second operand
         * @return The merged values
         */
        public Value binaryOperation(AbstractInsnNode instructionNode, Value value1, Value value2) throws AnalyzerException {
            return mergeValues(value1, value2);
        }


        /**
         * Handles an instruction with 3 operands.
         *
         * @param instructionNode The instruction
         * @param value1          The first operand
         * @param value2          The second operand
         * @param value3          The third operand
         * @return The merged values
         */
        public Value ternaryOperation(AbstractInsnNode instructionNode, Value value1, Value value2, Value value3) throws AnalyzerException {
            return mergeValues(value1, value2, value3);
        }


        /**
         * Handles an instruction of a method call.
         *
         * @param instructionNode The instruction
         * @param values          The operands
         * @return The merged values or an {@link #ARGUMENT_MATCHER} value if an argument matcher method was found
         */
        @SuppressWarnings({"unchecked"})
        public Value naryOperation(AbstractInsnNode instructionNode, List values) throws AnalyzerException {
            // check whether we are on the line we're interested in
            if (currentLineNr != lineNr) {
                return mergeValues(values);
            }
            // check wheter its a method call
            if (!(instructionNode instanceof MethodInsnNode)) {
                return mergeValues(values);
            }

            // check whether it's the method we're interested in
            MethodInsnNode methodInsnNode = (MethodInsnNode) instructionNode;
            if (invokedMethodName.equals(methodInsnNode.name) && invokedMethodDescriptor.equals(methodInsnNode.desc)) {
                currentInvocationIndex++;
                // check whether it's the correct invocation (in case the method is called more than once on the same line)
                if (currentInvocationIndex != invocationIndex) {
                    return mergeValues(values);
                }

                // we've found the method, now check which operands are argument matchers
                // for non-static invocations the first operand is always 'this'
                boolean isStatic = methodInsnNode.getOpcode() == INVOKESTATIC;
                resultArgumentMatcherIndexes = new ArrayList<Integer>();
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) == ARGUMENT_MATCHER) {
                        resultArgumentMatcherIndexes.add(isStatic ? i : i - 1);
                    }
                }
                return mergeValues(values);
            }

            // check whether the method is an argument matcher (i.e. has the @ArgumentMatcher annotation)
            Method matcherMethod = getMethod(methodInsnNode);
            if (matcherMethod != null) {
                if (matcherMethod.getAnnotation(ArgumentMatcher.class) != null) {
                    // we've found an argument matcher
                    return ARGUMENT_MATCHER;
                }
            }

            // nothing special found
            return mergeValues(values);
        }


        /**
         * Merges to values.
         *
         * @param value1 The first value
         * @param value2 The second value
         * @return The merged value
         */
        public Value merge(Value value1, Value value2) {
            return mergeValues(value1, value2);
        }


        /**
         * Utility method to return an argument matcher value when one of the values is an arguement matcher,
         * otherwhise a regular value is returned.
         *
         * @param values The values
         * @return argument matcher/regular value value
         */
        protected Value mergeValues(Value... values) {
            for (Value value : values) {
                if (value == ARGUMENT_MATCHER) {
                    return ARGUMENT_MATCHER;
                }
            }
            return REGULAR_VALUE;
        }


        /**
         * Utility method to return an argument matcher value when one of the values is an arguement matcher,
         * otherwhise a regular value is returned.
         *
         * @param values The values
         * @return argument matcher/regular value value
         */
        protected Value mergeValues(List<Value> values) {
            for (Value value : values) {
                if (value == ARGUMENT_MATCHER) {
                    return ARGUMENT_MATCHER;
                }
            }
            return REGULAR_VALUE;
        }


        /**
         * Finds a method using the ASM method node
         *
         * @param methodNode The ASM method node, not null
         * @return The method, null if not found
         */
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

