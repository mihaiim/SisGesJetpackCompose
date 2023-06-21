package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.model.Product
import com.mihaiim.sisgesjetpackcompose.domain.repository.AdministrationRepository
import com.mihaiim.sisgesjetpackcompose.domain.repository.ProductRepository
import com.mihaiim.sisgesjetpackcompose.domain.repository.StoragePositionRepository
import com.mihaiim.sisgesjetpackcompose.domain.repository.StorageRepository
import com.mihaiim.sisgesjetpackcompose.others.AdministrationEnum
import com.mihaiim.sisgesjetpackcompose.others.Constants.PRODUCT_CODE_LENGTH
import com.mihaiim.sisgesjetpackcompose.others.Constants.SHELF_CODE_LENGTH
import com.mihaiim.sisgesjetpackcompose.others.Result
import com.mihaiim.sisgesjetpackcompose.others.ScanScreenTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val administrationRepository: AdministrationRepository,
    private val productRepository: ProductRepository,
    private val storagePositionRepository: StoragePositionRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    val scanInProgress = mutableStateOf(false)
    val input1 = mutableStateOf("")
    val input2 = mutableStateOf("")
    val input3 = mutableStateOf("")
    val input4 = mutableStateOf("")
    val screenType = mutableStateOf(ScanScreenTypeEnum.NONE)
    val step = mutableStateOf(0)
    val quantityLeft = mutableStateOf(0)

    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()
    private val _errorResIdFlow = MutableSharedFlow<Int>()
    val errorResIdFlow = _errorResIdFlow.asSharedFlow()
    private val _navigateBackFlow = MutableSharedFlow<Unit>()
    val navigateBackFlow = _navigateBackFlow.asSharedFlow()
    private val _navigateToProductsListFlow = MutableSharedFlow<String>()
    val navigateToProductsListFlow = _navigateToProductsListFlow.asSharedFlow()
    private val _navigateToProductDetailsFlow = MutableSharedFlow<String>()
    val navigateToProductDetailsFlow = _navigateToProductDetailsFlow.asSharedFlow()

    private var oldShelfCode: String? = null
    private var input1HasFocus = true
    private var input2HasFocus = false
    private var input3HasFocus = false
    private var input4HasFocus = false

    var product: Product? = null
        private set

    fun changeInputsFocus(
        input1HasFocus: Boolean = false,
        input2HasFocus: Boolean = false,
        input3HasFocus: Boolean = false,
        input4HasFocus: Boolean = false,
    ) {
        this.input1HasFocus = input1HasFocus
        this.input2HasFocus = input2HasFocus
        this.input3HasFocus = input3HasFocus
        this.input4HasFocus = input4HasFocus
    }

    fun handleScanResult(result: String) {
        scanInProgress.value = false
        if (input1HasFocus) {
            input1.value = result
        } else if (input2HasFocus) {
            input2.value = result
        } else if (input3HasFocus) {
            input3.value = result
        } else {
            input4.value = result
        }
    }

    fun goForward() {
        when (screenType.value) {
            ScanScreenTypeEnum.IN -> goForwardIn(input1.value, input2.value)
            ScanScreenTypeEnum.OUT -> goForwardOut(input1.value, input2.value)
            ScanScreenTypeEnum.MOVE -> goForwardMove(
                input1.value,
                input2.value,
                input3.value,
                input4.value,
            )
            ScanScreenTypeEnum.SEE_STOCKS -> nextStepSeeStocks(input1.value)
            ScanScreenTypeEnum.NONE -> {}
        }
    }

    private fun checkQuantity(quantityStr: String): Boolean {
        if (quantityStr.isEmpty()) {
            viewModelScope.launch {
                _errorResIdFlow.emit(R.string.error_quantity_needed)
            }
            return false
        }
        try {
            val quantity = quantityStr.toInt()
            if (quantity < 0) {
                viewModelScope.launch {
                    _errorResIdFlow.emit(R.string.error_quantity_must_be_positive_number)
                }
                return false
            }
            return true
        } catch (e: Exception) {
            viewModelScope.launch {
                _errorResIdFlow.emit(R.string.error_quantity_must_be_a_number)
            }
            return false
        }
    }

    private fun goForwardIn(input1: String, input2: String) {
        if (!checkQuantity(input2)) {
            return
        }
        when (step.value) {
            0 -> {
                nextStepIn(
                    productCode = input1,
                    quantity = input2.toInt(),
                )
            }
            1 -> {
                nextStepIn(
                    shelfCode = input1,
                    quantity = input2.toInt(),
                )
            }
        }
    }

    private fun goForwardOut(input1: String, input2: String) {
        if (!checkQuantity(input2)) {
            return
        }
        when (step.value) {
            0 -> {
                nextStepOut(
                    productCode = input1,
                    quantity = input2.toInt(),
                )
            }
            1 -> {
                nextStepOut(
                    productCode = product?.code,
                    shelfCode = input1,
                    quantity = input2.toInt(),
                )
            }
        }
    }

    private fun goForwardMove(input1: String, input2: String, input3: String, input4: String) {
        when (step.value) {
            0 -> {
                nextStepMove(
                    productCode = input1,
                    shelfCode = input2,
                )
            }
            1 -> {
                if (!checkQuantity(input3)) {
                    return
                }
                nextStepMove(
                    productCode = input1,
                    shelfCode = input2,
                    quantity = input3.toInt(),
                )
            }
            else -> {
                nextStepMove(
                    productCode = input1,
                    shelfCode = input4,
                    quantity = input3.toInt(),
                )
            }
        }
    }

    private fun nextStepIn(
        productCode: String? = null,
        shelfCode: String? = null,
        quantity: Int? = null,
    ) = viewModelScope.launch {
        if (step.value == 0) {
            if (checkIfProductExists(productCode!!)) {
                product = getProduct(productCode)
                if (product == null) {
                    return@launch
                }
                quantityLeft.value = quantity!!
                incrementStepAndClearInputs()
            }
        } else if (step.value == 1) {
            if (quantity!! > quantityLeft.value) {
                _errorResIdFlow.emit(R.string.error_much_quantity_than_left_quantity)
                return@launch
            }
            if (checkIfShelfExists(shelfCode!!) &&
                productsInOut(shelfCode, quantity, AdministrationEnum.ADD)) {
                val newQuantity = quantityLeft.value - quantity
                if (newQuantity == 0) {
                    goBack()
                } else {
                    quantityLeft.value = newQuantity
                    incrementStepAndClearInputs(incrementStep = false)
                }
            }
        }
    }

    private fun nextStepOut(
        productCode: String? = null,
        shelfCode: String? = null,
        quantity: Int? = null,
    ) = viewModelScope.launch {
        if (step.value == 0) {
            if (checkIfProductExists(productCode!!)) {
                product = getProduct(productCode)
                if (product == null) {
                    return@launch
                }
                quantityLeft.value = quantity!!
                incrementStepAndClearInputs()
            }
        } else if (step.value == 1) {
            if (quantity!! > quantityLeft.value) {
                _errorResIdFlow.emit(R.string.error_much_quantity_than_left_quantity)
                return@launch
            }
            if (!checkIfShelfExists(shelfCode!!)) {
                return@launch
            }
            if (!checkIfShelfHasEnoughQuantity(productCode!!, shelfCode, quantity)) {
                return@launch
            }
            if (productsInOut(shelfCode, quantity, AdministrationEnum.RETRIEVE)) {
                val newQuantity = quantityLeft.value - quantity
                if (newQuantity == 0) {
                    goBack()
                } else {
                    quantityLeft.value = newQuantity
                    incrementStepAndClearInputs(incrementStep = false)
                }
            }
        }
    }

    private fun nextStepMove(
        productCode: String? = null,
        shelfCode: String? = null,
        quantity: Int? = null,
    ) = viewModelScope.launch {
        if (step.value == 0) {
            if (checkIfProductExists(productCode!!) &&
                checkIfShelfExists(shelfCode!!, true)) {
                oldShelfCode = shelfCode
                incrementStepAndClearInputs(clearInputs = false)
            }
        } else if (step.value == 1) {
            if (!checkIfShelfHasEnoughQuantity(productCode!!, shelfCode!!, quantity!!)) {
                return@launch
            }
            incrementStepAndClearInputs(clearInputs = false)
        } else if (step.value == 2) {
            if (!checkIfShelfExists(shelfCode!!, new = true)) {
                return@launch
            }
            if (moveProducts(productCode!!, shelfCode, quantity!!)) {
                goBack()
            }
        }
    }

    private fun nextStepSeeStocks(productCodeOrName: String) = viewModelScope.launch {
        if (productCodeOrName.isEmpty()) {
            _errorResIdFlow.emit(R.string.error_product_code_or_name_needed)
            return@launch
        }
        if (productCodeOrName.length == PRODUCT_CODE_LENGTH && productCodeOrName.isDigitsOnly()) {
            if (checkIfProductExists(productCodeOrName)) {
                scanInProgress.value = false
                _navigateToProductDetailsFlow.emit(productCodeOrName)
            }
        } else {
            scanInProgress.value = false
            _navigateToProductsListFlow.emit(productCodeOrName)
        }
    }

    private suspend fun checkIfProductExists(productCode: String): Boolean {
        val errorResId = verifyProductCode(productCode)
        if (errorResId == 0) {
            when(val result = productRepository.productExists(productCode)) {
                is Result.Success -> {
                    if (!result.data) {
                        _errorResIdFlow.emit(R.string.error_product_does_not_exist_with_the_code)
                    }
                    return result.data
                }
                is Result.Error -> _failFlow.emit(result.message)
            }
        } else {
            _errorResIdFlow.emit(errorResId)
        }
        return false
    }

    private suspend fun getProduct(productCode: String) =
        when(val result = productRepository.getMinifiedProductByCode(productCode)) {
            is Result.Success -> result.data
            is Result.Error -> {
                _failFlow.emit(result.message)
                null
            }
        }

    private suspend fun checkIfShelfExists(
        shelfCode: String,
        current: Boolean = false,
        new: Boolean = false,
    ): Boolean {
        val errorResId = verifyShelfCode(shelfCode, current, new)
        if (errorResId == 0) {
            when(val result = storagePositionRepository.positionExists(shelfCode)) {
                is Result.Success -> {
                    if (!result.data) {
                        _errorResIdFlow.emit(R.string.error_shelf_does_not_exist_with_the_code)
                    }
                    return result.data
                }
                is Result.Error -> _failFlow.emit(result.message)
            }
        } else {
            _errorResIdFlow.emit(errorResId)
        }
        return false
    }

    private suspend fun checkIfShelfHasEnoughQuantity(
        productCode: String,
        shelfCode: String,
        quantity: Int,
    ) = when (val result = storageRepository.getProductQuantityAtPosition(
        productCode,
        shelfCode,
    )) {
        is Result.Success -> {
            if (result.data < quantity) {
                _errorResIdFlow.emit(R.string.error_much_quantity_than_total_quantity_at_shelf)
                false
            } else {
                true
            }
        }
        is Result.Error -> {
            _failFlow.emit(result.message)
            false
        }
    }

    private suspend fun productsInOut(
        shelfCode: String,
        quantity: Int,
        type: AdministrationEnum,
    ) = when(val result = administrationRepository.insert(
        product!!.code,
        product!!.name,
        shelfCode,
        quantity,
        System.currentTimeMillis(),
        type,
    )) {
        is Result.Success -> result.data
        is Result.Error -> {
            _failFlow.emit(result.message)
            false
        }
    }

    private suspend fun moveProducts(
        productCode: String,
        newShelfCode: String,
        quantity: Int,
    ) = when(val result = storageRepository.moveProduct(
        productCode,
        oldShelfCode!!,
        newShelfCode,
        quantity,
    )) {
        is Result.Success -> result.data
        is Result.Error -> {
            _failFlow.emit(result.message)
            false
        }
    }

    private fun verifyProductCode(productCode: String): Int {
        if (productCode.isEmpty()) {
            return R.string.error_product_code_needed
        }
        if (productCode.length != PRODUCT_CODE_LENGTH || !productCode.isDigitsOnly()) {
            return R.string.error_wrong_product_code
        }
        return 0
    }

    private fun verifyShelfCode(
        shelfCode: String,
        current: Boolean = false,
        new: Boolean = false,
    ): Int {
        if (shelfCode.isEmpty()) {
            if (current) {
                return R.string.error_current_shelf_code_needed
            }
            if (new) {
                return R.string.error_new_shelf_code_needed
            }
            return R.string.error_shelf_code_needed
        }
        if (shelfCode.length != SHELF_CODE_LENGTH) {
            return R.string.error_wrong_shelf_code
        }
        if (!(shelfCode[0].isLetter() && shelfCode[2].isLetter() && shelfCode[4].isLetter())) {
            return R.string.error_wrong_shelf_code
        }
        if (!(shelfCode[1].isDigit() && shelfCode[3].isDigit() && shelfCode[5].isDigit())) {
            return R.string.error_wrong_shelf_code
        }
        return 0
    }

    private fun incrementStepAndClearInputs(
        incrementStep: Boolean = true,
        clearInputs: Boolean = true,
    ) {
        if (incrementStep) {
            step.value = step.value + 1
        }
        if (clearInputs) {
            input1.value = ""
            input2.value = ""
            input3.value = ""
            input4.value = ""
        }
    }

    private suspend fun goBack() {
        scanInProgress.value = false
        _navigateBackFlow.emit(Unit)
    }
}