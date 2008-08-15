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
package org.unitils.dbmaintainer.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.lob.ReaderInputStream;
import org.unitils.core.UnitilsException;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import org.unitils.thirdparty.org.apache.commons.io.NullWriter;

/**
 * A handle for getting the script content as a stream.
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class ScriptContentHandle {

	private MessageDigest scriptDigest;
	
	private Reader scriptReader;
	
    /**
     * Opens a stream to the content of the script.
     * 
     * NOTE: do not forget to close the stream after usage.
     *
     * @return The content stream, not null
     */
    public Reader openScriptContentReader() {
        scriptDigest = getScriptDigest();
        scriptReader = new InputStreamReader(new DigestInputStream(getScriptInputStream(), scriptDigest));
		return scriptReader;
    }

    protected MessageDigest getScriptDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new UnitilsException(e);
		}
	}

	public String getCheckSum() {
		try {
			if (scriptDigest == null) {
				readScript();
			} else if (scriptReader.ready()) {
				throw new UnitilsException("Cannot obtain checksum, since a script is currently being read");
			}
			return getHexPresentation(scriptDigest.digest());
		} catch (IOException e) {
			throw new UnitilsException(e);
		}
    }
	
	
	protected void readScript() throws IOException {
		Reader scriptReader = openScriptContentReader();
		IOUtils.copy(scriptReader, new NullWriter());
		scriptReader.close();
	}

	
	protected String getHexPresentation(byte[] byteArray) {
		StringBuffer result = new StringBuffer();
	    for (int i = 0; i < byteArray.length; i++) {
	    	result.append(Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return result.toString();
	}
    

    protected abstract InputStream getScriptInputStream();

    
    /**
     * A handle for getting the script content as a stream.
     */
    public static class UrlScriptContentHandle extends ScriptContentHandle {

        /* The URL of the script */
        private URL url;

        /**
         * Creates a content handle.
         *
         * @param url The url to the content, not null
         */
        public UrlScriptContentHandle(URL url) {
            this.url = url;
        }


        /**
         * Opens a stream to the content of the script.
         * 
         * @return The content stream, not null
         */
		@Override
		protected InputStream getScriptInputStream() {
			try {
				return url.openStream();
			} catch (IOException e) {
                throw new UnitilsException("Error while trying to create reader for url " + url, e);
            }
		}
    }


    /**
     * A handle for getting the script content as a stream.
     */
    public static class StringScriptContentHandle extends ScriptContentHandle {

        /* The content of the script */
        private String scriptContent;

        /**
         * Creates a content handle.
         *
         * @param scriptContent The content, not null
         */
        public StringScriptContentHandle(String scriptContent) {
            this.scriptContent = scriptContent;
        }


        /**
         * Opens a stream to the content of the script.
         *
         * @return The content stream, not null
         */
		@Override
		protected InputStream getScriptInputStream() {
			return new ReaderInputStream(new StringReader(scriptContent));
		}
        
        
    }


}