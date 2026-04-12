package com.anotasmart.data

import androidx.room.TypeConverter
import com.anotasmart.data.enums.*

class Converters {
    @TypeConverter
    fun fromItemType(value: ItemType) = value.name

    @TypeConverter
    fun toItemType(value: String) = enumValueOf<ItemType>(value)

    @TypeConverter
    fun fromUnitType(value: UnitType) = value.name

    @TypeConverter
    fun toUnitType(value: String) = enumValueOf<UnitType>(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType) = value.name

    @TypeConverter
    fun toCategoryType(value: String) = enumValueOf<CategoryType>(value)

    @TypeConverter
    fun fromSaleStatus(value: SaleStatus) = value.name

    @TypeConverter
    fun toSaleStatus(value: String) = enumValueOf<SaleStatus>(value)

    @TypeConverter
    fun fromInstallmentStatus(value: InstallmentStatus) = value.name

    @TypeConverter
    fun toInstallmentStatus(value: String) = enumValueOf<InstallmentStatus>(value)

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod) = value.name

    @TypeConverter
    fun toPaymentMethod(value: String) = enumValueOf<PaymentMethod>(value)
}
