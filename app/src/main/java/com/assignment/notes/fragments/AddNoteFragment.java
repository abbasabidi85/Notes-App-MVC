package com.assignment.notes.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.assignment.notes.R;
import com.assignment.notes.model.SaveNote;
import com.assignment.notes.model.NoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddNoteFragment extends Fragment implements SaveNote {

    String formattedDateTime;
    TextView dateTime;
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


        dateTime=rootView.findViewById(R.id.addNoteDateTime);
        mNoteTitle=rootView.findViewById(R.id.addNoteTitle);
        mNoteContent=rootView.findViewById(R.id.addNoteContent);
        //fabUploadNote=(FloatingActionButton) rootView.findViewById(R.id.addNotFab);

        firebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        mNoteTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);


        Toolbar toolbar =(Toolbar) rootView.findViewById(R.id.addNoteToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            // Define the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mm a");

            // Format the current date and time
            formattedDateTime = now.format(formatter);
        }

        dateTime.setText(formattedDateTime);


        return rootView;

    }

    //save note on pressing back button
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (mNoteTitle.getText().toString().isEmpty() && mNoteContent.getText().toString().isEmpty()){
                    Snackbar.make(getView(),"Empty note discarded",Snackbar.LENGTH_SHORT).show();
                }else {
                    String dateTime=formattedDateTime;
                    String title = mNoteTitle.getText().toString();
                    String content=mNoteContent.getText().toString();
                    uploadNote(dateTime,title,content);
                }

                setEnabled(false); // Disable the callback after handling
                requireActivity().onBackPressed(); // Call the activity's back pressed method
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void uploadNote(String formattedDateTime, String title, String content) {

        if (title.isEmpty() && content.isEmpty()){
            Snackbar.make(getView(),"Empty note discarded", Snackbar.LENGTH_SHORT).show();
        }
        else {
            NoteModel newNote = new NoteModel();
            newNote.setDateTime(formattedDateTime);
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
                    Snackbar.make(getView(),"Something went wrong", Snackbar.LENGTH_SHORT).show();
                }
            });

    }
}

    @Override
    public void saveNote(String updatedDateTime, String docID, String title, String content) {

    }

}

