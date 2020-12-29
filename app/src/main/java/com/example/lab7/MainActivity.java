package com.example.lab7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // ui
    private Spinner sSearch;
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private ValueEventListener databaseListener;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        //data structures
        final List<MonthlyExpense> monthlyExpenses = new ArrayList<>();
        final List<String> monthNames = new ArrayList<>();

        Spinner sSearch = (Spinner) findViewById(R.id.spinner);
        sSearch.setOnItemSelectedListener(this);
        //spinner adapter
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthNames);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearch.setAdapter(sAdapter);


        databaseReference.child("Calendar").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // create a new instance of MonthlyExpense
                        String month = monthSnapshot.getKey();
                        // save the key as month name
                        Log.d("medrea: ", month);
                        if(!monthNames.contains("month"))
                            monthNames.add(month);
                        // save the month and month name

                    } catch (Exception e) {
                    }
                }

                // notify the spinner that data may have changed
                sAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentMonth = sSearch.getSelectedItem().toString();
        tStatus.setText("Searching ...");
        createNewDBListener();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void clicked(View view) {

        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth = eSearch.getText().toString().toLowerCase();

                    tStatus.setText("Searching ...");
                    createNewDBListener();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:
            {
                databaseReference.child("Calendar").child(currentMonth).setValue(new MonthlyExpense(currentMonth, Float.valueOf(eExpenses.getText().toString()), Float.valueOf(eIncome.getText().toString()))).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d("medrea", String.valueOf(task.isSuccessful()));
                    }
                });

            }
            break;
        }
    }
    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("Calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpense monthlyExpense = dataSnapshot.getValue(MonthlyExpense.class);
                // explicit mapping of month name from entry key
                assert monthlyExpense != null;
                monthlyExpense.month = dataSnapshot.getKey();

                eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("Calendar").child(currentMonth).addValueEventListener(databaseListener);
    }


}