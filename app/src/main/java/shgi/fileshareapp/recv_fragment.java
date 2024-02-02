package shgi.fileshareapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import shgi.fileshareapp.helper_classes.File;

public class recv_fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recv_fragment, container, false);
    }
    public void renderFiles(){
        MainActivity mn = (MainActivity) getActivity();
        ArrayList<File> files = mn.getRcvFiles();
        if(files==null)files=new ArrayList<>();
        LinearLayout target = (LinearLayout) getActivity().findViewById(R.id.files_add_here_rcv_container);
        if(target!=null) {
            target.removeAllViews();
            for (File current_file : files) {
                LinearLayout parentfor_images = new LinearLayout(getActivity());
                parentfor_images.setOrientation(LinearLayout.VERTICAL);
                ImageView imageView = null;
                Bitmap src = null;
                if(current_file.fileType.equals("jpg")||current_file.fileType.equals("jpeg")||current_file.fileType.equals("png")||current_file.fileType.equals("heic")) {
                    try {
                        src = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(current_file.filepath));
                    }catch (Exception e){
                        Log.d("bitmap",e.toString());
                    }
                }
                if(src!=null){
                    try {
                        int width = target.getWidth()-20;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, width, src.getHeight() * width / src.getWidth(), false);
                        Bitmap clippedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), 300);
                        src=clippedBitmap;
                        imageView=new ImageView(getActivity());
                        imageView.setImageBitmap(clippedBitmap);
                    }catch(Exception e){
                        Log.d("ExceptionByTheBitmap", e.toString());
                        src=null;
                    }
                }
                if(src==null){
                    imageView = new ImageView(getActivity());
                    imageView.setImageResource(R.drawable.file_prev_foreground);
                }
                parentfor_images.addView(imageView);
                TextView tv = new TextView(getActivity());
                tv.setText(current_file.fileName);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                parentfor_images.addView(tv);
                parentfor_images.setBackgroundColor(getResources().getColor(R.color.cardBg));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 10, 0, 0);
                parentfor_images.setPadding(10,10,10,10);
                target.addView(parentfor_images, layoutParams);
            }
        }
    }
}