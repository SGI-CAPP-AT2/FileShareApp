package shgi.fileshareapp.helper_classes;

import java.io.Serializable;

public class ApplicationPayload implements Serializable {
    public static class File implements Serializable{
        public String fileName;
        public byte [] fileBytes;
        public int fileSize;
    }
    public static int TYPE_FILE = 0;
    public static int TYPE_MESSAGE = 1;
    public static int COMMAND_DEVICE_NAME = 2;
    public static int COMMAND_ACK = 3;
    public int payloadType;

    public int payloadCommand;
    public String payloadText;
    public File payloadFile;
    public ApplicationPayload(int type){
        payloadType=type;
    }
    public ApplicationPayload(int type, int command){
        payloadType=type;
        payloadCommand=command;
    }
    public ApplicationPayload(int type, int command, String text){
        payloadType=type;
        payloadCommand=command;
        payloadText=text;
    }
    public ApplicationPayload(int type, int command, File file){
        payloadType=type;
        payloadCommand=command;
        payloadFile=file;
    }
}
