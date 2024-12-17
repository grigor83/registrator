package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.registar.model.AssetList;

import java.util.List;
@Dao
public interface AssetListDao {
    @Insert
    long insert(AssetList list);

    @Update
    void update(AssetList list);

    @Delete
    void delete(AssetList list);

    @Query("SELECT * FROM asset_list")
    List<AssetList> getAll();

}

