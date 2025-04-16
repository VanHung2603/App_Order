package com.example.appfobo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

sealed class bottomNavItem (val router: String, val icon: ImageVector, val label: String) {
   object Home : bottomNavItem("home", Icons.Filled.Home, "Home")
   object Search : bottomNavItem("search", Icons.Filled.Notifications, "Infor")
   object Notification : bottomNavItem("notification", Icons.Filled.ShoppingCart, "List Order")
   object Profile : bottomNavItem("profile", Icons.Filled.Person, "Profile")
}