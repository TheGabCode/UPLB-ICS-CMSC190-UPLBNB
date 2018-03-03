//package com.cmsc190.ics.uplbnb;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class home extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
//    private List<Establishment_Item> establishment_items;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_home2);
//        //Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
//        setSupportActionBar(toolbar);
//
//        //recyclerView = (RecyclerView) findViewById(R.id.recyclerViewHome);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        establishment_items = new ArrayList<>();
//
//        for(int i = 0; i < 5; i++){
//            String cat;
//            if(i%2 == 0){
//                cat = "Apartment";
//            }
//            else{
//                cat = "Dormitory";
//            }
//            Establishment_Item e = new Establishment_Item("Establishment " + (i+1),cat);
//            establishment_items.add(e);
//        }
//
//        adapter = new Establishment_Item_Adapter(establishment_items,this);
//
//        recyclerView.setAdapter(adapter);
//        Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
//
//    }
//
//}
