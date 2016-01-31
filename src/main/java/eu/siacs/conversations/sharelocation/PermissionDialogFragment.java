package eu.siacs.conversations.sharelocation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class PermissionDialogFragment extends DialogFragment {
    public static final String TAG = "PermissionDialogFragment";

    @FragmentArg
    String[] permissions;

    @FragmentArg
    int requestCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(Permissions.getPermissionReasons(getActivity(), permissions))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), Permissions.removeGrantedPermissions(getContext(), permissions), requestCode);
                    }
                })
                .setCancelable(false)
                .create();
    }

    public void show(FragmentManager fragmentManager) {
        if(fragmentManager.findFragmentByTag(TAG) == null) {
            show(fragmentManager, TAG);
        }
    }
}
