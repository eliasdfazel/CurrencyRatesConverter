package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.*

@Dao
interface CurrencyDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataBaseTable(vararg arrayOfCurrencyDataModels: CurrencyDataModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDataBaseTable(vararg arrayOfCurrencyDataModels: CurrencyDataModel)

    @Delete
    fun deleteDataBaseTable(currencyDataModel: CurrencyDataModel)

    @Query("SELECT * FROM CurrencyRates")
    fun getAllCurrencyBaseTable(): List<CurrencyDataModel>
}