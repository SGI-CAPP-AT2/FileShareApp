package shgi.fileshareapp.helper_classes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PersistentBottomSheetDialogFragment extends BottomSheetDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener((DialogInterface d)->{
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from((FrameLayout) (dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)));
            behavior.setHideable(false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        });
        return dialog;
    }
}
