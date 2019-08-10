package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    public static final String TAG = "SIN_UP_ACTITVITY";
    public static final String SIGN_UP_ACTIVITY_ACTION = "com.example.traveldeal.VIEW_DEALS_ACTIVITY";
    private FirebaseAuth mFirebaseAuth;
    private TextView txtEmail;
    private TextView txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getActionBar().setTitle("Sign Up");

        txtEmail = findViewById(R.id.edtEmailAddress);
        txtPassword= findViewById(R.id.edtPassword);
        final Button register = findViewById(R.id.btnRegister);

        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.createUserWithEmailAndPassword(email, password);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AuthUI.getInstance()
                                .signOut(SignInActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                        Intent intent = new Intent(SIGN_UP_ACTIVITY_ACTION);
                                        startActivity(intent);
                                    }
                                });
                    }
                }, 4000);
            }
        });
    }

    private  void updateUI(FirebaseUser user){

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    }
}
