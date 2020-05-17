/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.IsCattleExistWithTagNumberUseCase
import com.pr656d.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetParentCattleUseCase
import com.pr656d.shared.domain.cattle.list.LoadCattleListUseCase
import com.pr656d.shared.domain.cattle.validator.CattleTagNumberValidatorUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
import com.pr656d.shared.utils.nameOrTagNumber
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [AddEditCattleViewModel]
 */
class AddEditCattleViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private val cattleRepository = object : FakeCattleRepository() {
        override fun getAllCattle(): LiveData<List<Cattle>> {
            val result = MutableLiveData<List<Cattle>>()
            result.postValue(TestData.cattleList)
            return result
        }

        override fun getCattleById(id: String): LiveData<Cattle?> {
            return MutableLiveData<Cattle?>().apply {
                value = TestData.cattleList.firstOrNull { it.id == id }
            }
        }

        override fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
            return TestData.cattleList.find { it.tagNumber == tagNumber } != null
        }
    }

    private fun createAddEditCattleViewModel(
        repository: CattleRepository = cattleRepository,
        addCattleUseCase: AddCattleUseCase = AddCattleUseCase(repository),
        updateCattleUseCase: UpdateCattleUseCase = UpdateCattleUseCase(repository),
        cattleTagNumberValidatorUseCase: CattleTagNumberValidatorUseCase =
            CattleTagNumberValidatorUseCase(IsCattleExistWithTagNumberUseCase(repository)),
        getParentListUseCase: GetParentListUseCase =
            GetParentListUseCase(LoadCattleListUseCase(repository)),
        getCattleByIdUseCase: GetCattleByIdUseCase = GetCattleByIdUseCase(repository),
        getParentCattleUseCase: GetParentCattleUseCase = GetParentCattleUseCase(repository)
    ): AddEditCattleViewModel {
        return AddEditCattleViewModel(
            addCattleUseCase = addCattleUseCase,
            updateCattleUseCase = updateCattleUseCase,
            getParentListUseCase = getParentListUseCase,
            cattleTagNumberValidatorUseCase = cattleTagNumberValidatorUseCase,
            getCattleByIdUseCase = getCattleByIdUseCase,
            getParentCattleUseCase = getParentCattleUseCase
        ).apply { observeUnobserved() }
    }

    private fun AddEditCattleViewModel.observeUnobserved() {
        /**
         * Live data which have to explicitly gets observed as they were
         * observed in xml by data binding.
         */
        oldCattle.observeForever {  }
        tagNumber.observeForever {  }
        name.observeForever {  }
        type.observeForever {  }
        typeList.observeForever {  }
        breed.observeForever {  }
        breedList.observeForever {  }
        group.observeForever {  }
        lactation.observeForever {  }
        dob.observeForever {  }
        parent.observeForever {  }
        homeBorn.observeForever {  }
        purchaseAmount.observeForever {  }
        purchaseDate.observeForever {  }
        tagNumberErrorMessage.observeForever {  }
        typeErrorMessage.observeForever {  }
        breedErrorMessage.observeForever {  }
        groupErrorMessage.observeForever {  }
        lactationErrorMessage.observeForever {  }
        parentList.observeForever {  }
    }

    @Test
    fun saveCalledWithAllFieldsEmpty_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /**
     * Save is called but all fields are empty except valid Tag number.
     * Tag number should contain digits only with length 19.
     */
    @Test
    fun saveCalledByProvidingValidTagNumberOnly_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Set valid tag number
        viewModel.tagNumber.postValue("386218762213")

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /**
     * Save is called but tag number contains string which is invalid.
     */
    @Test
    fun saveCalledByProvidingInValidTagNumberContainsString_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Set invalid tag number which contains String only.
        viewModel.tagNumber.postValue("contains_string")

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /**
     * Save is called but tag number contains string and digit combination which is invalid.
     */
    @Test
    fun saveCalledByProvidingInValidTagNumberContainsStringAndDigits_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Set invalid tag number which contains String and digit.
        viewModel.tagNumber.postValue("62187368String")

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /**
     * Save is called but tag number contains string which is invalid.
     */
    @Test
    fun saveCalledByProvidingInValidTagNumberLengthExceed_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Set invalid tag number which contains digit but exceeds length.
        viewModel.tagNumber.postValue("12345678901234567890")

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /**
     * Save is called but tag number but cattle already exist with same tag number which is invalid.
     */
    @Test
    fun saveCalledByProvidingInValidTagNumberCattleAlreadyExist_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Set invalid tag number which contains existing cattle tag number.
        viewModel.tagNumber.postValue("1")

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun tagNumberIsValid_setTagNumberErrorMessageIsValid() {
        val viewModel = createAddEditCattleViewModel()

        // Valid tag number
        viewModel.tagNumber.postValue("2768231987")

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertThat(VALID_FIELD, isEqualTo(tagNumberErrorMessage))
    }

    @Test
    fun tagNumberIsInValidContainsString_setTagNumberErrorMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Invalid tag number
        viewModel.tagNumber.postValue("some_string")

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertNotEquals(VALID_FIELD, tagNumberErrorMessage)
    }

    @Test
    fun tagNumberIsInvalidContainsDigitAndString_setTagNumberErrorMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Invalid tag number
        viewModel.tagNumber.postValue("21398st87ring")

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertNotEquals(VALID_FIELD, tagNumberErrorMessage)
    }

    @Test
    fun tagNumberIsInValidLengthExceed_setTagNumberErrorMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Invalid tag number
        viewModel.tagNumber.postValue("123456789012345678901")

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertNotEquals(VALID_FIELD, tagNumberErrorMessage)
    }

    @Test
    fun tagNumberIsNotValidCattleAlreadyExist_setTagNumberErrorMessage() {
        val viewModel = createAddEditCattleViewModel()

        val alreadyExistTagNumber = TestData.cattle1.tagNumber

        // Invalid tag number. This cattle already exist.
        viewModel.tagNumber.postValue(alreadyExistTagNumber.toString())

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertNotEquals(VALID_FIELD, tagNumberErrorMessage)
    }

    @Test
    fun allFieldsValid_callAddCattleAndNavigateUp() {
        val viewModel = createAddEditCattleViewModel()

        // Valid tag number.
        viewModel.tagNumber.value = "312546"

        // Valid type
        viewModel.type.value = AnimalType.Cow.displayName

        // Valid breed
        viewModel.breed.value = "HF"

        // Valid group
        viewModel.group.value = Cattle.Group.Milking.displayName

        // Valid lactation
        viewModel.lactation.value = "2"

        // Save cattle
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun allFieldsValid_callUpdateCattleAndNavigateUp() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle2

        viewModel.setCattle(cattle.id)

        // Update tag number.
        viewModel.tagNumber.value = "312546"
        // Update type
        viewModel.type.value = AnimalType.Cow.displayName
        // Update breed
        viewModel.breed.value = "HF"
        // Update group
        viewModel.group.value = Cattle.Group.Milking.displayName
        // Update lactation
        viewModel.lactation.value = "2"
        // Update purchase amount
        viewModel.purchaseAmount.value = "68999"

        // Save cattle
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun editingCattleButChangedNothing_navigateUp() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle2

        viewModel.setCattle(cattle.id)

        // Save cattle
        viewModel.save()

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertTrue(editing)

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun pickParentIsCalledAndParentIsAvailable_selectingParentIsTrue() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle2

        viewModel.setCattle(cattle.id)

        viewModel.pickParent()

        val selectingParent = LiveDataTestUtil.getValue(viewModel.selectingParent)!!
        assertTrue(selectingParent)
    }

    @Test
    fun pickParentIsCalledAndTagNumberNotAvailable_selectingParentIsFalse() {
        val viewModel = createAddEditCattleViewModel()

        viewModel.pickParent()

        val selectingParent = LiveDataTestUtil.getValue(viewModel.selectingParent)!!
        assertFalse(selectingParent)
    }

    @Test
    fun pickParentIsCalledAndTagNumberNotAvailable_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        viewModel.pickParent()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun pickParentIsCalledParentIsSelected_setParent() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle4
        val actualParentCattle = TestData.cattle5

        // Set cattle
        viewModel.setCattle(cattle.id)

        // Pick parent
        viewModel.pickParent()

        // Parent is selected
        viewModel.parentSelected(actualParentCattle)

        val parentCattle = LiveDataTestUtil.getValue(viewModel.parentCattle)
        assertThat(actualParentCattle, isEqualTo(parentCattle))

        val parent = LiveDataTestUtil.getValue(viewModel.parent)
        assertThat(actualParentCattle.nameOrTagNumber(), isEqualTo(parent))
    }

    @Test
    fun addCattleWithAllFieldsFilled_navigateUpOnSuccess() {
        val viewModel = createAddEditCattleViewModel()

        viewModel.apply {
            tagNumber.value = "123456789012"
            name.value = "Sita"
            type.value = AnimalType.Cow.displayName
            breed.value = "HF"
            group.value = Cattle.Group.Heifer.displayName
            lactation.value = "3"
            dob.value = LocalDate.ofEpochDay(1562606700)
            homeBorn.value = true
            purchaseAmount.value = "78000"
            purchaseDate.value = LocalDate.ofEpochDay(1542606700)
        }

        // Save cattle
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    private object FailingAddCattleUseCase : AddCattleUseCase(FakeCattleRepository()) {
        override fun execute(parameters: Cattle) {
            throw Exception("Error!")
        }
    }

    @Test
    fun addingNewCattleWithAllFieldsFilled_showErrorOnFailure() {
        val viewModel = createAddEditCattleViewModel(
            addCattleUseCase = FailingAddCattleUseCase
        )

        viewModel.apply {
            tagNumber.value = "123456789012"
            name.value = "Sita"
            type.value = AnimalType.Cow.displayName
            breed.value = "HF"
            group.value = Cattle.Group.Heifer.displayName
            lactation.value = "3"
            dob.value = LocalDate.ofEpochDay(1562606700)
            homeBorn.value = true
            purchaseAmount.value = "78000"
            purchaseDate.value = LocalDate.ofEpochDay(1542606700)
        }

        // Save cattle
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun tagNumberAvailable_fetchParentList() {
        val viewModel = createAddEditCattleViewModel()

        // Tag number which doesn't already exist
        viewModel.tagNumber.value = "142"

        val parentList = LiveDataTestUtil.getValue(viewModel.parentList)
        assertThat(TestData.cattleList, isEqualTo(parentList))
    }

    @Test
    fun pickParentCalledButTagNumberAlreadyExist_showMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Tag number which already exist
        viewModel.tagNumber.value = "1"

        viewModel.pickParent()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun pickParentCalled_parentListIsEmpty() {
        val viewModel = createAddEditCattleViewModel(
            object : FakeCattleRepository() {
                override fun getAllCattle(): LiveData<List<Cattle>> {
                    return MutableLiveData(emptyList())
                }
            }
        )

        viewModel.tagNumber.value = "1"

        val parentList = LiveDataTestUtil.getValue(viewModel.parentList)!!
        assertTrue(parentList.isEmpty())
    }

    @Test
    fun parentListIsEmpty_isEmptyParentListIsTrue() {
        val viewModel = createAddEditCattleViewModel(
            object : FakeCattleRepository() {
                override fun getAllCattle(): LiveData<List<Cattle>> {
                    return MutableLiveData(emptyList())
                }
            }
        )

        viewModel.tagNumber.value = "1"

        val isEmptyParentList = LiveDataTestUtil.getValue(viewModel.isEmptyParentList)!!
        assertTrue(isEmptyParentList)
    }

    @Test
    fun backPressedAndAllFieldsEmpty_navigateUp() {
        val viewModel = createAddEditCattleViewModel()

        // Back pressed
        viewModel.onBackPressed()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun backPressedAndAllFieldsNotEmpty_showBackConfirmationDialog() {
        val viewModel = createAddEditCattleViewModel()

        // Set tag number
        viewModel.tagNumber.value = "1"

        // Back pressed
        viewModel.onBackPressed()

        val showBackConfirmationDialog = LiveDataTestUtil.getValue(viewModel.showBackConfirmationDialog)
        assertThat(Unit, isEqualTo(showBackConfirmationDialog?.getContentIfNotHandled()))
    }

    @Test
    fun backPressedIsConfirmed_navigateUp() {
        val viewModel = createAddEditCattleViewModel()

        // Set tag number
        viewModel.tagNumber.value = "1"

        // Back pressed with confirmation is true
        viewModel.onBackPressed(backConfirmation = true)

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    /** Region : Add [Cattle] */

    @Test
    fun addCattle_editingIsFalse() {
        val viewModel = createAddEditCattleViewModel()

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertFalse(editing)
    }

    @Test
    fun changeBreedListAsTypeChanges() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle1

        // Set cattle
        viewModel.setCattle(cattle.id)

        // Type changed
        viewModel.type.value = AnimalType.Buffalo.displayName

        assertThat(
            R.array.list_breed_buffalo,
            isEqualTo(LiveDataTestUtil.getValue(viewModel.breedList))
        )

        // Type changed
        viewModel.type.value = AnimalType.Bull.displayName

        assertThat(
            R.array.list_breed_bull,
            isEqualTo(LiveDataTestUtil.getValue(viewModel.breedList))
        )

        // Type changed
        viewModel.type.value = AnimalType.Cow.displayName

        assertThat(
            R.array.list_breed_cow,
            isEqualTo(LiveDataTestUtil.getValue(viewModel.breedList))
        )
    }

    @Test
    fun cattleTypeIsBull_resetGroupAndLactation() {
        val viewModel = createAddEditCattleViewModel()

        // Set tag number
        viewModel.tagNumber.value = "1"

        viewModel.type.value = AnimalType.Bull.displayName

        assertNull(LiveDataTestUtil.getValue(viewModel.group))
        assertNull(LiveDataTestUtil.getValue(viewModel.lactation))
    }

    @Test
    fun saveCattleTypeBull_navigateUpOnSuccess() {
        val viewModel = createAddEditCattleViewModel()

        viewModel.tagNumber.value = "2131"

        viewModel.type.value = AnimalType.Bull.displayName

        viewModel.breed.value = "Gir"

        // Call save
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun setParentCalled_setParent() {
        val viewModel = createAddEditCattleViewModel()

        val actualParentCattle = TestData.cattle2

        // Set parent
        viewModel.setParent(actualParentCattle.id)

        val parentCattle = LiveDataTestUtil.getValue(viewModel.parentCattle)
        assertThat(actualParentCattle, isEqualTo(parentCattle))
    }

    /** Region end */

    /** Region : Edit [Cattle] */

    @Test
    fun editCattle_editingIsTrue() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattleList.random()

        // Set cattle will be called in edit cattle mode
        viewModel.setCattle(cattle.id)

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertTrue(editing)
    }

    @Test
    fun editingCattleTypeChanged_changeBreedAccordingly() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattle1

        // Set cattle
        viewModel.setCattle(cattle.id)

        // Type changed
        viewModel.type.value = AnimalType.Buffalo.displayName

        // Check breed is reset.
        assertNull(LiveDataTestUtil.getValue(viewModel.breed))

        // Type changed again back to cow.
        viewModel.type.value = AnimalType.Cow.displayName

        assertThat(
            R.array.list_breed_cow,
            isEqualTo(LiveDataTestUtil.getValue(viewModel.breedList))
        )

        // Check that we set breed as it was.
        assertThat(
            cattle.breed,
            isEqualTo(LiveDataTestUtil.getValue(viewModel.breed))
        )
    }

    @Test
    fun editingBullCattle_resetBreedAndLactation() {
        val viewModel = createAddEditCattleViewModel()

        val bullCattle = TestData.cattleBull

        viewModel.setCattle(bullCattle.id)

        assertNull(LiveDataTestUtil.getValue(viewModel.group))
        assertNull(LiveDataTestUtil.getValue(viewModel.lactation))
    }

    @Test
    fun editingCattleTypeChangedToBull_resetBreedAndLactation() {
        val viewModel = createAddEditCattleViewModel()

        val bullCattle = TestData.cattle1

        // Set the cattle
        viewModel.setCattle(bullCattle.id)

        // Change type to bull
        viewModel.type.value = AnimalType.Bull.displayName

        assertNull(LiveDataTestUtil.getValue(viewModel.group))

        assertNull(LiveDataTestUtil.getValue(viewModel.lactation))

        // Call save to check for any errors in fields
        viewModel.save()
    }

    /** Region end */
}