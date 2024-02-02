package shgi.fileshareapp.helper_classes;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;

public class DeviceRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {
    private WifiP2pDevice dv;
    public DeviceRadioButton(WifiP2pDevice dv, Activity mn){
        super(mn);
        this.dv=dv;
    }
    public WifiP2pDevice getDevice(){
        return dv;
    }
}
