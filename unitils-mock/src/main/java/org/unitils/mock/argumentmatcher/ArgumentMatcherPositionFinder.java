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
import org.objectweb.asm.Type;
import static org.objectweb.asm.Type.getMethodDescriptor;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.proxy.ProxyInvocation;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.ReflectionUtils.getClassWithName;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
     * @param fromLineNr      The begin line-nr of the invocation
     * @param toLineNr        The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
     * @param index           The index of the matcher on that line, 1 for the first, 2 for the second etc
     * @return The argument indexes, empty if there are no matchers
     */
    public static List<Integer> getArgumentMatcherIndexes(ProxyInvocation proxyInvocation, int fromLineNr, int toLineNr, int index) {
        Class<?> testClass = getClassWithName(proxyInvocation.getInvokedAt().getClassName());
        String testMethodName = proxyInvocation.getInvokedAt().getMethodName();
        Method method = proxyInvocation.getMethod();

        return getArgumentMatcherIndexes(testClass, testMethodName, method, fromLineNr, toLineNr, index);
    }


    /**
     * Locates the argument matchers for the method invocation on the given line.
     * An exception is raised when the given method cannot be found.
     *
     * @param clazz         The class containing the method invocation, not null
     * @param methodName    The method containing the method invocation, not null
     * @param invokedMethod The invocation to look for, not null
     * @param fromLineNr    The begin line-nr of the invocation
     * @param toLineNr      The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
     * @param index         The index of the matcher on that line, 1 for the first, 2 for the second etc
     * @return The argument indexes, empty if there are no matchers
     */
    @SuppressWarnings({"unchecked"})
    public static List<Integer> getArgumentMatcherIndexes(Class<?> clazz, String methodName, Method invokedMethod, int fromLineNr, int toLineNr, int index) {
        // read the bytecode of the test class
        ClassNode restClassNode = readClass(clazz);

        // find the correct test method
        List<MethodNode> testMethodNodes = restClassNode.methods;
        for (MethodNode testMethodNode : testMethodNodes) {

            // another method with the same name may exist
            // if no result was found it could be that the line nr was for the other method, so continue with the search
            if (methodName.equals(testMethodNode.name)) {
                List<Integer> result = findArgumentMatcherIndexes(restClassNode, testMethodNode, clazz, methodName, invokedMethod, fromLineNr, toLineNr, index);
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
     * @param classNode             The class containing the method invocation, not null
     * @param methodNode            The method containing the method invocation, not null
     * @param interpretedClass      The current class, not null
     * @param interpretedMethodName The current method name, not null
     * @param invokedMethod         The invocation to look for, not null
     * @param fromLineNr            The begin line-nr of the invocation
     * @param toLineNr              The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
     * @param index                 The index of the matcher on that line, 1 for the first, 2 for the second etc
     * @return The argument indexes, null if method was not found, empty if method found but there are no matchers
     */
    protected static List<Integer> findArgumentMatcherIndexes(ClassNode classNode, MethodNode methodNode, Class<?> interpretedClass, String interpretedMethodName, Method invokedMethod, int fromLineNr, int toLineNr, int index) {
        String invokedMethodName = invokedMethod.getName();
        String invokedMethodDescriptor = getMethodDescriptor(invokedMethod);
        try {
            // analyze the instructions in the method
            MethodInterpreter methodInterpreter = new MethodInterpreter(interpretedClass, interpretedMethodName, invokedMethodName, invokedMethodDescriptor, fromLineNr, toLineNr, index);
            Analyzer analyzer = new MethodAnalyzer(methodNode, methodInterpreter);
            analyzer.analyze(classNode.name, methodNode);
            // retrieve the found matcher indexes, if any
            return methodInterpreter.getResultArgumentMatcherIndexes();

        } catch (AnalyzerException e) {
            if (e.getCause() instanceof UnitilsException) {
                throw (UnitilsException) e.getCause();
            }
            throw new UnitilsException("Unable to find argument matchers for method invocation. Method name: " + invokedMethodName + ", method description; " + invokedMethodDescriptor + ", line nr; " + fromLineNr, e);
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
     * an argument matcher we return an ArugmentMatcherValue. For other instructions an ArugmentMatcherValue is returned if
     * one of its operands was a an ArugmentMatcherValue. When the actual invoked method is found, we then just
     * have to look at the operands: if one of the operands is an ArugmentMatcherValue, we've found the index of the argument matcher.
     * <p/>
     * For example:<br>
     * mock.methodCall(0, gt(2))   would give<br>
     * 1) load 0<br>
     * .... stack ( NotAnArgumentMatcherValue )<br>
     * 2) load 2<br>
     * .... stack ( NotAnArgumentMatcherValue, NotAnArgumentMatcherValue)<br>
     * 3) invoke argment matcher (pops last operand)<br>
     * .... stack ( NotAnArgumentMatcherValue, ArgumentMatcherValue)<br>
     * 4) invoke mock method using last 2 operands => we've found an argument matcher as second operand
     */
    protected static class MethodInterpreter extends BasicInterpreter {


        protected Class<?> interpretedClass;

        protected String interpretedMethodName;

        /* The name of the method to look for */
        protected String invokedMethodName;

        /* The signature of the method to look for */
        protected String invokedMethodDescriptor;

        /* The line nrs between which the invocation can be found */
        protected int fromLineNr, toLineNr;

        protected int index;

        /* The line that is currently being analyzed */
        protected int currentLineNr = 0;

        protected int currentIndex = 1;

        protected Method currentMatcherMethod;

        protected Set<MethodInsnNode> handledMethodInsnNodes = new HashSet<MethodInsnNode>();

        /* The resulting indexes or null if method was not found */
        protected List<Integer> resultArgumentMatcherIndexes;


        /**
         * Creates an interpreter.
         *
         * @param interpretedClass        The current class, not null
         * @param interpretedMethodName   The current method name, not null
         * @param invokedMethodName       The method to look for, not null
         * @param invokedMethodDescriptor The signature of the method to look for, not null
         * @param fromLineNr              The begin line-nr of the invocation
         * @param toLineNr                The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
         * @param index                   The index of the matcher on that line, 1 for the first, 2 for the second etc
         */
        public MethodInterpreter(Class<?> interpretedClass, String interpretedMethodName, String invokedMethodName, String invokedMethodDescriptor, int fromLineNr, int toLineNr, int index) {
            this.interpretedClass = interpretedClass;
            this.interpretedMethodName = interpretedMethodName;
            this.invokedMethodName = invokedMethodName;
            this.invokedMethodDescriptor = invokedMethodDescriptor;
            this.fromLineNr = fromLineNr;
            this.toLineNr = toLineNr;
            this.index = index;
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


        @Override
        public BasicValue copyOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
            BasicValue resultValue = super.copyOperation(insn, value);
            return getValue(resultValue, value);
        }


        @Override
        public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
            BasicValue resultValue = super.unaryOperation(insn, value);
            return getValue(resultValue, value);
        }


        @Override
        public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2) throws AnalyzerException {
            BasicValue resultValue = super.binaryOperation(insn, value1, value2);
            return getValue(resultValue, value1, value2);
        }


        @Override
        public BasicValue ternaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2, BasicValue value3) throws AnalyzerException {
            BasicValue resultValue = super.ternaryOperation(insn, value1, value2, value3);
            return getValue(resultValue, value1, value2, value3);
        }


        /**
         * Handles an instruction of a method call.
         *
         * @param instructionNode The instruction
         * @param values          The operands
         * @return The merged values or an ArugmentMatcherValue if an argument matcher method was found
         */
        @Override
        @SuppressWarnings({"unchecked"})
        public BasicValue naryOperation(AbstractInsnNode instructionNode, List values) throws AnalyzerException {
            BasicValue resultValue = super.naryOperation(instructionNode, values);

            if (!(instructionNode instanceof MethodInsnNode)) {
                return getValue(resultValue, values);
            }

            // check whether we are interested in the instruction
            MethodInsnNode methodInsnNode = (MethodInsnNode) instructionNode;
            if (instructionOutOfRange() || instructionAlreadyHandled(methodInsnNode)) {
                return getValue(resultValue, values);
            }

            if (isInvokedMethod(methodInsnNode)) {
                if (currentIndex++ != index) {
                    return getValue(resultValue, values);
                }
                if (resultArgumentMatcherIndexes != null) {
                    throwUnitilsException("Method invocation occurs more than once within the same clause. Method name: " + invokedMethodName);
                }
                // we've found the method, now check which operands are argument matchers
                resultArgumentMatcherIndexes = getArgumentMatcherIndexes(methodInsnNode, values);
                currentMatcherMethod = null;
                return createArgumentMatcherValue(resultValue);
            }

            Method method = getMethod(methodInsnNode);
            if (method != null) {
                // check whether the method is a match statement
                if (isMatcherMethod(method)) {
                    currentMatcherMethod = method;
                }
                // check whether the method is an argument matcher (i.e. has the @ArgumentMatcher annotation)
                if (isArgumentMatcherMethod(method)) {
                    if (currentMatcherMethod == null) {
                        throwUnitilsException("An argument matcher cannot be used outside the context of a match statement.");
                    }
                    // we've found an argument matcher
                    return createArgumentMatcherValue(resultValue);
                }
            }

            // nothing special found
            return getValue(resultValue, values);
        }


        protected boolean instructionOutOfRange() {
            return currentLineNr < fromLineNr || toLineNr < currentLineNr;
        }


        protected boolean instructionAlreadyHandled(MethodInsnNode methodInsnNode) {
            return !handledMethodInsnNodes.add(methodInsnNode);
        }


        protected boolean isInvokedMethod(MethodInsnNode methodInsnNode) {
            return invokedMethodName.equals(methodInsnNode.name) && invokedMethodDescriptor.equals(methodInsnNode.desc);
        }


        protected List<Integer> getArgumentMatcherIndexes(MethodInsnNode methodInsnNode, List values) {
            List<Integer> result = new ArrayList<Integer>();

            // for non-static invocations the first operand is always 'this'
            boolean isStatic = methodInsnNode.getOpcode() == INVOKESTATIC;
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) instanceof ArgumentMatcherValue) {
                    result.add(isStatic ? i : i - 1);
                }
            }
            return result;
        }


        protected boolean isMatcherMethod(Method method) {
            return method.getAnnotation(MatchStatement.class) != null;
        }


        protected boolean isArgumentMatcherMethod(Method method) {
            return method.getAnnotation(ArgumentMatcher.class) != null;
        }


        /**
         * Throws a {@link org.unitils.core.UnitilsException} with the given error message. The stacktrace is modified, to make
         * it point to the line of code that was analyzed by this class.
         *
         * @param errorMessage The error message
         */
        protected void throwUnitilsException(String errorMessage) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(errorMessage);
            stringBuilder.append("\n at ");
            stringBuilder.append(new StackTraceElement(interpretedClass.getName(), interpretedMethodName, interpretedClass.getName(), currentLineNr).toString());
            stringBuilder.append("\n");
            throw new UnitilsException(stringBuilder.toString());
        }


        /**
         * Merges two values.
         *
         * @param value1 The first value
         * @param value2 The second value
         * @return The merged value
         */
        @Override
        public BasicValue merge(BasicValue value1, BasicValue value2) {
            BasicValue resultValue = super.merge(value1, value2);
            if (value1 instanceof ArgumentMatcherValue || value2 instanceof ArgumentMatcherValue) {
                return createArgumentMatcherValue(resultValue);
            }
            return resultValue;
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
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName()) && methodDescriptor.equals(getMethodDescriptor(method))) {
                    return method;
                }
            }
            return null;
        }


        /**
         * @param resultValue The result value
         * @param values      The values that can be ArgumentMatcherValues
         * @return The result value, or a ArgumentMatcherValue of the same type if one of the values is an ArgumentMatcherValue
         */
        protected BasicValue getValue(BasicValue resultValue, BasicValue... values) {
            if (values != null) {
                for (BasicValue value : values) {
                    if (value instanceof ArgumentMatcherValue) {
                        return createArgumentMatcherValue(resultValue);
                    }
                }
            }
            return resultValue;
        }


        /**
         * @param resultValue The result value
         * @param values      The values that can be ArgumentMatcherValues
         * @return The result value, or a ArgumentMatcherValue of the same type if one of the values is an ArgumentMatcherValue
         */
        protected BasicValue getValue(BasicValue resultValue, List<BasicValue> values) {
            int nrOfArgumentMatcherValues = getNrOfArgumentMacherValues(values);

            if (nrOfArgumentMatcherValues > 1) {
                throwUnitilsException("An argument matcher cannot be used in an expression.");
            }
            if (nrOfArgumentMatcherValues == 1) {
                return createArgumentMatcherValue(resultValue);
            }
            return resultValue;
        }


        /**
         * @param values The values that can be ArgumentMatcherValues
         * @return The nr of values that are an ArgumentMatcherValue
         */
        protected int getNrOfArgumentMacherValues(List<BasicValue> values) {
            if (values == null) {
                return 0;
            }

            int count = 0;
            for (BasicValue value : values) {
                if (value instanceof ArgumentMatcherValue) {
                    count++;
                }
            }
            return count;
        }


        /**
         * @param resultValue The result value
         * @return An ArgumentMatcherValue of the same type
         */
        protected ArgumentMatcherValue createArgumentMatcherValue(BasicValue resultValue) {
            if (resultValue == null) {
                return new ArgumentMatcherValue(null);
            }
            Type type = ((BasicValue) resultValue).getType();
            return new ArgumentMatcherValue(type);
        }
    }

    /**
     * A value representing a found argument matcher invocation
     */
    protected static class ArgumentMatcherValue extends BasicValue {

        public ArgumentMatcherValue(Type type) {
            super(type);
        }
    }

}