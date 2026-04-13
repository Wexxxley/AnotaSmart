package com.anotasmart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anotasmart.data.entities.Client

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Query("SELECT * FROM Client WHERE id = :id")
    suspend fun getById(id: String): Client?

    @Query("SELECT * FROM Client ORDER BY nome ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllOrderedByNamePaginated(limit: Int, offset: Int): List<Client>

    @Query("SELECT * FROM Client WHERE nome LIKE '%' || :query || '%' ORDER BY nome ASC")
    suspend fun searchByName(query: String): List<Client>
}
