package com.example.screenshotorganizer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val CATEGORIES = listOf(
    "All Screenshots", "Shopping", "Payment", "Study", "Important", "Others"
)

@Composable
fun HomeScreen(
    vm: MainViewModel,
    onOpenCategory: (String) -> Unit,
    onSearch: () -> Unit,
    onReview: () -> Unit
) {
    val all by vm.all.collectAsState()
    val review by vm.toReview.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Screenshot Organizer", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Screenshots", style = MaterialTheme.typography.titleMedium)
                Text("${all.size}", fontSize = 36.sp, style = MaterialTheme.typography.displaySmall)
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Search")
        }

        if (review.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth().clickable { onReview() }) {
                Column(Modifier.padding(16.dp)) {
                    Text("इन screenshots को review करें", style = MaterialTheme.typography.titleMedium)
                    Text("${review.size} duplicate / पुराने screenshots मिले")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CATEGORIES) { cat ->
                val count = if (cat == "All Screenshots") all.size
                else all.count { it.category == cat }
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenCategory(cat) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cat, fontSize = 18.sp)
                        Text("$count", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
