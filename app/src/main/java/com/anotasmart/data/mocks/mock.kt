package com.anotasmart.ui.screens

import com.anotasmart.R
import com.anotasmart.model.CategoryType
import com.anotasmart.model.ItemType
import com.anotasmart.model.UnitType
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Product
import java.util.UUID

object MockDataSource {

    fun getMockCategories(): List<Category> {
        return listOf(
            Category(id = "1", nome = "TODOS", tipo = CategoryType.ITENS),
            Category(id = "2", nome = "BEBIDAS", tipo = CategoryType.ITENS),
            Category(id = "3", nome = "ALIMENTOS", tipo = CategoryType.ITENS),
            Category(id = "4", nome = "LIMPEZA", tipo = CategoryType.ITENS),
            Category(id = "5", nome = "HIGIENE", tipo = CategoryType.ITENS),
            Category(id = "6", nome = "ROUPAS", tipo = CategoryType.ITENS)
        )
    }

    fun getMockProducts(): List<Product> {
        return listOf(
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "2",
                nome = "PRODUTO mega hiper hiper ultra",
                precoCusto = 5.0,
                precoVenda = 10.00,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 15.0,
                imagePath = R.drawable.i1.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "3",
                nome = "PRODUTO B",
                precoCusto = 12.0,
                precoVenda = 25.50,
                unidadeMedida = UnitType.KG,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 8.5,
                imagePath = R.drawable.i2.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "2",
                nome = "PRODUTO C",
                precoCusto = 2.0,
                precoVenda = 5.00,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 50.0,
                imagePath = R.drawable.i3.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "4",
                nome = "PRODUTO D",
                precoCusto = 6.0,
                precoVenda = 12.00,
                unidadeMedida = UnitType.L,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 3.2,
                imagePath = R.drawable.i4.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "5",
                nome = "PRODUTO E",
                precoCusto = 4.0,
                precoVenda = 8.50,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 22.0,
                imagePath = R.drawable.i1.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "1",
                nome = "PRODUTO F",
                precoCusto = 7.0,
                precoVenda = 15.00,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 12.0,
                imagePath = R.drawable.i2.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "4",
                nome = "PRODUTO D",
                precoCusto = 6.0,
                precoVenda = 12.00,
                unidadeMedida = UnitType.L,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 3.2,
                imagePath = R.drawable.i4.toString()
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "5",
                nome = "PRODUTO E",
                precoCusto = 4.0,
                precoVenda = 8.50,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 22.0,
                imagePath = null
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "1",
                nome = "PRODUTO F",
                precoCusto = 7.0,
                precoVenda = 15.00,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 12.0,
                imagePath = null
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "4",
                nome = "PRODUTO D",
                precoCusto = 6.0,
                precoVenda = 12.00,
                unidadeMedida = UnitType.L,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 3.2,
                imagePath = null
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "5",
                nome = "PRODUTO E",
                precoCusto = 4.0,
                precoVenda = 8.50,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 22.0,
                imagePath = null
            ),
            Product(
                id = UUID.randomUUID().toString(),
                categoryId = "1",
                nome = "PRODUTO F",
                precoCusto = 7.0,
                precoVenda = 15.00,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 12.0,
                imagePath = null
            )
        )
    }
}