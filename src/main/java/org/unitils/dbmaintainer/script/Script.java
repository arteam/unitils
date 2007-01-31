package org.unitils.dbmaintainer.script;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class Script {

    private String fileName;

    private String scriptContent;

    public Script(String fileName, String scriptContent) {
        this.fileName = fileName;
        this.scriptContent = scriptContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getScriptContent() {
        return scriptContent;
    }

}
