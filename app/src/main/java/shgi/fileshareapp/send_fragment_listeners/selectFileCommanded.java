package shgi.fileshareapp.send_fragment_listeners;

import android.view.View;

import java.util.ArrayList;

import shgi.fileshareapp.MainActivity;
import shgi.fileshareapp.send_fragment;
public class selectFileCommanded implements View.OnClickListener{
    MainActivity main;
    send_fragment parent;
    public selectFileCommanded(MainActivity m, send_fragment sf){
        main=m;
        parent=sf;
    }
    @Override
    public void onClick(View view) {
            main.startFileSelection(this);
    }
    public void doneFilesSelections(ArrayList<shgi.fileshareapp.helper_classes.File> files){
        parent.renderFiles();
    }
}
