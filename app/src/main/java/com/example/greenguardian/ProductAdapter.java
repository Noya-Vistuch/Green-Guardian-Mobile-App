package com.example.greenguardian;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private Context context;
    private HelperDBProducts dbHelper;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new HelperDBProducts(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.price.setText("Price: " +product.getPrice() +"$");
        holder.comment.setText("Contact details:"+product.getComment());

        // Convert byte array to Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
        holder.image.setImageBitmap(bitmap);

        // Long Click Listener for Deleting a Product
        if (product.getUserId() == CurrentUser.userId) {
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(product.getId(), position);
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null); // Disable delete for others
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView price, comment;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_card_image);
            price = itemView.findViewById(R.id.product_card_price);
            comment = itemView.findViewById(R.id.product_card_comment);
        }
    }

    // Show confirmation dialog before deleting
    private void showDeleteConfirmationDialog(int productId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Product");
        builder.setMessage("Do you want to delete this product?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteProduct(productId, position);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Delete product from database and update UI
    private void deleteProduct(int productId, int position) {
        dbHelper.deleteProduct(productId);
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
    }
}


