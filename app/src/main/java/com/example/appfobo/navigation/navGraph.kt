package com.example.appfobo.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appfobo.screens.homeScreen
import com.example.appfobo.screens.noTificationScreen
import com.example.appfobo.screens.profileScreen
import com.example.appfobo.screens.searchScreen
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.appfobo.login.LoginUI
import com.example.appfobo.login.RegisterUI
import com.example.appfobo.model.AddProductScreen
import com.example.appfobo.screens.BillScreen
import com.example.appfobo.screens.addToCart

@Composable
fun navGraph(navController: NavHostController, paddingValues: PaddingValues){
    NavHost(navController = navController,
        startDestination = bottomNavItem.Home.router,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(bottomNavItem.Home.router){ homeScreen(navController = navController)}
        composable(bottomNavItem.Search.router){ searchScreen()}
        composable(bottomNavItem.Notification.router){ noTificationScreen()}
        composable(bottomNavItem.Profile.router) {
            profileScreen(navController = navController)
        }
        composable("login") {
            LoginUI(navController = navController,
                navToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterUI(
                navToLogin = { navController.popBackStack() }
            )
        }
        composable("addProduct") {
            AddProductScreen(navController = navController)
        }
        composable("notification") {
            noTificationScreen()
        }
        composable("billScreen") {
            BillScreen(navController = navController)
        }
    }
}

