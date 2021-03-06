package com.example.mytodoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NoteActivity extends AppCompatActivity{

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    int currentViewMode;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> noteAdapter;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    SharedPreferences pwpref;
    SharedPreferences.Editor pweditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mcreatenotesfab=findViewById(R.id.createnotefab);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        pref = getSharedPreferences("NoteActivity", MODE_PRIVATE);
        editor = pref.edit();

        pwpref = getSharedPreferences("Password", MODE_PRIVATE);
        pweditor = pwpref.edit();

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteActivity.this,AddNote.class));
            }
        });


        Query query=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();

        noteAdapter= new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebasemodel) {

                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colourcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                String pass = pwpref.getString(docId, "");


                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!pass.isEmpty()){
                            Intent intent=new Intent(v.getContext(),EnterPassword.class);
                            intent.putExtra("title",firebasemodel.getTitle());
                            intent.putExtra("content",firebasemodel.getContent());
                            intent.putExtra("noteId",docId);
                            intent.putExtra("mode",0);
                            v.getContext().startActivity(intent);
                        }
                        else{
                            Intent intent=new Intent(v.getContext(),EditNote.class);
                            intent.putExtra("title",firebasemodel.getTitle());
                            intent.putExtra("content",firebasemodel.getContent());
                            intent.putExtra("noteId",docId);
                            v.getContext().startActivity(intent);
                        }

                    }
                });


                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(!pass.isEmpty()){
                                    Intent intent=new Intent(v.getContext(),EnterPassword.class);
                                    intent.putExtra("title",firebasemodel.getTitle());
                                    intent.putExtra("content",firebasemodel.getContent());
                                    intent.putExtra("noteId",docId);
                                    intent.putExtra("mode",1);
                                    v.getContext().startActivity(intent);
                                }
                                else{
                                    DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            String title=firebasemodel.getTitle();
                                            String content=firebasemodel.getContent();
                                            DocumentReference documentReferenceForBin=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("recycleBin").document();
                                            Map<String ,Object> note= new HashMap<>();
                                            note.put("title",title);
                                            note.put("content",content);
                                            documentReferenceForBin.set(note);
                                            Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(),"Failed To Delete",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                                return false;
                            }
                        });

                        popupMenu.getMenu().add(pass.isEmpty()?"Enable Password":"Disable Password").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(pass.isEmpty()){
                                    Intent intent=new Intent(NoteActivity.this,EnablePassword.class);
                                    intent.putExtra("noteId",docId);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent=new Intent(NoteActivity.this,DisablePassword.class);
                                    intent.putExtra("noteId",docId);
                                    intent.putExtra("change", false);
                                    startActivity(intent);
                                }

                                return false;
                            }
                        });
                        if(!pass.isEmpty()){
                            popupMenu.getMenu().add("Change Password").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent=new Intent(NoteActivity.this,DisablePassword.class);
                                    intent.putExtra("noteId",docId);
                                    intent.putExtra("change", true);
                                    startActivity(intent);
                                    return false;
                                }
                            });
                        }
                        popupMenu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(!pass.isEmpty()){
                                    Intent intent=new Intent(v.getContext(),EnterPassword.class);
                                    intent.putExtra("title",firebasemodel.getTitle());
                                    intent.putExtra("content",firebasemodel.getContent());
                                    intent.putExtra("noteId",docId);
                                    intent.putExtra("mode",2);
                                    v.getContext().startActivity(intent);
                                }
                                else{
                                    Toast.makeText(v.getContext(),"Share",Toast.LENGTH_SHORT).show();
                                    String title = firebasemodel.getTitle();
                                    String content = firebasemodel.getContent();
                                    Intent sendIntent  = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    sendIntent.setType("text/plain");
                                    startActivity(shareIntent);
                                }

                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManagerWraper(2,StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager=new LinearLayoutManagerWrapper(NoteActivity.this, LinearLayoutManager.VERTICAL, false);
        currentViewMode = pref.getInt("currentViewMode", 0);
        if(currentViewMode == 0){
            mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        }
        else {
            mrecyclerview.setLayoutManager(linearLayoutManager);
        }
        mrecyclerview.setAdapter(noteAdapter);
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NoteActivity.this,MainActivity.class));
                break;
            case R.id.switchview:
                currentViewMode = pref.getInt("currentViewMode", 0);
                if(currentViewMode == 0){
                    editor.putInt("currentViewMode", 1);
                    editor.apply();
                    mrecyclerview.setLayoutManager(linearLayoutManager);
                }
                else{
                    editor.putInt("currentViewMode", 0);
                    editor.apply();
                    mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
                }
                mrecyclerview.setAdapter(noteAdapter);
                break;
            case R.id.about:
                startActivity(new Intent(NoteActivity.this,About.class));
                break;
            case R.id.settings:
                startActivity(new Intent(NoteActivity.this,Settings.class));
                break;
            case R.id.changepassword:
                startActivity(new Intent(NoteActivity.this,ChangePassword.class));
                break;
            case R.id.recyclebin:
                startActivity(new Intent(NoteActivity.this,RecycleBin.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }


    private int getRandomColor() {
        List<Integer> colorcode=new ArrayList<>();

        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.color6);
        colorcode.add(R.color.color7);
        colorcode.add(R.color.color8);
        colorcode.add(R.color.color9);
        colorcode.add(R.color.color10);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);

    }

    public class StaggeredGridLayoutManagerWraper extends androidx.recyclerview.widget.StaggeredGridLayoutManager{

        public StaggeredGridLayoutManagerWraper(int spanCount, int orientation) {
            super(spanCount, orientation);
        }
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {


        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}