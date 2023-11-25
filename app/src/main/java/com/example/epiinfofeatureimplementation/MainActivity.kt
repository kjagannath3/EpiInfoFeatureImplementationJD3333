package com.example.epiinfofeatureimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.epiinfofeatureimplementation.ui.theme.EpiInfoFeatureImplementationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpiInfoFeatureImplementationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello, my name is Keshav, this is  $name!",

        color = Color.Black,
        modifier = modifier
            .background(Color.Cyan)
            .padding(16.dp)
            .background(Color.Green)

    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EpiInfoFeatureImplementationTheme {
        Greeting("Android")
    }
}