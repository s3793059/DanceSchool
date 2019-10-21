package assistant.genuinecoder.s_assistant.main.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import assistant.genuinecoder.s_assistant.R;
import assistant.genuinecoder.s_assistant.main.AppBase;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";

    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        _loginLink = (TextView) findViewById(R.id.link_login);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);


        /*
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        */
        long then = 0;
        final long hold_time = 15000;
        _signupButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setTag(true);
                } else if (v.isPressed() && (boolean) v.getTag()) {
                    long eventDuration = event.getEventTime() - event.getDownTime();
                    if (eventDuration > hold_time) {
                        signup(true);
                        v.setTag(false);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        signup(false);
                    }
                }
                return false;
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup(boolean add_as_admin) {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUp.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        String qu = "Select * FROM USER WHERE access_level = 'Admin';";

        //Cursor cursor = Login.handler.execQuery(qu);
        //if (cursor == null || cursor.getCount() == 0) {
        if (add_as_admin) {
            qu = "INSERT INTO USER (name,email,password,access_level) " +
                    "VALUES('" +
                    name + "'," +
                    "'" + email + "'," +
                    "'" + password + "'," +
                    "'Admin');";
        }
        else {
            qu = "INSERT INTO USER (name,email,password,access_level) " +
                    "VALUES('" +
                    name + "'," +
                    "'" + email + "'," +
                    "'" + password + "'," +
                    "'User');";
        }

        if (Login.handler.execAction(qu)) {

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            onSignupSuccess();
                            // onSignupFailed();
                            Intent i = new Intent(SignUp.this, Login.class);
                            startActivity(i);
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        else {
            onSignupFailed();
        }

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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
