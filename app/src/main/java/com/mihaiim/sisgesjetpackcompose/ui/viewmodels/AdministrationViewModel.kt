package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.model.Administration
import com.mihaiim.sisgesjetpackcompose.domain.repository.AdministrationRepository
import com.mihaiim.sisgesjetpackcompose.others.AdministrationEnum
import com.mihaiim.sisgesjetpackcompose.others.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AdministrationViewModel @Inject constructor(
    private val administrationRepository: AdministrationRepository,
) : ViewModel() {

    val administrationList = mutableStateOf<List<Administration>>(listOf())
    val administrationType = mutableStateOf(AdministrationEnum.ADD_AND_RETRIEVE)
    val filterVisible = mutableStateOf(false)
    val productCode = mutableStateOf("")
    val productName = mutableStateOf("")
    val startDate = mutableStateOf<LocalDateTime>(LocalDateTime.now())
    val endDate = mutableStateOf<LocalDateTime>(LocalDateTime.now())

    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()
    private val _errorResIdFlow = MutableSharedFlow<Int>()
    val errorResIdFlow = _errorResIdFlow.asSharedFlow()

    init {
        resetFilters()
        getAdministration()
    }

    fun toggleFilterVisible() {
        filterVisible.value = !filterVisible.value
    }

    fun setStartDate(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        val dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, 0)
        if (dateTime.isAfter(endDate.value)) {
            viewModelScope.launch {
                _errorResIdFlow.emit(R.string.error_start_date_after_end_date)
            }
            return
        }
        startDate.value = dateTime
    }

    fun setEndDate(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        val dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, 59)
        if (dateTime.isBefore(startDate.value)) {
            viewModelScope.launch {
                _errorResIdFlow.emit(R.string.error_end_date_before_start_date)
            }
            return
        }
        endDate.value = dateTime
    }

    fun resetFilters() {
        administrationType.value = AdministrationEnum.ADD_AND_RETRIEVE
        productCode.value = ""
        productName.value = ""
        resetStartDate()
        resetEndDate()
    }

    fun getAdministration() = viewModelScope.launch {
        filterVisible.value = false
        val productCode = productCode.value.ifEmpty { null }
        val productName = productName.value.ifEmpty { null }
        val startDateMillis = getTimeInMillisFromLocalDateTime(startDate.value)
        val endDateMillis = getTimeInMillisFromLocalDateTime(endDate.value)

        when (val result = administrationRepository.get(
            administrationType.value,
            startDateMillis,
            endDateMillis,
            productCode,
            productName,
        )) {
            is Result.Success -> administrationList.value = result.data
            is Result.Error -> _failFlow.emit(result.message)
        }
    }

    private fun resetStartDate() {
        val now: LocalDate = LocalDate.now()
        val firstDay = LocalDateTime.of(
            now.year,
            now.month,
            1,
            0,
            0,
            0,
        )
        startDate.value = firstDay
    }

    private fun resetEndDate() {
        var now: LocalDate = LocalDate.now()
        now = now.withDayOfMonth(now.month.length(now.isLeapYear))
        val lastDay = LocalDateTime.of(
            now.year,
            now.month,
            now.dayOfMonth,
            23,
            59,
            59,
        )
        endDate.value = lastDay
    }

    private fun getTimeInMillisFromLocalDateTime(dateTime: LocalDateTime): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(dateTime.year,
            dateTime.monthValue - 1,
            dateTime.dayOfMonth,
            dateTime.hour,
            dateTime.minute,
            dateTime.second,
        )
        return calendar.time.time
    }
}