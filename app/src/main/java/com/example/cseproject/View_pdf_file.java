package com.example.cseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class View_pdf_file extends AppCompatActivity {
    ListView myPDFListView;
    DatabaseReference databaseReference;
    List<uploadPdf> uploadPdfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_file);
        myPDFListView=(ListView)findViewById(R.id.myBookListView);
        uploadPdfs=new ArrayList<>();
        viewAllFiles();
        myPDFListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uploadPdf uploadPdf = uploadPdfs.get(position);
                Intent intent=new Intent();
                intent.setData(Uri.parse(uploadPdf.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void viewAllFiles() {
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    uploadPdf uploadPdf=postSnapshot.getValue(com.example.cseproject.uploadPdf.class);
                    uploadPdfs.add(uploadPdf);



                }
                String[] uploads=new String[uploadPdfs.size()];
                for(int i=0;i<uploads.length;i++){
                    uploads[i]=uploadPdfs.get(i).getName();

                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads);
                myPDFListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
