package com.assignment.notes.fragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.notes.adapter.NotesAdapter;
import com.assignment.notes.R;
import com.assignment.notes.model.NoteModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment implements NotesAdapter.NoteListClickListener, NotesAdapter.CheckIfEmpty{

    Toolbar toolbar;
    FloatingActionButton fabAdd;

    TextView noNotes;
    ImageView noNotesImg;
    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    NotesAdapter notesAdapter;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    CollectionReference collectionReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        noNotes=(TextView)rootView.findViewById(R.id.noNotes);
        noNotesImg=(ImageView)rootView.findViewById(R.id.noNotesImg);
        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (isDarkModeEnabled()) { // check if dark mode is enabled
                actionBar.setLogo(R.drawable.notes_logo_dark);
            } else {
                actionBar.setLogo(R.drawable.notes_logo);
            }
        }

        fabAdd=(FloatingActionButton) rootView.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new AddNoteFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        );;
                fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mRecyclerView=(RecyclerView) rootView.findViewById(R.id.recyclerView);
        setupRecyclerView();

        return rootView;
    }

    private void setupRecyclerView() {
        collectionReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes");
        Query query=collectionReference.orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NoteModel> options = new FirestoreRecyclerOptions.Builder<NoteModel>()
                .setQuery(query, NoteModel.class).build();
        mStaggeredGridLayoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notesAdapter= new NotesAdapter(options,getContext(),this,this);
        }
        mRecyclerView.setAdapter(notesAdapter);
    }


    @Override
    public void onClickItem(String dateTime, String docID, String noteTitle, String noteContent) {

        Fragment fragment = new EditNoteFragment();
        Bundle result = new Bundle();
        result.putString("dateTime",dateTime);
        result.putString("docID",docID);
        result.putString("title", noteTitle);
        result.putString("content", noteContent);
        getParentFragmentManager().setFragmentResult("noteDetails",result);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                );

        fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //Toast.makeText(getContext(), docID, Toast.LENGTH_SHORT).show();
    }

    public boolean isDarkModeEnabled() {
        int nightModeFlags = getActivity().getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
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
        menuInflater.inflate(R.menu.navigation_drawer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int res_id= item.getItemId();

        if (res_id==R.id.action_profile){
            Fragment fragment = new ProfileFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    );;
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return true;
    }

    
    @Override
    public void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void isAdapterEmpty() {
        if(notesAdapter.getItemCount()==0){
            noNotesImg.setVisibility(View.VISIBLE);
            noNotes.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}