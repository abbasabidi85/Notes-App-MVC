package com.assignment.notes.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assignment.notes.R;
import com.assignment.notes.auth.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    String name, email;
    TextView userName, userEmail;
    CircleImageView userProfileImage;
    MaterialButton logoutButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_profile, container, false);

        userName=rootView.findViewById(R.id.userName);
        userEmail=rootView.findViewById(R.id.userEmail);
        userProfileImage=rootView.findViewById(R.id.userProfileImage);
        logoutButton=(MaterialButton)rootView.findViewById(R.id.userLogoutButton);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        sessionManager= new SessionManager(getActivity().getApplicationContext());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
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

            private void logout() {

                    GoogleSignInClient mGoogleSignInClient ;
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getBaseContext(), gso);
                    mGoogleSignInClient.signOut().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseAuth.signOut();
                                    sessionManager.setLoggedIn(false);
                                    sessionManager.clearSession();
                                    //signout firebase
                                    Fragment fragment = new LoginFragment();
                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(
                                                    R.anim.slide_in,  // enter
                                                    R.anim.fade_out,  // exit
                                                    R.anim.fade_in,   // popEnter
                                                    R.anim.slide_out  // popExit
                                            );
                                    fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
                                    fragmentTransaction.commit();
                                }
                            });
            }
        });

        Toolbar toolbar =rootView.findViewById(R.id.profileToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        name=firebaseUser.getDisplayName();
        email=firebaseUser.getEmail();

        userName.setText(name);
        userEmail.setText(email);

        return rootView;
    }
}