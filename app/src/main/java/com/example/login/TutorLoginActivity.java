package com.example.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.mateware.snacky.Snacky;

public class TutorLoginActivity extends AppCompatActivity {
    EditText etEmail, etPwd;
    Button login;
    View fgPwd, vReg, myView;
    Spinner spinner;
    String selectedTeacherType;

    String emailFormat = "[[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+]";
    boolean isOkEmail = false;
    boolean isOkPwd = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();


        progressDialog = new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailValidation(etEmail.getText().toString().trim());
                pwdValidation(etPwd.getText().toString().trim());
                if (isOkEmail && isOkPwd) {
                    parseLogin();
                }

            }
        });
        SetTutorType();
        fgPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TutorLoginActivity.this, ForgotPasswordActivityTutor.class));
            }
        });

        //ParseInstallation.getCurrentInstallation().saveInBackground();
        vReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TutorLoginActivity.this, TutorRegistrationActivity.class));

            }
        });
        isParseUserLoggedIn();
    }

    private void SetTutorType() {
    }

    private void isParseUserLoggedIn() {
        if (ParseUser.getCurrentUser() != null) {
            //ParseUser.getCurrentUser().logOut();
        }
    }

    private void TransitionToSocialMedia() {
    }

    private void parseLogin() {
        if (isOnline()) {
            progressDialog.setMessage("Logging in ");

            progressDialog.show();
            ParseUser.logInInBackground(etEmail.getText().toString(), etPwd.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null && e == null) {
                        FancyToast.makeText(TutorLoginActivity.this, user.get("username") + " " + "successfully logged in", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                        TransitionToSocialMedia();

                    } else {
                        errorSnackBuilder(e.getMessage(), myView);
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            errorSnackBuilder("Please Check your internet connection", myView);
        }
    }

    private void pwdValidation(String password) {
        if (TextUtils.isEmpty(password)) {

            FancyToast.makeText(this, "Enter a valid password !", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            isOkPwd = false;
        } else {
            isOkPwd = true;
        }
    }

    private void bindViews() {
        etEmail = findViewById(R.id.etEmail);
        etPwd = findViewById(R.id.etPwd);
        login = findViewById(R.id.logBtn);
        fgPwd = findViewById(R.id.tvFgPwd);
        vReg = findViewById(R.id.vReg);
        myView = findViewById(R.id.logView);
        spinner = findViewById(R.id.spinnerTuts);

    }

    public void emailValidation(String email) {

        if (TextUtils.isEmpty(email)) {
            FancyToast.makeText(this, "Email feild is empty", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isOkEmail = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FancyToast.makeText(this, "Enter a valid email address", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isOkEmail = false;
        } else {
            isOkEmail = true;
        }


    }

    public void successSnackBuilder(String msg, View snackView) {
        Snacky.builder().setView(snackView).setBackgroundColor(Color.parseColor("#FFA726")).setText(msg).setActionText("Ok").setActionTextColor(Color.parseColor("#ffffff"))

                .setTextColor(Color.parseColor("#ffffff"))

                .success()


                .show();
    }


    public void errorSnackBuilder(String msg, View snackView) {
        Snacky.builder().setView(snackView).setBackgroundColor(Color.parseColor("#B71C1C")).setText(msg).setActionText("Ok").setActionTextColor(Color.parseColor("#ffffff"))

                .setTextColor(Color.parseColor("#ffffff"))
                .error()


                .show();
    }


    private void errorsActionTriggered() {

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
