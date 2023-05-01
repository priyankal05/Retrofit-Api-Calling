package com.example.myapplication

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.List
import com.example.myapplication.model.WeatherData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var tinydb: TinyDB
     private lateinit var binding: ActivityMainBinding
     lateinit var listHistory: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tinydb = TinyDB(this)

        getApiData("surat")

        binding.imgSearch.setOnClickListener {
            binding.relaMain.visibility = View.GONE
            binding.relaSearch.visibility = View.VISIBLE
        }

        val adapter = ArrayAdapter(this, R.layout.activity_list_item, tinydb.getListString("searchHistory"))
        Log.e("TAG", "onCreate: "+tinydb.getListString("searchHistory") )
        binding.listSearch.adapter = adapter

        listHistory = tinydb.getListString("searchHistory")

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                listHistory.add(binding.edtSearch.text.toString())
                tinydb.putListString("searchHistory",listHistory)
                Log.e("TAG", "onQueryTextSubmit: $listHistory" )

                getApiData(binding.edtSearch.text.toString())
                binding.relaMain.visibility = View.VISIBLE
                binding.relaSearch.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })


        /*binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getApiData(binding.edtSearch.text.toString())
                binding.relaMain.visibility = View.VISIBLE
                binding.relaSearch.visibility = View.GONE

                binding.edtSearch.clearFocus()
                val `in` = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                `in`.hideSoftInputFromWindow(binding.edtSearch.getWindowToken(), 0)

            }
            true
        }*/

    }


    private fun getApiData(cityName : String) {

        val apiInterface = RetrofitHelper.getInstance().create(ApiInterface::class.java)

        val userCall: Call<WeatherData> = apiInterface.getData(cityName, "en","json","a8a37db71ea612cdd8c0e13c23416a7a")

        userCall.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val otpdata = response.body()

                if(response.isSuccessful)
                {
                    if (otpdata != null) {

                        val loginData: WeatherData? = response.body()

                        Log.e("TAG", "onResponse: $loginData")
                        /*val gson = Gson()
                        val json = gson.toJson(loginData)
                        tinydb.putString("WeatherData", json)*/

                        setData(loginData?.list!![0])
                    }
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    try {
                        Toast.makeText(this@MainActivity, ""+jObjError.getJSONArray("details").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                    }catch (e:Exception){
                        try {
                            Toast.makeText(this@MainActivity, ""+jObjError.getString("message"), Toast.LENGTH_SHORT).show()
                        }catch (e:Exception){
                            Toast.makeText(this@MainActivity, "Something went wrong in user", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.e("TAG", "main onFailure: "+t.message.toString()+"\n"+call)
            }
        })

    }

    private fun setData(loginData: List) {

        binding.txtCityName.text = loginData.name
        binding.txtCel.text = loginData.main?.temp.toString() +" C"
        binding.txtIntensity.text = loginData.weather[0].description.toString()
        binding.txtWind.text = "Wind: " +loginData.wind?.speed.toString()
        binding.txtPressure.text ="Pressure: " + loginData.main?.pressure.toString()
        binding.txtHumidity.text = "Humidity: " +loginData.main?.humidity.toString()
        binding.txtSunrise.text = "Sunrise: " +loginData.main?.tempMax.toString()
        binding.txtSunset.text = "Sunset: " +loginData.main?.tempMin.toString()
        binding.txtUvIndex.text = ""
    }
}