package com.example.epiinfofeatureimplementation

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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

class CollectData : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_data)
        val formDropdown = findViewById<Spinner>(R.id.spinner);
        val chooseFormButton = findViewById<Button>(R.id.button_choose_form);
        ArrayAdapter.createFromResource(
            this,
            R.array.form_dropdown,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            formDropdown.adapter = adapter
        }

        var dropDownVisible = false
        chooseFormButton.setOnClickListener {
            dropDownVisible = !dropDownVisible
            if (dropDownVisible) {
                formDropdown.visibility = View.VISIBLE
            } else {
                formDropdown.visibility = View.GONE
            }
        }

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
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    EpiInfoFeatureImplementationTheme {
        Greeting2("Android")
    }
}