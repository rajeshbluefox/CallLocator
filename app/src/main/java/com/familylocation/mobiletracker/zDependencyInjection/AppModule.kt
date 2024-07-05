package com.familylocation.mobiletracker.zDependencyInjection


import com.familylocation.mobiletracker.zAPIEndPoints.ApiHelper
import com.familylocation.mobiletracker.zAPIEndPoints.ApiHelperImplementation
import com.familylocation.mobiletracker.zAPIEndPoints.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideBaseUrl() = "https://gundasupermarket.com/Caller_Locator_App/"


    @Provides
    @Singleton
    fun provideOkHttpClient() = if (true) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiInterface::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImplementation): ApiHelper = apiHelper


}