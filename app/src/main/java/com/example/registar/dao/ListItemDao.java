package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.registar.model.ListItem;

import java.util.List;

@Dao
public interface ListItemDao {
    @Insert
    long insert(ListItem listItem);

    @Update
    void update(ListItem listItem);

    @Delete
    void delete(ListItem listItem);

    @Query("SELECT * FROM list_item")
    List<ListItem> getAll();

}
