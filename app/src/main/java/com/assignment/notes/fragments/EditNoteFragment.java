package com.assignment.notes.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.assignment.notes.R;
import com.assignment.notes.model.NoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditNoteFragment extends Fragment {
    String docID, noteTitle, noteContent;
    EditText noteTitleEditText, noteContentEditText;
    FloatingActionButton saveNoteFAB, deleteNoteFAB;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_edit_note, container, false);

        noteTitleEditText=(EditText) rootView.findViewById(R.id.editNoteTitle);
        noteContentEditText=(EditText) rootView.findViewById(R.id.editNoteContent);
        saveNoteFAB=(FloatingActionButton) rootView.findViewById(R.id.editNoteFab);
        deleteNoteFAB=(FloatingActionButton) rootView.findViewById(R.id.deleteNotefab);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar =rootView.findViewById(R.id.editNoteToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        noteContentEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        //get data from home fragment
        getParentFragmentManager().setFragmentResultListener("noteDetails", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                docID=result.getString("docID");
                noteTitle= result.getString("title");
                noteContent= result.getString("content");

                noteTitleEditText.append(noteTitle);
                noteContentEditText.append(noteContent);

            }
        });

        saveNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);

                String updatedTitle=noteTitleEditText.getText().toString();
                String updatedContent=noteContentEditText.getText().toString();

                if (updatedTitle.isEmpty() || updatedContent.isEmpty()){
                    noteTitleEditText.requestFocus();
                    Snackbar.make(rootView,"Both fields are required", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    NoteModel updatedNote = new NoteModel();
                    updatedNote.setTitle(updatedTitle);
                    updatedNote.setContent(updatedContent);

                    documentReference.set(updatedNote).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        deleteNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteNote();
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                builder.create();
                builder.show();
            }

            private void deleteNote() {
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(rootView,"Note deleted", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootView,"Unable to delete", Snackbar.LENGTH_SHORT).show();
                    }
                });
                getFragmentManager().popBackStackImmediate();
            }
        });

        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(noteContentEditText.getWindowToken(), 0);
    }

}