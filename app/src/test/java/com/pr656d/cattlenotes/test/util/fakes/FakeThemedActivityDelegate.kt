package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.model.Theme

class FakeThemedActivityDelegate(
    override val theme: LiveData<Theme> = MutableLiveData<Theme>().apply {
        setValue(Theme.SYSTEM)
    },
    override val currentTheme: Theme = Theme.SYSTEM
): ThemedActivityDelegate