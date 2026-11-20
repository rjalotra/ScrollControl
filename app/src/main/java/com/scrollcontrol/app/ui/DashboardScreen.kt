package com.scrollcontrol.app.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.scrollcontrol.app.ScrollControlApp
import com.scrollcontrol.app.data.BlockerSettings
import com.scrollcontrol.app.data.MotivationalQuotes
import com.scrollcontrol.app.service.InstagramReelsBlockerService
import com.scrollcontrol.app.ui.theme.EmeraldActive
import com.scrollcontrol.app.ui.theme.PrimaryAccent
import com.scrollcontrol.app.ui.theme.SecondaryAccent
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = ScrollControlApp.instance.preferencesRepository

    val settings by repo.settingsFlow.collectAsState(initial = BlockerSettings())
    var isAccessibilityEnabled by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityEnabled = checkAccessibilityPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        isAccessibilityEnabled = checkAccessibilityPermission(context)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val scrollState = rememberScrollState()
    var previewQuote by remember { mutableStateOf(MotivationalQuotes.getRandomQuote()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title & Tagline
        Column {
            Text(
                text = "ScrollControl",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Truecaller-style floating blocker for Instagram Reels",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Permission Card (Only shown if permission is missing)
        if (!isAccessibilityEnabled) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Accessibility Permission Required",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Text(
                        text = "To detect the Reels pill and 'Send to chat' buttons and display the floating blocker overlay, ScrollControl requires Accessibility permission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enable in Settings")
                    }
                }
            }
        }

        // Master Blocker Status Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (settings.isBlockerEnabled && isAccessibilityEnabled) EmeraldActive
                                    else Color.Gray
                                )
                        )
                        Text(
                            text = if (settings.isBlockerEnabled && isAccessibilityEnabled) "Floating Blocker Active"
                            else if (!isAccessibilityEnabled) "Waiting for Permission"
                            else "Blocker Paused",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (settings.isBlockerEnabled) "Monitoring Reels tab & DM scrolling with floating overlay" else "Interception is temporarily paused",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = settings.isBlockerEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch { repo.setBlockerEnabled(enabled) }
                    }
                )
            }
        }

        // Stats Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Focus Impact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Outlined.Block,
                        value = "${settings.reelsBlockedCount}",
                        label = "Reels Blocked"
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                    )
                    StatItem(
                        icon = Icons.Outlined.Timer,
                        value = "~${(settings.reelsBlockedCount * 1.5).toInt()}m",
                        label = "Time Saved"
                    )
                }
            }
        }

        // Motivational Quote Pop-up Preview Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Floating Overlay Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { previewQuote = MotivationalQuotes.getRandomQuote() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Shuffle",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "“${previewQuote.quote}”",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "— ${previewQuote.author} (${previewQuote.category})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val service = InstagramReelsBlockerService.instance
                            if (service != null) {
                                service.showScrollingLock(BlockActivity.REASON_REELS_TAB)
                            } else {
                                val testIntent = Intent(context, BlockActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    putExtra(BlockActivity.EXTRA_BLOCK_REASON, BlockActivity.REASON_REELS_TAB)
                                }
                                context.startActivity(testIntent)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Reels Tab", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            val service = InstagramReelsBlockerService.instance
                            if (service != null) {
                                service.showScrollingLock(BlockActivity.REASON_DM_SCROLL)
                            } else {
                                val testIntent = Intent(context, BlockActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    putExtra(BlockActivity.EXTRA_BLOCK_REASON, BlockActivity.REASON_DM_SCROLL)
                                }
                                context.startActivity(testIntent)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test DM Scroll", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Feature Protection Rules
        Text(
            text = "Protection Rules",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        RuleToggleCard(
            title = "Block Reels Scrolling in DMs",
            description = "Detects the 'Send to chat' button and displays the floating window to block scrolling beyond a friend's shared video",
            icon = Icons.Outlined.Chat,
            checked = settings.blockNextReelScrollInDm,
            onCheckedChange = { scope.launch { repo.setBlockNextReelScrollInDm(it) } }
        )

        RuleToggleCard(
            title = "Allow Single Reel in DMs",
            description = "Lets you watch the specific clip your friend sent without triggering the blocker",
            icon = Icons.Outlined.PlayCircle,
            checked = settings.allowReelsInDm,
            onCheckedChange = { scope.launch { repo.setAllowReelsInDm(it) } }
        )

        RuleToggleCard(
            title = "Block FYP Feed (Direct-Only Mode)",
            description = "Keeps you in your Direct Messages and redirects away from the Home feed",
            icon = Icons.Outlined.Home,
            checked = settings.blockHomeFeed,
            onCheckedChange = { scope.launch { repo.setBlockHomeFeed(it) } }
        )

        RuleToggleCard(
            title = "Block Explore / Search Grid",
            description = "Prevents browsing the algorithmic explore page",
            icon = Icons.Outlined.Explore,
            checked = settings.blockExploreTab,
            onCheckedChange = { scope.launch { repo.setBlockExploreTab(it) } }
        )

        // Quick Direct Launch Action
        Button(
            onClick = {
                InstagramReelsBlockerService.suppressBlockingUntil = System.currentTimeMillis() + 5000L
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.instagram.android")?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/direct/inbox/")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(webIntent)
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "DMs")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Instagram DMs Directly",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Detection Technical Info Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "How the Floating Overlay Works",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Text(
                    text = "• Truecaller-style Window: Instead of switching tasks or launching the ScrollControl app, an overlay window floats directly above Instagram.\n• Touch Blocking: The overlay absorbs all touch gestures, making vertical scrolling impossible.\n• Redirects: Tapping 'Return to Chat' or 'Back to Feed' automatically navigates you away from Reels and dismisses the window.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        // Reset Stats
        if (settings.reelsBlockedCount > 0) {
            TextButton(
                onClick = { scope.launch { repo.resetStats() } },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Blocked Stats", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RuleToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

private fun checkAccessibilityPermission(context: Context): Boolean {
    if (InstagramReelsBlockerService.isServiceRunning) return true

    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? android.view.accessibility.AccessibilityManager
    val enabledServices = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK) ?: emptyList()
    val expectedPackage = context.packageName
    for (service in enabledServices) {
        if (service.resolveInfo?.serviceInfo?.packageName == expectedPackage) {
            return true
        }
    }

    try {
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (!enabledServicesSetting.isNullOrEmpty()) {
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                val enabledService = ComponentName.unflattenFromString(componentNameString)
                if (enabledService != null && enabledService.packageName == expectedPackage) {
                    return true
                }
            }
        }
    } catch (e: Exception) {
        // ignore
    }
    return false
}
