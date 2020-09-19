package com.bruno.aybar.composechallenges.backup

sealed class BackupUi {

    object RequestBackup: BackupUi()

    data class BackupInProgress(val progress: Float): BackupUi()

    object BackupCompleted: BackupUi()
}