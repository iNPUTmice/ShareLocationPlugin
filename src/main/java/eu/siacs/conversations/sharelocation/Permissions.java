package eu.siacs.conversations.sharelocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class Permissions {
    private Permissions() {

    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean shouldExplainReason(Activity context, String permission) {
        return !hasPermission(context, permission) && ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
    }

    public static String[] removeGrantedPermissions(Context context, @NonNull String[] permissions) {
        List<String> result = new ArrayList<>();

        for(String permission : permissions) {
            if(!hasPermission(context, permission)) {
                result.add(permission);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public static String getPermissionReasons(Activity activity, String[] permissions) {
        List<String> reasons = new ArrayList<>();

        for(String permission : permissions) {
            if(!hasPermission(activity, permission) && shouldExplainReason(activity, permission)) {
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                    reasons.add(activity.getString(R.string.permission_location_reason));
                } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                    reasons.add(activity.getString(R.string.permission_storage_reason));
                }
            }
        }

        if(reasons.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();

            for(String reason :reasons) {
                if(builder.length() != 0) {
                    builder.append('\n');
                }

                builder.append(reason);
            }

            return builder.toString();
        }
    }
}
