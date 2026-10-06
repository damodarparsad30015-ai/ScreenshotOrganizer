package com.example.screenshotorganizer.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.screenshotorganizer.data.ScreenshotEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    vm: MainViewModel,
    title: String,
    showSearch: Boolean,
    reviewMode: Boolean
) {
    val filtered by vm.filtered.collectAsState()
    val review by vm.toReview.collectAsState()
    val query by vm.query.collectAsState()
    val items = if (reviewMode) review else filtered

    var selected by remember { mutableStateOf<ScreenshotEntity?>(null) }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Text(title, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        if (showSearch) {
            OutlinedTextField(
                value = query,
                onValueChange = { vm.query.value = it },
                label = { Text("Search (जैसे 500, Amazon, Payment)") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }
        Text("${items.size} screenshots")
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items, key = { it.id }) { item ->
                AsyncImage(
                    model = item.uri,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(0.6f)
                        .clickable { selected = item }
                )
            }
        }
    }

    selected?.let { item ->
        ViewerDialog(vm = vm, item = item, onClose = { selected = null })
    }
}

@Composable
fun ViewerDialog(vm: MainViewModel, item: ScreenshotEntity, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirmDelete by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(item.uri))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(send, "Share"))
                }) { Text("Share") }
                Button(onClick = { confirmDelete = true }) { Text("Delete") }
            }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Close") } },
        text = {
            Column {
                AsyncImage(
                    model = item.uri,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxWidth().height(420.dp)
                )
                Text("Category: ${item.category}")
            }
        }
    )

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete?") },
            text = { Text("क्या आप सच में इस screenshot को delete करना चाहते हैं?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        vm.deleteFromPhone(item)
                        confirmDelete = false
                        onClose()
                    }
                }) { Text("हाँ, Delete करें") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("नहीं") }
            }
        )
    }
}
