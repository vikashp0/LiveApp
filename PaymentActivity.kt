package com.example.liveapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveapp.databinding.ActivityPaymentBinding
import com.example.liveapp.model.CartItem
import com.example.liveapp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var totalAmount: Double = 0.0
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        fetchCartItems()

        binding.btnPayNow.setOnClickListener {
            processPayment()
        }
    }

    private fun fetchCartItems() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart").get()
            .addOnSuccessListener { snapshot ->
                cartItems.clear()
                for (doc in snapshot) {
                    val item = doc.toObject(CartItem::class.java)
                    cartItems.add(item)
                }
            }
    }

    private fun processPayment() {
        val address = binding.etAddress.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentType = when (binding.rgPayment.checkedRadioButtonId) {
            R.id.rbUPI -> "UPI"
            R.id.rbCard -> "Card"
            else -> "Cash"
        }

        val userId = auth.currentUser?.uid ?: return
        val orderId = "ORD${System.currentTimeMillis()}"

        val order = Order(
            orderId = orderId,
            userId = userId,
            items = cartItems.toList(),
            totalAmount = totalAmount,
            paymentType = paymentType,
            address = address,
            status = "Placed",
            timestamp = System.currentTimeMillis()
        )

        // Show success screen immediately as per user request
        val intent = Intent(this, OrderSuccessActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        intent.putExtra("PAYMENT_TYPE", paymentType)
        startActivity(intent)

        // Save to Firestore in background
        db.collection("orders").document(orderId).set(order)
            .addOnSuccessListener {
                clearCart(userId)
            }
            .addOnFailureListener {
                // Background failure - order history might be missing this item
            }
            
        finish()
    }

    private fun clearCart(userId: String) {
        db.collection("users").document(userId).collection("cart").get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}