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
import org.json.JSONArray
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    private lateinit var cars: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cars = arrayListOf()

        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(cars)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        requestAPI()
    }

    private fun requestAPI(){
        // we use Coroutines to fetch the data, then update the Recycler View if the data is valid
        CoroutineScope(IO).launch {
            // we fetch the data
            val data = async { fetchData() }.await()
            // once the data comes back, we populate our Recycler View
            if(data.isNotEmpty()){
                populateRV(data)
            }else{
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

    private fun fetchData(): String{
        // we will use URL.readText() to get our data (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.net.-u-r-l/read-text.html)
        // we make a call to the following API: https://raw.githubusercontent.com/AlminPiricDojo/JSON_files/main/cars.json
        // then save the data in a String variable called response
        var response = ""
        try{
            response = URL("https://raw.githubusercontent.com/AlminPiricDojo/JSON_files/main/cars.json").readText()
        }catch(e: Exception){
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string and returned
        return response
    }

    private suspend fun populateRV(result: String){
        withContext(Main){
            // we create a JSON array from the data
            val jsonArray = JSONArray(result)

            // to go deeper, we can use the getJSONObject method with the desired index value
            // here we access the first car in the JSON Array, then use the getString method and pass in the key (make, model, etc.)
            val make = jsonArray.getJSONObject(0).getString("make")
            println("MAKE: $make")
            val model = jsonArray.getJSONObject(0).getString("model")
            println("MODEL: $model")
            val year = jsonArray.getJSONObject(0).getString("year")
            println("YEAR: $year")
            // getting the owners is a slightly different process because we are now getting a list of strings
            val ownersJson = jsonArray.getJSONObject(0).getJSONArray("owners")
            // here we iterate through the list of strings and print each owner
            for(i in 0 until ownersJson.length()){
                println("OWNER: ${ownersJson.getString(i)}")
            }

            // we can now add this car to our Recycler View (we are only going to display the car model)
            cars.add(model)

            rvAdapter.notifyDataSetChanged()
        }
    }

}