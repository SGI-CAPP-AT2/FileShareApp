package shgi.fileshareapp.helper_classes;

import android.net.Uri;

public class File {
    public int id;
    public Uri filepath;
    public String fileName;
    public String fileType;
    public File(int id, Uri path, String name, String fileType){
        this.id=id;
        this.filepath=path;
        this.fileName=name;
        this.fileType=fileType;
    }
}
