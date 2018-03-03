package com.cmsc190.ics.uplbnb;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 8 Feb 2018.
 */

public class Renter_Registration extends Fragment implements View.OnClickListener {
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();

        View view =  inflater.inflate(R.layout.fragment_registration,container,false);

        progressDialog = new ProgressDialog(getActivity());
        buttonRegister = (Button) view.findViewById(R.id.buttonFinishRenterRegister);
        editTextEmail = (EditText) view.findViewById(R.id.editTextRegEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextRegPassword);
        editTextFirstName = (EditText) view.findViewById(R.id.editTextRegFirstName);
        editTextLastName = (EditText) view.findViewById(R.id.editTextRegLastName);
        buttonRegister.setOnClickListener(this);
        return view;
    }



    private void registerUser(){
        final Map<String, User> users = new HashMap<String,User>();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFirstName.getText().toString().trim();
        final String last_name = editTextLastName.getText().toString().trim();

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

        progressDialog.setMessage("Registering Renter...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            User new_user = new User(email,password,first_name,last_name,"renter","");
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(email.replace(".","_"));
                            databaseReference.setValue(new_user);
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Renter successfully registered!",Toast.LENGTH_SHORT).show();
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
