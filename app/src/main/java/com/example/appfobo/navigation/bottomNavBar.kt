package com.example.appfobo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
@Composable
fun bottomNavBar (navController: NavController){
    val items = listOf(
        bottomNavItem.Home,
        bottomNavItem.Search,
        bottomNavItem.Notification,
        bottomNavItem.Profile
    )
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currenRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if(currenRoute==item.router)Color.Blue else Color.Gray)},
                label = { Text(text = item.label,
                    color = if (currenRoute == item.router)Color.Blue else Color.Gray)},
                selected = currenRoute  == item.router,
                onClick = {
                    navController.navigate(item.router){
                        popUpTo(navController.graph.startDestinationId){ saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}