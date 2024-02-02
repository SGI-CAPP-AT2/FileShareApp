package shgi.fileshareapp.helper_classes;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.io.OutputStream;

import shgi.fileshareapp.MainActivity;
import shgi.fileshareapp.application_threads.ReceivingThread;

public class ReceiverOfFiles implements ReceivingThread.ReceiverListener {
    MainActivity activity;

    private int receivedFiles ;
    public ReceiverOfFiles(MainActivity mn){
        activity=mn;
        receivedFiles=0;
    }
    @Override
    public void run(ApplicationPayload p) {
        activity.showMessage("Trigged RUN at ReceiverOfFiles");
        if(p.payloadType==ApplicationPayload.TYPE_FILE) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "WifiP2p-"+System.currentTimeMillis() / 100 + "_" + p.payloadFile.fileName);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/shgi.fileshareapp/");
            Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            assert uri != null;
            activity.showMessage("file is at (NOT DONE)"+uri.toString());
            try {
                activity.showMessage("Started File Sharing");
                OutputStream fos = activity.getContentResolver().openOutputStream(uri);
                assert fos != null;
                fos.write(p.payloadFile.fileBytes);
                fos.close();
                activity.showMessage("Done File Saved");
                File f = new File(receivedFiles, uri, p.payloadFile.fileName,activity.getFileType(uri));
                Handler handler = new Handler(Looper.getMainLooper());
                activity.sendPayloadViaTcp(new ApplicationPayload(ApplicationPayload.TYPE_MESSAGE, ApplicationPayload.COMMAND_ACK, (++receivedFiles)+""));
                handler.post(()-> activity.addToReceivedFiles(f));
            } catch (Exception e) {
                activity.showMessage("Error "+e.getMessage());
            }
        }else if(p.payloadCommand==ApplicationPayload.COMMAND_ACK){
            activity.showMessage("Ack");
            activity.sentFilesAreReceived(Integer.parseInt(p.payloadText));
        }
    }
}
