package com.example.cryptoapi.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cryptoapi.R
import com.example.cryptoapi.databinding.ItemBinding
import com.example.cryptoapi.retrofit.getresponse.ResponseListItem
import com.example.cryptoapi.utils.Constants.animationDuration
import com.example.cryptoapi.utils.roundToTwoDecimals
import com.example.cryptoapi.utils.toDoubleToFloat
import javax.inject.Inject

class CryptoAdapter @Inject constructor():RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    inner class CryptoViewHolder( val binding: ItemBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bindData(item: ResponseListItem) {
            binding.apply {

                tvName.text = item.id
                tvPrice.text = "Eur ${item.current_price.roundToTwoDecimals()}"
                tvSymbol.text = item.symbol?.uppercase()
                imgCrypto.load(item.image){
                    crossfade(true)
                    crossfade(500)
                    placeholder(R.drawable.round_currency_bitcoin_24)
                    error(R.drawable.round_currency_bitcoin_24)
                }

                //Chart
                lineChart.gradientFillColors = intArrayOf(
                    Color.parseColor("#2a9085"),
                    Color.TRANSPARENT
                )
                lineChart.animation.duration =animationDuration
                val listData = item.sparkline_in_7d.price.toDoubleToFloat()
                lineChart.animate(listData)

                root.setOnClickListener {
                    onClick?.let {
                        it(item)
                    }
                }

            }
        }

    }

    private var onClick : ((ResponseListItem) -> Unit)?= null
    fun setOnItemClickListener(listener :(ResponseListItem) -> Unit){
        onClick = listener
    }

    private val differCallback = object:DiffUtil.ItemCallback<ResponseListItem>(){
        override fun areItemsTheSame(
            oldItem: ResponseListItem,
            newItem: ResponseListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseListItem,
            newItem: ResponseListItem
        ): Boolean {
          return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
       return CryptoViewHolder(
           ItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       )
    }

    override fun getItemCount(): Int {
      return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.bindData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
}

