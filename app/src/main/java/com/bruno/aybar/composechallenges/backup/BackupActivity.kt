package com.bruno.aybar.composechallenges.backup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

class BackupActivity : AppCompatActivity() {

    val viewModel = ViewModelProvider(this).get(BackupViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChallengesTheme {
                RequestBackupScreen(viewModel)
            }
        }
    }
}