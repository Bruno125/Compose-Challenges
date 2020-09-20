package com.bruno.aybar.composechallenges.backup

sealed class BackupUi {

    data class RequestBackup(val lastBackup: String): BackupUi()

    data class BackupInProgress(val progress: Float): BackupUi()

    object BackupCompleted: BackupUi()
}