package com.example.castroreyrobert.travelorganizerfirebase;


import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;



import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ListView lvThings;
    private ProgressDialog pDialog;
    private EditText etThings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lvThings = (ListView) findViewById(R.id.lvThings);
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        final EditText etThings = (EditText) findViewById(R.id.etThings);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked);

        lvThings.setAdapter(adapter);

        loadProgressDialog();

        //Connect to the firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Get a reference to the todoItems child items in the database
        final DatabaseReference myref = database.getReference("thingsToBring");

        // Assign a listener to detect changes to the child items
        // of the database reference.
        myref.addChildEventListener(new ChildEventListener() {

            // This function is called once for each child that exists
            // when the listener is added. Then it is called
            // each time a new child is added.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                adapter.add(value);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                adapter.remove(value);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("TAG", "onCancelled: ");
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference childRef = myref.push();
                childRef.setValue(etThings.getText().toString());

                etThings.setText("");
            }
        });


        lvThings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Check if the user clicks an item in the listView
                CheckedTextView check = (CheckedTextView)view;
                check.setChecked(!check.isChecked());

            }
        });

        //Displaying the menu if the user long clicks the item
        registerForContextMenu(lvThings);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.long_press, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int row = info.position;

        //Connect to the firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Get a reference to the todoItems child items in the database
        final DatabaseReference myref = database.getReference("thingsToBring");


        Object x = lvThings.getAdapter().getItem(row);

         switch(item.getItemId()) {
            case R.id.menu_delete:
                //Deleting from the firebase
                Query myQuery = myref.orderByValue().equalTo((String)x);
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            break;
             case R.id.menu_edit:
                 Query myquery = myref.orderByValue().equalTo((String)x);
                 myquery.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.hasChildren()) {
                             DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                             firstChild.getRef().removeValue();
                         }
                     }
                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
                 etThings = (EditText) findViewById(R.id.etThings);
                 etThings.setText(x.toString());
         }
        return super.onContextItemSelected(item);
    }

    public void loadProgressDialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setProgress(0);
        pDialog.setMessage("Loading.....Please wait....");
        pDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                pDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);
    }

}