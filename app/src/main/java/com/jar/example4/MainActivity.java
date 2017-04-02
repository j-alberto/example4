package com.jar.example4;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1233;

    private TextView tvCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCalls = (TextView) findViewById(R.id.tvCalls);
        tvCalls.setText("");
    }

    public void doShowCalls(View view) {
        if (checkPermissionStatus()) {
            queryCallsContentProvider();
        } else {
            requestPermission();
        }
    }

    public void requestPermission() {
        boolean hasReadCallLogPermission = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_CALL_LOG);
        boolean hasWriteCallLogPermission = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_CALL_LOG);

        if (hasReadCallLogPermission && hasWriteCallLogPermission) {
            Toast.makeText(this, "Permisos otorgados!", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}
                    , PERMISSION_CODE);
        }
    }

    public boolean checkPermissionStatus() {
        boolean hasReadCallLogPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) ==
                PackageManager.PERMISSION_GRANTED;
        boolean hasWriteCallLogPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) ==
                PackageManager.PERMISSION_GRANTED;

        return hasReadCallLogPermission && hasWriteCallLogPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (checkPermissionStatus()) {
                    Toast.makeText(this, "Permiso ya activo!", Toast.LENGTH_LONG).show();
                    queryCallsContentProvider();
                } else {
                    Toast.makeText(this, "No se activo el permiso", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void queryCallsContentProvider() {
        Uri uriCalls = CallLog.Calls.CONTENT_URI;

        String[] fields = {
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION
        };

      /*  ContentResolver resolver = getContentResolver();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        Cursor curCalls = getContentResolver().query(uriCalls, fields, null, null, CallLog.Calls.DATE + " DESC");

        while (curCalls.moveToNext()) {
            String number = curCalls.getString(curCalls.getColumnIndex(fields[0]));
            long date = curCalls.getLong(curCalls.getColumnIndex(fields[1]));
            int type = curCalls.getInt(curCalls.getColumnIndex(fields[2]));
            String duration = curCalls.getString(curCalls.getColumnIndex(fields[3]));
            String typeName = null;

            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeName = getString(R.string.call_incoming);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeName = getString(R.string.call_outgoing);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeName = getString(R.string.call_missing);
                    break;
                default:
                    typeName = getString(R.string.call_unknown);
            }

            String detail = String.format(getString(R.string.detail_template),
                    number, typeName, DateFormat.format("yyyy-MM-dd k:mm",date),duration);

            tvCalls.append(detail);
        }
    }
}
