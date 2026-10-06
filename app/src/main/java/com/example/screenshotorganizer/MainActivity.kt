package com.example.screenshotorganizer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.screenshotorganizer.ui.GalleryScreen
import com.example.screenshotorganizer.ui.HomeScreen
import com.example.screenshotorganizer.ui.MainViewModel
import com.example.screenshotorganizer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PermissionGate(vm)
                }
            }
        }
    }
}

@Composable
fun PermissionGate(vm: MainViewModel) {
    val permission = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    var granted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }

    LaunchedEffect(Unit) { launcher.launch(permission) }
    LaunchedEffect(granted) { if (granted) vm.scan() }

    if (granted) {
        AppNav(vm)
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Screenshots देखने के लिए अनुमति ज़रूरी है")
            Button(onClick = { launcher.launch(permission) }) { Text("अनुमति दें") }
        }
    }
}

@Composable
fun AppNav(vm: MainViewModel) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                vm = vm,
                onOpenCategory = { cat ->
                    vm.category.value = cat
                    vm.query.value = ""
                    nav.navigate("gallery")
                },
                onSearch = {
                    vm.category.value = "All Screenshots"
                    nav.navigate("search")
                },
                onReview = { nav.navigate("review") }
            )
        }
        composable("gallery") {
            GalleryScreen(vm = vm, title = vm.category.value, showSearch = false, reviewMode = false)
        }
        composable("search") {
            GalleryScreen(vm = vm, title = "Search", showSearch = true, reviewMode = false)
        }
        composable("review") {
            GalleryScreen(vm = vm, title = "Review करें", showSearch = false, reviewMode = true)
        }
    }
}
