package com.example.lab7;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewMainActivity extends AppCompatActivity {
    private TextView tStatus;
    private Button bPrevious, bNext;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    private String currentMonth;
    private List<Payment> payments = new ArrayList<>();
    private PaymentAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_activity_main);

        tStatus = (TextView) findViewById(R.id.tStatus);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        listPayments = (ListView) findViewById(R.id.listPayments);

        if (!AppState.isNetworkAvailable(this)) {
            // has local storage already
            if (AppState.get().hasLocalStorage(this)) {
                AppState a = new AppState();
                payments = a.loadFromLocalBackup(this, currentMonth);
                tStatus.setText("Found " + payments.size() + " payments for " + currentMonth + ".");
            } else {
                Toast.makeText(this, "This app needs an internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        bPrevious.setOnClickListener(v -> onPrev());
        bNext.setOnClickListener(v -> onNext());
        fabAdd.setOnClickListener(v -> onFab());

        // setup firebase
        addPayments(p -> {
            adapter = new PaymentAdapter(this, R.layout.item_payment, p);
            listPayments.setAdapter(adapter);
            tStatus.setText("Found " + payments.size() + " in the DB");
            listPayments.setOnItemClickListener((parent, view, position, id) -> {
                AppState.get().setCurrentPayment(payments.get(position));
                startActivity(new Intent(getApplicationContext(), AddPaymentActivity.class));
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addPayments(FirebaseCallback callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);
        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {

                    Payment payment = snapshot.getValue(Payment.class);
                    payment.setTimestamp(snapshot.getKey());
                    payments.add(payment);
                    callback.onCallBack(payments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < payments.size(); i++) {
                    if (payments.get(i).timestamp.equals(snapshot.getKey().toString()))
                        try {
                            Payment updatePayment = snapshot.getValue(Payment.class);
                            updatePayment.setTimestamp(snapshot.getKey());

                            payments.set(i, updatePayment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < payments.size(); i++) {
                    if (payments.get(i).timestamp.equals(snapshot.getValue(Payment.class).timestamp))
                        payments.remove(i);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onPrev() {
        Toast.makeText(this, "Previous pressed", Toast.LENGTH_SHORT).show();
    }

    public void onNext() {
        Toast.makeText(this, "Next pressed", Toast.LENGTH_SHORT).show();
    }

    public void onFab() {
        AppState.get().setCurrentPayment(null);
        startActivity(new Intent(this, AddPaymentActivity.class));
    }

    private interface FirebaseCallback {
        void onCallBack(List<Payment> p);
    }
}