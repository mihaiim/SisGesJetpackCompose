package com.mihaiim.sisgesjetpackcompose.domain.model

import android.content.Context
import com.mihaiim.sisgesjetpackcompose.R

data class Storage(
    val positionCode: String,
    val quantity: Int,
) {

    fun getProductPositionString(context: Context): String {
        val pieces = context.resources.getQuantityString(
            R.plurals.pieces,
            quantity,
            quantity,
        )
        return context.getString(R.string.product_positions_with_pieces, positionCode, pieces)
    }
}
