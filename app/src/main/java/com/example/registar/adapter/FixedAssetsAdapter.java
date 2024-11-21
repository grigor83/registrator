package com.example.registar.adapter;

import static com.example.registar.MainActivity.showCustomToast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registar.AssetActivity;
import com.example.registar.AssetActivityEditable;
import com.example.registar.R;
import com.example.registar.model.FixedAsset;

import java.util.ArrayList;
import java.util.List;

public class FixedAssetsAdapter extends RecyclerView.Adapter<FixedAssetsAdapter.ViewHolder>{
    private final List<FixedAsset> fixedAssetList, filteredFixedAssetList;
    private final Context context;
    private float touchX, touchY;
    private final ActivityResultLauncher<Intent> deleteActivityLauncher;

    public FixedAssetsAdapter(List<FixedAsset> fixedAssetList, Context context,
                              ActivityResultLauncher<Intent> deleteActivityLauncher) {
        this.fixedAssetList = fixedAssetList;
        this.filteredFixedAssetList = new ArrayList<>(fixedAssetList);
        this.context = context;
        this.deleteActivityLauncher = deleteActivityLauncher;
    }

    // Kreira prazan viewHolder u koji još nisu upisani podaci
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.assets_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FixedAsset asset = filteredFixedAssetList.get(position);
        holder.title.setText(asset.getTitle());
        holder.description.setText(asset.getDescription());
        holder.location.setText(asset.getLocation().getCity());
        switch (asset.getTitle()) {
            case "stolica":
                holder.icon.setImageResource(R.drawable.chair);
                break;
            case "računar":
                holder.icon.setImageResource(R.drawable.computer_24);
                break;
            default:
                holder.icon.setImageResource(R.mipmap.ic_launcher);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AssetActivity.class);
                intent.putExtra("clickedAsset", asset);
                intent.putExtra("position", holder.getAdapterPosition());
                deleteActivityLauncher.launch(intent);
            }
        });

        holder.itemView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchX = event.getRawX();
                touchY = event.getRawY();
            }
            return false;
        });

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, asset, holder.getAdapterPosition());
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return filteredFixedAssetList.size();
    }

    // Method to filter the list by title and location
    public void filter(String titleQuery, String locationQuery) {
        filteredFixedAssetList.clear();

        String title = titleQuery.toLowerCase();
        String location = locationQuery.toLowerCase();

        for (FixedAsset asset : fixedAssetList) {
            boolean matchesTitle = !titleQuery.isEmpty() && asset.getTitle().toLowerCase().startsWith(title);
            boolean matchesLocation = !locationQuery.isEmpty() && asset.getLocation().getCity().toLowerCase().startsWith(location);

            // If either query is empty, skip checking it; otherwise match one or both
            if ((titleQuery.isEmpty() && matchesLocation) ||
                    (locationQuery.isEmpty() && matchesTitle) ||
                    (matchesTitle && matchesLocation))
                filteredFixedAssetList.add(asset);
            else if (titleQuery.isEmpty() && locationQuery.isEmpty())
                filteredFixedAssetList.addAll(fixedAssetList);
        }

        notifyDataSetChanged();
    }

    private void showPopupMenu(View view, FixedAsset asset, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_crud, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setOnClickListener(v -> {
            startActivityEditable(asset);
            popupWindow.dismiss();
        });
        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteAsset(position);
            popupWindow.dismiss();
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    public void startActivityEditable(FixedAsset asset){
        Intent intent = new Intent(context, AssetActivityEditable.class);
        intent.putExtra("clickedAsset", asset);
        deleteActivityLauncher.launch(intent);
    }

    // Metod se poziva u FragmentOne u deleteActivityLauncher
    public void editAsset(FixedAsset returnedAsset) {
        for (int i = 0; i < filteredFixedAssetList.size(); i++) {
            if (filteredFixedAssetList.get(i).getId() == returnedAsset.getId()) {
                filteredFixedAssetList.set(i, returnedAsset);
                fixedAssetList.remove(returnedAsset);
                fixedAssetList.add(returnedAsset);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Metod se poziva u FragmentOne u deleteActivityLauncher i ovdje u adapteru na long click
    public void deleteAsset(int position){
        FixedAsset assetToRemove = filteredFixedAssetList.remove(position);
        if (assetToRemove != null)
            fixedAssetList.remove(assetToRemove);
        notifyItemRemoved(position);
        showCustomToast(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, location;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            icon = itemView.findViewById(R.id.icon);
        }
    }

}


