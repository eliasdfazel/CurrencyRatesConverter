package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.*


@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataBaseTable(arrayOfDatabaseDataModels: ArrayList<DatabaseDataModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDataBaseTable(arrayOfDatabaseDataModels: ArrayList<DatabaseDataModel>)

    @Delete
    suspend fun deleteDataBaseTable(databaseDataModel: DatabaseDataModel)

    @Query("SELECT * FROM CurrencyRates")
    suspend fun getAllCurrencyBaseTable(): List<DatabaseDataModel>

    @Query("SELECT COUNT(CurrencyCode) FROM CurrencyRates")
    suspend fun getRowCount(): Int
}