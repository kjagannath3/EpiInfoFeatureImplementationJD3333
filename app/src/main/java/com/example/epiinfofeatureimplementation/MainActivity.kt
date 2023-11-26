package com.example.epiinfofeatureimplementation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epiinfofeatureimplementation.ui.theme.EpiInfoFeatureImplementationTheme
import com.google.android.engage.common.datamodel.Image

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonStatCalc = findViewById<Button>(R.id.button_statcalc)
        val buttonCollectData = findViewById<Button>(R.id.button_collect_data)
        val buttonAnalyseData = findViewById<Button>(R.id.button_analyse_data)
        val imageViewLogo = findViewById<ImageView>(R.id.imageViewLogo)


        // Set up the click listeners for each button
        buttonStatCalc.setOnClickListener {
            // Handle StatCalc button click
        }

        imageViewLogo.setImageResource(R.drawable.epiinfo)

        buttonCollectData.setOnClickListener {
            val intent = Intent(this, CollectData::class.java)
            startActivity(intent)
        }

        buttonAnalyseData.setOnClickListener {
            val records = Intent(this, ViewRecords::class.java)
            startActivity(records)

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Icon(imageVector = Icons.Default.Add,
        contentDescription = null,
        modifier
            .background(Color.Cyan)
        )

    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(400.dp)
    ){
        Text(
            text = "Hello, my name is Keshav, this is  $name!",

            color = Color.Black,
            modifier = modifier
                .align(Alignment.BottomEnd)
        )
        Text(
            text = "Some other text",
            color = Color.Blue,
            fontSize = 30.sp,
            modifier = modifier
                .align(Alignment.TopCenter)

        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EpiInfoFeatureImplementationTheme {
        Greeting("Android")
    }
}