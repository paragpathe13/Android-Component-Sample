package com.example.myviewmodelsample.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myviewmodelsample.databinding.WorkerListItemsBinding

class WorkerListAdapter (private val categories: List<String>) : RecyclerView.Adapter<WorkerListAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: WorkerListItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.tvCategoryName.text = category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = WorkerListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
