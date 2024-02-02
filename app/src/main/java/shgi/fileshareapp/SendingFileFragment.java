package shgi.fileshareapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import shgi.fileshareapp.databinding.FragmentSendingFileListDialogBinding;
import shgi.fileshareapp.helper_classes.PersistentBottomSheetDialogFragment;

public class SendingFileFragment extends PersistentBottomSheetDialogFragment {
    private FragmentSendingFileListDialogBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSendingFileListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void dismiss(){
        this.getDialog().dismiss();
    }
}