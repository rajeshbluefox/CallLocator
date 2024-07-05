package com.familylocation.mobiletracker.callThemesModule.supportFunctions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callThemesModule.modalClass.ThemesData
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class CallerThemesAdapterNew(
    private var context: Context,
    private var themesList: ArrayList<ThemesData>,
    private val onThemeClicked: () -> Unit,
    private val nativeAd: NativeAd? // Add nativeAd parameter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_AD = 1
        const val VIEW_TYPE_THEME = 2
    }

    class CallerThemesAdapterNewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeThumbnail: ImageView = view.findViewById(R.id.ivThemeThumbnail)
    }

    class AdXClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flAdPlaceholder: FrameLayout = itemView.findViewById(R.id.fl_adplaceholder)

        fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            adView.mediaView = mediaView

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            try {
                (adView.headlineView as TextView).text = nativeAd.headline
            } catch (e: Exception) {
                e.printStackTrace()
            }

            adView.bodyView?.let { bodyView ->
                if (nativeAd.body == null) {
                    bodyView.visibility = View.INVISIBLE
                } else {
                    bodyView.visibility = View.VISIBLE
                    (bodyView as TextView).text = nativeAd.body
                }
            }

            adView.callToActionView?.let { callToActionView ->
                if (nativeAd.callToAction == null) {
                    callToActionView.visibility = View.INVISIBLE
                } else {
                    callToActionView.visibility = View.VISIBLE
                    (callToActionView as Button).text = nativeAd.callToAction
                }
            }

            adView.iconView?.let { iconView ->
                if (nativeAd.icon == null) {
                    iconView.visibility = View.GONE
                } else {
                    (iconView as ImageView).setImageDrawable(nativeAd.icon!!.drawable)
                    iconView.visibility = View.VISIBLE
                }
            }

            adView.priceView?.let { priceView ->
                if (nativeAd.price == null) {
                    priceView.visibility = View.INVISIBLE
                } else {
                    priceView.visibility = View.VISIBLE
                    (priceView as TextView).text = nativeAd.price
                }
            }

            adView.storeView?.let { storeView ->
                if (nativeAd.store == null) {
                    storeView.visibility = View.INVISIBLE
                } else {
                    storeView.visibility = View.VISIBLE
                    (storeView as TextView).text = nativeAd.store
                }
            }

            adView.starRatingView?.let { starRatingView ->
                if (nativeAd.starRating == null) {
                    starRatingView.visibility = View.INVISIBLE
                } else {
                    (starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                    starRatingView.visibility = View.VISIBLE
                }
            }

            adView.advertiserView?.let { advertiserView ->
                if (nativeAd.advertiser == null) {
                    advertiserView.visibility = View.INVISIBLE
                } else {
                    advertiserView.visibility = View.VISIBLE
                    (advertiserView as TextView).text = nativeAd.advertiser
                }
            }

            adView.storeView?.visibility = View.GONE
            adView.priceView?.visibility = View.GONE

            adView.setNativeAd(nativeAd)

            val vc = nativeAd.mediaContent!!.videoController

            if (vc.hasVideoContent()) {
                vc.setVideoLifecycleCallbacks(object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (themesList[position].id == "0") {
            VIEW_TYPE_AD
        } else {
            VIEW_TYPE_THEME
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD) {
            val itemView2 = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_native_ad, parent, false)
            AdXClass(itemView2)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_caller_theme, parent, false)
            CallerThemesAdapterNewViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_AD -> {
                Log.e("Test", "Ads Logic")
                val vHolder = holder as AdXClass
                if (nativeAd == null) {
                    Log.e("Test", "nativeAd is null")
                    vHolder.flAdPlaceholder.visibility = View.GONE
                } else {
                    Log.e("Test", "nativeAd is not null")
                    val adView = LayoutInflater.from(context)
                        .inflate(R.layout.ad_unified, null) as NativeAdView
                    vHolder.populateUnifiedNativeAdView(nativeAd, adView)
                    vHolder.flAdPlaceholder.removeAllViews()
                    vHolder.flAdPlaceholder.addView(adView)
                }
            }
            VIEW_TYPE_THEME -> {
                val bin = holder as CallerThemesAdapterNewViewHolder
                val themeItem = themesList[position]
                Glide.with(context)
                    .load(themeItem.thumbNail)
                    .fitCenter()
                    .into(bin.themeThumbnail)
                bin.themeThumbnail.setOnClickListener {
//                   UtilFunctions.showToast(context,"Coming Soon")
                    SelectedTheme.selectedTheme = themeItem
                    SelectedTheme.selectedThemeCount = position + 1
                    onThemeClicked.invoke()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return themesList.size
    }
}


