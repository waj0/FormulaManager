package cz.muni.fi.android.formulaManager.app.UI;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.service.Updater;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "cz.fi.android.formulamanager.MainActivity";
    protected BroadcastReceiver connectivityChangedReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ConnectivityManager mgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
        {

            connectivityChangedReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                    if (!noConnectivity)
                    {
                        startUpdater();
                        try
                        {
                            unregisterReceiver(connectivityChangedReceiver);
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "Handled exception during unregistering receiver: " + e.getMessage());
                        }
                        connectivityChangedReceiver = null;
                        Log.d(TAG,"Download started!");
                    }

                }
            };
            registerReceiver(connectivityChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } else {
            startUpdater();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings: {
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void startUpdater()
    {
        Toast.makeText(this, "Update started.", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Service started");
        final Intent service = new Intent(this, Updater.class);
        this.startService(service);
    }
}
