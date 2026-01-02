package com.ozodrukh.feature_dialogs.ui.dialogs

import androidx.lifecycle.ViewModel
import com.ozodrukh.feature_dialogs.data.MockData
import com.ozodrukh.feature_dialogs.models.Dialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DialogsViewModel : ViewModel() {

    private val _dialogs = MutableStateFlow(MockData.dialogs)
    val dialogs: StateFlow<List<Dialog>> = _dialogs.asStateFlow()

}
