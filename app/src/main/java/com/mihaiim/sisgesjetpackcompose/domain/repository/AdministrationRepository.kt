package com.mihaiim.sisgesjetpackcompose.domain.repository

import com.mihaiim.sisgesjetpackcompose.domain.model.Administration
import com.mihaiim.sisgesjetpackcompose.others.AdministrationEnum
import com.mihaiim.sisgesjetpackcompose.others.Result

interface AdministrationRepository {

    suspend fun insert(
        productCode: String,
        productName: String,
        positionCode: String,
        quantity: Int,
        timestamp: Long,
        type: AdministrationEnum,
    ): Result<Boolean>

    suspend fun get(
        type: AdministrationEnum,
        startDate: Long,
        endDate: Long,
        productCode: String? = null,
        productName: String? = null,
    ): Result<List<Administration>>
}