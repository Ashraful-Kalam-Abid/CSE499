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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import com.pusher.pushnotifications.PushNotifications;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText signInEmailEditText,signInPasswordEditText;
    private TextView signUpTextView;
    private  Button signInButton;
    private SignInButton signInButton2;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="MainActivity";
    private int RC_SIGN_IN=1;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        signInButton2=findViewById(R.id.sign_In_Button_Id);

        signInEmailEditText=findViewById(R.id.signInEmailEditTextId);
        signInPasswordEditText=findViewById(R.id.signInPasswordEditTextId);
        signInButton=findViewById(R.id.signInButtonId);
        signUpTextView=findViewById(R.id.signUpTextViewId);
        progressBar=findViewById(R.id.progressbarId);



        signUpTextView.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        PushNotifications.start(getApplicationContext(), "d2835856-a47c-4e87-bc03-df976b35fce3");
        PushNotifications.addDeviceInterest("hello");

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
        signInButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


    }
    private void signInGoogle(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private  void handleSignInResult(Task<GoogleSignInAccount> completedTask ){
        try{
            GoogleSignInAccount acc=completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Sign In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch(ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);


        }

    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                    Intent intent = new Intent(getApplicationContext(), Teacher.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signInButtonId:
                UserLogin();
                break;

            case R.id.signUpTextViewId:
                Intent intent=new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                break;


        }
    }

    private void UserLogin() {
        String email=signInEmailEditText.getText().toString().trim();
        String password=signInPasswordEditText.getText().toString().trim();
        if(email.isEmpty()){
            signInEmailEditText.setError("Enter an email address");
            signInEmailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           signInEmailEditText.setError("Enter a valid email address");
            signInEmailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            signInPasswordEditText.setError("Enter a Password");
            signInPasswordEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            signInPasswordEditText.setError("Minimum password length is 6");
            signInPasswordEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    String UID=mAuth.getCurrentUser().getUid();
                    //Toast.makeText(MainActivity.this,UID, Toast.LENGTH_LONG).show();
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String type=dataSnapshot.child("type").getValue().toString();
                            //Toast.makeText(MainActivity.this, "Here i am idiot", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, type, Toast.LENGTH_LONG).show();


                            if(type.equals("Teacher")) {
                                Toast.makeText(MainActivity.this, type, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Teacher.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            if(type.equals("Student")) {
                                Toast.makeText(MainActivity.this, type, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Student.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                       }else{
                    Toast.makeText(MainActivity.this, "Login Not Successfull", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}
