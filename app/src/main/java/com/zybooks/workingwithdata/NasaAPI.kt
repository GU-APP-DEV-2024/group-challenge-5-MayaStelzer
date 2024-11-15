package com.zybooks.workingwithdata

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

const val TAG = "NASA_API"

class NasaAPI : AppCompatActivity() {
    lateinit var startDateTextView: TextView
    lateinit var startDateEditText: EditText
    lateinit var endDateTextView: TextView
    lateinit var endDateEditText: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var imageDataSet: ArrayList<ImageData>
    lateinit var imageCustomAdapter: ImageCustomAdapter
    lateinit var countEditText: EditText

    data class ImageData(val url: String, val description: String = "", val date: String = "") {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nasa_api)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.title = "Working with NASA"


        startDateTextView = findViewById(R.id.dateTextView)
        startDateEditText = findViewById(R.id.dateEditText)
        startDateEditText.doAfterTextChanged {
            countEditText.isEnabled = startDateEditText.text.isEmpty()
        }

        endDateTextView = findViewById(R.id.endDateTextView)
        endDateEditText = findViewById(R.id.endDateEditText)
        endDateEditText.doAfterTextChanged {
            countEditText.isEnabled = endDateEditText.text.isEmpty()
        }

        countEditText = findViewById(R.id.countEditText)
        countEditText.doAfterTextChanged {
            startDateEditText.isEnabled = countEditText.text.isEmpty()
            endDateEditText.isEnabled = countEditText.text.isEmpty()
        }

        val rangeCheckBox: CheckBox = findViewById(R.id.rangeCheckBox)
        rangeCheckBox.setOnClickListener {
            if (rangeCheckBox.isChecked ) {
                endDateTextView.visibility = View.VISIBLE
                endDateEditText.visibility = View.VISIBLE
                startDateTextView.text = getString(R.string.start)
            } else {
                endDateTextView.visibility = View.INVISIBLE
                endDateEditText.visibility = View.INVISIBLE
                startDateTextView.text = getString(R.string.date)

            }
        }

        val searchButton:Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            searchAPOD()
        }

        val clearButton: Button = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            clearEditTextFields()
        }

        imageDataSet = arrayListOf(ImageData("https://apod.nasa.gov/apod/image/1908/EtnaCloudsMoon_Giannobile_960.jpg", "What's happening above that volcano? Although Mount Etna is seen erupting, the clouds are not related to the eruption. They are lenticular clouds formed when moist air is forced upwards near a mountain or volcano.  The surreal scene was captured by chance late last month when the astrophotographer went to Mount Etna, a UNESCO World Heritage Site in Sicily, Italy, to photograph the conjunction between the Moon and the star Aldebaran. The Moon appears in a bright crescent phase, illuminating an edge of the lower lenticular cloud.  Red hot lava flows on the right.  Besides some breathtaking stills, a companion time-lapse video of the scene shows the lenticular clouds forming and wavering as stars trail far in the distance.    Follow APOD in English on: Instagram, Facebook,  Reddit, or Twitter"),
            ImageData("https://epic.gsfc.nasa.gov/archive/natural/2024/11/12/jpg/epic_1b_20241112003634.jpg"))

        imageCustomAdapter = ImageCustomAdapter(imageDataSet)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = imageCustomAdapter
    }

    // Create and make request
    // Create a new JsonObjectRequest that requests available subjects
    private fun searchAPOD() {
//
    }

    private fun processRequest(response: JSONArray) {
        Log.d(TAG, response.toString())
        for (index in 0 .. response.length() - 1) {
            var jsonObject = response.getJSONObject(index)
            var url = jsonObject.getString("url")
            var explanation = jsonObject.getString("explanation")
            imageDataSet.add(ImageData(url, explanation))
        }
        imageCustomAdapter.notifyDataSetChanged()
    }


    private fun clearEditTextFields() {
        countEditText.text.clear()
        startDateEditText.text.clear()
        endDateEditText.text.clear()
    }
}