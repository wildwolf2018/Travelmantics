package com.example.travelmantics;

import android.renderscript.Script;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseManager {
    private static FirebaseManager sFirebaseManager = null;
    private   FirebaseDatabase mFirebaseDatabase;
    private  DatabaseReference mDatabaseRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;

    private FirebaseManager(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference();
        mStorageRef = mFirebaseStorage.getReference();
    }


    public static FirebaseManager getInstance(){
        if(sFirebaseManager == null){
            sFirebaseManager = new FirebaseManager();
        }
        return sFirebaseManager;
    }

    public  DatabaseReference getDbReference(String path){
        return mDatabaseRef.child(path);
    }

    public FirebaseStorage getFirebaseStorage(){
        return mFirebaseStorage;
    }

    public StorageReference getStorageRef(){
        return mStorageRef;
    }
}
