package com.familylocation.mobiletracker.bankInformationModule.model;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;

import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.bankInformationModule.BankViewActivity;

public class BankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<BankModel> mListData;

    NativeAd nativeAd;

    public BankAdapter(Context mContext, ArrayList<BankModel> mListData,NativeAd nativeAd) {
        this.mContext = mContext;
        this.mListData = mListData;

        this.nativeAd = nativeAd;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bank_card, parent, false);
//        return new BankViewHolder(view);

        if (viewType == 1) {
            View itemView2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_native_ad, parent, false);
            return new AdXClass(itemView2);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bank_card, parent, false);
            return new BankViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        switch (holder.getItemViewType()) {
            case 1:
                Log.e("Test","Ads Logic");
                AdXClass vHolder = (AdXClass) holder;
                if (nativeAd == null) {
                    Log.e("Test","nativeAd is null");
                    vHolder.fl_adplaceholder.setVisibility(View.GONE);
                } else {
                    Log.e("Test","nativeAd is not null");
                    NativeAdView adView = (NativeAdView) LayoutInflater.from(mContext)
                            .inflate(R.layout.ad_unified, null);
                    vHolder.populateUnifiedNativeAdView(nativeAd, adView);
                    vHolder.fl_adplaceholder.removeAllViews();
                    vHolder.fl_adplaceholder.addView(adView);
                }
                break;
            case 2:
                Log.e("Test","Bank Logic");
                BankViewHolder mHolder = (BankViewHolder) holder;

                final String title = mListData.get(i).getTitle();
                final String balance = mListData.get(i).getBalance();
                final String customer = mListData.get(i).getCustomer();
                final int img = mListData.get(i).getImg();
                mHolder.bankViewTitle.setText(title);
                mHolder.bankViewImg.setImageResource(img);
                mHolder.bankViewCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.BankInfo_ringtone_click = Utils.BankInfo_ringtone_click + 1;

                        Intent mIntent = new Intent(mContext, BankViewActivity.class);
                        mIntent.putExtra("title", title);
                        mIntent.putExtra("balance", balance);
                        mIntent.putExtra("customer", customer);
                        mIntent.putExtra("img", img);
                        mContext.startActivity(mIntent);
                    }
                });
                break;
        }



    }



    @Override
    public int getItemViewType(int position) {
        if (!mListData.get(position).getTitle().isEmpty()) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class BankViewHolder extends RecyclerView.ViewHolder {

        CardView bankViewCard;
        TextView bankViewTitle;
        ImageView bankViewImg;

        public BankViewHolder(View view) {
            super(view);

            bankViewCard = view.findViewById(R.id.bank_view_card);
            bankViewImg = view.findViewById(R.id.bank_view_img);
            bankViewTitle = view.findViewById(R.id.bank_view_title);
        }
    }

    class AdXClass extends RecyclerView.ViewHolder {

        FrameLayout fl_adplaceholder;

        public AdXClass(View itemView) {
            super(itemView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);

        }

        private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline is guaranteed to be in every UnifiedNativeAd.
            try {
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            adView.getStoreView().setVisibility(View.GONE);
            adView.getPriceView().setVisibility(View.GONE);

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {
//			videoStatus.setText(String.format(Locale.getDefault(),
//					"Video status: Ad contains a %.2f:1 video asset.",
//					vc.getAspectRatio()));

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
//					refresh.setEnabled(true);
//					videoStatus.setText("Video status: Video playback has ended.");
                        super.onVideoEnd();
                    }
                });
            } else {
//			videoStatus.setText("Video status: Ad does not contain a video asset.");
//			refresh.setEnabled(true);
            }
        }

    }


}
