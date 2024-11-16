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
import com.zybooks.workingwithdata.NasaAPI.ImageData
import org.json.JSONArray

class EPIC : AppCompatActivity() {
    lateinit var startDateTextView: TextView
    lateinit var startDateEditText: EditText
    lateinit var endDateTextView: TextView
    lateinit var endDateEditText: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var imageDataSet: ArrayList<ImageData>
    lateinit var imageCustomAdapter: ImageCustomAdapter
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_epic)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.title = "Earth Polychromatic Imaging Camera (EPIC)"

        startDateTextView = findViewById(R.id.dateTextView)
        startDateEditText = findViewById(R.id.dateEditText)

        endDateTextView = findViewById(R.id.endDateTextView)
        endDateEditText = findViewById(R.id.endDateEditText)
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
        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            searchAPOD()
        }
        val clearButton: Button = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            clearEditTextFields()
        }
        imageDataSet = arrayListOf(
            ImageData("Earth from DSCOVR", "2024-11-15", "https://epic.gsfc.nasa.gov/archive/natural/2024/11/15/png/epic_1.png"),
            ImageData("Blue Marble", "2024-11-14", "https://epic.gsfc.nasa.gov/archive/natural/2024/11/14/png/epic_2.png"),
            ImageData("Cloud Patterns", "2024-11-13", "https://epic.gsfc.nasa.gov/archive/natural/2024/11/13/png/epic_3.png")
        )

        imageCustomAdapter = ImageCustomAdapter(imageDataSet)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = imageCustomAdapter
        requestQueue = Volley.newRequestQueue(this)
    }
    private fun searchAPOD() {
        val startDate = startDateEditText.text.toString()
        val endDate = if (endDateEditText.visibility == View.VISIBLE) endDateEditText.text.toString() else null

        val apiKey = BuildConfig.NASA_API_KEY

        val url = if (endDate != null) {
            "https://epic.gsfc.nasa.gov/api/natural/range/$startDate/$endDate?api_key=$apiKey"
        } else {
            "https://epic.gsfc.nasa.gov/api/natural/date/$startDate?api_key=$apiKey"
        }

        Log.d("EPIC_API_REQUEST", "Fetching data from: $url")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                processRequest(response)
            },
            { error ->
                Log.e("EPIC_API_ERROR", "Error: ${error.message}")
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
    private fun processRequest(response: JSONArray) {
        imageDataSet.clear()
        for (i in 0 until response.length()) {
            val item = response.getJSONObject(i)
            val date = item.getString("date")
            val caption = item.getString("caption")
            val imageUrl = "https://epic.gsfc.nasa.gov/archive/natural/${date.replace("-", "/")}/png/${item.getString("image")}.png"

            imageDataSet.add(ImageData(caption, date, imageUrl))
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun clearEditTextFields() {
        startDateEditText.text.clear()
        endDateEditText.text.clear()
    }
}