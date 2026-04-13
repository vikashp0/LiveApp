package com.example.liveapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveapp.adapter.MedicineAdapter
import com.example.liveapp.databinding.ActivityMedicineListBinding
import com.example.liveapp.model.Medicine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicineListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicineListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val medicineList = mutableListOf<Medicine>()
    private lateinit var adapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        fetchMedicines()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.fabAddMedicine.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = MedicineAdapter(medicineList)
        binding.rvMedicines.layoutManager = LinearLayoutManager(this)
        binding.rvMedicines.adapter = adapter
    }

    private fun fetchMedicines() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("medicines")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                medicineList.clear()
                value?.forEach { document ->
                    val medicine = document.toObject(Medicine::class.java)
                    medicineList.add(medicine)
                }
                adapter.notifyDataSetChanged()
            }
    }
}