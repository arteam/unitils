/*
 * Copyright 2011,  Unitils.org
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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the file resolver.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class FileResolverTest {

    /* Tested object */
    private FileResolver fileResolver = new FileResolver();


    @Test
    public void withPackagePrefix() throws Exception {
        URI result = fileResolver.resolveFileName("FileResolverTest.txt", FileResolverTest.class);
        assertEquals("FileResolverTest.txt", new File(result).getName());
    }

    @Test
    public void noPackagePrefixWhenFileNameStartsWithSlash() throws Exception {
        URI result = fileResolver.resolveFileName("/org/unitils/FileResolverTest-otherPackage.txt", FileResolverTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void packagePrefixWhenClassIsInDefaultPackage() throws Exception {
        URI result = fileResolver.resolveFileName("FileResolverTest-defaultPackage.txt", Class.forName("DefaultPackageClass"));
        assertEquals("FileResolverTest-defaultPackage.txt", new File(result).getName());
    }

    @Test
    public void packagePrefixingDisabled() throws Exception {
        fileResolver = new FileResolver(false, null);

        // no slash --> a prefix would have been used
        URI result = fileResolver.resolveFileName("org/unitils/FileResolverTest-otherPackage.txt", FileResolverTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void pathPrefixWithoutSlashIsRelativeToClassPath() throws Exception {
        fileResolver = new FileResolver(false, "org/unitils");

        URI result = fileResolver.resolveFileName("FileResolverTest-otherPackage.txt", FileResolverTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void pathPrefixWithSlashIsAbsolute() throws Exception {
        String directory = new File(getClass().getClassLoader().getResource("org/unitils/FileResolverTest-otherPackage.txt").toURI()).getParent();
        fileResolver = new FileResolver(false, "/" + directory);

        URI result = fileResolver.resolveFileName("/FileResolverTest-otherPackage.txt", FileResolverTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void fileNotFoundInClassPath() throws Exception {
        try {
            fileResolver.resolveFileName("xxx.txt", FileResolverTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            // expected
        }
    }

    @Test
    public void absoluteFileNotFound() throws Exception {
        fileResolver = new FileResolver(false, "/xxxx");
        try {
            fileResolver.resolveFileName("xxx.txt", FileResolverTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            // expected
        }
    }

    @Test
    public void defaultFileName() throws Exception {
        URI result = fileResolver.resolveDefaultFileName("txt", FileResolverTest.class);
        assertEquals("FileResolverTest.txt", new File(result).getName());
    }

}