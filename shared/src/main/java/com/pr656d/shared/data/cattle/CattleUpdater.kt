package com.pr656d.shared.data.cattle

import com.google.firebase.firestore.*
import com.pr656d.model.Cattle
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
        return document.data.run {
            Cattle(
                tagNumber = get(KEY_TAG_NUMBER) as Long,
                name = get(KEY_NAME) as? String,
                image = (get(KEY_IMAGE_URL) as? String)?.let { Cattle.Image(null, it) },
                type = (get(KEY_TYPE) as String).toType(),
                breed = (get(KEY_BREED) as String).toBreed(),
                group = (get(KEY_GROUP) as String).toGroup(),
                lactation = get(KEY_LACTATION) as Long,
                homeBorn = get(KEY_HOME_BORN) as Boolean,
                purchaseAmount = get(KEY_PURCHASE_AMOUNT) as? Long,
                purchaseDate = (get(KEY_PURCHASE_Date) as? Long)?.let {
                    TimeUtils.toLocalDate(it)
                },
                dateOfBirth = (get(KEY_DOB) as? Long)?.let { TimeUtils.toLocalDate(it) },
                parent = get(KEY_PARENT) as? String
            ).apply { id = document.id }
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val CATTLE_COLLECTION = "cattleList"
        private const val KEY_TAG_NUMBER = "tagNumber"
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE_URL = "imageUrl"
        private const val KEY_TYPE = "type"
        private const val KEY_BREED = "breed"
        private const val KEY_GROUP = "group"
        private const val KEY_LACTATION = "lactation"
        private const val KEY_HOME_BORN = "homeBorn"
        private const val KEY_PURCHASE_AMOUNT = "purchaseAmount"
        private const val KEY_PURCHASE_Date = "purchaseDate"
        private const val KEY_DOB = "dateOfBirth"
        private const val KEY_PARENT = "parentId"
    }
}