package com.pr656d.cattlenotes.di

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.local.prefs.CattleNotesSharedPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(application: CattleNotesApplication): Context {
        return application.applicationContext
    }

    @Provides
    fun provideAuthUI(): AuthUI = AuthUI.getInstance()

    @Provides
    fun provideFirebaseAuth(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    @Provides
    fun provideSharedPreferences(application: CattleNotesApplication) =
        application.getSharedPreferences(
            CattleNotesSharedPreferences.preferencesName,
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    fun providesAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideLinearLayoutManager(context: Context) = LinearLayoutManager(context)
}
