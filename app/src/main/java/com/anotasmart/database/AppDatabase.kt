package com.anotasmart.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.anotasmart.database.dao.CartItemDao
import com.anotasmart.database.dao.CategoryDao
import com.anotasmart.database.dao.ClientDao
import com.anotasmart.database.dao.ExpenseDao
import com.anotasmart.database.dao.InstallmentDao
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.database.dao.SaleDao
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
        Installment::class,
        Expense::class,
        CartItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(EnumsConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun clientDao(): ClientDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun saleDao(): SaleDao
    abstract fun installmentDao(): InstallmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anotasmart_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
