package com.demo.cleanarchitecturedemo.di

import androidx.room.Room
import com.demo.cleanarchitecturedemo.data.local.AppDatabase
import com.demo.cleanarchitecturedemo.data.remote.ApiService
import com.demo.cleanarchitecturedemo.data.repository.PostRepositoryImpl
import com.demo.cleanarchitecturedemo.domain.repository.PostRepository
import com.demo.cleanarchitecturedemo.domain.usecase.GetPostsUseCase
import com.demo.cleanarchitecturedemo.presentation.ui.PostsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app-db").build()
    }
    single { get<AppDatabase>().postDao() }
    single {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    factory<PostRepository> { PostRepositoryImpl(get(), get()) }
    factory { GetPostsUseCase(get()) }
    viewModel { PostsViewModel(get()) }
}