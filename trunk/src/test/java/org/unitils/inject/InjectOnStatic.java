/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.inject;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@SuppressWarnings({"UnusedDeclaration"})
public class InjectOnStatic {

    private static ToInject toInject;

    private static ToInject toInjectField;

    private static TestObject testObject;

    private static TestObject testObjectField;

    static {
        testObject = new TestObject();
        testObjectField = new TestObject();
    }

    public static void setToInject(ToInject toInject) {
        InjectOnStatic.toInject = toInject;
    }

    public static void setTestObject(TestObject testObject) {
        InjectOnStatic.testObject = testObject;
    }

    public static ToInject getToInject() {
        return toInject;
    }

    public static ToInject getToInjectField() {
        return toInjectField;
    }

    public static TestObject getTestObject() {
        return testObject;
    }

    public static TestObject getTestObjectField() {
        return testObjectField;
    }

    public static class TestObject {

        private ToInject toInject;

        public void setToInject(ToInject toInject) {
            this.toInject = toInject;
        }

        public ToInject getToInject() {
            return toInject;
        }

    }

    public static class ToInject {

    }
}
