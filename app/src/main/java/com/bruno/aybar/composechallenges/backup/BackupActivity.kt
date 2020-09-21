package com.bruno.aybar.composechallenges.backup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import com.bruno.aybar.composechallenges.ui.ComposeChallengesTheme

class BackupActivity : AppCompatActivity() {

    lateinit var viewModel: BackupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BackupViewModel::class.java)
        setContent {
            ComposeChallengesTheme {
                BackupScreen(viewModel)
            }
        }
    }
}