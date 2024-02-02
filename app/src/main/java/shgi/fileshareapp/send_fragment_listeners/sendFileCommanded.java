package shgi.fileshareapp.send_fragment_listeners;

import android.view.View;

import shgi.fileshareapp.MainActivity;

public class sendFileCommanded implements View.OnClickListener {
    MainActivity main;
    public sendFileCommanded(MainActivity m){
        main=m;
    }

    @Override
    public void onClick(View view) {
        main.startFileSend();
    }
}
