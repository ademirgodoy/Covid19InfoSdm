package br.com.ifsp.covid19infosdm

import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import br.com.ifsp.covid19infosdm.model.CaseList
import br.com.ifsp.covid19infosdm.viewmodel.Covid19ViewModel
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: Covid19ViewModel
    private lateinit var countryAdapter: ArrayAdapter<String>

    /* Classe para os serviços que serão acessados */
    private enum class Information (val type:String){
        DAY_ONE ("Day one"),
        BY_COUNTRY("By country")
    }

    /* Classe para status que será buscado no serviço */
    private enum class Status(val type: String){
        CONFIRMED ("Confirmed"),
        RECOVERED ("Recovered"),
        DEATHS ("Deaths")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = Covid19ViewModel(this)

        countryAdapterInit()
        informationAdapterInit()
        statusAdapterInit()
    }

    fun onRetrieveClick(view: View){
        when (informacaoSP.selectedItem.toString()){
            Information.DAY_ONE.type -> {fetchDayOne()}
            Information.BY_COUNTRY.type -> {fetchByCountry()}
        }
    }

    private fun countryAdapterInit(){
        /* Preenchido por Webservice*/
        countryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        paisSP.adapter = countryAdapter
        viewModel.fetchCountries().observe(
            this,
            Observer { countryList ->
                countryList.forEach{ countryListItem ->
                    if (countryListItem.country.isNotEmpty()){
                        countryAdapter.add(countryListItem.country)
                    }
                }

            }
        )
    }

    private fun informationAdapterInit(){
        val informationList = arrayListOf<String>()
        Information.values().forEach { informationList.add(it.type) }

        informacaoSP.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, informationList)
        informacaoSP.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position){
                    Information.DAY_ONE.ordinal -> {
                        modoVisualTV.visibility = View.GONE
                        modoRG.visibility = View.GONE
                    }
                    Information.BY_COUNTRY.ordinal -> {
                        modoVisualTV.visibility = View.VISIBLE
                        modoRG.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun statusAdapterInit(){
        val statusList = arrayListOf<String>()
        Status.values().forEach { statusList.add(it.type) }

        statusSP.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, statusList)
    }

    private fun fetchDayOne(){
        ModoGrafico(ligado=false)
        viewModel.fetchDayOne(paisSP.selectedItem.toString(), statusSP.selectedItem.toString()).observe(
            this,
            Observer {casesList ->
                resultadoTV.text = casesListToString(casesList)
            }
        )
    }

    private fun fetchByCountry(){
        viewModel.fetchByCountry(paisSP.selectedItem.toString(), statusSP.selectedItem.toString()).observe(
            this,
            Observer {casesList ->
                if(textoRB.isChecked){
                    /* Modo texto */
                    ModoGrafico(ligado = false)
                    resultadoTV.text = casesListToString(casesList)
                }
                else{
                    /* Modo Grafico */
                    ModoGrafico(ligado = true)
                    resultadoGV.removeAllSeries()
                    resultadoGV.gridLabelRenderer.resetStyles()

                    /* Preparando Pontos*/
                    val pointsArrayList = arrayListOf<DataPoint>()
                    casesList.forEach{
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date.substring(0,10))
                        val point = DataPoint(date, it.cases.toDouble())
                        pointsArrayList.add(point)
                    }
                    val pointsSeries = LineGraphSeries(pointsArrayList.toTypedArray())
                    resultadoGV.addSeries(pointsSeries)

                    /* Formatando Gráficos*/
                    resultadoGV.gridLabelRenderer.setHumanRounding(false)
                    resultadoGV.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)

                    resultadoGV.gridLabelRenderer.numHorizontalLabels = 4
                    val primeiraData = Date(pointsArrayList.first().x.toLong())
                    val ultimaData = Date(pointsArrayList.last().x.toLong())
                    resultadoGV.viewport.setMinX(primeiraData.time.toDouble())
                    resultadoGV.viewport.setMaxX(ultimaData.time.toDouble())
                    resultadoGV.viewport.isXAxisBoundsManual = true

                    resultadoGV.gridLabelRenderer.numVerticalLabels = 4
                    resultadoGV.viewport.setMinY(pointsArrayList.first().y)
                    resultadoGV.viewport.setMaxY(pointsArrayList.last().y)
                    resultadoGV.viewport.isYAxisBoundsManual = true
                }
            }
        )
    }

    private fun ModoGrafico(ligado:Boolean){
        if(ligado){
            resultadoTV.visibility = View.GONE
            resultadoGV.visibility = View.VISIBLE
        }
        else{
            resultadoTV.visibility = View.VISIBLE
            resultadoGV.visibility = View.GONE
        }
    }

    private fun casesListToString(caseList: CaseList):String {
        var resultadoSb = StringBuffer()
        caseList.forEach{
            resultadoSb.append("Pais: ${it.country}\n")
            if (it.countryCode.isNotEmpty()){
                resultadoSb.append("Sigla: ${it.countryCode}\n")
            }
            resultadoSb.append("Data: ${it.date.substring(0,10)}\n")
            resultadoSb.append("Casos: ${it.cases}\n")
        }

        return resultadoSb.toString()
    }
}
