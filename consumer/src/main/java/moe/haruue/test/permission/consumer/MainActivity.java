package moe.haruue.test.permission.consumer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PACKAGE_NAME_PERMISSION_PROVIDER = "moe.haruue.test.permission.provider";
    private static final String ACTIVITY_NAME_PROTECTED_ACTIVITY = "moe.haruue.test.permission.provider.ProtectedActivity";
    private static final String PERMISSION_TEST = "moe.haruue.test.permission.TEST";
    private static final int REQUEST_CODE_START_PROTECTED_ACTIVITY = 0x0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonPermission = findViewById(R.id.button_permission);
        buttonPermission.setOnClickListener(this);
        Button buttonSettings = findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_permission) {
            if (isPackageInstalled(PACKAGE_NAME_PERMISSION_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, PERMISSION_TEST) == PackageManager.PERMISSION_GRANTED) {
                    startProtectedActivity();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            PERMISSION_TEST
                    }, REQUEST_CODE_START_PROTECTED_ACTIVITY);
                }
            } else {
                Toast.makeText(this, "Please install the Perm Provider first", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.button_settings) {
            startApplicationDetailsSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_START_PROTECTED_ACTIVITY) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (PERMISSION_TEST.equals(permission)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        startProtectedActivity();
                    } else if (grantResult == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Unknown permission grant result: " + grantResult, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void startProtectedActivity() {
        Intent startIntent = new Intent();
        startIntent.setComponent(new ComponentName(PACKAGE_NAME_PERMISSION_PROVIDER, ACTIVITY_NAME_PROTECTED_ACTIVITY));
        startActivity(startIntent);
    }

    private void startApplicationDetailsSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingsIntent.setData(Uri.parse("package:" + getPackageName()));
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsIntent);
    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean result;
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            result = pi != null;
        } catch (PackageManager.NameNotFoundException e) {
            result = false;
        }
        return result;
    }

}
