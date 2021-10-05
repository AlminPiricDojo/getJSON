package com.example.getjson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    private lateinit var prices: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prices = arrayListOf()

        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(prices)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        requestAPI()
    }

    private fun requestAPI(){
        // we use Coroutines to fetch the data, then update the Recycler View if the data is valid
        CoroutineScope(IO).launch {
            // we fetch the prices
            val data = async { fetchPrices() }.await()
            // once the data comes back, we populate our Recycler View
            if(data.isNotEmpty()){
                populateRV(data)
            }else{
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

    private fun fetchPrices(): String{
        // we will use URL.readText() to get our data (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.net.-u-r-l/read-text.html)
        // we make a call to the following API: https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json
        var response = ""
        try{
            response = URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json").readText()
        }catch(e: Exception){
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string
        return response
    }

    private suspend fun populateRV(result: String){
        withContext(Main){
            // we create a JSON object from the data
            val jsonObj = JSONObject(result)

            // to go deeper, we can use the getString method (here we get the value of USD)
            val usd = jsonObj.getJSONObject("eur").getString("usd")
            println("USD VALUE: $usd")
            val aud = jsonObj.getJSONObject("eur").getString("aud")
            println("AUD VALUE: $aud")

            // if our JSON data contains JSON arrays, we use getJSONArray (no need in this case)

            // we can now add these to our Recycler View
            prices.add(usd)
            prices.add(aud)

            rvAdapter.notifyDataSetChanged()
        }
    }

}