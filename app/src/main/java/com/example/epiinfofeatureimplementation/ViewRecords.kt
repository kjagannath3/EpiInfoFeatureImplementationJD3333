package com.example.epiinfofeatureimplementation

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.epiinfofeatureimplementation.ui.theme.EpiInfoFeatureImplementationTheme


class ViewRecords : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_records)

        setupHomeButton(this)
    }
}

private fun setupHomeButton(activity: ComponentActivity) {
    val homeButton = activity.findViewById<Button>(R.id.button_home)
    homeButton.setOnClickListener {
        // Navigate to MainActivity (activity_main.xml)
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
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