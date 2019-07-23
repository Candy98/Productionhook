package com.example.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etEmail;
    Button submitEmailFgp;
    boolean isOkEmail = false;
    MainActivity snackBuilder;
    View myView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        viewBinder();
        snackBuilder = new MainActivity();
        progressDialog = new ProgressDialog(this);

        submitEmailFgp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAddressValidator(etEmail.getText().toString());
                if (isOkEmail) {
                    parseEmailChecker();
                }

            }
        });


    }

    private void parseEmailChecker() {
        progressDialog.setMessage("Resetting");
        progressDialog.show();
        ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery("User");
        userParseQuery.whereEqualTo("email", etEmail.getText().toString());
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects != null && e == null) {
                    parseSendEmailForPwd();
                } else {
                    snackBuilder.errorSnackBuilder(e.getMessage(), myView);
                }
                progressDialog.dismiss();
            }
        });
    }

    private void parseSendEmailForPwd() {
        ParseUser user = new ParseUser();
        progressDialog.setMessage("Resetting");
        progressDialog.show();
        ParseUser.requestPasswordResetInBackground(etEmail.getText().toString(), new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    snackBuilder.successSnackBuilder("Password reset link sent successfully to " + etEmail.getText().toString(), myView);
                } else {
                    snackBuilder.errorSnackBuilder(e.getMessage(), myView);
                }
                progressDialog.dismiss();
            }
        });

    }

    private void emailAddressValidator(String email) {

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


    private void viewBinder() {
        etEmail = findViewById(R.id.etEmailFgp);
        submitEmailFgp = findViewById(R.id.submitEmailFgp);
        myView = findViewById(R.id.fgpView);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
