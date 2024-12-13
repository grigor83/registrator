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

import com.example.registar.DepartmentActivity;
import com.example.registar.DepartmentEditActivity;
import com.example.registar.R;
import com.example.registar.fragment.FifthFragment;
import com.example.registar.model.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder>{

    private final List<Department> departments, filteredDepartments;
    private final ActivityResultLauncher<Intent> departmentActivityLauncher;
    private View popupView;
    private float touchX, touchY;
    public int highlightedItemPosition = -1;

    public DepartmentAdapter(ActivityResultLauncher<Intent> departmentActivityLauncher, List<Department> departments) {
        this.departmentActivityLauncher = departmentActivityLauncher;
        this.departments = departments;
        this.filteredDepartments = new ArrayList<>(departments);
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
        final Department department = filteredDepartments.get(position);
        holder.name.setText(department.getName());

        // Set the background based on whether the item is highlighted
        if (highlightedItemPosition == position)
            holder.itemView.setBackgroundResource(R.drawable.background_textview); // Highlighted background
        else
            holder.itemView.setBackgroundResource(R.drawable.background_highlighted); // Default background

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(popupView.getContext(), DepartmentActivity.class);
                startDepartmentActivity(intent, department, holder.getAdapterPosition());
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

            showPopupMenu(v, department, holder.getAdapterPosition());
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return filteredDepartments.size();
    }

    private void showPopupMenu(View view, Department department, int adapterPosition) {
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setOnClickListener(v -> {
            Intent intent = new Intent(popupView.getContext(), DepartmentEditActivity.class);
            startDepartmentActivity(intent, department, adapterPosition);
            popupWindow.dismiss();
        });
        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteDepartmentFromList(adapterPosition);
            popupWindow.dismiss();
            FifthFragment.deleteDepartment(department);
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    private void startDepartmentActivity(Intent intent, Department department, int adapterPosition) {
        intent.putExtra("clickedDepartment", department);
        intent.putExtra("position", adapterPosition);
        departmentActivityLauncher.launch(intent);
        highlightedItemPosition = -1;
    }

    public void refresh() {
        highlightedItemPosition = -1;
        filteredDepartments.clear();
        filteredDepartments.addAll(departments);
        notifyDataSetChanged();
    }

    public void filter(String departmentQuery) {
        filteredDepartments.clear();
        String name = departmentQuery.toLowerCase();

        for (Department department : departments) {
            boolean matchesName = !name.isEmpty() && department.getName().toLowerCase().contains(name);

            // If either query is empty, skip checking it; otherwise match one or both
            if (!departmentQuery.isEmpty() && matchesName)
                filteredDepartments.add(department);
            else if (departmentQuery.isEmpty()){
                filteredDepartments.clear();
                filteredDepartments.addAll(departments);
            }
        }

        notifyDataSetChanged();
    }

    public Department deleteDepartmentFromList(int position){
        Department department = filteredDepartments.remove(position);
        if (department != null)
            departments.remove(department);

        highlightedItemPosition = -1;
        notifyItemRemoved(position);
        showCustomToast(popupView.getContext(), null);
        return department;
    }
    public void addDepartmentToList(Department department) {
        departments.add(department);
        filteredDepartments.add(department);
        notifyDataSetChanged();
    }
    public void replaceDepartmentInList(Department department) {
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getId() == department.getId()) {
                departments.set(i, department);
                filteredDepartments.clear();
                filteredDepartments.addAll(departments);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.department);
    }
}
}
