package com.example.cseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsAcivity extends AppCompatActivity {
    private Button UpdateAccountSetting;
    private EditText userName;
    private EditText Email;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_acivity);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();

        Initializefields();
        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UpdateSetting();
            }
        });
    }
    private void Initializefields(){
        UpdateAccountSetting=(Button) findViewById(R.id.update_setting_button);
        userName=(EditText) findViewById(R.id.set_user_name);
        Email=(EditText) findViewById(R.id.set_email);
    }
    private void UpdateSetting(){
        String name = userName.getText().toString();
        String email=Email.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write your user name",Toast.LENGTH_SHORT).show();
        }
        else{
            User user =new User(name,email,"","Student");

            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                    }
                    else{
                    }
                }
            });
            }

    }
}
