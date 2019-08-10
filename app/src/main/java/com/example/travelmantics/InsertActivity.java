package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class InsertActivity extends AppCompatActivity {
    public static final int PICTURE_RESULT = 30;
    public static final String GS_TRAVELMANTICS_STORAGE_URI = "gs://travelmantics-64300.appspot.com";
    private FirebaseManager mManager;
    private TravelDeal deal;
    private String path = "";
    private TextView txtTitle;
    private TextView txtPrice;
    private TextView txtDescription;
    private final int SAVE = 0;
    private final int EDIT = 1;
    private final int DELETE = 2;
    private int status  = 0;
    private Intent dealIntent;
    private MenuItem save, edit, delete;
    private boolean isMageSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getActionBar() != null)
            getActionBar().hide();
        Toolbar toolbar = findViewById(R.id.toolSaveContainer);
        setSupportActionBar(toolbar);

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);

        Button btnSelect = findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager = FirebaseManager.getInstance();
        dealIntent = getIntent();
        if(dealIntent != null) {
            deal = (TravelDeal) dealIntent.getSerializableExtra("TravelDeal");
            if(deal == null) return;
            displayDealDetails();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseManager manager = FirebaseManager.getInstance();
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            if(data != null && data.getData() != null){
                isMageSelected = true;
                final Uri uri = data.getData();
                final String pathName = uri.getLastPathSegment();
                path = pathName;
                final StorageReference storageRef = manager.getStorageRef().child("deal_pics/" + pathName);
                storageRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final ConstraintLayout layout = findViewById(R.id.cl_createDeal);
                        final ImageView imageView = findViewById(R.id.imageView);
                        Snackbar.make(layout, "Image uploaded successfully", Snackbar.LENGTH_SHORT).show();
                     //   deal.setImageUrl(pathName);
                        downloadImage(pathName, imageView);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        final ConstraintLayout layout = findViewById(R.id.cl_createDeal);
                        Snackbar.make(layout, "Image failed to upload", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public static void downloadImage(String path, final ImageView imageView){
        FirebaseManager manager = FirebaseManager.getInstance();
        StorageReference storageRef = manager.getFirebaseStorage().getReferenceFromUrl(GS_TRAVELMANTICS_STORAGE_URI +
                "/deal_pics/" + path);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                displayImage(uri.toString(), imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("INSERT", "Download Unsuccesful");
            }
        });
    }

    private static void displayImage(String url, ImageView imageView) {
        if(url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop().
                    into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        save = menu.findItem(R.id.save);
        edit = menu.findItem(R.id.edit);
        delete = menu.findItem(R.id.delete);
        if(deal != null) {
            save.setEnabled(false);
        }
        else if(deal == null){
            edit.setEnabled(false);
            delete.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch(itemID) {
            case R.id.save:
                status = SAVE;
                saveDeal();
                return true;
            case R.id.edit:
                status = EDIT;
                saveDeal();
                return  true;
            case R.id.delete:
                status = DELETE;
                saveDeal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void saveDeal() {
        String title = txtTitle.getText().toString();
        String price = txtPrice.getText().toString();
        String description = txtDescription.getText().toString();
        final DatabaseReference traveldeals = mManager.getDbReference("traveldeals");
        if (status == SAVE) {
            DatabaseReference ref = traveldeals.push();
            String userId = ref.getKey();
            deal = new TravelDeal(userId, title, price, description);
            deal.setUrl(GS_TRAVELMANTICS_STORAGE_URI + "/deal_pics/" + path);
            ref.setValue(deal);
            Toast.makeText(this,"New travel deal added", Toast.LENGTH_LONG).show();
        } else if (status == EDIT) {
            DatabaseReference db = traveldeals.child(deal.getId());
            deal.setTitle(title);
            deal.setPrice(price);
            deal.setDescription(description);
            if(isMageSelected){
                deal.setUrl(GS_TRAVELMANTICS_STORAGE_URI + "/deal_pics/" + path);
            }
            db.setValue(deal);
            isMageSelected = false;
            Toast.makeText(this,"Travel deal modified", Toast.LENGTH_LONG).show();
        } else if (status == DELETE) {
            traveldeals.child(deal.getId()).removeValue();
            Toast.makeText(this,"Travel deal deleted", Toast.LENGTH_LONG).show();
         }
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        final ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                final String LAUCH_VIEW_ACTIVITY_ACTION = "com.example.travelmantics.INSERT_ACTIVITY";
                Intent displayIntent = new Intent(LAUCH_VIEW_ACTIVITY_ACTION);
                startActivity(displayIntent);
            }
        }, 5000);
    }

    private void displayDealDetails(){
        txtTitle.setText(deal.getTitle());
        txtPrice.setText(deal.getPrice());
        txtDescription.setText(deal.getDescription());
        Uri path = Uri.parse(deal.getUrl());
        final ImageView imageView = findViewById(R.id.imageView);
        downloadImage(path.getLastPathSegment(), imageView);
    }

    private void message(String _message){
        final ConstraintLayout layout = findViewById(R.id.cl_createDeal);
        Snackbar.make(layout, _message, Snackbar.LENGTH_SHORT);
    }
}
