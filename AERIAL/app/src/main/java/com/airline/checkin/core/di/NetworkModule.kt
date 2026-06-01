package com.airline.checkin.core.di

import com.airline.checkin.core.network.AuthInterceptor
import com.airline.checkin.data.remote.api.AuthApi
import com.airline.checkin.data.remote.api.BoardingPassApi
import com.airline.checkin.data.remote.api.CheckInApi
import com.airline.checkin.data.remote.api.FlightApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Using the local machine's IP address.
    private const val BASE_URL = "http://10.59.94.120:3000/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides 
    @Singleton 
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            authInterceptor: AuthInterceptor
    ): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideFlightApi(retrofit: Retrofit): FlightApi = retrofit.create(FlightApi::class.java)

    @Provides
    @Singleton
    fun provideCheckInApi(retrofit: Retrofit): CheckInApi = retrofit.create(CheckInApi::class.java)

    @Provides
    @Singleton
    fun provideBoardingPassApi(retrofit: Retrofit): BoardingPassApi =
            retrofit.create(BoardingPassApi::class.java)
}

/*

Ton app appelle authApi.login()
        ↓
AuthInterceptor         → ajoute "Authorization: Bearer token123" à la lettre
        ↓
HttpLoggingInterceptor  → affiche la lettre dans Logcat (pour déboguer)
        ↓
OkHttpClient            → envoie la lettre au serveur
        ↓
Serveur répond
        ↓
HttpLoggingInterceptor  → affiche la réponse dans Logcat
        ↓
Ton app reçoit le résultat 

*/