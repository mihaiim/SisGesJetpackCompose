package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.repository.UserRepository
import com.mihaiim.sisgesjetpackcompose.others.Constants.PASSWORD_MIN_LENGTH
import com.mihaiim.sisgesjetpackcompose.others.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel()  {

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")

    private val _successFlow = MutableSharedFlow<Unit>()
    val successFlow = _successFlow.asSharedFlow()
    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()
    private val _errorResIdFlow = MutableSharedFlow<Int>()
    val errorResIdFlow = _errorResIdFlow.asSharedFlow()

    fun loginUser() {
        val email = email.value.trim()
        val errorResId = verifyLogin(email, password.value)
        viewModelScope.launch {
            if (errorResId == 0) {
                when (val result = userRepository.login(email, password.value)) {
                    is Result.Success -> _successFlow.emit(Unit)
                    is Result.Error -> _failFlow.emit(result.message)
                }
            } else {
                _errorResIdFlow.emit(errorResId)
            }
        }
    }

    fun registerUser() {
        val firstName = firstName.value.trim()
        val lastName = lastName.value.trim()
        val email = email.value.trim()

        val errorResId = verifyRegister(firstName, lastName, email, password.value)
        viewModelScope.launch {
            if (errorResId == 0) {
                when (val result = userRepository.register(
                    firstName,
                    lastName,
                    email,
                    password.value,
                )) {
                    is Result.Success -> _successFlow.emit(Unit)
                    is Result.Error -> _failFlow.emit(result.message)
                }
            } else {
                _errorResIdFlow.emit(errorResId)
            }
        }
    }

    private fun verifyLogin(
        email: String,
        password: String,
    ) = if (email.isEmpty() || password.isEmpty())
            R.string.error_all_fields_must_be_filled
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            R.string.error_email_wrong_format
        else if (password.length < PASSWORD_MIN_LENGTH)
            R.string.error_password_too_short
        else 0

    private fun verifyRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) = if (firstName.isEmpty() || lastName.isEmpty()) R.string.error_all_fields_must_be_filled
        else verifyLogin(email, password)
}