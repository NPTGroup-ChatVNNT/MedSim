package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cases.ClinicalCaseRepository
import com.example.data.model.ClinicalCase
import com.example.data.model.DebriefResult
import com.example.data.repository.MedSimRepository
import com.example.ui.screens.*
import com.example.ui.theme.MedDeepBackground
import com.example.ui.theme.MyApplicationTheme

enum class ScreenState {
    HOME,
    ADMISSION,
    SIMULATOR,
    DEBRIEF,
    PROFILE
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: MedSimRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = MedSimRepository(applicationContext)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MedDeepBackground
                ) {
                    val userProfile by repository.getUserProfile().collectAsStateWithLifecycle(initialValue = null)
                    val pastAttempts by repository.getAllAttempts().collectAsStateWithLifecycle(initialValue = emptyList())

                    var currentScreen by remember { mutableStateOf(ScreenState.HOME) }
                    var selectedCase by remember { mutableStateOf<ClinicalCase?>(null) }
                    var currentDebrief by remember { mutableStateOf<DebriefResult?>(null) }

                    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                        when (screen) {
                            ScreenState.HOME -> {
                                HomeScreen(
                                    userProfile = userProfile,
                                    pastAttempts = pastAttempts,
                                    onCaseSelected = { clinicalCase ->
                                        selectedCase = clinicalCase
                                        currentScreen = ScreenState.ADMISSION
                                    },
                                    onOpenProfile = {
                                        currentScreen = ScreenState.PROFILE
                                    }
                                )
                            }

                            ScreenState.ADMISSION -> {
                                val activeCase = selectedCase ?: ClinicalCaseRepository.cases.first()
                                CaseAdmissionScreen(
                                    clinicalCase = activeCase,
                                    onStartCase = {
                                        currentScreen = ScreenState.SIMULATOR
                                    },
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }

                            ScreenState.SIMULATOR -> {
                                val activeCase = selectedCase ?: ClinicalCaseRepository.cases.first()
                                ClinicalSimulatorScreen(
                                    clinicalCase = activeCase,
                                    repository = repository,
                                    onCompleteCase = { debriefResult ->
                                        currentDebrief = debriefResult
                                        currentScreen = ScreenState.DEBRIEF
                                    },
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }

                            ScreenState.DEBRIEF -> {
                                val debrief = currentDebrief
                                if (debrief != null) {
                                    DebriefScreen(
                                        debrief = debrief,
                                        repository = repository,
                                        onBackToHome = {
                                            currentScreen = ScreenState.HOME
                                        },
                                        onRetryCase = {
                                            currentScreen = ScreenState.SIMULATOR
                                        }
                                    )
                                } else {
                                    currentScreen = ScreenState.HOME
                                }
                            }

                            ScreenState.PROFILE -> {
                                ProfileScreen(
                                    userProfile = userProfile,
                                    pastAttempts = pastAttempts,
                                    repository = repository,
                                    onBack = {
                                        currentScreen = ScreenState.HOME
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
