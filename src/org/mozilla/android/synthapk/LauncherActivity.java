package org.mozilla.android.synthapk;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean success = attemptStartWebApp() || installRuntime();
        assert success;
    }

    public boolean installRuntime() {
        Intent intent = new Intent(getApplicationContext(), InstallRuntimeActivity.class);
        if (isCallable(intent) > 0) {
            startActivity(intent);
            return true;
        }
        return false;
    }

    private boolean attemptStartWebApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setType(C.WEBAPP_MIMETYPE);

        intent.putExtra(C.EXTRA_PACKAGE_NAME, getPackageName());

        String iconUri = "android.resource://" + getPackageName() + "/drawable/ic_launcher";

        intent.putExtra(C.EXTRA_ICON_URI, iconUri);

        if (isCallable(intent) > 0) {
            startWebApp(intent);
            return true;
        }
        return false;
    }

            }

    }

    }

    private void startWebApp(Intent intent) {
        startActivity(intent);
    }

    private int isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size();
    }

}
