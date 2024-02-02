package shgi.fileshareapp.helper_classes;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import shgi.fileshareapp.MainActivity;

public class PeerListImpl implements WifiP2pManager.PeerListListener {
    MainActivity mn;
    public PeerListImpl(MainActivity activity){
        mn=activity;
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Log.d("PeerListImpl", "onPeersAvailableCalled");
        mn.foundPeers(wifiP2pDeviceList);
    }
}