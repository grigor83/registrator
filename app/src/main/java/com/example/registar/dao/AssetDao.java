package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.registar.model.Asset;
import com.example.registar.model.AssetWithRelations;

import java.util.List;

@Dao
public interface AssetDao {
    @Insert
    long insert(Asset asset);

    @Update
    void update(Asset asset);

    @Delete
    void delete(Asset asset);

    // Note... is varargs, here assets is an array
    @Delete
    void deleteAssets(Asset... assets);

    @Query("SELECT * FROM asset WHERE id = :assetId")
    Asset getById(int assetId);

    @Transaction
    @Query("SELECT * FROM asset WHERE id = :assetId")
    AssetWithRelations getAssetWithRelationsById(int assetId);

    @Transaction
    @Query("SELECT * FROM asset")
    List<AssetWithRelations> getAssetWithRelations();

    @Transaction
    @Query("SELECT * FROM asset WHERE locationId = :locationId")
    List<Asset> getAllByLocationId(int locationId);

}
