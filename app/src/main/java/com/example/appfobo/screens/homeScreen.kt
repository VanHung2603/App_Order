package com.example.appfobo.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.appfobo.model.Product
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

@Composable
fun homeScreen(navController: NavController) {
    val products = remember { mutableStateOf<List<Product>>(emptyList()) }

    // Lấy dữ liệu từ Firebase
    LaunchedEffect(true) {
        val database = FirebaseDatabase.getInstance().getReference("products")
        database.get().addOnSuccessListener { dataSnapshot ->
            val productList = mutableListOf<Product>()
            for (productSnapshot in dataSnapshot.children) {
                val product = productSnapshot.getValue(Product::class.java)
                product?.let { productList.add(it) }
            }
            products.value = productList
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ProductList(products.value, navController)
    }
}


@Composable
fun ProductList(products: List<Product>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(products) { product ->
            ProductItem(product, navController)
        }
    }
}


@Composable
fun ProductItem(product: Product, navController: NavController) {
    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        elevation = 4.dp) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(painter = rememberImagePainter(product.imageUrl), contentDescription = product.name, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = product.name, style = MaterialTheme.typography.h6)
                Text(text = "${product.price} VND", style = MaterialTheme.typography.body1, color = Color.Gray)
                Text(text = product.description, style = MaterialTheme.typography.body2)
                Button(onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId == null) {
                        Toast.makeText(context, "Vui lòng đăng nhập để mua", Toast.LENGTH_SHORT).show()
                    } else {
                        addToCart(product)
                        Toast.makeText(context, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.fillMaxWidth()){
                    Text("Mua")
                }
            }
        }
    }
}
fun addToCart(product: Product) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId).push()
    cartRef.setValue(product)
}

