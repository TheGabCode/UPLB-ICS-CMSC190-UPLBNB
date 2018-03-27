package com.cmsc190.ics.uplbnb;

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
    Button sign_inButton;
    EditText editTextEmail;
    EditText editTextPassword;
    FirebaseUser firebaseUser;
    User user;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseDatabase = FirebaseDatabase.getInstance();
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
            SpannableString content = new SpannableString("SIGN UP");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            textViewSignUp.setText(content);

        textViewSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent i = new Intent(getApplicationContext(),registration.class);
                startActivity(i);
            }

        });

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
                    String id;
                    id = firebaseUser.getUid();
                    databaseReference = firebaseDatabase.getReference("user");
                    databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            if(user.getUser_type().equals("renter")){
                                //renter
                                Intent i = new Intent(getApplicationContext(),RenterHome.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(i);
                                progressDialog.dismiss();
                            }else if(user.getUser_type().equals("owner")){
                                //owner
                                Intent i = new Intent(getApplicationContext(),OwnerHome.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(i);
                                progressDialog.dismiss();
                            }
                            else if(user.getUser_type().equals("admin")){
                                //admin
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
