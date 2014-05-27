package cz.muni.fi.android.formulaManager.app.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.service.Updater;

/**
 * Created by Majo on 1. 5. 2014.
 */
public class SettingsActivity extends Activity {
    //
    ///facebook api testing
    //
    private static final String TAG = "cz.muni.fi.android.formulaManager.SettingsActivity";
    public static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private boolean pendingPublishReauthorization = false;
    private UiLifecycleHelper uiHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        /*try { //getting hash for facebook
            PackageInfo info = getPackageManager().getPackageInfo(
                    "cz.muni.fi.android.formulaManager.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        //facebook api testing
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        //LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        Session session = Session.getActiveSession();
        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                        this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
        }

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
/*
        OpenGraphObject formula = OpenGraphObject.Factory.createForPost("muni_formulamanager:Formula");
        formula.setProperty("name", "test2");
        formula.setProperty("raw_formula", "rawtestform");
        formula.setProperty("parameter_name", "a");
        formula.setProperty("parameter_type", 1);

        OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
        action.setProperty("formula", formula);

        FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(this, action, "formula")
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
*/
    }

    public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    public void update(View view) {
        Log.d(TAG,"Update service will be started");
        final Intent service = new Intent(this, Updater.class);
        this.startService(service);
    }
}
