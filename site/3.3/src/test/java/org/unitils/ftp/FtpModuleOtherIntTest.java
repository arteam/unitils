package org.unitils.ftp;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockftpserver.fake.FakeFtpServer;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.ftp.annotations.TestFtpServer;

/**
 * 
 * Integration test.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 1.0.0
 *
 */
@Ignore
//START SNIPPET: ftpExample
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FtpModuleOtherIntTest {

    @TestFtpServer(baseFolder="src/test/resources/otherFolder")
    public FakeFtpServer server;


    @Test
    public void getFile() throws IOException {
        //do some FTP calls.

        //verification
        server.getFileSystem().exists("file.txt");
    }
}
//END SNIPPET: ftpExample