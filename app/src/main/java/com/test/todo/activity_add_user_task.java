package com.test.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class activity_add_user_task extends AppCompatActivity {
    private EditText createdBy;
    private EditText createdOn;
    private EditText task1;
    private EditText type;
    private EditText email;
    private EditText imageUrl;
    private Button addBtn;
    private Button chooseImg;
    public String imageuri;
    DatabaseReference reff;
    private StorageReference mStorageRef;
    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_task);
        createdBy = (EditText) findViewById(R.id.createdBy);
        createdOn = (EditText) findViewById(R.id.createdOn);
        task1 = (EditText) findViewById(R.id.task);
        type = (EditText) findViewById(R.id.type);
        email = (EditText) findViewById(R.id.email);
        addBtn = (Button) findViewById(R.id.addBtn);
        chooseImg = (Button) findViewById(R.id.chooseImg);
        chooseImg.setOnClickListener(v ->{
            Filechooser();
        });
        imageUrl = (EditText) findViewById(R.id.imageUrl);
        task = new Task();
        reff = FirebaseDatabase.getInstance().getReferenceFromUrl("https://todolist2-c16ea.firebaseio.com/");
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://todolist2-c16ea.appspot.com").child("images/");
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setCreatedBy(createdBy.getText().toString());
                task.setDateCreated(createdOn.getText().toString());
                task.setTask(task1.getText().toString());
                task.setType(type.getText().toString());
                task.setUseremail(email.getText().toString());
                if( createdOn.getText().toString().trim().equals(""))
                {
                    createdOn.setError( "Required!" );
                }
                if( createdBy.getText().toString().trim().equals(""))
                {
                    createdBy.setError( "Required!" );
                }
                if( task1.getText().toString().trim().equals(""))
                {
                    task1.setError( "Required!" );
                }
                if( type.getText().toString().trim().equals(""))
                {
                    type.setError( "Required!" );
                }
                if( email.getText().toString().trim().equals(""))
                {
                    email.setError( "Required!" );
                }
                if( imageUrl.getText().toString().trim().equals(""))
                {
                    imageUrl.setError( "Required!" );
                }
                else {
                    Log.d("F==>", ""+imageuri);
                    Uri file = Uri.fromFile(new File(imageUrl.getText().toString()));
                    mStorageRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    task.setImage("images/"+);
                                    reff.push().setValue(task);
                                    Toast.makeText(activity_add_user_task.this, "data inserted", Toast.LENGTH_LONG).show();
                                    createdBy.setText("");
                                    createdOn.setText("");
                                    task1.setText("");
                                    type.setText("");
                                    email.setText("");
                                    imageUrl.setText("");
                                }
                            });
                    }
                };
            });
        }

    private void Filechooser(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(this, "Unable to choose image", Toast.LENGTH_SHORT).show();
            }

            Uri imageUri = data.getData();
            imageuri = getRealPathFromUri(imageUri);
            imageUrl.setText(imageuri);
        }
    }

    private String getRealPathFromUri(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, imageUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        Log.d("TAG==>", ""+cursor.getColumnCount());
        cursor.close();
        return result;
    }
}