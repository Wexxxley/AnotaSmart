package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM Client ORDER BY nome ASC")
    fun getAll(): Flow<List<Client>>

    @Query("SELECT * FROM Client WHERE id = :id")
    fun getById(id: String): Client?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(client: Client): Long

    @Update
    fun update(client: Client): Int

    @Delete
    fun delete(client: Client): Int
}
