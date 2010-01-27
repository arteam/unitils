package org.unitils.dbmaintainer.clean.impl;


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

/**
 * This class is intended to permit multiple attempts (passes) to drop database
 * objects. It helps to solve the problem whereby it is impossible to drop one
 * database object because a dependent object exists (for example when dropping
 * Oracle 11 reference partitioned objects it is necessary to drop all child
 * tables first). </p> This class is used in a <code>do-while</code> loop with
 * the method {@link #continueExecutionAfterPass()} being the loop continuation
 * condition. 
 * <pre>
       do{
           dropObjects(dbSupport);
       }
       while ( multiPassErrorHandler.continueExecutionAfterPass() );
 * </pre>
 * 
 * In addition whenever an exception occurs it must be recorded with
 * the {@link #addError(RuntimeException)} method.
 * 
 * <pre>
        try {
            dropObject(objectName);
        } catch (RuntimeException e) {
            multiPassErrorHandler.addError(e);
        }
 * </pre>
 * 
 * @author Mark Jeffrey
 */
public class MultiPassErrorHandler {

    private int numPasses;
    private RuntimeException firstException;
    private int exceptionsThisPass;
    private int exceptionsLastPass;
    private boolean exceptionsDecreasing;

    /**
     * Call this method whenever there is an exception dropping a database
     * object. This method keeps track of:
     * <ol>
     * <li>the first exception (so that it may be reported later).
     * <li>the exception count so we can determine if they are still decreasing.
     * </ol>
     * 
     * If an Exception occurs during a pass then we need to do two things:
     * 
     * @param exception
     */
    public void addError(RuntimeException exception) {
        if (firstException == null) {
            firstException = exception;
        }
        exceptionsThisPass++;
    }

    /**
     * We stop execution if:
     * <ul>
     * <li>We have no exceptions during the most recent pass.
     * </ul>
     * We continue execution if:
     * <ul>
     * <li>We had exceptions and it was the first pass.
     * <li>We are after the first pass and the number of exceptions is non-zero
     * but decreasing.
     * </ul>
     * We throw the first exception of the pass if:
     * <ul>
     * <li>After the first pass we did not have a decreasing number of
     * exceptions.
     * </ul>
     * 
     * @return <code>true</code> if we should continue execution (trying another
     *         pass), <code>false</code> if no exceptions occurred in the most
     *         recent pass.
     * @throws RuntimeException
     *             If we do not get a decreasing number of exceptions each pass
     *             then we throw the first exception from the most recent pass.
     */
    public boolean continueExecutionAfterPass() {
        recordResultOfPass();
        boolean continueExecution = checkContinueExecution();
        resetForNextpass();
        return continueExecution;

    }

    private void recordResultOfPass() {
        numPasses++;
        exceptionsDecreasing = exceptionsThisPass < exceptionsLastPass;
    }

    private boolean checkContinueExecution() {
        if (numPasses > 1 && !exceptionsDecreasing) {
            throw firstException;
        }

        boolean continueExecution;
        if (exceptionsThisPass == 0) {
            continueExecution = false;
        } else if (numPasses == 1 && exceptionsThisPass > 0) {
            continueExecution = true;
        } else if (exceptionsDecreasing) {
            continueExecution = true;
        } else {
            continueExecution = false;
        }
        return continueExecution;
    }

    private void resetForNextpass() {
        exceptionsLastPass = exceptionsThisPass;
        exceptionsThisPass = 0;
        firstException = null;
    }

}
