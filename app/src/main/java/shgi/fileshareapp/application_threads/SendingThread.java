package shgi.fileshareapp.application_threads;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import shgi.fileshareapp.MainActivity;
import shgi.fileshareapp.helper_classes.ApplicationPayload;

public class SendingThread extends Thread{
    private ArrayList<ApplicationPayload> payloadBuffer;
    private ObjectOutputStream outputObject;
    private final MainActivity mn;
    public SendingThread(OutputStream outputStream, MainActivity mn) throws IOException {
        payloadBuffer=new ArrayList<>();
        outputObject=new ObjectOutputStream(outputStream);
        this.mn=mn;
    }
    @Override
    public void run() {
        while (mn.isAllowedToRun){
            if(payloadBuffer.size()!=0){
                try {
                    ArrayList<ApplicationPayload> buffer_copy = new ArrayList<>(payloadBuffer);
                    mn.showMessage("Buffer is at : "+buffer_copy.size());
                    payloadBuffer.clear();
                    for (ApplicationPayload current_payload : buffer_copy) {
                        Log.d("CurrentPayload", current_payload.payloadText);
                        outputObject.writeObject(current_payload);
                    }
                }catch (Exception e){}
            }
        }
    }
    public void addToSendingBuffer(ApplicationPayload payload){
        payloadBuffer.add(payload);
    }
}
