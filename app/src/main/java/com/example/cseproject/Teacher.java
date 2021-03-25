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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class Teacher extends AppCompatActivity {
    private TextView mtextView;
    private  Button CaptureImageButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //private Bitmap Bitmap,imageBitmap;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_teacher);
        Button  uploadBtn=findViewById(R.id.upload);
        //Button copytext=findViewById(R.id.copytext);
        CaptureImageButton=findViewById(R.id.capture);


        mtextView=findViewById(R.id.text);
        //capture Option
        CaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        //upload optionm

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,100);

            }
        });
        //text copying
        /*copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CopiedText=mtextView.getText().toString();
                ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("image Copied",CopiedText);
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(getApplicationContext(),"Text Copied",Toast.LENGTH_LONG).show();
            }
        });*/
    }
    //sign Out

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.signOutMenuId){
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.uploadBook){
            Intent intent =new Intent(getApplicationContext(),UploadBook.class);
            startActivity(intent);

        }
        if(item.getItemId()==R.id.downloadBook){
            Intent intent=new Intent(getApplicationContext(),View_pdf_file.class);
            startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }
    //Camera Related Code

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent,101);

        }
    }
    //text searcher youtube
    public String  searchForYoutube(){
        String S=mtextView.getText().toString();
        String S1 = new String();
        S1="https://www.youtube.com/results?search_query=";
        //S1+=S;
        //return  S1;
        for(int i=0;i<S.length();i++){
            if(S.charAt(i)=='.'){
                // return S1;
                break;
            }
            S1+=S.charAt(i);

        }
        return S1;
    }
    //Youtube search
    public void browser1(View view){
        Intent browserIntet=new Intent(Intent.ACTION_VIEW, Uri.parse(searchForYoutube()));
        startActivity(browserIntet);
    }

    //text sercher from Google
    public String  searchFromGoogle(){
        String S=mtextView.getText().toString();
        String S1 = new String();
        S1="https://www.google.com/search?ei=UZU-X-OqCpT49QOonZ-QDg&q=";
        //S1+=S;
        //return  S1;
        for(int i=0;i<S.length();i++){
            if(S.charAt(i)=='.'){
                // return S1;
                break;
            }
            S1+=S.charAt(i);

        }
        return S1;
    }
    public void browser2(View view){
        Intent browserIntet=new Intent(Intent.ACTION_VIEW, Uri.parse(searchFromGoogle()));
        startActivity(browserIntet);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==101 && resultCode==RESULT_OK){
                mtextView.setText("I am here right now frans");
                Bundle extras=data.getExtras();
                Bitmap imageBitmap=(Bitmap) extras.get("data");
                FirebaseVisionImage image= FirebaseVisionImage.fromBitmap(imageBitmap);
                FirebaseVisionTextRecognizer recognizer=FirebaseVision.getInstance().getOnDeviceTextRecognizer();

                recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {


                        String text=firebaseVisionText.getText() ;
                        mtextView.setText("");
                        if (text.isEmpty()){
                            mtextView.append("No text found");
                        }

                        else{

                            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                mtextView.append("\n \n" + block.getText());
                            }
                        }
                    }
                });

            }
            else if(requestCode==100 && resultCode==RESULT_OK) {
                {
                    FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(Teacher.this, data.getData());
                    FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                    mtextView.append("hi i am here");
                    recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            String text = firebaseVisionText.getText();
                            mtextView.setText("");
                            if (text.isEmpty()) {
                                mtextView.append("No text found");
                            } else {

                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

                                    mtextView.append("\n \n" + block.getText());

                                }
                            }
                        }
                    });

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
