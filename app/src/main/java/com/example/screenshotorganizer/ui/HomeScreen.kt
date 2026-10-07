package com.example.screenshotorganizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val CATEGORIES = listOf(
    "All Screenshots", "Shopping", "Payment", "Study", "Important", "Others"
)

private val BgDark = Color(0xFF070D1F)
private val CardDark = Color(0xFF0E1A3A)

private fun catColors(cat: String): List<Color> = when (cat) {
    "All Screenshots" -> listOf(Color(0xFF1E6BFF), Color(0xFF2A3FD6))
    "Shopping" -> listOf(Color(0xFFE91E78), Color(0xFFB0206E))
    "Payment" -> listOf(Color(0xFF1FA64A), Color(0xFF0E7A34))
    "Study" -> listOf(Color(0xFF7B3FE4), Color(0xFF5524B5))
    "Important" -> listOf(Color(0xFFF2A21A), Color(0xFFD97A0B))
    else -> listOf(Color(0xFF4A5775), Color(0xFF2F3A55))
}

private fun catIcon(cat: String): ImageVector = when (cat) {
    "All Screenshots" -> Icons.Filled.Image
    "Shopping" -> Icons.Filled.ShoppingBag
    "Payment" -> Icons.Filled.CreditCard
    "Study" -> Icons.Filled.School
    "Important" -> Icons.Filled.Star
    else -> Icons.Filled.Apps
}

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
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1E9BFF), Color(0xFF2A3FD6)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PhotoLibrary, null, tint = Color.White)
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text("Screenshot Saver", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Clean • Organize • Save Space", color = Color(0xFFB8C4E6), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1E5BFF), Color(0xFF3A2BD0))))
                .padding(20.dp)
        ) {
            Column {
                Text("Total Screenshots", color = Color.White, fontSize = 16.sp)
                Text("${all.size}", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Text("Your Memories Organized!", color = Color(0xFFCFD8FF), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(CardDark)
                .border(1.dp, Color(0xFF2A4A8A), RoundedCornerShape(27.dp))
                .clickable { onSearch() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Search, null, tint = Color.White)
            Text("Search screenshots...", color = Color(0xFFB8C4E6), modifier = Modifier.padding(start = 12.dp))
        }

        if (review.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF2A1E1E))
                    .border(1.dp, Color(0xFFF2A21A), RoundedCornerShape(18.dp))
                    .clickable { onReview() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, null, tint = Color(0xFFF2A21A), modifier = Modifier.size(32.dp))
                Column(Modifier.padding(start = 12.dp)) {
                    Text("इन screenshots को review करें", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${review.size} duplicate / पुराने screenshots मिले", color = Color(0xFFD6D6D6), fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("Categories", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        CATEGORIES.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { cat ->
                    val count = if (cat == "All Screenshots") all.size
                    else all.count { it.category == cat }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(catColors(cat)))
                            .clickable { onOpenCategory(cat) }
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                            Icon(catIcon(cat), null, tint = Color.White)
                            Column {
                                Text(cat, color = Color.White, fontSize = 14.sp)
                                Text("$count", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .clickable { onReview() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Delete, null, tint = Color(0xFF9FA8FF), modifier = Modifier.size(32.dp))
            Column(Modifier.padding(start = 12.dp)) {
                Text("Delete Unwanted Screenshots", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Free up storage and keep your gallery clean", color = Color(0xFFB8C4E6), fontSize = 12.sp)
            }
        }
    }
}
