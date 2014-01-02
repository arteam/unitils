/*
 * Copyright 2013,  Unitils.org
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
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class FileResolverResolveFileNameTest {

    private FileResolver fileResolver = new FileResolver();


    @Test
    public void withPackagePrefix() throws Exception {
        URI result = fileResolver.resolveFileName("FileResolverTest.txt", FileResolverResolveFileNameTest.class);
        assertEquals("FileResolverTest.txt", new File(result).getName());
    }

    @Test
    public void noPackagePrefixWhenFileNameStartsWithSlash() throws Exception {
        URI result = fileResolver.resolveFileName("/org/unitils/FileResolverTest-otherPackage.txt", FileResolverResolveFileNameTest.class);
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
        URI result = fileResolver.resolveFileName("org/unitils/FileResolverTest-otherPackage.txt", FileResolverResolveFileNameTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void pathPrefixWithoutSlashIsRelativeToClassPath() throws Exception {
        fileResolver = new FileResolver(false, "org/unitils");

        URI result = fileResolver.resolveFileName("FileResolverTest-otherPackage.txt", FileResolverResolveFileNameTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void pathPrefixWithSlashIsAbsolute() throws Exception {
        String directory = new File(getClass().getClassLoader().getResource("org/unitils/FileResolverTest-otherPackage.txt").toURI()).getParent();
        fileResolver = new FileResolver(false, "/" + directory);

        URI result = fileResolver.resolveFileName("/FileResolverTest-otherPackage.txt", FileResolverResolveFileNameTest.class);
        assertEquals("FileResolverTest-otherPackage.txt", new File(result).getName());
    }

    @Test
    public void exceptionWhenFileNotFoundInClassPath() throws Exception {
        try {
            fileResolver.resolveFileName("xxx.txt", FileResolverResolveFileNameTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("File with name org/unitils/core/util/xxx.txt cannot be found.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenAbsoluteFileNotFound() throws Exception {
        fileResolver = new FileResolver(false, "/xxxx");
        try {
            fileResolver.resolveFileName("xxx.txt", FileResolverResolveFileNameTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            String message = e.getMessage();
            assertTrue(message.startsWith("File with name"));
            assertTrue(message.endsWith("xxx.txt cannot be found."));
        }
    }

    @Test
    public void exceptionWhenToUriFails() throws Exception {
        fileResolver = new FileResolver(true, null) {
            @Override
            protected URI toUri(String fullFileName, URL fileUrl) throws URISyntaxException {
                throw new URISyntaxException("input", "reason");
            }
        };
        try {
            fileResolver.resolveFileName("FileResolverTest.txt", FileResolverResolveFileNameTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("File with name org/unitils/core/util/FileResolverTest.txt cannot be found.\n" +
                    "Reason: URISyntaxException: reason: input", e.getMessage());
        }
    }
}