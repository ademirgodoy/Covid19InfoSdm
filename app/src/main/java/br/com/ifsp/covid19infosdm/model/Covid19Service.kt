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
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class Covid19Service (val context: Context){
    private val requestQueue = Volley.newRequestQueue(context)
    private val gson = Gson()

    /* Cria uma implementação da interface usando um objeto retrofit*/
    private val retrofitServices = with (Retrofit.Builder()){
        baseUrl(BASE_URL)
        addConverterFactory(GsonConverterFactory.create())
        build()
    }.create(Covid19Api.RetrofitServices::class.java)

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

    /* Acesso a web service usando retrofic. Como os serviços retornam o mesmo tipo de resposta
    * foram aglutinados nume mesma função
    * */
    fun callService (countryName: String, status: String, service: String): MutableLiveData<CaseList>{
        val caseList: MutableLiveData<CaseList> = MutableLiveData()

        /* Callback usado pelos servicos que retrnam o mesmo tipo de Json*/
        val callback = object : Callback<CaseList>{
            override fun onResponse(call: Call<CaseList>, response: Response<CaseList>) {
                if (response.isSuccessful){
                    caseList.value = response.body()
                }
            }

            override fun onFailure(call: Call<CaseList>, t: Throwable) {
                Log.e("Covid19InforSdm","Erro durante a execução do serviço")
            }

        }

        return caseList
    }



}