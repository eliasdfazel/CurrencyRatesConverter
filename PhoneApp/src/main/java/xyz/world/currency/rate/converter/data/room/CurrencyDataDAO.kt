package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.*

@Dao
interface CurrencyDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(vararg arrayOfCurrencyDataModels: CurrencyDataModel)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateData(vararg arrayOfCurrencyDataModels: CurrencyDataModel)


    @Delete
    fun deleteData(currencyDataModel: CurrencyDataModel)


    @Query("SELECT * FROM CurrencyRates ORDER BY CurrencyCode ASC")
    fun getAllWidgetData(): List<CurrencyDataModel>
//
//    @Query("SELECT * FROM WidgetData ORDER BY AppName ASC")
//    suspend fun getAllWidgetDataCoroutines(): List<CurrencyDataModel>
//
//
//    @Query("SELECT * FROM WidgetData WHERE PackageName IN (:PackageName) AND ClassNameProvider IN (:ClassNameWidgetProvider)")
//    fun loadWidgetByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String): CurrencyDataModel
//
//
//    @Query("UPDATE WidgetData SET WidgetId = :WidgetId WHERE PackageName = :PackageName AND ClassNameProvider == :ClassNameProvider")
//    fun updateWidgetIdByPackageNameClassName(PackageName: String, ClassNameProvider: String, WidgetId: Int): Int
//
//
//    @Query("UPDATE WidgetData SET WidgetLabel = :WidgetLabel WHERE WidgetId = :WidgetId")
//    fun updateWidgetLabelByWidgetId(WidgetId: Int, WidgetLabel: String): Int
//
//
//    @Query("UPDATE WidgetData SET Recovery = :AddedWidgetRecovery WHERE PackageName= :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
//    fun updateRecoveryByClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String, AddedWidgetRecovery: Boolean): Int
//
//
//    @Query("DELETE FROM WidgetData WHERE PackageName = :PackageName AND ClassNameProvider = :ClassNameWidgetProvider")
//    fun deleteByWidgetClassNameProviderWidget(PackageName: String, ClassNameWidgetProvider: String)
}