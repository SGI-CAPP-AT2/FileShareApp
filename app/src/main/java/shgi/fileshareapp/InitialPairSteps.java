package shgi.fileshareapp;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import shgi.fileshareapp.helper_classes.DeviceRadioButton;
import shgi.fileshareapp.helper_classes.PersistentBottomSheetDialogFragment;

public class InitialPairSteps extends PersistentBottomSheetDialogFragment {
    private shgi.fileshareapp.databinding.InitialPairStepsDialogBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = shgi.fileshareapp.databinding.InitialPairStepsDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void renderDevices(WifiP2pDeviceList dvl){
        this.getDialog().findViewById(R.id.steps_pair_1).setVisibility(View.GONE);
        RadioGroup dev_grp = this.getDialog().findViewById(R.id.found_devices);
        dev_grp.removeAllViews();
        for(WifiP2pDevice dv: dvl.getDeviceList()){
            DeviceRadioButton deviceBtn = new DeviceRadioButton(dv, this.getActivity());
            deviceBtn.setText(dv.deviceName);
            dev_grp.addView(deviceBtn);
        }
        this.getDialog().findViewById(R.id.steps_pair_2).setVisibility(View.VISIBLE);
        Button contn_btn = this.getDialog().findViewById(R.id.btn_to_contn_pairing);
        contn_btn.setOnClickListener((View v)->{
            RadioGroup group = this.getDialog().findViewById(R.id.found_devices);
            DeviceRadioButton checked_dv = this.getDialog().findViewById(group.getCheckedRadioButtonId());
            if(checked_dv!=null) {
                WifiP2pDevice dv = checked_dv.getDevice();
                ((MainActivity) getActivity()).connectToDevice(dv);
            }
        });
    }
    public void showTcpEstablishment(){
        ConstraintLayout null_waiting = (ConstraintLayout) this.getDialog().findViewById(R.id.null_waiting_dialog);
        null_waiting.setVisibility(View.GONE);
        LinearLayout ll_pair_steps = (LinearLayout) this.getDialog().findViewById(R.id.p2p_steps);
        ll_pair_steps.setVisibility(View.GONE);
        LinearLayout ll_tcp_steps = (LinearLayout) this.getDialog().findViewById(R.id.tcp_setup);
        ll_tcp_steps.setVisibility(View.VISIBLE);
    }
    public void showNullWaiting(){
        LinearLayout ll_pair_steps = (LinearLayout) this.getDialog().findViewById(R.id.p2p_steps);
        ll_pair_steps.setVisibility(View.GONE);
        LinearLayout ll_tcp_steps = (LinearLayout) this.getDialog().findViewById(R.id.tcp_setup);
        ll_tcp_steps.setVisibility(View.GONE);
        ConstraintLayout null_waiting = (ConstraintLayout) this.getDialog().findViewById(R.id.null_waiting_dialog);
        null_waiting.setVisibility(View.VISIBLE);
    }
    public void dismiss(){
        this.getDialog().dismiss();
    }
}
