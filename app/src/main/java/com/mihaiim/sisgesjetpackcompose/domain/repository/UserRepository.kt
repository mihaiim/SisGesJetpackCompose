package com.mihaiim.sisgesjetpackcompose.domain.repository

import android.net.Uri
import com.mihaiim.sisgesjetpackcompose.domain.model.UpdateProfile
import com.mihaiim.sisgesjetpackcompose.domain.model.User
import com.mihaiim.sisgesjetpackcompose.others.Result

interface UserRepository {

    suspend fun getUserData(): Result<User>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun update(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        password: String? = null,
        photoUri: Uri? = null,
    ): Result<UpdateProfile>

    suspend fun removeProfilePicture(): Result<Boolean>

    fun logout()
}