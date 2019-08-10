package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ViewDealsActivity extends AppCompatActivity {
    public static final String COM_EXAMPLE_TRAVELDEAL_VIEW_DEALS_ACTIVITY = "com.example.traveldeal.VIEW_DEALS_ACTIVITY";
    private RecyclerView mRecyclerView;
    private DealAdapter mDealAdapter;
    private FirebaseManager mManager;
    private TravelDeal deal;
    private String path = "";
    private ArrayList<TravelDeal> mTravelDeals;
    private DatabaseReference mDbRef;
    private boolean canView = false;
    private MenuItem create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deals);
        mRecyclerView = findViewById(R.id.rv_display_deals);
        mDealAdapter = new DealAdapter(this, mTravelDeals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mDealAdapter);

        mManager = FirebaseManager.getInstance();
        mDbRef = mManager.getDbReference("traveldeals");
        mDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                mDealAdapter.changeList(travelDeal);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mDealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_deals_menu, menu);
        create = menu.findItem(R.id.create_action);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if((user.getEmail()).equals("tebohothg@gmail.com")) {
            create.setEnabled(true);
        }else {
            menu.removeItem(R.id.create_action);
            invalidateOptionsMenu();
        }
            return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        switch(itemID) {
            case R.id.create_action:
                finish();
                Intent intent = new Intent(this, InsertActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                Intent intent = new Intent(COM_EXAMPLE_TRAVELDEAL_VIEW_DEALS_ACTIVITY);
                                startActivity(intent);
                            }
                        });
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
