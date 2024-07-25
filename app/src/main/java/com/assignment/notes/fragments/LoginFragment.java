package com.assignment.notes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.assignment.notes.R;
import com.assignment.notes.auth.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment {

    SignInButton signInButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    SessionManager sessionManager;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        signInButton=(SignInButton) rootView.findViewById(R.id.sign_in_button);

        firebaseAuth= FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        //check if the user is already signed in
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        if (sessionManager.isLoggedIn() && firebaseAuth.getCurrentUser()!=null){

            Fragment fragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
            fragmentTransaction.commit();
        }
        else
        {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(),gso);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    googleSignIn();
                }
            });
        }

        return rootView;
    }
    private void googleSignIn() {

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            } catch (Exception e){
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {

        View v=getActivity().findViewById(R.id.fragmentLoginLayout);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            sessionManager.setLoggedIn(true);

                            Map<String, Object> map= new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("name", user.getDisplayName());
                            map.put("profile", user.getPhotoUrl().toString());

                            DocumentReference documentReference = db.collection("users").document(user.getUid());
                            documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Fragment fragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(
                                                    R.anim.slide_in,  // enter
                                                    R.anim.fade_out,  // exit
                                                    R.anim.fade_in,   // popEnter
                                                    R.anim.slide_out  // popExit
                                            );
                                    fragmentTransaction.replace(R.id.mainFrameLayout,fragment);
                                    fragmentTransaction.commit();
                                    Snackbar.make(v,"Welcome back, " + firebaseAuth.getCurrentUser().getDisplayName(), Snackbar.LENGTH_SHORT).show();

                                }
                            });


                        }
                        else {
                            Snackbar.make(v,"Something went wrong", Snackbar.LENGTH_SHORT).show();
                            sessionManager.setLoggedIn(false);
                        }
                    }
                });
}
}