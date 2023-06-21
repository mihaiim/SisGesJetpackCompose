package com.mihaiim.sisgesjetpackcompose.domain.repository

import com.mihaiim.sisgesjetpackcompose.others.Result

interface StoragePositionRepository {

    suspend fun positionExists(code: String): Result<Boolean>
}