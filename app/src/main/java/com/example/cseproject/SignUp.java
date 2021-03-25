package com.example.cseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText signUpEmailEditText,signUpPasswordEditText,signUpNameEditText,signUpPhoneEditText;
    private TextView signInTextView;
    private  Button signUpButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    Spinner _spinner;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        progressBar=findViewById(R.id.progressbarId);
        signUpEmailEditText=findViewById(R.id.signUpEmailEditTextId);
        signUpPasswordEditText=findViewById(R.id.signUpPasswordEditTextId);
        signUpNameEditText=findViewById(R.id.signUpNameEditTextId);
        signUpButton=findViewById(R.id.signUpButtonId);
        signInTextView=findViewById(R.id.signInTextViewId);
        signUpPhoneEditText=findViewById(R.id.signUpPhoneEditTextId);
        _spinner=findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.UserType,R.layout.support_simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);





        signInTextView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signUpButtonId:

                UserRegister();
                break;

            case R.id.signInTextViewId:
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;


        }
    }

    private void UserRegister() {
        final String usertype=_spinner.getSelectedItem().toString();
        final String email=signUpEmailEditText.getText().toString().trim();
        final String name=signUpNameEditText.getText().toString().trim();
        final String phone=signUpPhoneEditText.getText().toString().trim();
        String password=signUpPasswordEditText.getText().toString().trim();
       if(name.isEmpty()){
           signUpNameEditText.setError("Enter ur name");
           signUpEmailEditText.requestFocus();

       }
       if(phone.isEmpty()){
           signUpPhoneEditText.setError("Enter ur phone number");
           signUpEmailEditText.requestFocus();
       }
        if(email.isEmpty()){
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            signUpPasswordEditText.setError("Enter a Password");
            signUpPasswordEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            signUpPasswordEditText.setError("Minimum password length is 6");
            signUpEmailEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Register Is Successfull", Toast.LENGTH_SHORT).show();
                    User user =new User(name,email,phone,usertype);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "All data Registered  successfully", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(SignUp.this, "All user data  successfully failed to register ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(SignUp.this, "User is already registered", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignUp.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });



    }
}
