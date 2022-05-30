package com.example.mytodoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DisablePassword extends AppCompatActivity {

    SharedPreferences pwpref;
    SharedPreferences.Editor pweditor;

    private EditText disablepassword;
    private RelativeLayout disablepasswordbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_password);
        getSupportActionBar().hide();

        pwpref = getSharedPreferences("Password", MODE_PRIVATE);
        pweditor = pwpref.edit();
        String noteID = getIntent().getStringExtra("noteId");
        boolean change = getIntent().getBooleanExtra("change", false);

        disablepassword = findViewById(R.id.disablepassword);
        disablepasswordbutton = findViewById(R.id.disablepasswordbutton);

        disablepasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordinput = disablepassword.getText().toString().trim();
                String password = pwpref.getString(noteID,"");
                if(passwordinput.equals(password)){
                    if(!change){
                        pweditor.putString(noteID, "");
                        pweditor.apply();
                        Toast.makeText(DisablePassword.this, "Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DisablePassword.this,NoteActivity.class));
                    }
                    else{
                        pweditor.putString(noteID, "");
                        pweditor.apply();
                        Intent intent=new Intent(DisablePassword.this,EnablePassword.class);
                        intent.putExtra("noteId",noteID);
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(DisablePassword.this, "Your Password Is Not Correct", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}