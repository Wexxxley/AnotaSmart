package com.anotasmart.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.anotasmart.model.*
import com.anotasmart.model.entity.*

class EnumsConverters {
    @TypeConverter
    fun fromItemType(value: ItemType) = value.name
    @TypeConverter
    fun toItemType(value: String) = ItemType.valueOf(value)

    @TypeConverter
    fun fromUnitType(value: UnitType) = value.name
    @TypeConverter
    fun toUnitType(value: String) = UnitType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType) = value.name
    @TypeConverter
    fun toCategoryType(value: String) = CategoryType.valueOf(value)

    @TypeConverter
    fun fromSaleStatus(value: SaleStatus) = value.name
    @TypeConverter
    fun toSaleStatus(value: String) = SaleStatus.valueOf(value)

    @TypeConverter
    fun fromInstallmentStatus(value: InstallmentStatus) = value.name
    @TypeConverter
    fun toInstallmentStatus(value: String) = InstallmentStatus.valueOf(value)

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?) = value?.name
    @TypeConverter
    fun toPaymentMethod(value: String?) = value?.let { PaymentMethod.valueOf(it) }
}

@Database(
    entities = [
        Category::class,
        Product::class,
        Client::class,
        Sale::class,
        SaleItem::class,
        Installment::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(EnumsConverters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anotasmart_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
