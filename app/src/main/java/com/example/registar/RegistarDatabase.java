package com.example.registar;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.registar.dao.AssetDao;
import com.example.registar.dao.AssetListDao;
import com.example.registar.dao.DepartmentDao;
import com.example.registar.dao.EmployeeDao;
import com.example.registar.dao.ListItemDao;
import com.example.registar.dao.LocationDao;
import com.example.registar.model.Asset;
import com.example.registar.model.AssetList;
import com.example.registar.model.Department;
import com.example.registar.model.Employee;
import com.example.registar.model.ListItem;
import com.example.registar.model.Location;
import com.example.registar.util.Constants;
import com.example.registar.util.LocalDateConverter;

@Database(entities = { Asset.class, Employee.class, Location.class,
                        Department.class, AssetList.class, ListItem.class }, version = 1)
@TypeConverters({LocalDateConverter.class})
public abstract class RegistarDatabase extends RoomDatabase {
    public abstract AssetDao assetDao();
    public abstract EmployeeDao employeeDao();
    public abstract LocationDao locationDao();
    public abstract DepartmentDao departmentDao();
    public abstract AssetListDao assetListDao();
    public abstract ListItemDao listItemDao();

    private static RegistarDatabase registarDB;

    public static /*synchronized*/ RegistarDatabase getInstance(Context context) {
        if (null == registarDB)
            registarDB = buildDatabaseInstance(context);

        return registarDB;
    }

    private static RegistarDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context, RegistarDatabase.class, Constants.DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public  void cleanUp(){
        registarDB = null;
    }

}
