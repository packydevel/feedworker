package org.feedworker.object;
/**
 *
 * @author luca
 */
public class Subtitle {
    private String id, showId, showName, name, version, fileName, fileSize, 
                date, description, infoUrl;

    public Subtitle(String name, String version, String fileName, String fileSize, 
                    String date, String description, String infoUrl) {
        this.name = name;
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.date = date;
        this.description = description;
        this.infoUrl = infoUrl;
    }

    public Subtitle(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
    
    public String[] toArraySingle(){
        return new String[]{name, version, fileName, fileSize, date, description, infoUrl};
    }
    
    public String[] toArrayIdNameVersion(){
        return new String[]{id, name, version};
    }
    
}