package com.example.mytodoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends AppCompatActivity {

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mNewConfirm;
    private RelativeLayout changeButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();

        mOldPassword = findViewById(R.id.oldpassword);
        mNewPassword = findViewById(R.id.newpassword);
        mNewConfirm = findViewById(R.id.newconfirm);
        changeButton = findViewById(R.id.change);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = mOldPassword.getText().toString().trim();
                String newPassword = mNewPassword.getText().toString().trim();
                String newConfirm = mNewConfirm.getText().toString().trim();

                if(oldPassword.isEmpty() || newPassword.isEmpty() || newConfirm.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All Fields are Required",Toast.LENGTH_SHORT).show();
                }
                else if(newPassword.length()<7)
                {
                    Toast.makeText(getApplicationContext(),"Password Should Greater than 7 Digits",Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(newConfirm))
                {
                    Toast.makeText(getApplicationContext(),"Confirm Password Is Not Correct",Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(newConfirm))
                {
                    Toast.makeText(getApplicationContext(),"Confirm Password Is Not Correct",Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Change Password Successful, Login Again To Use App",Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(ChangePassword.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed To Change Password",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }
}