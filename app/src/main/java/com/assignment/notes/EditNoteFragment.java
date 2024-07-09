package com.assignment.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditNoteFragment extends Fragment {
    String docID, noteTitleText, noteContentText, title, content;
    EditText noteTitle, noteContent;
    FloatingActionButton saveNoteFAB, deleteNoteFAB;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_edit_note, container, false);

        noteTitle=(EditText) rootView.findViewById(R.id.editNoteTitle);
        noteContent=(EditText) rootView.findViewById(R.id.editNoteContent);
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

        //get data from home fragment
        getParentFragmentManager().setFragmentResultListener("noteDetails", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                docID=result.getString("docID");
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);

                getTitleText(documentReference);
                getContentText(documentReference);
            }

            //get Title and content text from document snapshot
            private String getTitleText(DocumentReference documentReference) {
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                      title=documentSnapshot.getString("title");
                        noteTitle.setText(title);
                    }
                });
                return title;
            }
            private String getContentText(DocumentReference documentReference) {
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        content=documentSnapshot.getString("content");
                        noteContent.setText(content);
                    }
                });
                return content;
            }
        });

        saveNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);

                String updatedTitle=noteTitle.getText().toString();
                String updatedContent=noteContent.getText().toString();

                if (updatedTitle.isEmpty() || updatedContent.isEmpty()){
                    noteTitle.requestFocus();
                    Toast.makeText(getContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String,Object> updatedNote=new HashMap<>();
                    updatedNote.put("title",updatedTitle);
                    updatedNote.put("content", updatedContent);
                    documentReference.set(updatedNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStackImmediate();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        deleteNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStackImmediate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return rootView;
    }
}