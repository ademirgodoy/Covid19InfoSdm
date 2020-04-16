package br.com.ifsp.covid19infosdm.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.com.ifsp.covid19infosdm.model.Covid19Api.BASE_URL
import br.com.ifsp.covid19infosdm.model.Covid19Api.COUNTRIES_ENDPOINT
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray

class Covid19Service (val context: Context){
    private val requestQueue = Volley.newRequestQueue(context)
    private val gson = Gson()

    /* Acesso a web service usando Volley*/
    fun callGetCountries(): MutableLiveData<CountryList> {
        val url = "${BASE_URL}${COUNTRIES_ENDPOINT}"
        val countriesListLd = MutableLiveData<CountryList>()
        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { countriesList ->
                countriesListLd.value = gson.fromJson(countriesList.toString(),CountryList::class.java)
            },
            {error -> Log.e("Covid19InfoSdm","${error.message}")}
        )

        requestQueue.add(request)

        return countriesListLd
    }

}