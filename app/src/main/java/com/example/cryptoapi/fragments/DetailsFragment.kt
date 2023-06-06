package com.example.cryptoapi.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.cryptoapi.R
import com.example.cryptoapi.databinding.FragmentDetailsBinding
import com.example.cryptoapi.utils.Constants.animationDuration
import com.example.cryptoapi.utils.DataStatus
import com.example.cryptoapi.utils.isVisible
import com.example.cryptoapi.utils.roundToThreeDecimals
import com.example.cryptoapi.utils.toDoubleToFloat
import com.example.cryptoapi.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@AndroidEntryPoint
class DetailsFragment: Fragment(R.layout.fragment_details) {

    private lateinit var binding: FragmentDetailsBinding
    private val viewModel :MainViewModel by viewModels()
    private val args : DetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)
        viewModel.getCoinDetails(args.id)
        return binding.root
    }


  @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //put data into views
        lifecycleScope.launch {
            binding.apply {
                viewModel.coinDetails.observe(viewLifecycleOwner){
                    when(it.status){
                        DataStatus.Status.LOADING ->{
                            pBarLoading.isVisible(true,mainLayout)
                        }
                        DataStatus.Status.SUCCESS ->{
                            it.data?.let { data->

                                pBarLoading.isVisible(false,mainLayout)

                                //name
                                tvCoinNameSymbol.text= "${data.name} [${data.symbol.uppercase()}]"

                                //price
                                tvCurrentPrice.text= data.market_data.current_price.eur.roundToThreeDecimals()
                                val number = data.market_data.price_change_percentage_24h.roundToThreeDecimals()
                                tvChangePercentage.text = "$number%"
                                when{
                                   number > 0.toString()->{
                                        tvChangePercentage.setTextColor(Color.GREEN)
                                        imgArrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                                    }
                                    number < 0.toString() ->{
                                        tvChangePercentage.setTextColor(Color.RED)
                                        imgArrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                                    }
                                    else ->{
                                        tvChangePercentage.setTextColor(Color.LTGRAY)
                                        imgArrow.setImageResource(R.drawable.baseline_minimize_24)
                                    }
                                }

                                //logo
                                imgCoinLogo.load(data.image.large){
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
                                            val listData = data.market_data.sparkline_7d.price.toDoubleToFloat()
                                                    lineChart.animate(listData)

                                //categories
                                tvCategories.text= data.categories?.get(0)!!

                                //date
                                tvGenesisDate.text = if (data.genesis_date != null && data.genesis_date.isNotEmpty())
                                    data.genesis_date
                                else "-"

                                //links
                                tvLink.text =data.links?.homepage?.get(0)
                                tvLink.setOnClickListener {
                                    val uri = Uri.parse(data.links?.homepage?.get(0))
                                    val intent = Intent(Intent.ACTION_VIEW,uri)
                                    requireContext().startActivity(intent)
                                }

                                //description
                                tvDescription.text = if (data.description?.en != null && data.description.en.isNotEmpty())
                                    Jsoup.parse(data.description.en).text()
                                else "-"
                                tvDescription.movementMethod = ScrollingMovementMethod()//handling long description response
                            }
                        }
                        DataStatus.Status.ERROR ->{
                            pBarLoading.isVisible(true,mainLayout)
                            Toast.makeText(requireContext(),"Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

}