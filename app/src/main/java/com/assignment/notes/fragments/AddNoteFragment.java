package com.assignment.notes.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.assignment.notes.R;
import com.assignment.notes.model.NoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNoteFragment extends Fragment {

    EditText mNoteTitle, mNoteContent;
    FloatingActionButton fabUploadNote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_note, container, false);

        mNoteTitle=rootView.findViewById(R.id.addNoteTitle);
        mNoteContent=rootView.findViewById(R.id.addNoteContent);
        fabUploadNote=(FloatingActionButton) rootView.findViewById(R.id.addNotFab);

        firebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        mNoteTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);


        Toolbar toolbar =rootView.findViewById(R.id.addNoteToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fabUploadNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mNoteTitle.getText().toString();
                String content=mNoteContent.getText().toString();

                if (title.isEmpty() || content.isEmpty()){
                    mNoteTitle.requestFocus();
                    Snackbar.make(rootView,"Both fields are required", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    //TODO
                    NoteModel newNote = new NoteModel();
                    newNote.setTitle(title);
                    newNote.setContent(content);

                    DocumentReference documentReference=db.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    documentReference.set(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(rootView,"Something went wrong", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    getFragmentManager().popBackStackImmediate();

                }
            }
        });


        return rootView;
    }
}
