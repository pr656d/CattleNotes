package com.pr656d.shared.data.cattle.datasources

import com.google.firebase.firestore.*
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toBreed
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toType
import timber.log.Timber
import javax.inject.Inject

interface CattleUpdater {
    fun initialize()

    fun stop()
}

class FirestoreCattleUpdater @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authIdDataSource: AuthIdDataSource,
    private val cattleRepository: CattleRepository
) : CattleUpdater {

    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("user Id not found at cattle updater")
            throw IllegalArgumentException("user Id not found at cattle updater")
        }
    }

    private var registration: ListenerRegistration? = null

    override fun initialize() {
        // Remove previous subscriptions if exists
        registration?.remove()

        val cattleListListener =
            { snapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
                DefaultScheduler.execute {
                    if (e != null) {
                        Timber.d("cattleList listener error : ${e.printStackTrace()}")
                        return@execute
                    }

                    for (doc in snapshots!!.documentChanges) {
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                cattleRepository.addCattle(
                                    cattle = getCattle(doc.document),
                                    saveToLocal = true
                                )
                            }
                            DocumentChange.Type.MODIFIED -> {
                                cattleRepository.updateCattle(
                                    cattle = getCattle(doc.document),
                                    saveToLocal = true
                                )
                            }
                            DocumentChange.Type.REMOVED -> {
                                cattleRepository.deleteCattle(
                                    cattle = getCattle(doc.document),
                                    saveToLocal = true
                                )
                            }
                        }
                    }
                }
        }

        registration = firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(CATTLE_COLLECTION)
            .addSnapshotListener(cattleListListener)
    }

    override fun stop() {
        registration?.remove()
    }

    private fun getCattle(document: QueryDocumentSnapshot): Cattle {
        return Cattle(
            tagNumber = document.data[KEY_TAG_NUMBER] as Long,
            name = document.data[KEY_NAME] as? String,
            image = (document.data[KEY_IMAGE_URL] as? String)?.let { Cattle.Image(null, it) },
            type = (document.data[KEY_TYPE] as String).toType(),
            breed = (document.data[KEY_BREED] as String).toBreed(),
            group = (document.data[KEY_GROUP] as String).toGroup(),
            lactation = document.data[KEY_LACTATION] as Long,
            homeBorn = document.data[KEY_HOME_BORN] as Boolean,
            purchaseAmount = document.data[KEY_PURCHASE_AMOUNT] as? Long,
            purchaseDate = (document.data[KEY_PURCHASE_Date] as? Long)?.let { TimeUtils.toLocalDate(it) },
            dateOfBirth = (document.data[KEY_DOB] as? Long)?.let { TimeUtils.toLocalDate(it) },
            parent = document.data[KEY_PARENT] as? String
        ).apply { id = document.id }
    }

    companion object {
        const val USERS_COLLECTION = "users"
        const val CATTLE_COLLECTION = "cattleList"
        const val KEY_TAG_NUMBER = "tagNumber"
        const val KEY_NAME = "name"
        const val KEY_IMAGE_URL = "imageUrl"
        const val KEY_TYPE = "type"
        const val KEY_BREED = "breed"
        const val KEY_GROUP = "group"
        const val KEY_LACTATION = "lactation"
        const val KEY_HOME_BORN = "homeBorn"
        const val KEY_PURCHASE_AMOUNT = "purchaseAmount"
        const val KEY_PURCHASE_Date = "purchaseDate"
        const val KEY_DOB = "dateOfBirth"
        const val KEY_PARENT = "parentId"
    }
}