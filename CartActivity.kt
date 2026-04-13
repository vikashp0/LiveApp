package com.example.liveapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveapp.adapter.CartAdapter
import com.example.liveapp.databinding.ActivityCartBinding
import com.example.liveapp.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        fetchCartItems()

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("TOTAL_AMOUNT", calculateTotal())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(cartItems, { item ->
            updateCartItem(item)
        }, { item ->
            removeCartItem(item)
        })
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
    }

    private fun fetchCartItems() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                cartItems.clear()
                value?.forEach { doc ->
                    val item = doc.toObject(CartItem::class.java)
                    cartItems.add(item)
                }
                adapter.notifyDataSetChanged()
                updateTotalUI()
            }
    }

    private fun updateCartItem(item: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart")
            .document(item.productId).set(item)
    }

    private fun removeCartItem(item: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart")
            .document(item.productId).delete()
    }

    private fun calculateTotal(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    private fun updateTotalUI() {
        binding.tvTotalAmount.text = "₹ ${calculateTotal()}"
    }
}