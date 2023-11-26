package com.example.epiinfofeatureimplementation

import android.os.Bundle

import android.view.Surface

import android.content.Intent
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp


class ViewRecords : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EpiInfoFeatureImplementationTheme {
                Column {
                    TitleTextView()
                    RecyclerView()
                }

                HomeButton()
            }
        }





    }
}


@Composable
fun HomeButton() {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(top = 13.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
        , horizontalArrangement = Arrangement.End
    ) {
        // Your home button
        Image(
            painter = painterResource(id = R.drawable.home_button),
            contentDescription = "Home",
            modifier = Modifier
                .size(width = 29.dp, height = 30.dp)
                .clickable {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
                .fillMaxWidth()


        )
    }
}



@Composable
fun RecordListItem(name : String) {
    Surface(color = Color.Gray,
        modifier = Modifier.padding(vertical = 4.dp,  horizontal = 8.dp)
            .background(color = Color(0XFF3566d0),
                shape = RoundedCornerShape(8.dp))) {
        Column(modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()) {
            Row {
                Column {
                    Text(text = "Record")
                    Text(text = name, style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ))
                }
            }
        }

    }
}

@Composable
fun TitleTextView() {
    Text(
        text = stringResource(R.string.title_activity_view_records),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth() // Corresponds to layout_width="match_parent"
            .background(
                color =  Color(0XFF3566d0), // Replace with your actual color or drawable as a Brush
                shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
            )
            .padding(16.dp) // Add padding if needed
    )
}

@Composable
fun RecyclerView(names : List<String> = List(10){"$it"}) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 600.dp)
        //.verticalScroll(rememberScrollState())
    ) {
        items(names) {
            currentName ->
                RecordListItem(name = currentName)
        }
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
        RecyclerView(List<String>(10){"$it"})
    }
}