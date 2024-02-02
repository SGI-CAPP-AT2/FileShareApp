package shgi.fileshareapp.send_fragment_listeners;

import android.view.View;

import shgi.fileshareapp.MainActivity;
import shgi.fileshareapp.send_fragment;

public class unselectFileCommanded implements View.OnClickListener{
    MainActivity main;
    send_fragment parent;
    public unselectFileCommanded(MainActivity m, send_fragment sf){
        main=m;
        parent=sf;
    }

    @Override
    public void onClick(View view) {
        main.unSelectFiles();
    }
}
