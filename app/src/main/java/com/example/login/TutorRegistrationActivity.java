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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.mateware.snacky.Snacky;

public class TutorRegistrationActivity extends AppCompatActivity {
    View vLog, myView;
    EditText etFullName, etEmail, etPwd, etRePwd, etPhNo, etPincode;
    TextView etFetchLoc;
    Button regBtn;
    String selectedStream;

    boolean isUname = false, isEmail = false, isPhnNo = false, isPwd = false, isRePwdMach = false, isStreamSelected = false, isLevelSelected = false;
    TutorLoginActivity sucSnack;
    ProgressDialog progressDialog;
    Intent intent;
    ArrayAdapter<String> spinnerArrayAdapterSub;
    ArrayAdapter<String> spinnerArrayAdapterLevel;

    String[] selectSubjects = {"Select Subject", "Maths", "English", "Physics", "Computer Science", "All"};
    String[] maths = {"Elementary", "Secondary,Higher Secondary", "Graduation", "Post Graduation"};
    String[] english = {"Elementary", "Secondary,Higher Secondary", "Graduation", "Post Graduation"};
    String[] physics = {"Secondary", "Higher Secondary", "Graduation", "Post Graduation"};
    String[] computerScience = {"Elementary", "Secondary,Higher Secondary", "Graduation", "Post Graduation"};
    String[] all = {"Elementary", "Secondary,Higher Secondary", "Graduation", "Post Graduation"};


    Spinner spinnerSelectStream;
    Spinner spinnnerSelectedCourse;
    String selectedLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_tutor);
        viewBinder();
        objInitializer();
        intent = new Intent(TutorRegistrationActivity.this, TutorLoginActivity.class);
        spinnerArrayAdapterSub = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectSubjects);
        spinnerSelectStream.setAdapter(spinnerArrayAdapterSub);
        etFetchLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spinnerSelectStream.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedStream = (String) spinnerSelectStream.getItemAtPosition(i);
                if (i == 0) {
                    spinnnerSelectedCourse.setVisibility(View.GONE);
                    isStreamSelected = false;

                } else {
                    isStreamSelected = true;
                    switch (i) {
                        case 1:
                            AdapterSetter(maths);
                            break;
                        case 2:
                            AdapterSetter(english);
                            break;
                        case 3:
                            AdapterSetter(physics);
                            break;
                        case 4:
                            AdapterSetter(computerScience);
                            break;
                        case 5:
                            AdapterSetter(all);
                            break;
                        default:


                    }
                }
                spinnnerSelectedCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedLevel = (String) spinnnerSelectedCourse.getItemAtPosition(i);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

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
                if (isUname && isEmail && isPhnNo && isPwd && isRePwdMach && isStreamSelected) {
                    parseConnect();
                }
            }
        });
        isParseUserSignedUpChecker();

    }

    private void AdapterSetter(String[] levels) {
        spinnnerSelectedCourse.setVisibility(View.VISIBLE);
        spinnerArrayAdapterLevel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        spinnnerSelectedCourse.setAdapter(spinnerArrayAdapterLevel);
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
            appUser.put("Selectedcourse", selectedStream);
            appUser.put("Level", selectedLevel);
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
            spinnerSelectStream = findViewById(R.id.spinnerTuts);
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
        spinnerSelectStream = findViewById(R.id.spinnerTuts);
        spinnnerSelectedCourse = findViewById(R.id.spinnerSelectedCourse);
        etFetchLoc = findViewById(R.id.etFetchLoc);


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
        startActivity(new Intent(TutorRegistrationActivity.this, TutorLoginActivity.class));
        TutorRegistrationActivity.this.finish();
    }


}
