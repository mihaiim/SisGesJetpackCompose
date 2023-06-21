package com.mihaiim.sisgesjetpackcompose.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaiim.sisgesjetpackcompose.domain.model.Product
import com.mihaiim.sisgesjetpackcompose.domain.model.Storage
import com.mihaiim.sisgesjetpackcompose.domain.repository.ProductRepository
import com.mihaiim.sisgesjetpackcompose.domain.repository.StorageRepository
import com.mihaiim.sisgesjetpackcompose.others.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    val productsList = mutableStateOf<List<Product>>(listOf())
    val productDetails = mutableStateOf(Product())
    val positions = mutableStateOf<List<Storage>>(listOf())

    private val _failFlow = MutableSharedFlow<String>()
    val failFlow = _failFlow.asSharedFlow()

    fun getProductsList(searchTerm: String) = viewModelScope.launch {
        when (val result = productRepository.getMinifiedProductsByName(searchTerm.trim())) {
            is Result.Success -> productsList.value = result.data
            is Result.Error -> _failFlow.emit(result.message)
        }
    }

    fun getProductDetails(productCode: String) = viewModelScope.launch {
        when (val result = productRepository.getProductDetails(productCode)) {
            is Result.Success -> productDetails.value = result.data
            is Result.Error -> _failFlow.emit(result.message)
        }
    }

    fun getProductPositions(productCode: String) = viewModelScope.launch {
        when (val result = storageRepository.getProductPositionsAndQuantities(productCode)) {
            is Result.Success -> positions.value = result.data
            is Result.Error -> _failFlow.emit(result.message)
        }
    }
}