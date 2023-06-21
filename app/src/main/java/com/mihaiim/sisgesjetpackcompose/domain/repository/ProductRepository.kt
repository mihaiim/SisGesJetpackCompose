package com.mihaiim.sisgesjetpackcompose.domain.repository

import com.mihaiim.sisgesjetpackcompose.domain.model.Product
import com.mihaiim.sisgesjetpackcompose.others.Result

interface ProductRepository {

    suspend fun getMinifiedProductByCode(productCode: String): Result<Product>

    suspend fun getMinifiedProductsByName(searchTerm: String): Result<List<Product>>

    suspend fun getProductDetails(productCode: String): Result<Product>

    suspend fun productExists(productCode: String): Result<Boolean>
}