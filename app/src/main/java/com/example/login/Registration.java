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

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.mateware.snacky.Snacky;

public class Registration extends AppCompatActivity {
    View vLog, myView;
    EditText etFullName, etEmail, etPwd, etRePwd, etPhNo;
    Button regBtn;
    boolean isUname = false, isEmail = false, isPhnNo = false, isPwd = false, isRePwdMach = false;
    MainActivity sucSnack;
    ProgressDialog progressDialog;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        viewBinder();
        objInitializer();
        intent = new Intent(Registration.this, MainActivity.class);
        vLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameValidator(etFullName.getText().toString());
                emailValidator(etEmail.getText().toString());
                phoneNoValidator(etPhNo.getText().toString());
                passwordValidator(etPwd.getText().toString());
                reEnterPwdValidator(etRePwd.getText().toString());
                if (isUname && isEmail && isPhnNo && isPwd && isRePwdMach) {
                    parseConnect();
                }
            }
        });
        isParseUserSignedUpChecker();

    }

    private void isParseUserSignedUpChecker() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
    }

    private void objInitializer() {
        sucSnack = new MainActivity();
        progressDialog = new ProgressDialog(this);
    }

    private void parseConnect() {
        if (isOnline()) {

            ParseUser appUser = new ParseUser();
            appUser.setUsername(etFullName.getText().toString());
            appUser.setPassword(etPwd.getText().toString());
            appUser.setEmail(etEmail.getText().toString());
            appUser.put("Phone", etPhNo.getText().toString());
            progressDialog.setMessage("Registering " + etFullName.getText());

            progressDialog.show();
            appUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        successSnackBuilderLengthLong("Registration success " + etFullName.getText().toString(), myView);

                    } else {
                        sucSnack.errorSnackBuilder(e.getMessage(), myView);
                    }
                    progressDialog.dismiss();
                }
            });

        } else {
            sucSnack.errorSnackBuilder("Please check your internet connection and try again", myView);
        }

    }


    public void reEnterPwdValidator(String repwd) {
        String prvpwd = null;
        if (isPwd) {
            prvpwd = etPwd.getText().toString();
            pwdChecker(prvpwd, repwd);
        } else if (repwd.length() == 0) {
            FancyToast.makeText(this, "Please re-enter your password for confirmation ", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            isRePwdMach = false;
        }
    }

    public void pwdChecker(String prvpwd, String repwd) {
        if (prvpwd.equals(repwd)) {
            isRePwdMach = true;
        } else {
            isRePwdMach = false;
            FancyToast.makeText(this, "Entered password doesnot match ", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

        }

    }

    public void passwordValidator(String pwd) {
        if (pwd.length() == 0) {
            FancyToast.makeText(this, "Please enter password ", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            isPwd = false;
        } else {
            isPwd = true;
        }
    }

    public void phoneNoValidator(String phno) {
        if (phno.length() == 10) {
            isPhnNo = true;
        } else if (phno.length() == 0) {
            FancyToast.makeText(this, "Phone no feield is empty ", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isPhnNo = false;
        } else {
            FancyToast.makeText(this, "Please enter a valid phone no ", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isPhnNo = false;
        }
    }

    public void emailValidator(String email) {
        if (TextUtils.isEmpty(email)) {
            FancyToast.makeText(this, "Email feild is empty", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isEmail = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FancyToast.makeText(this, "Enter a valid email address", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            isEmail = false;
        } else {
            isEmail = true;
        }


    }


    public void nameValidator(String name) {
        if (name.length() == 0) {
            FancyToast.makeText(this, "Please enter Username", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            isUname = false;
        } else {
            isUname = true;
        }

    }

    private void viewBinder() {
        vLog = findViewById(R.id.vLog);
        etFullName = findViewById(R.id.etFullname);
        etEmail = findViewById(R.id.etEmail);
        etPwd = findViewById(R.id.etPwd);
        etRePwd = findViewById(R.id.etRePwd);
        etPhNo = findViewById(R.id.etPhno);
        regBtn = findViewById(R.id.regBtn);
        myView = findViewById(R.id.regLayout);

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void successSnackBuilderLengthLong(String msg, View snackView) {
        Snacky.builder().setView(snackView).setBackgroundColor(Color.parseColor("#FFA726")).setText(msg).setActionText("Ok").setActionTextColor(Color.parseColor("#ffffff"))
                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        successActionTriggered();
                    }
                })
                .setTextColor(Color.parseColor("#ffffff"))
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .success()


                .show();
    }

    private void successActionTriggered() {
        startActivity(new Intent(Registration.this, MainActivity.class));
        Registration.this.finish();
    }


}
