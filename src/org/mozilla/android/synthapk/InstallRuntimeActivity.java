package org.mozilla.android.synthapk;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

public class InstallRuntimeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(C.TAG, "Process pid=" + Process.myPid() + " (installer)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installer);

        boolean success = installRuntime();
        assert success;
    }

    public boolean installRuntime() {
        final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ C.FENNEC_PACKAGE_NAME));

        if (isCallable(marketIntent) > 0) {
            // we must use this rather than getApplicationContext() according to
            // https://stackoverflow.com/questions/5796611/dialog-throwing-unable-to-add-window-token-null-is-not-for-an-application-wi
            new AlertDialog.Builder(this)
                .setCancelable(true)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.install_fennec_dialog_message)
                .setTitle(R.string.install_fennec_dialog_title)

                .setPositiveButton(R.string.install_fennec_dialog_button_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.i("Installing runtime");
                        startActivityForResult(marketIntent, R.id.install_runtime_from_market);
                    }
                })

                .setNegativeButton(R.string.install_fennec_dialog_button_cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
            return true;
        }
        return false;
    }

    private boolean installWebApp() {
        Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);

        if (isCallable(intent) > 0) {
            Logger.i("Installing webapp " + getPackageName());
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean nextStep = false;
        Log.i(C.TAG, "Returning from the marketplace. Result code = " + resultCode);
        if (requestCode == R.id.install_runtime_from_market) {
            nextStep = LauncherActivity.isLaunchable(this) ? installWebApp() : installRuntime();
            finish();
        }
        assert nextStep;
    }

    private int isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size();
    }

}
