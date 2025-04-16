package com.example.appfobo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appfobo.model.Bill
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun BillScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val billsRef = FirebaseDatabase.getInstance().getReference("bills").child(userId)

    val billList = remember { mutableStateListOf<Bill>() }

    // Lắng nghe sự thay đổi trong danh sách hóa đơn
    LaunchedEffect(userId) {
        billsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                billList.clear()
                for (child in snapshot.children) {
                    val bill = child.getValue(Bill::class.java)
                    bill?.let { billList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(billList) { bill ->
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Hiển thị tổng tiền của hóa đơn
                        Text("Tổng tiền: ${bill.totalPrice} VND", fontSize = 18.sp)
                        Text("Số sản phẩm: ${bill.items.sumOf { it.quantity }}", fontSize = 16.sp)
                        bill.items.forEach { item ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Sản phẩm: ${item.name}")
                            Text("Giá: ${item.price} VND")
                            Text("Số lượng: ${item.quantity}")
                        }
                    }
                }
            }
        }
        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Huỷ", fontSize = 16.sp)
        }
    }
}
