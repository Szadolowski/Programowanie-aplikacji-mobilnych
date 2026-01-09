package com.example.steptrack.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepViewModel : ViewModel() {
    private val _isTracking = MutableLiveData<Boolean>(false)
    val isTracking: LiveData<Boolean> = _isTracking

    private val _stepCount = MutableLiveData<Int>(0)
    val stepCount: LiveData<Int> = _stepCount

    fun toggleTracking() {
        _isTracking.value = !(_isTracking.value ?: false)
    }

    fun setTracking(tracking: Boolean) {
        _isTracking.value = tracking
    }

    fun setSteps(count: Int) {
        _stepCount.value = count
    }

    fun addStep() {
        _stepCount.value = (_stepCount.value ?: 0) + 1
    }

    fun resetSteps() {
        _stepCount.value = 0
    }
}
