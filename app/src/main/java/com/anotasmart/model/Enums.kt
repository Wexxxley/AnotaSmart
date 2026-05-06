package com.anotasmart.model

enum class ItemType {
    PRODUTO, SERVICO
}

enum class UnitType {
    UN, KG, L, M
}

enum class CategoryType {
    ITENS, DESPESAS
}

enum class SaleStatus {
    ORCAMENTO, PENDENTE, ATRASADA, FINALIZADA, CANCELADA
}

enum class InstallmentStatus {
    PENDENTE, PAGA, ATRASADA
}

enum class PaymentMethod {
    DINHEIRO, PIX, DEBITO, CREDITO
}
