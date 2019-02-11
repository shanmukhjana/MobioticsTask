package com.example.shanm.mobioticstask.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amitshekhar.DebugDB;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shanm.mobioticstask.R;
import com.example.shanm.mobioticstask.modal.Modal;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Modal> arrayList;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    //  Button button;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    private ProgressDialog dialog;
    public static final String response = "";

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.rcv);

        dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");

        getAllItems();

        String data = DebugDB.getAddressLog();
        Log.i("urlurl", "" + data);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    // finish();
                    startActivity(new Intent(MainActivity.this, FirebaseLogin.class));
                }
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuId:
                // newGame();
                mAuth.signOut();
                // AuthU
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getAllItems() {
        dialog.show();
        String url = "https://interview-e18de.firebaseio.com/media.json?print=pretty";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String des = jsonObject.getString("description");
                        String id = jsonObject.getString("id");
                        String thumb = jsonObject.getString("thumb");
                        String title = jsonObject.getString("title");
                        String url1 = jsonObject.getString("url");
                        Modal modal = new Modal(des, id, thumb, title, url1);
                        arrayList.add(modal);
                    }

                    myAdapter = new MyAdapter(MainActivity.this, arrayList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(myAdapter);
                    dialog.dismiss();
                    // jsonObject.get("recommended")
                } catch (Exception e) {
                    dialog.dismiss();
                    Log.d("ssssssssss", "" + e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}
