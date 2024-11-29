package com.example.registar.adapter;

import static com.example.registar.MainActivity.showCustomToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.example.registar.AssetEditActivity;
import com.example.registar.helper.BitmapHelper;
import com.example.registar.R;
import com.example.registar.model.Asset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.ViewHolder>{
    private final List<Asset> assetList, filteredAssetList;
    private View popupView;
    private float touchX, touchY;
    private final ActivityResultLauncher<Intent> deleteAssetLauncher;
    public int highlightedItemPosition = -1;

    public AssetsAdapter(List<Asset> assetList,
                         ActivityResultLauncher<Intent> deleteActivityLauncher) {
        this.assetList = assetList;
        this.filteredAssetList = new ArrayList<>(assetList);
        this.deleteAssetLauncher = deleteActivityLauncher;
    }

    // Kreira prazan viewHolder u koji još nisu upisani podaci
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.assets_recycler_item, parent, false);
        this.popupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_crud, null);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Asset asset = filteredAssetList.get(position);
        holder.title.setText(asset.getTitle());
        holder.description.setText(asset.getDescription());
        holder.location.setText(asset.getLocation().getCity());

        // Set the background based on whether the item is highlighted
        if (highlightedItemPosition == position)
            holder.itemView.setBackgroundResource(R.drawable.background_textview); // Highlighted background
        else
            holder.itemView.setBackgroundResource(R.drawable.background_highlighted); // Default background

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(popupView.getContext(), AssetActivity.class);
                intent.putExtra("clickedAsset", asset);
                intent.putExtra("position", holder.getAdapterPosition());
                deleteAssetLauncher.launch(intent);

                highlightedItemPosition = -1;
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
            // Highlight the item only on the first long click
            if (highlightedItemPosition != holder.getAdapterPosition()) {
                // Remove highlight from the previous item and highlight the current one
                highlightedItemPosition = holder.getAdapterPosition();
                notifyItemChanged(position);
            }
            showPopupMenu(v, asset, holder.getAdapterPosition());
            return true;
        });

        File file = new File(asset.getImagePath());
        if (file.exists()) {
            Log.i("BitmapHelper", "File does exist: " + asset.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            // Dimensions of imageview are now available
            holder.icon.post(() -> {
                BitmapHelper.processImageInBackground(popupView.getContext(), holder.icon, imageUri);
            });
        }
        else {
            holder.icon.setImageBitmap(null);
            holder.icon.setImageResource(R.drawable.chair);
        }

    }

    @Override
    public int getItemCount() {
        return filteredAssetList.size();
    }

    // Method to filter the list by title and location
    public void filter(String titleQuery, String locationQuery) {
        filteredAssetList.clear();

        String title = titleQuery.toLowerCase();
        String location = locationQuery.toLowerCase();

        for (Asset asset : assetList) {
            boolean matchesTitle = !titleQuery.isEmpty() && asset.getTitle().toLowerCase().startsWith(title);
            boolean matchesLocation = !locationQuery.isEmpty() && asset.getLocation().getCity().toLowerCase().startsWith(location);

            // If either query is empty, skip checking it; otherwise match one or both
            if ((titleQuery.isEmpty() && matchesLocation) ||
                    (locationQuery.isEmpty() && matchesTitle) ||
                    (matchesTitle && matchesLocation))
                filteredAssetList.add(asset);
            else if (titleQuery.isEmpty() && locationQuery.isEmpty()){
                filteredAssetList.clear();
                filteredAssetList.addAll(assetList);
            }
        }

        notifyDataSetChanged();
    }

    private void showPopupMenu(View view, Asset asset, int position) {
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setOnClickListener(v -> {
            startEditActivity(asset);
            popupWindow.dismiss();
        });
        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteAssetFromList(position);
            popupWindow.dismiss();
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    public void startEditActivity(Asset asset){
        Intent intent = new Intent(popupView.getContext(), AssetEditActivity.class);
        intent.putExtra("clickedAsset", asset);
        deleteAssetLauncher.launch(intent);

        highlightedItemPosition = -1;
        notifyDataSetChanged();
    }

    // Metod se poziva u FirstFragment u deleteActivityLauncher
    public void replaceAssetInList(Asset returnedAsset) {
        for (int i = 0; i < assetList.size(); i++) {
            if (assetList.get(i).getId() == returnedAsset.getId()) {
                assetList.set(i, returnedAsset);
                filteredAssetList.clear();
                filteredAssetList.addAll(assetList);
                notifyDataSetChanged();
                break;
            }
        }
    }

    // Metod se poziva u FirstFragment u deleteActivityLauncher i ovdje u adapteru na long click
    public void deleteAssetFromList(int position){
        Asset assetToRemove = filteredAssetList.remove(position);
        if (assetToRemove != null)
            assetList.remove(assetToRemove);

        highlightedItemPosition = -1;
        notifyItemRemoved(position);
        showCustomToast(popupView.getContext());
    }

    public void addAssetToList(Asset createdAsset) {
        assetList.add(createdAsset);
        filteredAssetList.add(createdAsset);
        notifyDataSetChanged();
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


