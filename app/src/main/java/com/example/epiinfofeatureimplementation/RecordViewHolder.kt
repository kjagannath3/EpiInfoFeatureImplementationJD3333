package com.example.epiinfofeatureimplementation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
    val descriptionTextView: TextView = itemView.findViewById(R.id.description_text_view)
}