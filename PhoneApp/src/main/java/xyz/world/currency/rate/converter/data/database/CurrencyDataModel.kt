package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.world.currency.rate.converter.data.database.DatabasePath

@Entity(tableName = DatabasePath.CURRENCY_DATABASE_NAME)
data class CurrencyDataModel(
        @NonNull @PrimaryKey var CurrencyCode: String,

        @NonNull @ColumnInfo(name = "CountryName") var CountryName: String,

        @NonNull @ColumnInfo(name = "CurrencyRate") var CurrencyRate: Double,
        @ColumnInfo(name = "LastUpdateTime") var LastUpdateTime: String?)