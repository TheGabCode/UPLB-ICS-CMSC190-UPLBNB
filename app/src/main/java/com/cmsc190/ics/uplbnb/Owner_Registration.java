package com.cmsc190.ics.uplbnb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Dell on 8 Feb 2018.
 */

public class Owner_Registration extends Fragment implements View.OnClickListener {
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextNumber;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth loginAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        loginAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        View view =  inflater.inflate(R.layout.fragment_registration_owner,container,false);

        progressDialog = new ProgressDialog(getActivity());
        buttonRegister = (Button) view.findViewById(R.id.buttonFinishOwnerRegister);
        editTextEmail = (EditText) view.findViewById(R.id.editTextRegEmailOwner);
        editTextPassword = (EditText) view.findViewById(R.id.editTextRegPasswordOwner);
        editTextFirstName = (EditText) view.findViewById(R.id.editTextRegFirstNameOwner);
        editTextLastName = (EditText) view.findViewById(R.id.editTextRegLastNameOwner);
        editTextNumber = (EditText) view.findViewById(R.id.editTextRegNumberOwner);
        buttonRegister.setOnClickListener(this);
        return view;
    }



    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFirstName.getText().toString().trim();
        final String last_name = editTextLastName.getText().toString().trim();
        final String number = editTextNumber.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getActivity(), "Please enter email.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(), "Please enter password.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(first_name)){
            Toast.makeText(getActivity(), "Please enter first name.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(last_name)){
            Toast.makeText(getActivity(), "Please enter full name.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(number)){
            Toast.makeText(getActivity(),"Please enter number.",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering Owner...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            loginAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        String id = firebaseUser.getUid();
                                        User new_user = new User(email,password,first_name,last_name,"owner",number,id);
                                        databaseReference.child("user").child(id).setValue(new_user);
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(),"Owner successfully registered!",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getContext(),OwnerHome.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        progressDialog.dismiss();

                                    }
                                    else{
                                        Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            });







                        }
                        else{
                            progressDialog.dismiss();
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getActivity(),"Email already registered!",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View view){
        if(view == buttonRegister){
            registerUser();
        }
    }
}
