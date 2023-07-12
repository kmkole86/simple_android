package com.kmkole86.simple.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kmkole86.simple.main.navigation.HomeNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    Scaffold(
        modifier = modifier,
        content = { HomeNavHost(modifier = modifier.padding(it), navController = navController) }
    )
}