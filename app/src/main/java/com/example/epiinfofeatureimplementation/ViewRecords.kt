package com.example.epiinfofeatureimplementation

import RecordViewAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.RecyclerView
import com.example.epiinfofeatureimplementation.ui.theme.EpiInfoFeatureImplementationTheme

class ViewRecords : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_records)
        val recyclerView = findViewById<RecyclerView>(R.id.record_list)
        val myDataset = arrayOf("Record 1", "Record 2", "Record 3")
        val myAdapter = RecordViewAdapter(myDataset)
        recyclerView.adapter = myAdapter
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    EpiInfoFeatureImplementationTheme {
        Greeting3("Android")
    }
}