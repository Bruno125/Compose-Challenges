package com.bruno.aybar.composechallenges.backup

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BackupViewModel : ViewModel() {

    private val date = "28 may 2020"
    private val requestBackup = BackupUi.RequestBackup(date)

    private val _state = MutableLiveData<BackupUi>(requestBackup)
    val state: LiveData<BackupUi> get() = _state

    private var downloadingJob: Job? = null

    private var progress = 0

    fun onBackup() {
        downloadingJob = viewModelScope.launch(Dispatchers.IO) {
            startDownloading()
        }
    }

    fun onCancel() {
        downloadingJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            regressProgress()
        }
    }

    fun onOk() {
        onCancel() // for now just do the same as cancel, rollback everything
    }

    private suspend fun startDownloading() {
        progress = 0
        _state.postValue(BackupUi.BackupInProgress(progress))
        delay(1000)
        while(progress < 100) {
            delay(Random.nextLong(20, 40))
            progress += 1
            _state.postValue(BackupUi.BackupInProgress(progress))
        }
        delay(100)
        _state.postValue(BackupUi.BackupCompleted)
    }

    private suspend fun regressProgress() {
        delay(300)
        while (progress > 0) {
            delay(10)
            progress -= 1
            _state.postValue(BackupUi.BackupInProgress(progress))
        }
        delay(100)
        _state.postValue(requestBackup)
    }

}