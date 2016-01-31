package eu.siacs.conversations.sharelocation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

public abstract class LocationUtil {
    private LocationUtil() {

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean isLocationEnabledKitkat(Context context) {
        try {
            int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean isLocationEnabledLegacy(Context context) {
        String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(locationProviders);
    }

    public static boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return isLocationEnabledKitkat(context);
        }else{
            return isLocationEnabledLegacy(context);
        }
    }
}
