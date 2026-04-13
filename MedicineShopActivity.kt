package com.example.liveapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.liveapp.adapter.MedicineProductAdapter
import com.example.liveapp.databinding.ActivityMedicineShopBinding
import com.example.liveapp.model.MedicineProduct
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MedicineShopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicineShopBinding
    private lateinit var db: FirebaseFirestore
    private val allProducts = mutableListOf<MedicineProduct>()
    private val filteredProducts = mutableListOf<MedicineProduct>()
    private lateinit var adapter: MedicineProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchMedicines()
        setupSearch()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = MedicineProductAdapter(filteredProducts)
        binding.rvMedicineShop.layoutManager = GridLayoutManager(this, 2)
        binding.rvMedicineShop.adapter = adapter
    }

    private fun fetchMedicines() {
        allProducts.clear()

        val localProducts = listOf(
            MedicineProduct("1", "Biochendryl", "Cough Syrup", 50.0, 60.0, "10% OFF", "biochemdryl", "Syrup", 10),
            MedicineProduct("2", "Bumtum", "Baby Diapers", 120.0, 150.0, "20% OFF", "bumtum", "Baby Care", 20),
            MedicineProduct("3", "Ceftriaaxone", "Antibiotic Injection", 200.0, 250.0, "20% OFF", "ceftriaaxone", "Injection", 5),
            MedicineProduct("4", "Colgate", "Toothpaste", 80.0, 100.0, "20% OFF", "colgate", "Dental", 30),
            MedicineProduct("5", "Dynagliptim", "Diabetes Control", 150.0, 180.0, "15% OFF", "dynagliptlm", "Diabetes", 15),
            MedicineProduct("6", "Injection", "Medical Kit", 90.0, 120.0, "25% OFF", "injection", "Medical", 10),
            MedicineProduct("7", "Moov", "Pain Relief Balm", 70.0, 90.0, "20% OFF", "moov", "Pain Relief", 25),
            MedicineProduct("8", "Pampers", "Baby Pants", 300.0, 350.0, "15% OFF", "pampers", "Baby Care", 40),
            MedicineProduct("9", "Relax Gel", "Muscle Relief", 60.0, 80.0, "25% OFF", "relax", "Pain Relief", 20),
            MedicineProduct("10", "Sensodyne", "Sensitive Teeth", 120.0, 150.0, "20% OFF", "synsodyne", "Dental", 30),
            MedicineProduct("11", "Sanitizer", "Hand Sanitizer", 45.0, 50.0, "10% OFF", "sanitizer", "Hygiene", 50),
            MedicineProduct("12", "Alkof DX", "Cough Syrup", 110.0, 130.0, "15% OFF", "alkofdx", "Syrup", 15),
            MedicineProduct("13", "Hand Gloves", "Disposable Gloves", 199.0, 250.0, "20% OFF", "handgloves", "Hygiene", 100),
            MedicineProduct("14", "Zental", "Deworming Tablet", 35.0, 45.0, "22% OFF", "zental", "Medicine", 25),
            MedicineProduct("15", "Atorvastatin", "Cholesterol Control", 145.0, 180.0, "19% OFF", "atorvastatin", "Heart", 20)
        )

        allProducts.addAll(localProducts)
        filterProducts("")
    }

    private fun setupSearch() {
        binding.etSearchShop.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        filteredProducts.clear()
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts)
        } else {
            val lowerQuery = query.lowercase(Locale.getDefault())
            for (product in allProducts) {
                if (product.name.lowercase(Locale.getDefault()).contains(lowerQuery) ||
                    product.category.lowercase(Locale.getDefault()).contains(lowerQuery)) {
                    filteredProducts.add(product)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}