package com.voxmate.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voxmate.app.model.Message

class ConversationAdapter(private val items: MutableList<Message>) : RecyclerView.Adapter<ConversationAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = items[position]
        holder.tvMessage.text = msg.text
        // simple styling: align right for user messages
        val params = holder.tvMessage.layoutParams as ViewGroup.MarginLayoutParams
        if (msg.isUser) {
            holder.tvMessage.setBackgroundColor(0xFF0078D7.toInt())
            params.marginStart = 80
            params.marginEnd = 0
        } else {
            holder.tvMessage.setBackgroundColor(0xFF666666.toInt())
            params.marginStart = 0
            params.marginEnd = 80
        }
        holder.tvMessage.layoutParams = params
    }

    override fun getItemCount(): Int = items.size

    fun add(message: Message) {
        items.add(message)
        notifyItemInserted(items.size - 1)
    }

    fun setAll(list: List<Message>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
