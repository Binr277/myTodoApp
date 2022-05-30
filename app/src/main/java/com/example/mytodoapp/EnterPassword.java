package com.example.mytodoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EnterPassword extends AppCompatActivity {

    SharedPreferences pwpref;
    SharedPreferences.Editor pweditor;

    private EditText enterpassword;
    private RelativeLayout enterpasswordbutton;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        getSupportActionBar().hide();

        pwpref = getSharedPreferences("Password", MODE_PRIVATE);
        pweditor = pwpref.edit();
        String noteID = getIntent().getStringExtra("noteId");
        int mode = getIntent().getIntExtra("mode",0);


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore= FirebaseFirestore.getInstance();

        enterpassword = findViewById(R.id.enterpassword);
        enterpasswordbutton = findViewById(R.id.enterpasswordbutton);

        enterpasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordinput = enterpassword.getText().toString().trim();
                String password = pwpref.getString(noteID,"");
                if(passwordinput.equals(password)){
                    if(mode==0){
                        //edit
                        String notetitle=getIntent().getStringExtra("title");
                        String notecontent=getIntent().getStringExtra("content");

                        Intent intent=new Intent(EnterPassword.this,EditNote.class);
                        intent.putExtra("title",notetitle);
                        intent.putExtra("content",notecontent);
                        intent.putExtra("noteId",noteID);
                        startActivity(intent);
                    }
                    else if(mode==1){
                        //delete
                        DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(noteID);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String title=getIntent().getStringExtra("title");
                                String content=getIntent().getStringExtra("content");
                                DocumentReference documentReferenceForBin=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("recycleBin").document();
                                Map<String ,Object> note= new HashMap<>();
                                note.put("title",title);
                                note.put("content",content);
                                documentReferenceForBin.set(note);
                                Toast.makeText(EnterPassword.this, "This note is deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EnterPassword.this,NoteActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EnterPassword.this, "Fail to delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(EnterPassword.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}