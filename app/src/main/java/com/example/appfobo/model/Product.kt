package com.example.appfobo.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    var quantity: Int =1,
    val cartItemId: String? = null
)
val availableProducts = listOf(
    Product(
        name = "Mì Quảng",
        price = 10.0,
        description = "Mì quảng ngon",
        imageUrl = "https://images.pexels.com/photos/699953/pexels-photo-699953.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    Product(
        name = "Phở",
        price = 5.0,
        description = "Phở Việt Nam",
        imageUrl = "https://images.pexels.com/photos/2133989/pexels-photo-2133989.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    Product(
        name = "Ram cuốn",
        price = 12.0,
        description = "Ram cuốn",
        imageUrl = "https://images.pexels.com/photos/840216/pexels-photo-840216.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    Product(
        name = "Nước ép dưa hấu",
        price = 12.0,
        description = "Dưa hấu",
        imageUrl = "https://images.pexels.com/photos/1337825/pexels-photo-1337825.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    Product(
        name = "Nước ép táo",
        price = 12.0,
        description = "Táo",
        imageUrl = "https://images.pexels.com/photos/4551975/pexels-photo-4551975.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    )

)
@Composable
public fun AddProductScreen(navController: NavController) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm sản phẩm", fontSize = 16.sp)
        }
        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Huỷ", fontSize = 16.sp)
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Chọn sản phẩm cần thêm") },
                text = {
                    Column {
                        availableProducts.forEach { product ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProduct = product }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = product == selectedProduct,
                                    onClick = { selectedProduct = product }
                                )
                                Text(product.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedProduct?.let { addProductToFirebase(it) }
                            showDialog = false
                        },
                        enabled = selectedProduct != null
                    ) {
                        Text("Thêm")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { navController.navigateUp() }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

fun addProductToFirebase(product: Product) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId)
    val cartItemId = cartRef.push().key ?: return
    val newProduct = product.copy(quantity = 1, cartItemId = cartItemId)

    // Lưu sản phẩm vào Firebase dưới cartItemId duy nhất
    cartRef.child(cartItemId).setValue(newProduct).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            println("Sản phẩm đã được thêm vào giỏ hàng.")
        } else {
            println("Lỗi khi thêm sản phẩm vào giỏ hàng: ${task.exception?.message}")
        }
    }
}






