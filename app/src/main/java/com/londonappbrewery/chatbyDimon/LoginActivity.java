package com.londonappbrewery.chatbyDimon;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    // member variables
    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // FireBaseAuth Instance
        mAuth = FirebaseAuth.getInstance();

        try {
            if (mAuth.getCurrentUser() != null) {
                Log.d("chatlog", "User is already logged in");
                Intent i = new Intent(LoginActivity.this, MainChatActivity.class);
                finish();
                startActivity(i);
            }
        }catch(Exception e)
        {
            Log.d("chatlog","user is not logged in" + e);
        }
    }


    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
         //making an attempt to log in
        attemptLogin();

    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.londonappbrewery.chatbyDimon.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {

        //Use FirebaseAuth to sign in with email & password
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(email.equals("") || password.equals("")){
            showAlertDialog("Please enter email & password");
            return;
        }
        Toast.makeText(this,"Logging in...", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    Log.d("chatlog","SigninWithEmailAndPassword FAILED!",task.getException());
                    showAlertDialog("There was a problem signing in");
                } else{
                    Log.d("chatlog","SigninWithEmailAndPassword Sucessfull!"+task.isSuccessful());
                    Intent intent = new Intent(LoginActivity.this,MainChatActivity.class);
                    finish();
                    startActivity(intent);
                    }
            }
        });
    }

    //Show error on screen with an alert dialog
    private void showAlertDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




}