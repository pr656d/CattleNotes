package com.pr656d.cattlenotes.ui.breeding.history

import com.pr656d.model.Breeding

interface BreedingHistoryActionListener {

    /**
     * Open [AddEditBreedingFragment] to edit breeding.
     */
    fun editBreeding(breeding: Breeding)

    /**
     * Delete breeding.
     */
    fun deleteBreeding(breeding: Breeding) {
        deleteBreeding(breeding, false)
    }

    /**
     * Delete breeding with delete confirmation.
     */
    fun deleteBreeding(breeding: Breeding, deleteConfirmation: Boolean)
}