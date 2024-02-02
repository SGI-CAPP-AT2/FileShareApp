package shgi.fileshareapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import shgi.fileshareapp.application_threads.ReceivingThread;
import shgi.fileshareapp.application_threads.SendingThread;
import shgi.fileshareapp.helper_classes.ApplicationPayload;
import shgi.fileshareapp.helper_classes.File;
import shgi.fileshareapp.helper_classes.ReceiverOfFiles;
import shgi.fileshareapp.helper_classes.WifiBroadcastReceiver;
import shgi.fileshareapp.send_fragment_listeners.selectFileCommanded;

public class MainActivity extends AppCompatActivity {
    private static final int FILE_REQUEST_CODE_FOR_SENDER = 1;
    private ArrayList<File> sendingFilesUris;
    private ArrayList<File> recievedFiles;
    private selectFileCommanded afterDoneResponseObjectForSendingFiles;
    private int id_for_files = 0;
    private send_fragment sf;
    private recv_fragment rf;
    private SendingFileFragment sff;
    private InitialPairSteps discoveryFragment;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private final int REQUEST_FOR_NEARBY = 102;
    public final int APPLICATION_PORT = 8722;
    private SendingThread sendingThread;
    private ReceivingThread receivingThread;
    public boolean isAllowedToRun;
    private int sendFilesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sendFilesCount=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tbl = findViewById(R.id.tablayout_main);
        ViewPager main_pager = findViewById(R.id.viewpage_main);
        tbl.setupWithViewPager(main_pager);
        MainTabAdapter mtb = new MainTabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        sf = new send_fragment();
        mtb.addFragment(sf, "Send File");
        rf = new recv_fragment();
        mtb.addFragment(rf, "Recieve File");
        main_pager.setAdapter(mtb);
        sff = new SendingFileFragment();
        discoveryFragment = new InitialPairSteps();
        sendingFilesUris = new ArrayList<>();
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiBroadcastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startDiscovery();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void startFileSelection(selectFileCommanded obj) {
        afterDoneResponseObjectForSendingFiles = obj;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FILE_REQUEST_CODE_FOR_SENDER);
    }

    public String getFileType(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private byte [] getFileBytes(Uri file_uri) throws IOException {
        ContentResolver cR = getContentResolver();
        InputStream ip = cR.openInputStream(file_uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024; // Adjust buffer size as needed
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = ip.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byte [] fileBytes = byteBuffer.toByteArray();
        return  fileBytes;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE_FOR_SENDER && resultCode == RESULT_OK) {
            ClipData clips = data.getClipData();
            String filename = "FileNameNotFound";
            String fileType;
            if (clips != null) {
                for (int i = 0; i < clips.getItemCount(); i++) {
                    Uri uri = clips.getItemAt(i).getUri();
                    if (uri != null) {
                        fileType = getFileType(uri);
                        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                filename = cursor.getString(nameIndex);
                            }
                        } catch (Exception e) {
                            Log.d("ExceptionReadingName", e.toString());
                        }
                        sendingFilesUris.add(new File(id_for_files++, uri, filename, fileType));
                        Log.d("File Selected", uri.toString());
                    }
                }
            } else {
                Uri uri = data.getData();
                if (uri != null) {
                    fileType = getFileType(uri);
                    try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            filename = cursor.getString(nameIndex);
                        }
                    } catch (Exception e) {
                        Log.d("ExceptionReadingName", e.toString());
                    }
                    sendingFilesUris.add(new File(id_for_files++, uri, filename, fileType));
                    Log.d("File Selected", uri.toString());
                }
            }
            afterDoneResponseObjectForSendingFiles.doneFilesSelections(sendingFilesUris);
        }
    }

    public void unSelectFiles() {
        sendingFilesUris.clear();
        sf.renderFiles();
    }

    public ArrayList<File> getAllSelectedFiles() {
        return sendingFilesUris;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void startDiscovery() {
        discoveryFragment.show(getSupportFragmentManager(), "DiscoveryInitial");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            Log.d("rejectionPermission", "No Permission Granted");
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
            }, REQUEST_FOR_NEARBY);
            return;
        }
        Log.d("havePermission", "going to discoverpeers");
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                showMessage("Successfully");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("Failedtofindpeer", reasonCode + " ");
            }
        });
    }

    public void startFileSend() {
        sff.show(getSupportFragmentManager(), "bottomSheet");
        for(File f: sendingFilesUris){
            ApplicationPayload p = new ApplicationPayload(ApplicationPayload.TYPE_FILE);
            p.payloadFile= new ApplicationPayload.File();
            p.payloadText="File is "+f.fileName;
            p.payloadFile.fileName=f.fileName;
            try {
                showMessage("Sending "+p.payloadFile.fileName);
                byte [] file_b = getFileBytes(f.filepath);
                p.payloadFile.fileBytes= file_b;
                p.payloadFile.fileSize=file_b.length;
                sendingThread.addToSendingBuffer(p);
                sendFilesCount++;
                showMessage("Sent "+p.payloadFile.fileName);
            }catch (Exception e){}
        }
    }
    public void sentFilesAreReceived(int count){
        if(count==sendFilesCount) {
            if (sff.getDialog() != null)
                sff.dismiss();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    unSelectFiles();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FOR_NEARBY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        startFileSend();
                    }
                } else {
                    showMessage("Permission Denied");
                }
                return;
            }
        }
    }

    public void connectToDevice(WifiP2pDevice dv) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dv.deviceAddress;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("managerConnect", "called");
                startTCP();
            }
            @Override
            public void onFailure(int i) {
                showMessage("Failed to Connect");
            }
        });
    }
    public InitialPairSteps getDiscoveryFragment(){
        return discoveryFragment;
    }
    public void startTCP(){
        discoveryFragment.showTcpEstablishment();
        manager.requestConnectionInfo(channel,(WifiP2pInfo info)->{
            if(info.groupFormed&&info.isGroupOwner){
                Thread networkThread = new Thread(()->{
                    try {
                        ServerSocket serverSocket = new ServerSocket(APPLICATION_PORT);
                        showMessage("Started Server Socket");
                        Socket client = serverSocket.accept();
                        showMessage("Accepted TCP connection : "+client.getInetAddress().toString());
                        startThreads(client.getInputStream(), client.getOutputStream());
                        showMessage("Started Threads");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                networkThread.start();
            }else if(info.groupFormed){
                Thread networkThread = new Thread(()-> {
                    try {
                        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
                        Socket client = new Socket(groupOwnerAddress, APPLICATION_PORT);
                        showMessage("Made Connection : "+client.getInetAddress().toString());
                        startThreads(client.getInputStream(), client.getOutputStream());
                        showMessage("Started Threads");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                networkThread.start();
            }
        });
    }
    public void startThreads(InputStream i, OutputStream o){
        try {
            showMessage("Starting Threads at startThreads");
            sendingThread=new SendingThread(o, this);
            receivingThread= new ReceivingThread(i, this);
            isAllowedToRun=true;
            sendingThread.start();
            receivingThread.start();
            showMessage("Sending Message...");
            sendName();
            Log.d("TCPConn", "sentName");
            receivingThread.setOnReceiveListener((ApplicationPayload p)->{
                if(p.payloadCommand==ApplicationPayload.COMMAND_DEVICE_NAME){
                    setConnectedState(p.payloadText);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(discoveryFragment.getDialog()!=null)
                                discoveryFragment.dismiss();
                        }
                    });
                    tcpSuccess();
                }
            });
        }catch (Exception e){
            showMessage(e.toString());
        }
    }
    public void tcpSuccess(){
        ReceiverOfFiles rf = new ReceiverOfFiles(this);
        receivingThread.setOnReceiveListener(rf);
    }
    public void setConnectedState(String name){
        TextView tv_name = findViewById(R.id.connection_state);
        tv_name.setText(name);
    }
    public void sendName(){
        sendingThread.addToSendingBuffer(new ApplicationPayload(
                ApplicationPayload.TYPE_MESSAGE,
                ApplicationPayload.COMMAND_DEVICE_NAME,
                Build.MODEL
        ));
    }
    public void sendPayloadViaTcp(ApplicationPayload p){
        sendingThread.addToSendingBuffer(p);
    }
    public void foundPeers(WifiP2pDeviceList list){
        if(discoveryFragment.getDialog()!=null) {
            discoveryFragment.renderDevices(list);
        }
    }
    public void showMessage(String msg){
        Log.d("MessageMainActivity", msg);
    }
    @Override
    protected void onDestroy() {
        isAllowedToRun=false;
        super.onDestroy();
    }
    public void addToReceivedFiles(File file){
        if(recievedFiles==null) recievedFiles=new ArrayList<>();
        recievedFiles.add(file);
        rf.renderFiles();
    }
    public ArrayList<File> getRcvFiles(){
        return recievedFiles;
    }
    public void showToast(String msg){
        try {
            Looper.prepare();
        }catch (Exception e){
            showMessage("Created");
        }
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void restartApp(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
