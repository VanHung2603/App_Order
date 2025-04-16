package com.example.appfobo.screens

import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.appfobo.model.AddProductScreen
import com.example.appfobo.model.addProductToFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun profileScreen(navController: NavHostController) {
    var showAddProductScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser?.uid ?: "")
    var notificationsEnabled by rememberSaveable { mutableStateOf(false) }
    var userName by remember { mutableStateOf("...") }
    var userRole by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(firebaseUser) {
        firebaseUser?.uid?.let { uid ->
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val role = snapshot.child("role").getValue(String::class.java)
                    if (name != null) userName = name
                    if (role != null) userRole = role
                }
                override fun onCancelled(error: DatabaseError) {
                    userName = "Error"
                    userRole = "user"
                }
            })
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Hi! ${userName} ", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
            backgroundColor = Color.White
        )

        if (firebaseUser == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome to Food Order", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Sign in to place orders",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.navigate("login") }) {
                    Text("Sign In", fontSize = 16.sp)
                }
            }

            SectionTitle("Support")
            MenuItem("App Settings", Icons.Filled.Settings) {}
            MenuItem("Help Center", Icons.Filled.Help) {}
            MenuItem("About Us", Icons.Filled.Info) {}
        } else if(userRole == "admin"){
            SectionTitle("Admin Management")
            MenuItem("Thêm sản phẩm", Icons.Default.Shop){
                navController.navigate("addProduct")
            }

            MenuItem("Quản lý nhân viên", Icons.Default.Person) { }
            MenuItem("Quản lý người dùng", Icons.Default.Person) { }
            MenuItem("Xem số lượng đặt hàng", Icons.Default.Fastfood) { }
            MenuItem("Đơn hàng của bạn", Icons.Default.AddShoppingCart) {
                navController.navigate("billScreen")
            }
            TextButton(
                onClick = {
                    AlertDialog.Builder(context)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("profile") { inclusive = true }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.Red, fontSize = 16.sp)
                }
            }
        } else  {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    val email = firebaseUser.email
                    val firstLetter = email?.substringBefore("@")
                        ?.firstOrNull()
                        ?.toString()
                        ?.uppercase()
                    if (firstLetter != null) {
                        Text(firstLetter, color = Color.White, fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(userName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(firebaseUser.email ?: "", fontSize = 14.sp, color = Color.Gray)
                }

            }

            SectionTitle("Account")
            MenuItem("Thông tin cá nhân", Icons.Default.Person) { }
//            MenuItem("Saved Addresses", Icons.Default.LocationOn) { }
//            MenuItem("Payment Methods", Icons.Default.CreditCard) { }
            MenuItem("Đơn hàng của bạn", Icons.Default.AddShoppingCart) {
                navController.navigate("billScreen")
            }
//            SectionTitle("Preferences")
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colors.primary)
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Text("Notifications", fontSize = 16.sp)
//                }
//                Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
//            }

            TextButton(
                onClick = {
                    AlertDialog.Builder(context)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("profile") { inclusive = true }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.Red, fontSize = 16.sp)
                }
            }

        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
fun MenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colors.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 16.sp)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
