package com.assignment.notes.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.assignment.notes.R;
import com.assignment.notes.model.SaveNote;
import com.assignment.notes.model.NoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EditNoteFragment extends Fragment implements SaveNote {
    String docID, formattedDateTime, noteTitle, noteContent;
    TextView dateTime;
    EditText noteTitleEditText, noteContentEditText;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_edit_note, container, false);

        dateTime=rootView.findViewById(R.id.editNoteDateTime);
        noteTitleEditText=(EditText) rootView.findViewById(R.id.editNoteTitle);
        noteContentEditText=(EditText) rootView.findViewById(R.id.editNoteContent);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar =(Toolbar) rootView.findViewById(R.id.editNoteToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                formattedDateTime=result.getString("dateTime");
                docID=result.getString("docID");
                noteTitle= result.getString("title");
                noteContent= result.getString("content");

                dateTime.append(formattedDateTime);
                noteTitleEditText.append(noteTitle);
                noteContentEditText.append(noteContent);

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
    //save note on pressing back button
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(noteTitleEditText.getText().toString().equals(noteTitle) && noteContentEditText.getText().toString().equals(noteContent)){

                }else if (noteTitleEditText.getText().toString().isEmpty() && noteContentEditText.getText().toString().isEmpty()){

                }else{
                    onBackSaveNote();
                }

                setEnabled(false); // Disable the callback after handling
                requireActivity().onBackPressed(); // Call the activity's back pressed method
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void onBackSaveNote() {
        String updatedDateTime=getTime();
        String updatedTitle=noteTitleEditText.getText().toString();
        String updatedContent=noteContentEditText.getText().toString();
        saveNote(updatedDateTime,docID,updatedTitle,updatedContent);
    }

    private String getTime() {
        String updatedDateTime=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            // Define the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mm:a");

            // Format the current date and time
            updatedDateTime = now.format(formatter);
        }
        return updatedDateTime;
    }

    @Override
    public void saveNote(String updatedDateTime, String docID, String updatedTitle, String updatedContent) {
        DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);

        NoteModel updatedNote = new NoteModel();
        updatedNote.setDateTime(updatedDateTime);
        updatedNote.setTitle(updatedTitle);
        updatedNote.setContent(updatedContent);

        documentReference.set(updatedNote).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public void uploadNote(String formattedDateTime, String title, String content) {
    }

    private void deleteNote(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Delete note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docID);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(getView(),"Note deleted", Snackbar.LENGTH_SHORT).show();
                                getFragmentManager().popBackStackImmediate();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(getView(),"Unable to delete", Snackbar.LENGTH_SHORT).show();
                            }
                        });
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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.delete_action_button, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int res_id= item.getItemId();

        if (res_id==R.id.action_delete){
            deleteNote();
        }
        return true;
    }
}