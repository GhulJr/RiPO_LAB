package com.example.colordetection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_color.view.*

class ColorsAdapter(colors: ArrayList<ColorModel> = ArrayList())
    : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    var colors: ArrayList<ColorModel> = colors
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false))

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(colors[position])

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(colorModel: ColorModel) {
            itemView.setBackgroundColor(colorModel.swatch.rgb)
            itemView.colorName.apply {
                setTextColor(colorModel.swatch.titleTextColor)
                text = "#${colorModel.swatch.rgb.toUInt().toString(16)}"
            }
        }
    }
}