package br.com.ifsp.covid19infosdm.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import br.com.ifsp.covid19infosdm.model.Covid19Api
import br.com.ifsp.covid19infosdm.model.Covid19Service

class Covid19ViewModel (context: Context):ViewModel() {
    private val model = Covid19Service(context)

    fun fetchCountries() = model.callGetCountries()

    fun fetchDayOne(countryName: String, status: String) = model.callService(countryName,status,Covid19Api.RetrofitServices.Services.DAY_ONE)

    fun fetchByCountry(countryName: String, status: String) = model.callService(countryName,status,Covid19Api.RetrofitServices.Services.BY_COUNTRY)
}