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
        this.tagNumber, this.name, this.type.displayName, this.imageUrl, this.breed?.displayName,
        this.group?.displayName, this.calving,this.dateOfBirth, this.aiDate,
        this.repeatHeatDate, this.pregnancyCheckDate, this.dryOffDate, this.calvingDate,
        this.purchaseAmount, this.purchaseDate
    )

fun CattleEntity.toCattle(): Cattle =
    Cattle(
        this.tagNumber, this.name, this.type.convertToType(), this.imageUrl,
        this.breed?.convertToBreed(), this.group?.convertToGroup(), this.calving, this.dateOfBirth,
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

fun String.convertToType(): Cattle.CattleType =
    when (this) {
        Cattle.CattleType.COW.displayName -> Cattle.CattleType.COW
        Cattle.CattleType.BUFFALO.displayName -> Cattle.CattleType.BUFFALO
        Cattle.CattleType.BULL.displayName -> Cattle.CattleType.BULL
        else -> Cattle.CattleType.NONE
    }

fun String.convertToBreed(): Cattle.CattleBreed =
    when (this) {
        Cattle.CattleBreed.HF.displayName -> Cattle.CattleBreed.HF
        Cattle.CattleBreed.JERSY.displayName -> Cattle.CattleBreed.JERSY
        Cattle.CattleBreed.GIR.displayName -> Cattle.CattleBreed.GIR
        Cattle.CattleBreed.KANKREJ.displayName -> Cattle.CattleBreed.KANKREJ
        Cattle.CattleBreed.SHAHIVAL.displayName -> Cattle.CattleBreed.SHAHIVAL
        else -> Cattle.CattleBreed.NONE
    }

fun String.convertToGroup(): Cattle.CattleGroup =
    when (this) {
        Cattle.CattleGroup.HEIFER.displayName -> Cattle.CattleGroup.HEIFER
        Cattle.CattleGroup.MILKING.displayName -> Cattle.CattleGroup.MILKING
        Cattle.CattleGroup.DRY.displayName -> Cattle.CattleGroup.DRY
        Cattle.CattleGroup.CALF_MALE.displayName -> Cattle.CattleGroup.CALF_MALE
        Cattle.CattleGroup.CALF_FEMALE.displayName -> Cattle.CattleGroup.CALF_FEMALE
        else -> Cattle.CattleGroup.NONE
    }

// endregion

// region Nav controller

fun FragmentActivity.navigateTo(@IdRes host: Int, @IdRes destination: Int) {
    Navigation.findNavController(this, host).navigate(destination)
}

fun Fragment.navigateTo(@IdRes host: Int = R.id.nav_host_fragment, @IdRes destination: Int) {
    Navigation.findNavController(this.requireActivity(), host).navigate(destination)
}

// endregion