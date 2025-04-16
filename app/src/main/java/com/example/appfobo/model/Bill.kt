package com.example.appfobo.model

data class Bill(
    val userId: String = "",
    val items: List<ProductInBill> = emptyList(),
    val totalPrice: Int = 0,
    val timestamp: Long = 0L
)
