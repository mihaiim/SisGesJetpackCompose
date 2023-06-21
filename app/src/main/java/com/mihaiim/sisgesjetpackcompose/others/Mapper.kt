package com.mihaiim.sisgesjetpackcompose.others

import com.mihaiim.sisgesjetpackcompose.data.dto.AdministrationDto
import com.mihaiim.sisgesjetpackcompose.data.dto.ProductDto
import com.mihaiim.sisgesjetpackcompose.domain.model.Administration
import com.mihaiim.sisgesjetpackcompose.domain.model.Product

fun ProductDto.toProduct() = Product(
    code = code,
    name = name,
)

fun AdministrationDto.toAdministration() = Administration(
    type = if (type == 0) AdministrationEnum.ADD else AdministrationEnum.RETRIEVE,
    productCode = productCode,
    productName = productName,
    quantity = quantity,
    timestamp = timestamp,
)
