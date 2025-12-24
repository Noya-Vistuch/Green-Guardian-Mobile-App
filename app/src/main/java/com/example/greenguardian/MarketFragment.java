package com.example.greenguardian;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class MarketFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private HelperDBProducts dbHelper;
    private ArrayList<Product> productList;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdd = view.findViewById(R.id.fab_add);
        dbHelper = new HelperDBProducts(getActivity());

        productList = new ArrayList<>();
        loadProducts();

        // Set GridLayoutManager with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        productAdapter = new ProductAdapter(getActivity(), productList);
        recyclerView.setAdapter(productAdapter);

        // FAB click opens AddProductActivity
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Load products from database
    private void loadProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                @SuppressLint("Range") String price = cursor.getString(cursor.getColumnIndex("price"));
                @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex("comment"));
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("user_id"));

                productList.add(new Product(id, image, price, comment, userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshProducts();
    }

    private void refreshProducts() {
        productList.clear(); // Clear the old list
        loadProducts(); // Reload from the database
        productAdapter.notifyDataSetChanged(); // Update RecyclerView
    }

}

