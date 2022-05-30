package com.example.mytodoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class RecycleBin extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecyclebinview;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<FirebaseModel, RecycleBin.NoteViewHolder> recyclebin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);
        getSupportActionBar().setTitle("Recycle Bin");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("recycleBin").orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> bin = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();

        recyclebin = new FirestoreRecyclerAdapter<FirebaseModel, RecycleBin.NoteViewHolder>(bin) {
            @RequiresApi(api = Build.VERSION_CODES.M)

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebasemodel) {


                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colourcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=recyclebin.getSnapshots().getSnapshot(i).getId();

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);

                        popupMenu.getMenu().add("Restore").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReferenceForBin=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("recycleBin").document(docId);
                                documentReferenceForBin.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        String title=firebasemodel.getTitle();
                                        String content=firebasemodel.getContent();
                                        DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                                        Map<String ,Object> note= new HashMap<>();
                                        note.put("title",title);
                                        note.put("content",content);
                                        documentReference.set(note);
                                        Toast.makeText(v.getContext(),"Restore Successful",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Failed To Restore",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("recycleBin").document(docId);

                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Failed To Delete",Toast.LENGTH_SHORT).show();
                                    }
                                });

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
                return new RecycleBin.NoteViewHolder(view);
            }

        };
        mrecyclebinview=findViewById(R.id.recyclebinview);
        mrecyclebinview.setHasFixedSize(true);
        linearLayoutManager=new RecycleBin.LinearLayoutManagerWrapper(RecycleBin.this, LinearLayoutManager.VERTICAL, false);
        mrecyclebinview.setLayoutManager(linearLayoutManager);
        mrecyclebinview.setAdapter(recyclebin);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
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
    public class LinearLayoutManagerWrapper extends LinearLayoutManager {


        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        recyclebin.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(recyclebin!=null)
        {
            recyclebin.stopListening();
        }
    }
}