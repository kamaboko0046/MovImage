package com.kamaboko.imageavoid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title=""

        // Admob
        MobileAds.initialize( this )
        val adView: AdView = findViewById(R.id.adBanner)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

    }
}