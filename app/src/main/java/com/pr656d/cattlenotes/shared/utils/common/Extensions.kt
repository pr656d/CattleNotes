package com.pr656d.cattlenotes.shared.utils.common

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.local.db.CattleEntity
import com.pr656d.cattlenotes.model.Cattle
import java.time.ZonedDateTime


/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

/**
 * Allows calls like
 *
 * `supportFragmentManager.inTransaction { add(...) }`
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

// region ViewModels

/**
 * For Actvities, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)

/**
 * For Fragments, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)

/**
 * Like [Fragment.viewModelProvider] for Fragments that want a [ViewModel] scoped to the Activity.
 */
inline fun <reified VM : ViewModel> Fragment.activityViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(requireActivity(), provider).get(VM::class.java)

/**
 * Like [Fragment.viewModelProvider] for Fragments that want a [ViewModel] scoped to the parent
 * Fragment.
 */
inline fun <reified VM : ViewModel> Fragment.parentViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(parentFragment!!, provider).get(VM::class.java)

// endregion

// region LiveData

/** Uses `Transformations.map` on a LiveData */
fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, body)
}

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
}

// endregion

// region ZonedDateTime
@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()
// endregion

// region converter

fun Cattle.toCattleEntity(): CattleEntity =
    CattleEntity(
        this.tagNumber, this.name, this.type, this.imageUrl, this.breed,
        this.group, this.calving,this.dateOfBirth, this.aiDate,
        this.repeatHeatDate, this.pregnancyCheckDate, this.dryOffDate, this.calvingDate,
        this.purchaseAmount, this.purchaseDate
    )

fun CattleEntity.toCattle(): Cattle =
    Cattle(
        this.tagNumber, this.name, this.type, this.imageUrl,
        this.breed, this.group, this.calving, this.dateOfBirth,
        this.aiDate, this.repeatHeatDate, this.pregnancyCheckDate, this.dryOffDate,
        this.calvingDate, this.purchaseAmount, this.purchaseDate
    )

fun List<CattleEntity>.toCattleList(): List<Cattle> =
    arrayListOf<Cattle>().apply {
        this@toCattleList.forEach { add(it.toCattle()) }
    }

fun List<Cattle>.toCattleEntityList(): List<CattleEntity> =
    arrayListOf<CattleEntity>().apply {
        this@toCattleEntityList.forEach { add(it.toCattleEntity()) }
    }

// endregion

// region Nav controller

fun FragmentActivity.navigateTo(@IdRes host: Int, @IdRes destination: Int) {
    Navigation.findNavController(this, host).navigate(destination)
}

fun Fragment.navigateTo(@IdRes host: Int = R.id.nav_host_main, @IdRes destination: Int) {
    Navigation.findNavController(this.requireActivity(), host).navigate(destination)
}

// endregion