package com.example.mtgportal.injection

import android.app.Application
import androidx.room.Room
import com.example.mtgportal.BuildConfig.DEBUG
import com.example.mtgportal.R
import com.example.mtgportal.api.ApiService
import com.example.mtgportal.database.AppDatabase
import com.example.mtgportal.database.FavoriteCardsDao
import com.example.mtgportal.repository.CardTypesRepository
import com.example.mtgportal.repository.CardsRepository
import com.example.mtgportal.repository.FavoritesCardsRepository
import com.example.mtgportal.ui.favorite.FavoritesViewModel
import com.example.mtgportal.ui.home.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {
    fun provideCountriesApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    single { provideCountriesApi(get()) }
}

val networkModule = module {
    val connectTimeout: Long = 40
    val readTimeout: Long = 40

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
        if (DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        okHttpClientBuilder.build()
        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    single { provideHttpClient() }
    single {
        val baseUrl = androidContext().getString(R.string.BASE_URL)
        provideRetrofit(get(), baseUrl)
    }
}

val databaseModule = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java, "cards-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideFavoriteCardsDao(database: AppDatabase): FavoriteCardsDao {
        return database.favoriteCardsDao()
    }
    single { provideDatabase(androidApplication()) }
    single { provideFavoriteCardsDao(get()) }
}

val repositoryModule = module {
    fun provideCardRepository(api: ApiService): CardsRepository =
        CardsRepository.getInstance(api)
    fun provideFavoriteCardRepository(favoriteCardsDao: FavoriteCardsDao): FavoritesCardsRepository =
        FavoritesCardsRepository.getInstance(favoriteCardsDao)
    fun provideCardTypesRepository(api: ApiService): CardTypesRepository =
        CardTypesRepository.getInstance(api)
    single { provideCardRepository(get()) }
    single { provideFavoriteCardRepository(get()) }
    single { provideCardTypesRepository(get()) }
}

val viewModelModule = module {
    viewModel {
        HomeViewModel(get(), get(), get(), get())
    }
    viewModel {
        FavoritesViewModel(get())
    }
}