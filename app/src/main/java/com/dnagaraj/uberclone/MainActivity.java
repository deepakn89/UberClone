package com.dnagaraj.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import javax.sql.StatementEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    
    private Toolbar toolbar;

    private EditText etUsername,etPassword,etAnonymous;

    private Button btnSignup,btnOneTimeLogin;

    private RadioButton passengerRadiobutton,driverRadiobutton;

    enum State{
        SIGNUP, LOGIN
    }

    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (ParseUser.getCurrentUser() != null) {
            // transition
             ParseUser.logOut();
        }

        toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("UberClone");
        toolbar.inflateMenu(R.menu.my_menu);

        etUsername=findViewById(R.id.etUsername);
        etPassword=findViewById(R.id.etpassword);
        etAnonymous=findViewById(R.id.etAnanymous);

        btnSignup=findViewById(R.id.btnSignup);
        btnOneTimeLogin=findViewById(R.id.btnAnonymousLogin);

        passengerRadiobutton=findViewById(R.id.rbPassenger);
        driverRadiobutton=findViewById(R.id.rbDriver);

        state=State.SIGNUP;

        btnSignup.setOnClickListener(this);
        btnOneTimeLogin.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_menu:
                //Menu is login and button is signup
                if(state==State.SIGNUP){
                    state= State.LOGIN;
                    item.setTitle(R.string.signup);
                    btnSignup.setText(R.string.login);
                }else if(state==State.LOGIN){
                    //menu is signup and button is login
                    state=State.SIGNUP;
                    item.setTitle(R.string.login);
                    btnSignup.setText(R.string.signup);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSignup:
                if (state == State.SIGNUP) {

                    if (!passengerRadiobutton.isChecked() && !driverRadiobutton.isChecked()) {
                        Toast.makeText(this, "Are you a pasenger or driver ?", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final ParseUser parseUser = new ParseUser();
                    parseUser.setUsername(etUsername.getText().toString());
                    parseUser.setPassword(etPassword.getText().toString());

                    if (passengerRadiobutton.isChecked()) {
                        parseUser.put("as", "Passenger");
                    } else if (driverRadiobutton.isChecked()) {
                        parseUser.put("as", "Driver");
                    }

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                FancyToast.makeText(MainActivity.this, ParseUser.getCurrentUser().getUsername() + " signed up successfully",
                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();

                            } else {
                                FancyToast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            }
                        }
                    });
                } else if (state == State.LOGIN) {
                    ParseUser.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                FancyToast.makeText(MainActivity.this, ParseUser.getCurrentUser().getUsername() + "  logged in successfully",
                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            }else{
                                FancyToast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            }
                        }
                    });
                }
            break;

            case R.id.btnAnonymousLogin:
                Log.d(TAG, "onClick: btnAnonymousLogin");
                if(etAnonymous.getText().toString().equals("Driver")|| etAnonymous.getText().toString().equals("Passenger")){
                    if(ParseUser.getCurrentUser()==null){
                        Log.d(TAG, "onClick: current user null");
                        ParseAnonymousUtils.logIn(new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if(user!=null && e==null){
//                                    FancyToast.makeText(MainActivity.this, "Anonymous user logged in successfully",
//                                            Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                    user.put("as",etAnonymous.getText().toString());
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                FancyToast.makeText(MainActivity.this, "Anonymous user logged in successfully as "+etAnonymous.getText().toString(),
                                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                            }
                                        }
                                    });
                                }else{
                                    FancyToast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                }
                            }
                        });
                    }else{
                        Log.d(TAG, "onClick: current user not null = "+ParseUser.getCurrentUser().getUsername());
                    }
                }else{
                    Toast.makeText(this, "Are you a passenger or driver ?", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;


        }

    }
}