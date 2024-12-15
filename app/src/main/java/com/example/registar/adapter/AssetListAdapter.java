package com.example.registar.adapter;

import static com.example.registar.MainActivity.showCustomToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registar.AssetListActivity;
import com.example.registar.R;
import com.example.registar.fragment.FourthFragment;
import com.example.registar.model.AssetList;

import java.util.List;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder>{
    private final List<AssetList> assetLists;
    private final ActivityResultLauncher<Intent> assetListActivityLauncher;
    private View popupView;
    private float touchX, touchY;
    public int highlightedItemPosition = -1;

    public AssetListAdapter(ActivityResultLauncher<Intent> assetListActivityLauncher,
                            List<AssetList> assetLists) {
        this.assetListActivityLauncher = assetListActivityLauncher;
        this.assetLists = assetLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item_department, parent, false);
        this.popupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_crud, null);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AssetList assetList = assetLists.get(position);
        holder.name.setText(String.valueOf(assetList.getId()));

        // Set the background based on whether the item is highlighted
        if (highlightedItemPosition == position)
            holder.itemView.setBackgroundResource(R.drawable.background_textview); // Highlighted background
        else
            holder.itemView.setBackgroundResource(R.drawable.background_highlighted); // Default background

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(popupView.getContext(), AssetListActivity.class);
                startAssetListActivity(intent, assetList, holder.getAdapterPosition());
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

            showPopupMenu(v, assetList, holder.getAdapterPosition());
            return true;
        });

    }

    private void startAssetListActivity(Intent intent, AssetList assetList, int adapterPosition) {
        intent.putExtra("clickedAssetList", assetList);
        intent.putExtra("position", adapterPosition);
        assetListActivityLauncher.launch(intent);
        highlightedItemPosition = -1;
    }

    @Override
    public int getItemCount() {
        return assetLists.size();
    }
    private void showPopupMenu(View view, AssetList assetList, int adapterPosition) {
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setVisibility(View.GONE);

        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteAssetListFromList(adapterPosition);
            popupWindow.dismiss();
            FourthFragment.deleteAssetList(assetList);
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    public void refresh() {
        highlightedItemPosition = -1;
        notifyDataSetChanged();
    }

    public AssetList deleteAssetListFromList(int adapterPosition) {
        AssetList assetList = assetLists.remove(adapterPosition);
        if (assetList != null)
            assetLists.remove(assetList);

        highlightedItemPosition = -1;
        notifyItemRemoved(adapterPosition);
        showCustomToast(popupView.getContext(), null);
        return assetList;
    }

    public void addAssetListToList(AssetList assetList) {
        assetLists.add(assetList);
        notifyItemInserted(assetLists.size() - 1);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.department);
        }
    }
}
