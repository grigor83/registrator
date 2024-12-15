package com.example.registar.adapter;

import static com.example.registar.MainActivity.showCustomToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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

import com.example.registar.LocationActivity;
import com.example.registar.LocationEditActivity;
import com.example.registar.R;
import com.example.registar.fragment.ThirdFragment;
import com.example.registar.util.BitmapHelper;
import com.example.registar.model.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private final List<Location> locations, filteredLocations;
    private final ActivityResultLauncher<Intent> locationActivityLauncher;
    private View popupView;
    private float touchX, touchY;
    public int highlightedItemPosition = -1;

    public LocationAdapter(List<Location> locations, ActivityResultLauncher<Intent> locationActivityLauncher) {
        this.locations = locations;
        this.filteredLocations = new ArrayList<>(locations);
        this.locationActivityLauncher = locationActivityLauncher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.recycler_item_employee, parent, false);
        this.popupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_crud, null);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location location = filteredLocations.get(position);
        holder.city.setText(location.getCity());
        holder.address.setText(location.getAddress());

        // Set the background based on whether the item is highlighted
        if (highlightedItemPosition == position)
            holder.itemView.setBackgroundResource(R.drawable.background_textview); // Highlighted background
        else
            holder.itemView.setBackgroundResource(R.drawable.background_highlighted); // Default background

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(popupView.getContext(), LocationActivity.class);
                startLocationActivity(intent, location, holder.getAdapterPosition());
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

            showPopupMenu(v, location, holder.getAdapterPosition());
            return true;
        });

        File file = new File(location.getImagePath());
        if (file.exists()) {
            Uri imageUri = Uri.fromFile(file);
            // Dimensions of imageview are now available
            holder.icon.post(() -> {
                BitmapHelper.processImageInBackground(popupView.getContext(), holder.icon, imageUri, true);
            });
        }
        else {
            holder.icon.setImageBitmap(null);
            holder.icon.setImageResource(R.drawable.location_image);
        }
    }

    @Override
    public int getItemCount() {
        return filteredLocations.size();
    }

    private void startLocationActivity(Intent intent, Location location, int adapterPosition) {
        intent.putExtra("clickedLocation", location);
        intent.putExtra("position", adapterPosition);
        locationActivityLauncher.launch(intent);
        highlightedItemPosition = -1;
    }

    private void showPopupMenu(View view, Location location, int adapterPosition) {
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setOnClickListener(v -> {
            Intent intent = new Intent(popupView.getContext(), LocationEditActivity.class);
            startLocationActivity(intent, location, adapterPosition);
            popupWindow.dismiss();
        });
        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteLocationFromList(adapterPosition);
            popupWindow.dismiss();
            ThirdFragment.deleteLocation(location);
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    public Location deleteLocationFromList(int position) {
        Location location = filteredLocations.remove(position);
        if (location != null)
            locations.remove(location);

        highlightedItemPosition = -1;
        notifyItemRemoved(position);
        showCustomToast(popupView.getContext(), null);
        return location;
    }

    public void addLocationToList(Location location) {
        locations.add(location);
        filteredLocations.add(location);
        notifyDataSetChanged();
    }

    public void replaceLocationInList(Location location) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getId() == location.getId()) {
                locations.set(i, location);
                filteredLocations.clear();
                filteredLocations.addAll(locations);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void refresh() {
        highlightedItemPosition = -1;
        filteredLocations.clear();
        filteredLocations.addAll(locations);
        notifyDataSetChanged();
    }

    public void filter(String cityQuery, String addressQuery) {
        filteredLocations.clear();

        String city = cityQuery.toLowerCase();
        String address = addressQuery.toLowerCase();

        for (Location location : locations) {
            boolean matchesCity = !cityQuery.isEmpty() && location.getCity().toLowerCase().contains(city);
            boolean matchesAddress = !addressQuery.isEmpty() && location.getAddress().toLowerCase().contains(address);

            // If either query is empty, skip checking it; otherwise match one or both
            if ((cityQuery.isEmpty() && matchesAddress) ||
                    (addressQuery.isEmpty() && matchesCity) ||
                    (matchesCity && matchesAddress)){
                filteredLocations.add(location);
            }
            else if (cityQuery.isEmpty() && addressQuery.isEmpty()) {
                filteredLocations.clear();
                filteredLocations.addAll(locations);
            }
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView city, address;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.fullName);
            address = itemView.findViewById(R.id.department);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
