package br.com.ifsp.covid19infosdm.model


import com.google.gson.annotations.SerializedName

data class CountryListItem(
    @SerializedName("Country")
    val country: String,
    @SerializedName("ISO2")
    val iSO2: String,
    @SerializedName("Slug")
    val slug: String
)