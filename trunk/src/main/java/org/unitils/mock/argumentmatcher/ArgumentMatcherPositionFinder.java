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
import org.unitils.mock.proxy.ProxyInvocation;
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
     * @return The argument indexes, empty if there are no matchers
     */
    public static List<Integer> getArgumentMatcherIndexes(ProxyInvocation proxyInvocation, int fromLineNr, int toLineNr) {
        Class<?> testClass = getClassWithName(proxyInvocation.getInvokedAt().getClassName());
        String testMethodName = proxyInvocation.getInvokedAt().getMethodName();
        Method method = proxyInvocation.getMethod();

        return getArgumentMatcherIndexes(testClass, testMethodName, method, fromLineNr, toLineNr);
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
     * @return The argument indexes, empty if there are no matchers
     */
    @SuppressWarnings({"unchecked"})
    public static List<Integer> getArgumentMatcherIndexes(Class<?> clazz, String methodName, Method invokedMethod, int fromLineNr, int toLineNr) {
        // read the bytecode of the test class
        ClassNode restClassNode = readClass(clazz);

        // find the correct test method
        List<MethodNode> testMethodNodes = restClassNode.methods;
        for (MethodNode testMethodNode : testMethodNodes) {

            // another method with the same name may exist
            // if no result was found it could be that the line nr was for the other method, so continue with the search
            if (methodName.equals(testMethodNode.name)) {
                List<Integer> result = findArgumentMatcherIndexes(restClassNode, testMethodNode, clazz, methodName, invokedMethod, fromLineNr, toLineNr);
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
     * @return The argument indexes, null if method was not found, empty if method found but there are no matchers
     */
    protected static List<Integer> findArgumentMatcherIndexes(ClassNode classNode, MethodNode methodNode, Class<?> interpretedClass, String interpretedMethodName, Method invokedMethod, int fromLineNr, int toLineNr) {
        String invokedMethodName = invokedMethod.getName();
        String invokedMethodDescriptor = getMethodDescriptor(invokedMethod);
        try {
            // analyze the instructions in the method
            MethodInterpreter methodInterpreter = new MethodInterpreter(interpretedClass, interpretedMethodName, invokedMethodName, invokedMethodDescriptor, fromLineNr, toLineNr);
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

        /* The line that is currently being analyzed */
        protected int currentLineNr = 0;

        protected boolean currentlyInMatchStatement = false;

        protected boolean matchStatementFound;

        /* The resulting indexes or null if method was not found */
        protected List<Integer> resultArgumentMatcherIndexes;

        /* The line nrs that were already treated */
        protected Set<Integer> lineNrs = new HashSet<Integer>();

        /* True if the current line was already processed and should be skipped */
        protected boolean lineAlreaydProcessed;


        /**
         * Creates an interpreter.
         *
         * @param interpretedClass        The current class, not null
         * @param interpretedMethodName   The current method name, not null
         * @param invokedMethodName       The method to look for, not null
         * @param invokedMethodDescriptor The signature of the method to look for, not null
         * @param fromLineNr              The begin line-nr of the invocation
         * @param toLineNr                The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
         */
        public MethodInterpreter(Class<?> interpretedClass, String interpretedMethodName, String invokedMethodName, String invokedMethodDescriptor, int fromLineNr, int toLineNr) {
            this.interpretedClass = interpretedClass;
            this.interpretedMethodName = interpretedMethodName;
            this.invokedMethodName = invokedMethodName;
            this.invokedMethodDescriptor = invokedMethodDescriptor;
            this.fromLineNr = fromLineNr;
            this.toLineNr = toLineNr;
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
            lineAlreaydProcessed = lineNrs.contains(currentLineNr);
            lineNrs.add(currentLineNr);
        }


        @Override
        public Value copyOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
            Value resultValue = super.copyOperation(insn, value);
            return getValue(resultValue, value);
        }


        @Override
        public Value unaryOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
            Value resultValue = super.unaryOperation(insn, value);
            return getValue(resultValue, value);
        }


        @Override
        public Value binaryOperation(AbstractInsnNode insn, Value value1, Value value2) throws AnalyzerException {
            Value resultValue = super.binaryOperation(insn, value1, value2);
            return getValue(resultValue, value1, value2);
        }


        @Override
        public Value ternaryOperation(AbstractInsnNode insn, Value value1, Value value2, Value value3) throws AnalyzerException {
            Value resultValue = super.ternaryOperation(insn, value1, value2, value3);
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
        public Value naryOperation(AbstractInsnNode instructionNode, List values) throws AnalyzerException {
            Value resultValue = super.naryOperation(instructionNode, values);

            // skip if the line was already processed
            if (lineAlreaydProcessed) {
                return getValue(resultValue, values);
            }
            // check whether we are on the line we're interested in
            if (currentLineNr < fromLineNr || currentLineNr > toLineNr) {
                return getValue(resultValue, values);
            }
            // check wheter its a method call
            if (!(instructionNode instanceof MethodInsnNode)) {
                return getValue(resultValue, values);
            }

            // check whether it's the method we're interested in
            MethodInsnNode methodInsnNode = (MethodInsnNode) instructionNode;
            if (invokedMethodName.equals(methodInsnNode.name) && invokedMethodDescriptor.equals(methodInsnNode.desc)) {
                if (!currentlyInMatchStatement) {
                    throwUnitilsException("Method invocation occurs more than once within the same clause");
                }
                currentlyInMatchStatement = false;

                // we've found the method, now check which operands are argument matchers
                // for non-static invocations the first operand is always 'this'
                boolean isStatic = methodInsnNode.getOpcode() == INVOKESTATIC;
                resultArgumentMatcherIndexes = new ArrayList<Integer>();
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof ArgumentMatcherValue) {
                        resultArgumentMatcherIndexes.add(isStatic ? i : i - 1);
                    }
                }
                return createArgumentMatcherValue(resultValue);
            }

            Method matcherMethod = getMethod(methodInsnNode);
            if (matcherMethod != null) {
                // check whether the method is a match statement
                if (matcherMethod.getAnnotation(MatchStatement.class) != null) {
                    if (matchStatementFound) {
                        throwUnitilsException("Two match statements found on the same line. This is not supported");
                    }
                    matchStatementFound = true;
                    currentlyInMatchStatement = true;
                }
                // check whether the method is an argument matcher (i.e. has the @ArgumentMatcher annotation)
                if (matcherMethod.getAnnotation(ArgumentMatcher.class) != null) {
                    if (!currentlyInMatchStatement) {
                        throwUnitilsException("An argument matcher cannot be used outside the context of a match statement");
                    }
                    // we've found an argument matcher
                    return createArgumentMatcherValue(resultValue);
                }
            }

            // If the method is not an argument matcher, make sure none of the arguments of this method is an argument matcher,
            // since this is not supported
            for (Value value : (List<Value>) values) {
                if (value instanceof ArgumentMatcherValue) {
                    throwUnitilsException("An argument matcher's return value cannot be used inside an expression");
                }
            }
            // nothing special found
            return getValue(resultValue, values);
        }


        /**
         * Throws a {@link org.unitils.core.UnitilsException} with the given error message. The stacktrace is modified, to make
         * it point to the line of code that was analyzed by this class.
         *
         * @param errorMessage The error message
         */
        protected void throwUnitilsException(String errorMessage) {
            UnitilsException exception = new UnitilsException(errorMessage);
            exception.setStackTrace(new StackTraceElement[]{new StackTraceElement(interpretedClass.getName(), interpretedMethodName, interpretedClass.getName(), currentLineNr)});
            throw exception;
        }


        /**
         * Merges two values.
         *
         * @param value1 The first value
         * @param value2 The second value
         * @return The merged value
         */
        @Override
        public Value merge(Value value1, Value value2) {
            Value resultValue = super.merge(value1, value2);
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
        protected Value getValue(Value resultValue, Value... values) {
            if (values != null) {
                for (Value value : values) {
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
        protected Value getValue(Value resultValue, List<Value> values) {
            if (values != null) {
                for (Value value : values) {
                    if (value instanceof ArgumentMatcherValue) {
                        return createArgumentMatcherValue(resultValue);
                    }
                }
            }
            return resultValue;
        }


        /**
         * @param resultValue The result value
         * @return An ArgumentMatcherValue of the same type
         */
        protected ArgumentMatcherValue createArgumentMatcherValue(Value resultValue) {
            if (resultValue == null || !(resultValue instanceof BasicValue)) {
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