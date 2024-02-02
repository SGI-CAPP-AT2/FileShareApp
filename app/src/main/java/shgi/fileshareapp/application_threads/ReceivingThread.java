package shgi.fileshareapp.application_threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import shgi.fileshareapp.MainActivity;
import shgi.fileshareapp.helper_classes.ApplicationPayload;

public class ReceivingThread extends Thread{
    private ReceiverListener listener;
    private final ObjectInputStream inputObject;
    private final MainActivity mn;
    public interface ReceiverListener{
        void run(ApplicationPayload p) throws IOException;
    }
    public ReceivingThread(InputStream is, MainActivity mn) throws IOException {
        inputObject = new ObjectInputStream(is);
        this.mn=mn;
    }
    @Override
    public void run() {
        while(mn.isAllowedToRun){
            try {
                ApplicationPayload payload;
                if ((payload = (ApplicationPayload) inputObject.readObject()) != null) {
                    if (listener != null) {
                        listener.run(payload);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                mn.showToast("Device Offline");
                mn.setConnectedState("Disconnected");
                mn.restartApp();
            }
        }
    }
    public void setOnReceiveListener(ReceiverListener r){
        this.listener=r;
    }
}
