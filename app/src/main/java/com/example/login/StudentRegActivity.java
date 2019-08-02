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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.mateware.snacky.Snacky;

public class StudentRegActivity extends AppCompatActivity {
    View vLog, myView;
    EditText etFullName, etEmail, etPwd, etRePwd, etPhNo;
    Button regBtn;
    String selectedCourse;
    boolean isUname = false, isEmail = false, isPhnNo = false, isPwd = false, isRePwdMach = false;
    TutorLoginActivity sucSnack;
    ProgressDialog progressDialog;
    Intent intent;
    ArrayAdapter<String> arrayAdapter;

    String[] tutorTypes = {"Select Course", "BCA", "Btech", "Primary School", "High School", "Higher Secondary"};
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_tutor);
        viewBinder();
        objInitializer();
        //intent = new Intent(TutorRegistrationActivity.this, TutorLoginActivity.class);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tutorTypes);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    selectedCourse= (String) spinner.getItemAtPosition(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        sucSnack = new TutorLoginActivity();
        progressDialog = new ProgressDialog(this);
    }

    private void parseConnect() {
        if (isOnline()) {

            ParseUser appUser = new ParseUser();
            appUser.setUsername(etFullName.getText().toString());
            appUser.setPassword(etPwd.getText().toString());
            appUser.setEmail(etEmail.getText().toString());
            appUser.put("Phone", etPhNo.getText().toString());
            appUser.put("Selectedcourse",selectedCourse);
            progressDialog.setMessage("Registering " + etFullName.getText());

            progressDialog.show();
            appUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        successSnackBuilderLengthLong("TutorRegistrationActivity success " + etFullName.getText().toString(), myView);

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
        etFullName = findViewById(R.id.etFullnameSt);
        etEmail = findViewById(R.id.etEmailSt);
        etRePwd = findViewById(R.id.etRePwdSt);
        etPwd = findViewById(R.id.etPwdSt);
        etRePwd = findViewById(R.id.etRePwdSt);
        etPhNo = findViewById(R.id.etphoneNoSt);
        regBtn = findViewById(R.id.regBtnSt);
        myView = findViewById(R.id.regLayoutSt);
        spinner = findViewById(R.id.spinnerSelectedCourseSt);

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
        startActivity(new Intent(StudentRegActivity.this, TutorLoginActivity.class));
        StudentRegActivity.this.finish();
    }


}
