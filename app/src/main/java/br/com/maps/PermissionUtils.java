package br.com.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelson336 on 30/03/17.
 */

public class PermissionUtils {

    public static String[] checkPermissionContext(AppCompatActivity activity, boolean granted, String... permissions) {

        List<String> permissionsReturnList = new ArrayList<String>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                boolean check = granted ? ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED :
                        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;

                if (check) {
                    permissionsReturnList.add(permission);
                }
            }
        }

        String[] permissionReturnArray = new String[permissionsReturnList.size()];
        permissionsReturnList.toArray(permissionReturnArray);
        return permissionReturnArray;
    }

    public static boolean isPermissionsGrantedContext(AppCompatActivity activity, String[] permissions) {
        String[] permissionGranted = PermissionUtils.checkPermissionContext(activity, true, permissions);
        return permissionGranted.length > 0 && permissionGranted.length == permissions.length;
    }
}
