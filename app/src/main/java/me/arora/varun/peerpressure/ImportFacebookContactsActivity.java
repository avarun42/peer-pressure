package me.arora.varun.peerpressure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImportFacebookContactsActivity extends Activity {
    private TextView info;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_import_facebook_contacts);
        info = (TextView)findViewById(R.id.login_info);
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                        + loginResult.getAccessToken().getUserId()
                        + "\n"
                        + "Auth Token: "
                        + loginResult.getAccessToken().getToken()
                );
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void syncFBClicked(View v) {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
            AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(
                        JSONArray friends,
                        GraphResponse response) {
                    try {
                        info.setText(friends.toString());
                        for (int i = 0; i < friends.length(); i++) {
                            JSONObject friend = friends.getJSONObject(i);
                            info.setText(
                                    "# Mutual friends with " + friend.getString("name") + ": "
                                            + friend.getJSONObject("context")
                                                    .getJSONObject("mutual_friends")
                                                    .getJSONObject("summary")
                                                    .getString("total_count")
                                            + "\n\n\nSyncing friends..."
                            );
                        }
                    } catch (JSONException E) {
                        info.setText("Sync attempt failed.");
                    }
                }
            }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, context.fields(mutual_friends)");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
