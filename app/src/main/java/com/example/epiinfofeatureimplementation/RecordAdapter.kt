package com.example.epiinfofeatureimplementation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val mDataset: List<RecordModel>) : RecyclerView.Adapter<RecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_view_records, parent, false)
        return RecordViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = mDataset[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
    }

    override fun getItemCount() = mDataset.size
}
