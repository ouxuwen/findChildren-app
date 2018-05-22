package com.amap.location.findchildren;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Bind;

import static com.amap.location.findchildren.RequestConnect.*;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        // login here
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        final Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                String json = (String) msg.obj;
                JSONObject jsonObject = null;
                String dj = null;
                try {
                    jsonObject = new JSONObject(json);
                    dj = (String) jsonObject.get("status");
                    if(dj.contentEquals("success") ){
                        onLoginSuccess(jsonObject);

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }else{
                        onLoginFailed();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();

            };
        };
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                String result =  myLogin(email,password,SERVER_URL+LOGIN_URL);

                Message msg = Message.obtain();
                msg.obj = result;
                handler.sendMessage(msg);
                Looper.loop();
                // On complete call either onLoginSuccess or onLoginFailed

            }
        }.start();




    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(JSONObject json) {
        _loginButton.setEnabled(true);

        Boolean isLogin;
        String username;
        String email;
        String type;
        String son;
        try {
            isLogin = json.getBoolean("isLogin");
            username = json.getString("username");
            email = json.getString("email");
            type = json.getString("type");
            son =  json.getString("son");
            if(type.contentEquals("parent")){
                Intent intent = new Intent(this, GeoFence_Polygon_Activity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, Location_Activity.class);
                startActivity(intent);
            }


            SPUtils.put(getApplicationContext(),"isLogin",isLogin);
            SPUtils.put(getApplicationContext(),"username",username);
            SPUtils.put(getApplicationContext(),"email",email);
            SPUtils.put(getApplicationContext(),"type",type);
            if(son != null){
                SPUtils.put(getApplicationContext(),"son",son);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "Login Successfully", Toast.LENGTH_LONG).show();

        // finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
