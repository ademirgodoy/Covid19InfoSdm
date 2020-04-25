package br.com.ifsp.covid19infosdm.model


import com.google.gson.annotations.SerializedName

data class CaseListItem(
    @SerializedName("Cases")
    val cases: Int,
    @SerializedName("Country")
    val country: String,
    @SerializedName("CountryCode")
    val countryCode: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("Lat")
    val lat: String,
    @SerializedName("Lon")
    val lon: String,
    @SerializedName("Status")
    val status: String
)