package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import com.pr656d.model.Animal
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.IsCattleExistWithTagNumberUseCase
import com.pr656d.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.shared.domain.cattle.list.LoadObservableCattleListUseCase
import com.pr656d.shared.domain.cattle.validator.CattleTagNumberValidatorUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
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
        override fun updateCattle(cattle: Cattle): Int {
            return 1
        }

        override fun getObservableAllCattle(): LiveData<List<Cattle>> {
            val result = MutableLiveData<List<Cattle>>()
            result.postValue(TestData.cattleList)
            return result
        }

        override fun getCattleByTagNumber(tagNumber: Long): Cattle? {
            return TestData.cattleList.firstOrNull { it.tagNumber == tagNumber }
        }
    }

    private fun createAddEditCattleViewModel(
        repository: CattleRepository = cattleRepository,
        addCattleUseCase: AddCattleUseCase = AddCattleUseCase(repository),
        updateCattleUseCase: UpdateCattleUseCase = UpdateCattleUseCase(repository),
        cattleTagNumberValidatorUseCase: CattleTagNumberValidatorUseCase =
            CattleTagNumberValidatorUseCase(IsCattleExistWithTagNumberUseCase(repository)),
        getParentListUseCase: GetParentListUseCase =
            GetParentListUseCase(LoadObservableCattleListUseCase(repository))
    ): AddEditCattleViewModel {
        return AddEditCattleViewModel(
            addCattleUseCase,
            updateCattleUseCase,
            cattleTagNumberValidatorUseCase,
            getParentListUseCase
        ).apply { observeUnobserved() }
    }

    private fun AddEditCattleViewModel.observeUnobserved() {
        /**
         * Live data which have to explicitly gets observed as they were
         * observed in xml by data binding.
         */
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
    fun tagNumberIsInValidCattleAlreadyExist_setTagNumberErrorMessage() {
        val viewModel = createAddEditCattleViewModel()

        // Invalid tag number. This cattle already exist.
        viewModel.tagNumber.postValue("1")

        val tagNumberErrorMessage = LiveDataTestUtil.getValue(viewModel.tagNumberErrorMessage)
        assertNotEquals(VALID_FIELD, tagNumberErrorMessage)
    }

    @Test
    fun allFieldsValid_callAddCattleAndNavigateUp() {
        val viewModel = createAddEditCattleViewModel()

        // Valid tag number.
        viewModel.tagNumber.value = "312546"

        // Valid type
        viewModel.type.value = Animal.Type.COW.displayName

        // Valid breed
        viewModel.breed.value = Cattle.Breed.HF.displayName

        // Valid group
        viewModel.group.value = Cattle.Group.MILKING.displayName

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

        viewModel.setCattle(cattle)

        // Update tag number.
        viewModel.tagNumber.value = "312546"
        // Update type
        viewModel.type.value = Animal.Type.COW.displayName
        // Update breed
        viewModel.breed.value = Cattle.Breed.HF.displayName
        // Update group
        viewModel.group.value = Cattle.Group.MILKING.displayName
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

        viewModel.setCattle(cattle)

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

        viewModel.setCattle(cattle)

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
        val parentCattle = TestData.cattle5

        // Set cattle
        viewModel.setCattle(cattle)

        // Pick parent
        viewModel.pickParent()
        // Parent is selected
        viewModel.parentSelected(parentCattle)

        val parent = LiveDataTestUtil.getValue(viewModel.parent)
        assertThat(parentCattle.tagNumber.toString(), isEqualTo(parent))
    }

    @Test
    fun addCattleWithAllFieldsFilled_navigateUpOnSuccess() {
        val viewModel = createAddEditCattleViewModel()

        viewModel.apply {
            tagNumber.value = "123456789012"
            name.value = "Sita"
            type.value = Animal.Type.COW.displayName
            breed.value = Cattle.Breed.HF.displayName
            group.value = Cattle.Group.HEIFER.displayName
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
            type.value = Animal.Type.COW.displayName
            breed.value = Cattle.Breed.HF.displayName
            group.value = Cattle.Group.HEIFER.displayName
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
                override fun getObservableAllCattle(): LiveData<List<Cattle>> {
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
                override fun getObservableAllCattle(): LiveData<List<Cattle>> {
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

    /** Region end */

    /** Region : Edit [Cattle] */

    @Test
    fun editCattle_editingIsTrue() {
        val viewModel = createAddEditCattleViewModel()

        val cattle = TestData.cattleList.random()

        // Set cattle will be called in edit cattle mode
        viewModel.setCattle(cattle)

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertTrue(editing)
    }

    /** Region end */
}