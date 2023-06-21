package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mihaiim.sisgesjetpackcompose.domain.model.User
import com.mihaiim.sisgesjetpackcompose.domain.repository.UserRepository
import com.mihaiim.sisgesjetpackcompose.others.Constants.KEY_USER
import com.mihaiim.sisgesjetpackcompose.others.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPref: SharedPreferences,
    private val gson: Gson,
) : ViewModel()  {

    val firstName = mutableStateOf("")
    val user = mutableStateOf<User>(User())

    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()

    init {
        getUserData()
    }

    fun getUserData() = viewModelScope.launch {
        when(val result = userRepository.getUserData()) {
            is Result.Success -> {
                saveUserToSharedPref(result.data)
                user.value = result.data
            }
            is Result.Error -> _failFlow.emit(result.message)
        }
    }

    private fun saveUserToSharedPref(user: User) {
        sharedPref.edit()
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }
}