package com.example.appfobo.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.appfobo.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun noTificationScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var totalAmount by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val cartRef =
        remember { FirebaseDatabase.getInstance().getReference("cart").child(userId ?: "") }

    val cartItems = remember { mutableStateListOf<Product>() }
    val context = LocalContext.current
    var billData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var billId by remember { mutableStateOf<String?>(null) }


    // Lắng nghe sự thay đổi trong giỏ hàng
    LaunchedEffect(userId) {
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    val cartItemId = child.key
                    product?.let {
                        cartItems.add(it.copy(cartItemId = cartItemId))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Your Orders",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                backgroundColor = Color.White
            )
            if (userId == null || cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No orders yet", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your order history will appear here",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)
                    ) {
                    items(cartItems) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp), elevation = 4.dp
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Image(
                                    painter = rememberImagePainter(product.imageUrl),
                                    contentDescription = product.name,
                                    modifier = Modifier.size(100.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = product.name, style = MaterialTheme.typography.h6)
                                    Text(
                                        text = "${product.price} VND",
                                        style = MaterialTheme.typography.body1,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = product.description,
                                        style = MaterialTheme.typography.body2
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                product.cartItemId?.let { cartItemId ->
                                                    if (product.quantity > 1) {
                                                        val index =
                                                            cartItems.indexOfFirst { it.cartItemId == cartItemId }
                                                        if (index != -1) {
                                                            val updatedProduct =
                                                                product.copy(quantity = product.quantity - 1)
                                                            cartItems[index] = updatedProduct
                                                            updateCartQuantity(
                                                                cartItemId,
                                                                updatedProduct.quantity
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Text("-")
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "${product.quantity}",
                                            style = MaterialTheme.typography.body1
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Button(
                                            onClick = {
                                                product.cartItemId?.let { cartItemId ->
                                                    val index =
                                                        cartItems.indexOfFirst { it.cartItemId == cartItemId }
                                                    if (index != -1) {
                                                        val updatedProduct =
                                                            product.copy(quantity = product.quantity + 1)
                                                        cartItems[index] = updatedProduct
                                                        updateCartQuantity(
                                                            cartItemId,
                                                            updatedProduct.quantity
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Text("+")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Giỏ hàng đang trống, vui lòng đặt hàng", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val totalPrice = cartItems.sumOf { it.price * it.quantity }.toLong()
                totalAmount = totalPrice.toInt()
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                val billsRef = FirebaseDatabase.getInstance().getReference("bills").child(userId)
                billId = billsRef.push().key

                billData = mapOf(
                    "items" to cartItems.map { it.copy(cartItemId = null) },
                    "totalPrice" to totalPrice,
                    "timestamp" to ServerValue.TIMESTAMP
                )

                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Thanh toán")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Xác nhận thanh toán") },
                text = { Text("Tổng số tiền: $totalAmount VND") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@TextButton
                        val billsRef = FirebaseDatabase.getInstance().getReference("bills").child(userId)
                        val finalBillId = billId ?: return@TextButton
                        val finalBillData = billData ?: return@TextButton

                        // Thêm bill vào Firebase khi người dùng xác nhận
                        billsRef.child(finalBillId).setValue(finalBillData)

                        cartRef.removeValue().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                                cartItems.clear()
                            }
                        }
                    }) {
                        Text("Xác nhận")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }

}
fun updateCartQuantity(cartItemId: String, quantity: Int) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId)
    val updates = mapOf<String, Any>(
        "quantity" to quantity
    )
    cartRef.child(cartItemId).updateChildren(updates).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            println("Số lượng đã được cập nhật.")
        } else {
            println("Lỗi khi cập nhật số lượng: ${task.exception?.message}")
        }
    }
}











