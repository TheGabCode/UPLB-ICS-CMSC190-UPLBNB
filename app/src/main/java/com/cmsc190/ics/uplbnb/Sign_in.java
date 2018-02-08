package com.cmsc190.ics.uplbnb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Sign_in extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button sign_inButton = (Button) findViewById(R.id.buttonSignIn);

        sign_inButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(),home.class);
                startActivity(i);

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
}
