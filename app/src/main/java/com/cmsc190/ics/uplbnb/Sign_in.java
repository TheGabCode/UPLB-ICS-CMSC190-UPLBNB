package com.cmsc190.ics.uplbnb;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sign_in extends AppCompatActivity {
    FirebaseAuth loginAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference logReference;
    Button sign_inButton;
    Button sign_upButton;
    EditText editTextEmail;
    EditText editTextPassword;
    FirebaseUser firebaseUser;
    User user;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            String userId = firebaseUser.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    Intent i;
                    if(user.getUser_type().equals("renter")){
                        i = new Intent(getApplicationContext(),RenterHome.class);
                        finish();
                        startActivity(i);

                    }
                    else if(user.getUser_type().equals("owner")){
                        i = new Intent(getApplicationContext(),OwnerHome.class);

                        finish();
                        startActivity(i);
                    }
                    else{
                        i = new Intent(getApplicationContext(),AdminHome.class);
                        finish();
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        setContentView(R.layout.sign_in_new);

        sign_upButton = findViewById(R.id.buttonSignUp);
        progressDialog = new ProgressDialog(this);
        loginAuth = FirebaseAuth.getInstance();
        sign_inButton = (Button) findViewById(R.id.buttonSignIn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        sign_inButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                userLogin();


            }
        });

            TextView textViewSignUp = (TextView) findViewById(R.id.signuptext);

        sign_upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),registration.class);
                startActivity(i);
            }
        });
        textViewSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent i = new Intent(getApplicationContext(),registration.class);
                startActivity(i);
            }

        });

    }

    public void sendLog(String message,String uId){
        logReference = firebaseDatabase.getReference("logs").push();
        Log log = new Log(message,uId);
        logReference.setValue(log);
    }

    public void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please enter password.",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Signing in...");
        progressDialog.show();
        loginAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    final String id = firebaseUser.getUid();
                    databaseReference = firebaseDatabase.getReference("user");
                    databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            if(user.getUser_type().equals("renter")){
                                //renter
                                Intent i = new Intent(getApplicationContext(),RenterHome.class);
                                String logMessage = user.getEmail() + " - " + user.getFullname() + " logs in as renter.";
                                sendLog(logMessage,firebaseUser.getUid());
                                finish();

                                startActivity(i);
                                progressDialog.dismiss();
                            }else if(user.getUser_type().equals("owner")){
                                //owner
                                Intent i = new Intent(getApplicationContext(),OwnerHome.class);
                                String logMessage = user.getEmail() + " - " + user.getFullname() + " logs in as owner.";
                                sendLog(logMessage,firebaseUser.getUid());
                                finish();
                                startActivity(i);
                                progressDialog.dismiss();
                            }
                            else if(user.getUser_type().equals("admin")){
                                //admin
                                Intent i = new Intent(getApplicationContext(),AdminHome.class);
                                String logMessage = user.getEmail() + " - " + user.getFullname() + " logs in as admin.";
                                sendLog(logMessage,firebaseUser.getUid());
                                finish();
                                startActivity(i);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });
    }

}
