package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ads.AdMobManager
import com.example.ui.components.AmbientGlowBackground
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import com.example.ui.components.TestAdDialog
import com.example.ui.dialogs.AddAppointmentDialog
import com.example.ui.dialogs.AddTaskDialog
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.rewards.FloatingRewardPill
import com.example.ui.rewards.RewardGameDialog
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.MonetizationScreen
import com.example.ui.screens.TasksScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BusinessViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showSimulatedInterstitial by AdMobManager.showSimulatedInterstitial.collectAsStateWithLifecycle()
    var isRewardGameDialogOpen by remember { mutableStateOf(false) }

    // Back handling: if on secondary tab, return to Dashboard first
    BackHandler(enabled = uiState.selectedTab != 0) {
        viewModel.selectTab(0)
    }

    // Force RTL for natural Arabic experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AmbientGlowBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (uiState.selectedTab) {
                                    0 -> stringResource(R.string.app_name)
                                    1 -> "إدارة وتنظيم المهام"
                                    2 -> "جدولة مواعيد العمل"
                                    3 -> "الأرباح والمالية"
                                    4 -> "أرباح إعلانات AdMob"
                                    else -> stringResource(R.string.app_name)
                                },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        },
                        actions = {
                            // Floating Reward Game Button in Top Bar
                            FloatingRewardPill(
                                onClick = { isRewardGameDialogOpen = true },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0F1A15).copy(alpha = 0.85f),
                            titleContentColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_nav_bar"),
                        containerColor = Color(0xFF0D1713).copy(alpha = 0.95f),
                        tonalElevation = 12.dp
                    ) {
                        NavigationBarItem(
                            selected = uiState.selectedTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "الرئيسية") },
                            label = { Text("الرئيسية") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = NeonEmerald,
                                selectedTextColor = NeonEmerald,
                                unselectedIconColor = Color(0xFF8B9D93),
                                unselectedTextColor = Color(0xFF8B9D93)
                            ),
                            modifier = Modifier.testTag("nav_tab_dashboard")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            icon = { Icon(Icons.Default.Assignment, contentDescription = "المهام") },
                            label = { Text("المهام") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = NeonEmerald,
                                selectedTextColor = NeonEmerald,
                                unselectedIconColor = Color(0xFF8B9D93),
                                unselectedTextColor = Color(0xFF8B9D93)
                            ),
                            modifier = Modifier.testTag("nav_tab_tasks")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 2,
                            onClick = { viewModel.selectTab(2) },
                            icon = { Icon(Icons.Default.Event, contentDescription = "المواعيد") },
                            label = { Text("المواعيد") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = NeonEmerald,
                                selectedTextColor = NeonEmerald,
                                unselectedIconColor = Color(0xFF8B9D93),
                                unselectedTextColor = Color(0xFF8B9D93)
                            ),
                            modifier = Modifier.testTag("nav_tab_appointments")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 3,
                            onClick = { viewModel.selectTab(3) },
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "الأرباح") },
                            label = { Text("الأرباح") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = NeonEmerald,
                                selectedTextColor = NeonEmerald,
                                unselectedIconColor = Color(0xFF8B9D93),
                                unselectedTextColor = Color(0xFF8B9D93)
                            ),
                            modifier = Modifier.testTag("nav_tab_finance")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 4,
                            onClick = { viewModel.selectTab(4) },
                            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "AdMob") },
                            label = { Text("AdMob") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = NeonGold,
                                selectedTextColor = NeonGold,
                                unselectedIconColor = Color(0xFF8B9D93),
                                unselectedTextColor = Color(0xFF8B9D93)
                            ),
                            modifier = Modifier.testTag("nav_tab_admob")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (uiState.selectedTab) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToTasks = { viewModel.selectTab(1) },
                            onNavigateToAppointments = { viewModel.selectTab(2) },
                            onNavigateToFinance = { viewModel.selectTab(3) },
                            onNavigateToMonetization = { viewModel.selectTab(4) }
                        )
                        1 -> TasksScreen(viewModel = viewModel)
                        2 -> AppointmentsScreen(viewModel = viewModel)
                        3 -> FinanceScreen(viewModel = viewModel)
                        4 -> MonetizationScreen(viewModel = viewModel)
                    }
                }
            }

            // Reward Game Dialog
            if (isRewardGameDialogOpen) {
                RewardGameDialog(
                    onDismiss = { isRewardGameDialogOpen = false }
                )
            }

            // Active Input Dialogs
            if (uiState.isAddTaskDialogOpen) {
                AddTaskDialog(
                    onDismiss = { viewModel.setAddTaskDialog(false) },
                    onConfirm = { title, desc, priority, dueDate, client, act ->
                        viewModel.addTask(title, desc, priority, dueDate, client, act)
                    }
                )
            }

            if (uiState.isAddAppointmentDialogOpen) {
                AddAppointmentDialog(
                    onDismiss = { viewModel.setAddAppointmentDialog(false) },
                    onConfirm = { title, client, dateTime, duration, loc, type, deal, notes, act ->
                        viewModel.addAppointment(title, client, dateTime, duration, loc, type, deal, notes, act)
                    }
                )
            }

            if (uiState.isAddTransactionDialogOpen) {
                AddTransactionDialog(
                    onDismiss = { viewModel.setAddTransactionDialog(false) },
                    onConfirm = { title, amount, type, category, notes ->
                        viewModel.addTransaction(title, amount, type, category, notes)
                    }
                )
            }

            // Guaranteed Test Ad Dialog (shows immediately when testing ads)
            if (showSimulatedInterstitial) {
                TestAdDialog(
                    onDismiss = { AdMobManager.dismissSimulatedInterstitial() }
                )
            }
        }
    }
}
