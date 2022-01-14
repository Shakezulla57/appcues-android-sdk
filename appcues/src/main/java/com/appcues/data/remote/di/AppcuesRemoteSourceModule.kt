package com.appcues.data.remote.di

import com.appcues.data.remote.AppcuesRemoteSource
import com.appcues.data.remote.retrofit.AppcuesService
import com.appcues.data.remote.retrofit.RetrofitAppcuesRemoteSource
import com.appcues.data.remote.retrofit.RetrofitWrapper
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.core.module.Module
import org.koin.dsl.module

internal object AppcuesRemoteSourceModule {

    private const val BASE_URL = "https://api.appcues.com/"

    fun install(): Module = module {
        single<AppcuesRemoteSource> {
            RetrofitAppcuesRemoteSource(
                appcuesService = RetrofitWrapper(BASE_URL.toHttpUrl()).create(AppcuesService::class)
            )
        }
    }
}
