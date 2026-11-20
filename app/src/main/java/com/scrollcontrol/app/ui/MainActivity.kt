package com.scrollcontrol.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.scrollcontrol.app.ui.theme.ScrollControlTheme

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Control", Icons.Filled.Shield, Icons.Outlined.Shield),
    FOCUS_DM("Focus DMs", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
    HELP("Guide", Icons.Filled.HelpOutline, Icons.Outlined.HelpOutline)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScrollControlTheme {
                val initialTab = if (intent?.getStringExtra("EXTRA_START_TAB") == "FOCUS_DM") AppTab.FOCUS_DM else AppTab.DASHBOARD
                var selectedTab by remember { mutableStateOf(initialTab) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            AppTab.values().forEach { tab ->
                                val selected = selectedTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { selectedTab = tab },
                                    label = { Text(tab.title) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        AppTab.DASHBOARD -> DashboardScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.FOCUS_DM -> FocusDmScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.HELP -> HelpScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
