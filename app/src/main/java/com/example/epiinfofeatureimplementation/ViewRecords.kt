package com.example.epiinfofeatureimplementation

import android.os.Bundle

import android.view.Surface

import android.content.Intent
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epiinfofeatureimplementation.ui.theme.EpiInfoFeatureImplementationTheme
import androidx.compose.foundation.lazy.items



class ViewRecords : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_records)

        setupHomeButton(this)

    }
}

@Composable
fun RecordListItem(name : String) {
    Surface(color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp,  horizontal = 8.dp)) {
        Column(modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()) {
            Row {
                Column {
                    Text(text = "Course")
                    Text(text = name, style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ))
                }
            }
        }
    }
}

@Composable
fun RecyclerView(names : List<String> = List(10){"$it"}) {
    LazyColumn {
        items(names) {
            currentName ->
                Text(text = currentName)
        }

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
        RecordListItem(name = "Keshav")
    }
}