package eu.siacs.conversations.sharelocation;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

@EActivity
@OptionsMenu(R.menu.base)
public abstract class BaseActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUIRED_REQUEST_CODE = 123;
    public static final int PERMISSION_REQUESTED_REQUEST_CODE = 124;

    public void requirePermissions(String[] permissions) {
        getPermissions(permissions, PERMISSION_REQUIRED_REQUEST_CODE);
    }

    public void requestPermissions(String[] permissions) {
        getPermissions(permissions, PERMISSION_REQUESTED_REQUEST_CODE);
    }

    private void getPermissions(String[] permissions, int requestCode) {
        String[] missing = Permissions.removeGrantedPermissions(this, permissions);

        if(missing.length > 0) {
            String reasons = Permissions.getPermissionReasons(this, missing);

            if(!TextUtils.isEmpty(reasons)) {
                PermissionDialogFragment_.builder()
                        .permissions(missing)
                        .requestCode(requestCode)
                        .build()
                        .show(getSupportFragmentManager());
            } else {
                ActivityCompat.requestPermissions(this, missing, requestCode);
            }
        } else {
            onGotRequiredPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUIRED_REQUEST_CODE) {
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    finish();
                    return;
                }
            }

            onGotRequiredPermissions();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Called when the permissions requested with requirePermissions are granted
     */
    public void onGotRequiredPermissions() {

    }

    @OptionsItem
    public void about() {
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .start(this);
    }
}
