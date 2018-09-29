package com.example.dell.yoursapp;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.yoursapp.Common.Common;
import com.example.dell.yoursapp.Interface.ItemClickListener;
import com.example.dell.yoursapp.Model.Category;
import com.example.dell.yoursapp.Model.Restaurant;
import com.example.dell.yoursapp.ViewHolder.MenuViewHolder;
import com.example.dell.yoursapp.ViewHolder.RestaurantViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RestaurantList extends AppCompatActivity{

    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;


    FirebaseRecyclerOptions<Restaurant> options=new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants"),Restaurant.class)
            .build();

    FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_item, parent, false);
            return new RestaurantViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
            holder.txtRestaurantName.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage()).into(holder.restaurantImage);
            final Restaurant clickItem = model;
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Get CategoryId and send to new Activity
                    Intent foodList = new Intent(RestaurantList.this, Home.class);
                    //Because CategoryId is key, so we just get the key of this item
                    Common.restaurantSelected = adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });
        }



    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else{
                    Toast.makeText(getBaseContext(),"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else{
                    Toast.makeText(RestaurantList.this,"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //Load menu
        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

}

    private void loadRestaurant() {

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}