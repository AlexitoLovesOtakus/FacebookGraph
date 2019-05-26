package com.example.gwen.facebookgraph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Declaring Variables.
    CallbackManager callbackManager;
    TextView FacebookDataTextView;
    LoginButton loginButton;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Passing MainActivity in Facebook SDK.
        FacebookSdk.sdkInitialize(MainActivity.this);

        // Adding Callback Manager.
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        // Assign TextView ID.
        FacebookDataTextView = (TextView) findViewById(R.id.TextView1);

        // Assign Facebook Login button ID.
        loginButton = (LoginButton) findViewById(R.id.login_button);

        // Giving permission to Login Button.
        loginButton.setReadPermissions("user_posts");


        // Checking the Access Token.
        if (AccessToken.getCurrentAccessToken() != null) {

            GraphLoginRequest(AccessToken.getCurrentAccessToken());

            // If already login in then show the Toast.
            Toast.makeText(MainActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();

        } else {

            // If not login in then show the Toast.
            Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Adding Click listener to Facebook login button.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Calling method to access User Data After successfully login.
                GraphLoginRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Toast.makeText(MainActivity.this, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {

                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }

        });

        // Detect user is login or not. If logout then clear the TextView and delete all the user info from TextView.
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {

                    // Clear the TextView after logout.
                    FacebookDataTextView.setText("");

                }
            }
        };
    }

    // Method to access Facebook User Data.
    protected void GraphLoginRequest(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        try {

                            // Adding all user info one by one into TextView.
                            FacebookDataTextView.setText("ID: " + jsonObject.getString("id"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));
                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nFeed : " + jsonObject.getString("feed"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,feed"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(MainActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(MainActivity.this);
    }
}
