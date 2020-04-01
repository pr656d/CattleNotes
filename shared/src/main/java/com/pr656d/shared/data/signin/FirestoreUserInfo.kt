package com.pr656d.shared.data.signin

import com.google.firebase.firestore.DocumentSnapshot

open class FirestoreUserInfo (
    private val snapshot: DocumentSnapshot
) : UserInfoOnFirestore {
    override fun getFarmName(): String? {
        return snapshot.get(FARM_NAME_KEY) as? String
    }

    override fun getFarmAddress(): String? {
        return snapshot.get(FARM_ADDRESS_KEY) as? String
    }

    override fun getGender(): String? {
        return snapshot.get(GENDER_KEY) as? String
    }

    override fun getDateOfBirth(): String? {
        return snapshot.get(DOB_KEY) as? String
    }

    override fun getAddress(): String? {
        return snapshot.get(ADDRESS_KEY) as? String
    }

    override fun getDairyCode(): String? {
        return snapshot.get(DAIRY_CODE) as? String
    }

    override fun getDairyCustomerId(): String? {
        return snapshot.get(DAIRY_CUSTOMER_ID) as? String
    }

    companion object {
        const val FARM_NAME_KEY = "farmName"
        const val FARM_ADDRESS_KEY = "farmAddress"
        const val GENDER_KEY = "gender"
        const val DOB_KEY = "dateOfBirth"
        const val ADDRESS_KEY = "address"
        const val DAIRY_CODE = "dairyCode"
        const val DAIRY_CUSTOMER_ID = "dairyCustomerId"
    }
}