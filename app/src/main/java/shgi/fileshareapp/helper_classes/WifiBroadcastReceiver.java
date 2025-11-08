package shgi.fileshareapp.helper_classes;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.core.app.ActivityCompat;

import shgi.fileshareapp.MainActivity;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private PeerListImpl myPeerListListener;
    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                 MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        myPeerListListener=new PeerListImpl(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.showMessage("Your P2P is Enabled");
            } else {
                activity.showMessage("Your P2P is Disabled");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.requestPeers(channel, myPeerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Make sure discovery fragment/dialog is present before calling UI methods
            if (activity.getDiscoveryFragment() != null && activity.getDiscoveryFragment().getDialog() != null) {
                activity.getDiscoveryFragment().showNullWaiting();
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo != null && networkInfo.isConnected()){
                activity.startTCP();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }

    }

}
