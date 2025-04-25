package com.example.mynotes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    recyclerviewadapter myadapter;
    FloatingActionButton fabxml;



    ArrayList<notesmodel> notesmodelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        RecyclerView recyclerView = findViewById(R.id.recyclerviewxml);
        fabxml = findViewById(R.id.fabxml);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myadapter = new recyclerviewadapter(MainActivity.this, notesmodelArrayList);
        recyclerView.setAdapter(myadapter);

        fabxml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog adddialog = new Dialog(MainActivity.this);
                adddialog.setContentView(R.layout.addupdatedialog);

                EditText titlexml = adddialog.findViewById(R.id.titlexml);
                EditText descxml = adddialog.findViewById(R.id.descxml);
                TextView headingxml = adddialog.findViewById(R.id.headingxml);
                AppCompatButton savebtnxml = adddialog.findViewById(R.id.savebtnxml);

                savebtnxml.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = titlexml.getText().toString();
                        String desc = descxml.getText().toString();
                        headingxml.setText("Add New Note !");
//                        notesmodelArrayList.add(new notesmodel(title, desc));
//                        myadapter.notifyItemInserted(notesmodelArrayList.size()-1);
//                        recyclerView.scrollToPosition(notesmodelArrayList.size()-1);
//                        adddialog.dismiss();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Add to Room DB
                                dbnotesmodel newNote = new dbnotesmodel(title, desc);
                                dbhelpernotes.getDB(MainActivity.this).noteDao().addnote(newNote);

                                // Fetch updated list from DB and update UI
                                List<dbnotesmodel> updatedNotes = dbhelpernotes.getDB(MainActivity.this).noteDao().getall();

                                notesmodelArrayList.clear(); // Clear old list
                                for (dbnotesmodel note : updatedNotes) {
                                    notesmodelArrayList.add(new notesmodel(note.getId(), note.getTitle(), note.getDesc()));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myadapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(notesmodelArrayList.size() - 1);
                                        adddialog.dismiss();
                                    }
                                });
                            }
                        }).start();


                    }
                });
                adddialog.show();
            }
        });



        // Fetch all notes from DB to display in RecyclerView when app starts
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<dbnotesmodel> allnotes = dbhelpernotes.getDB(MainActivity.this).noteDao().getall();
                notesmodelArrayList.clear();
                for (dbnotesmodel note : allnotes) {
                    notesmodelArrayList.add(new notesmodel(note.getId(), note.getTitle(), note.getDesc()));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myadapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();



        List<dbnotesmodel> allnotes = dbhelpernotes.getDB(MainActivity.this).noteDao().getall();




    }
}