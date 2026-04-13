package com.example.liveapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveapp.adapter.HealthTipAdapter
import com.example.liveapp.adapter.MedicineProductAdapter
import com.example.liveapp.databinding.ActivityMainBinding
import com.example.liveapp.model.HealthTip
import com.example.liveapp.model.MedicineProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val healthTips = mutableListOf<HealthTip>()
    private val localProducts = mutableListOf<MedicineProduct>()

    private lateinit var tipAdapter: HealthTipAdapter
    private lateinit var productAdapter: MedicineProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupUI()
        fetchUserData(currentUser.uid)
        fetchHealthTips()
        loadLocalProducts()

        setupClickListeners()
    }

    private fun setupUI() {
        tipAdapter = HealthTipAdapter(healthTips)
        binding.rvHealthTips.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tipAdapter
        }

        productAdapter = MedicineProductAdapter(localProducts)
        binding.rvPopularProducts.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = productAdapter
        }
    }

    private fun loadLocalProducts() {
        localProducts.clear()
        val products = listOf(
            MedicineProduct("1", "Biochendryl", "Medicine", 50.0, 60.0, "10% OFF", "biochemdryl", "General", 10),
            MedicineProduct("2", "Bumtum", "Baby product", 120.0, 150.0, "20% OFF", "bumtum", "Baby", 20),
            MedicineProduct("3", "Ceftriaaxone", "Injection", 200.0, 250.0, "20% OFF", "ceftriaaxone", "Antibiotic", 5),
            MedicineProduct("4", "Colgate", "Toothpaste", 80.0, 100.0, "20% OFF", "colgate", "Dental", 30),
            MedicineProduct("5", "Dynagliptim", "Diabetes", 150.0, 180.0, "15% OFF", "dynagliptlm", "Diabetes", 15),
            MedicineProduct("6", "Injection", "Medical", 90.0, 120.0, "25% OFF", "injection", "Medical", 10),
            MedicineProduct("7", "Moov", "Pain relief", 70.0, 90.0, "20% OFF", "moov", "Pain Relief", 25),
            MedicineProduct("8", "Pampers", "Baby diaper", 300.0, 350.0, "15% OFF", "pampers", "Baby", 40),
            MedicineProduct("9", "Relax", "Pain relief", 60.0, 80.0, "25% OFF", "relax", "Pain Relief", 20),
            MedicineProduct("10", "Sensodyne", "Toothpaste", 120.0, 150.0, "20% OFF", "synsodyne", "Dental", 30)
        )
        localProducts.addAll(products)
        productAdapter.notifyDataSetChanged()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.ivProfileTop.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.ivCartTop.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Handle History Icon Click (only)
        binding.ivOrderHistory.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        binding.tvSeeAllCategories.setOnClickListener {
            startActivity(Intent(this, MedicineShopActivity::class.java))
        }

        binding.catTablets.setOnClickListener {
            startActivity(Intent(this, MedicineShopActivity::class.java))
        }

        binding.catSyrups.setOnClickListener {
            startActivity(Intent(this, MedicineShopActivity::class.java))
        }

        binding.catDoctors.setOnClickListener {
            startActivity(Intent(this, DoctorListActivity::class.java))
        }

        binding.catReminders.setOnClickListener {
            startActivity(Intent(this, MedicineListActivity::class.java))
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "User"
                    binding.tvWelcome.text = "Welcome, $name"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "User data load failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchHealthTips() {
        db.collection("health_tips").get()
            .addOnSuccessListener { result ->
                healthTips.clear()
                for (document in result) {
                    try {
                        val tip = document.toObject(HealthTip::class.java)
                        healthTips.add(tip)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                tipAdapter.notifyDataSetChanged()
            }
    }
}