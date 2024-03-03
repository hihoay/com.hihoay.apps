package app.module.lang

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.module.R
import app.module.databinding.ItemLanguageBinding

class AdapterLanguage(
    var context: Activity,
    private val onItemClick: (Language) -> Unit
) : RecyclerView.Adapter<AdapterLanguage.ViewHolder>() {

    private val languge = mutableListOf<Language>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        context,
        ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(languge[position])
    }

    override fun getItemCount() = languge.size

    fun setData(data: MutableList<Language>) {
        languge.clear()
        languge.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(
        var context: Activity,
        private val binding: ItemLanguageBinding,
        private val onClick: (Language) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Language) {
            binding.apply {
                txtFlag.setText(item.flag)
                txtFlag.visibility = View.GONE
//                txtLanguage.text = (item.local + " - " + item.country_name).trim()
                txtLanguage.text = (item.local).trim()
                if (item.flag == CompassObject.language.flag && item.local == CompassObject.language.local) {
                    imgSelect.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                } else imgSelect.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                itemV.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }
}
