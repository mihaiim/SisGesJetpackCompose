package com.mihaiim.sisgesjetpackcompose.data.dto

data class UserDto(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val photoUri: String? = null,
)
