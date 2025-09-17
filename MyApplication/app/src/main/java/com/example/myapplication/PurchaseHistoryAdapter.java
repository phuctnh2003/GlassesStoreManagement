package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder> {

    private List<PurchaseHistory> purchaseHistoryList;

    public PurchaseHistoryAdapter(List<PurchaseHistory> purchaseHistoryList) {
        this.purchaseHistoryList = purchaseHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseHistory purchaseHistory = purchaseHistoryList.get(position);
        holder.tvProductName.setText(purchaseHistory.getProductName());
        holder.tvQuantity.setText("Quantity: " + purchaseHistory.getQuantity());
        holder.tvPurchaseDate.setText("Date: " + purchaseHistory.getPurchaseDate());
    }

    @Override
    public int getItemCount() {
        return purchaseHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPurchaseDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
        }
    }
}