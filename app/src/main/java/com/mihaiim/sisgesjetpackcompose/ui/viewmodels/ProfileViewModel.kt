package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import android.content.SharedPreferences
import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.model.UpdateProfile
import com.mihaiim.sisgesjetpackcompose.domain.model.User
import com.mihaiim.sisgesjetpackcompose.domain.repository.UserRepository
import com.mihaiim.sisgesjetpackcompose.others.Constants.KEY_USER
import com.mihaiim.sisgesjetpackcompose.others.Constants.PASSWORD_MIN_LENGTH
import com.mihaiim.sisgesjetpackcompose.others.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPref: SharedPreferences,
    private val gson: Gson,
) : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val photoUri = mutableStateOf<Uri?>(null)

    private val _successFlow = MutableSharedFlow<Unit>()
    val successFlow = _successFlow.asSharedFlow()
    private val _pictureRemovedFlow = MutableSharedFlow<Unit>()
    val pictureRemovedFlow = _pictureRemovedFlow.asSharedFlow()
    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()
    private val _errorResIdFlow = MutableSharedFlow<Int>()
    val errorResIdFlow = _errorResIdFlow.asSharedFlow()

    private var newProfilePicture = false
    lateinit var user: User
    var tempPhotoUri: Uri? = null

    init {
        getUserFromSharedPref()
    }

    fun setPhotoUri(photoUri: Uri) {
        this.photoUri.value = photoUri
        newProfilePicture = true
        tempPhotoUri = null
    }

    fun updateUser() = viewModelScope.launch {
        val firstName = firstName.value.trim()
        val lastName = lastName.value.trim()
        val email = email.value.trim()

        val newFirstName = if (user.firstName == firstName) null else firstName
        val newLastName = if (user.lastName == lastName) null else lastName
        val newEmail = if (user.email == email) null else email
        val newPhotoUri = if (newProfilePicture) photoUri.value else null

        val errorResId = verifyData(newFirstName, newLastName, newEmail, password.value)
        if (errorResId == 0) {
            viewModelScope.launch {
                when(val result = userRepository.update(
                    newFirstName,
                    newLastName,
                    newEmail,
                    password.value,
                    newPhotoUri,
                )) {
                    is Result.Success -> {
                        saveUserToSharedPref(result.data)
                        _successFlow.emit(Unit)
                    }
                    is Result.Error -> _failFlow.emit(result.message)
                }
            }
        } else {
            _errorResIdFlow.emit(errorResId)
        }
    }

    fun removeProfilePicture() = viewModelScope.launch {
        when(val result = userRepository.removeProfilePicture()) {
            is Result.Success -> {
                removeProfilePictureFromMemory()
                _pictureRemovedFlow.emit(Unit)
                photoUri.value = null
                newProfilePicture = false
            }
            is Result.Error -> _failFlow.emit(result.message)
        }
    }

    private fun verifyData(
        firstName: String?,
        lastName: String?,
        email: String?,
        password: String?,
    ) = if (lastName != null && lastName.isEmpty())
            R.string.error_last_name_empty
        else if (firstName != null && firstName.isEmpty())
            R.string.error_first_name_empty
        else if (email != null && (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            R.string.error_email_wrong_format
        else if (password != null && password.isNotEmpty() && password.length < PASSWORD_MIN_LENGTH)
            R.string.error_password_too_short
        else 0

    private fun getUserFromSharedPref() {
        sharedPref.getString(KEY_USER, null)?.let {
            user = gson.fromJson(it, User::class.java)
            email.value = user.email
            firstName.value = user.firstName
            lastName.value = user.lastName
            photoUri.value = user.photoUri
        }
    }

    private fun saveUserToSharedPref(updateProfile: UpdateProfile) {
        sharedPref.edit()
            .putString(KEY_USER, gson.toJson(
                User(
                    user.id,
                    updateProfile.email ?: user.email,
                    updateProfile.firstName ?: user.firstName,
                    updateProfile.lastName ?: user.lastName,
                    updateProfile.photoUri ?: user.photoUri,
                )
            ))
            .apply()
    }

    private fun removeProfilePictureFromMemory() {
        user.photoUri = null
        sharedPref.edit()
            .putString(KEY_USER, gson.toJson(
                User(
                    user.id,
                    user.email,
                    user.firstName,
                    user.lastName,
                    user.photoUri,
                )
            ))
            .apply()
    }
}