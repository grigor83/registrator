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

import com.example.registar.EmployeeActivity;
import com.example.registar.EmployeeEditActivity;
import com.example.registar.R;
import com.example.registar.fragment.SecondFragment;
import com.example.registar.model.EmployeeWithRelations;
import com.example.registar.util.BitmapHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private final List<EmployeeWithRelations> employees, filteredEmployees;
    private final ActivityResultLauncher<Intent> employeeActivityLauncher;
    private View popupView;
    private float touchX, touchY;
    public int highlightedItemPosition = -1;

    public EmployeeAdapter(List<EmployeeWithRelations> employees, ActivityResultLauncher<Intent> employeeActivityLauncher) {
        this.employees = employees;
        this.filteredEmployees = new ArrayList<>(employees);
        this.employeeActivityLauncher = employeeActivityLauncher;
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
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {
        final EmployeeWithRelations employee = filteredEmployees.get(position);
        holder.fullName.setText(employee.getEmployee().getFullName());
        if (employee.getDepartment() != null)
            holder.department.setText(employee.getDepartment().getName());

        // Set the background based on whether the item is highlighted
        if (highlightedItemPosition == position)
            holder.itemView.setBackgroundResource(R.drawable.background_textview); // Highlighted background
        else
            holder.itemView.setBackgroundResource(R.drawable.background_highlighted); // Default background

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(popupView.getContext(), EmployeeActivity.class);
                startEmployeeActivity(intent, employee, holder.getAdapterPosition());
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

            showPopupMenu(v, employee, holder.getAdapterPosition());
            return true;
        });

        File file = new File(employee.getEmployee().getImagePath());
        if (file.exists()) {
            Uri imageUri = Uri.fromFile(file);
            // Dimensions of imageview are now available
            holder.icon.post(() -> {
                BitmapHelper.processImageInBackground(popupView.getContext(), holder.icon, imageUri, true);
            });
        }
        else {
            holder.icon.setImageBitmap(null);
            holder.icon.setImageResource(R.drawable.chair);
        }
    }

    @Override
    public int getItemCount() {
        return filteredEmployees.size();
    }

    private void showPopupMenu(View view, EmployeeWithRelations employee, int position) {
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupView.findViewById(R.id.action_edit).setOnClickListener(v -> {
            Intent intent = new Intent(popupView.getContext(), EmployeeEditActivity.class);
            startEmployeeActivity(intent, employee, position);
            popupWindow.dismiss();
        });
        popupView.findViewById(R.id.action_delete).setOnClickListener(v -> {
            deleteEmployeeFromList(position);
            popupWindow.dismiss();
            SecondFragment.deleteEmployee(employee);
        });

        // Show the popup at the exact touch location
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (int) touchX, (int) touchY);
    }

    private void startEmployeeActivity(Intent intent, EmployeeWithRelations employee, int position) {
        intent.putExtra("clickedEmployee", employee);
        intent.putExtra("position", position);
        employeeActivityLauncher.launch(intent);
        highlightedItemPosition = -1;
    }

    public void refresh() {
        highlightedItemPosition = -1;
        filteredEmployees.clear();
        filteredEmployees.addAll(employees);
        notifyDataSetChanged();
    }

    public void filter(String nameQuery, String departmentQuery) {
        filteredEmployees.clear();

        String name = nameQuery.toLowerCase();
        String department = departmentQuery.toLowerCase();

        for (EmployeeWithRelations employee : employees) {
            boolean matchesName = !name.isEmpty() && employee.getEmployee().getFullName().toLowerCase().contains(name);
            boolean matchesDepartment = !department.isEmpty() && employee.getDepartment().getName().toLowerCase().startsWith(department);

            // If either query is empty, skip checking it; otherwise match one or both
            if ((nameQuery.isEmpty() && matchesDepartment) ||
                    (departmentQuery.isEmpty() && matchesName) ||
                    (matchesName && matchesDepartment))
                filteredEmployees.add(employee);
            else if (nameQuery.isEmpty() && departmentQuery.isEmpty()){
                filteredEmployees.clear();
                filteredEmployees.addAll(employees);
            }
        }

        notifyDataSetChanged();
    }

    public EmployeeWithRelations deleteEmployeeFromList(int position){
        EmployeeWithRelations employee = filteredEmployees.remove(position);
        if (employee != null)
            employees.remove(employee);

        highlightedItemPosition = -1;
        notifyItemRemoved(position);
        showCustomToast(popupView.getContext(), null);
        return employee;
    }
    public void addEmployeeToList(EmployeeWithRelations employee) {
        employees.add(employee);
        filteredEmployees.add(employee);
        notifyDataSetChanged();
    }
    public void replaceEmployeeInList(EmployeeWithRelations employee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployee().getId() == employee.getEmployee().getId()) {
                employees.set(i, employee);
                filteredEmployees.clear();
                filteredEmployees.addAll(employees);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, department;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            department = itemView.findViewById(R.id.department);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
