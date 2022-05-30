package com.example.mytodoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EnablePassword extends AppCompatActivity {

    SharedPreferences pwpref;
    SharedPreferences.Editor pweditor;

    private EditText enablepassword;
    private RelativeLayout enablepasswordbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_password);
        getSupportActionBar().hide();

        pwpref = getSharedPreferences("Password", MODE_PRIVATE);
        pweditor = pwpref.edit();
        String noteID = getIntent().getStringExtra("noteId");

        enablepassword = findViewById(R.id.enablepassword);
        enablepasswordbutton = findViewById(R.id.enablepasswordbutton);

        enablepasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = enablepassword.getText().toString().trim();
                if(password.isEmpty()){
                    Toast.makeText(EnablePassword.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    pweditor.putString(noteID, password);
                    pweditor.apply();
                    Toast.makeText(EnablePassword.this, "Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EnablePassword.this,NoteActivity.class));
                }
            }
        });

    }
}