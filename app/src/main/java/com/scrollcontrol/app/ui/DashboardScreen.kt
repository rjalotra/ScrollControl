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
import androidx.compose.ui.graphics.Brush
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
import com.scrollcontrol.app.ui.theme.*
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
            .background(ZenDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Japanese Floral Minimalist Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ScrollControl",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SakuraPink.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SakuraPink.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "桜 ZEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakuraPink,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mindful Instagram protection & calm focus",
                    fontSize = 13.sp,
                    color = ZenMutedText
                )
            }

            // Close-up Japanese Sakura Blossom
            SakuraFlower(
                modifier = Modifier.size(46.dp)
            )
        }

        // Accessibility Permission Notice (if needed)
        if (!isAccessibilityEnabled) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D161C)
                ),
                border = BorderStroke(1.dp, SakuraDeep.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp),
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
                        SinglePetal(tint = SakuraDeep)
                        Text(
                            text = "Accessibility Permission Required",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFFFCCD5)
                        )
                    }
                    Text(
                        text = "To detect the Reels pill and 'Send to chat' buttons and display the floating blocker overlay, ScrollControl requires Accessibility permission.",
                        fontSize = 12.sp,
                        color = Color(0xFFE2B6C0),
                        lineHeight = 17.sp
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SakuraDeep
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enable in Settings", fontSize = 12.sp)
                    }
                }
            }
        }

        // ==========================================
        // 1. MAIN OPTION AT THE VERY TOP
        // ==========================================
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ZenCardSurface
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (settings.isBlockerEnabled && isAccessibilityEnabled) SakuraPink.copy(alpha = 0.6f) else Color(0x22FFFFFF),
                        Color(0x11FFFFFF)
                    )
                )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Visual state icon with glowing sakura or soft slate
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (settings.isBlockerEnabled && isAccessibilityEnabled)
                                    Brush.radialGradient(listOf(SakuraPink.copy(alpha = 0.3f), Color.Transparent))
                                else
                                    Color(0xFF23202A)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        SakuraFlower(
                            modifier = Modifier.size(34.dp),
                            petalGradient = if (settings.isBlockerEnabled && isAccessibilityEnabled)
                                listOf(Color(0xFFFF94AF), Color(0xFFFF5277))
                            else
                                listOf(Color(0xFF706B78), Color(0xFF4C4753)),
                            stamenColor = if (settings.isBlockerEnabled && isAccessibilityEnabled) GoldAccent else Color.Gray
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (settings.isBlockerEnabled && isAccessibilityEnabled) ZenActiveGreen
                                        else if (!isAccessibilityEnabled) GoldAccent
                                        else Color.Gray
                                    )
                            )
                            Text(
                                text = if (settings.isBlockerEnabled && isAccessibilityEnabled) "Protection Active"
                                else if (!isAccessibilityEnabled) "Awaiting Permission"
                                else "Protection Paused",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (settings.isBlockerEnabled) "Monitoring Reels tab & DM scrolling" else "Interception temporarily off",
                            fontSize = 12.sp,
                            color = ZenMutedText
                        )
                    }
                }

                Switch(
                    checked = settings.isBlockerEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch { repo.setBlockerEnabled(enabled) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SakuraPink,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF2C2834)
                    )
                )
            }
        }

        // ==========================================
        // 2. REST OF OPTIONS GROUPED TOGETHER BELOW MAIN
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SinglePetal(tint = SakuraPink, modifier = Modifier.size(14.dp))
                Text(
                    text = "Protection Rules",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Grouped Options Container Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ZenCardSurface
                ),
                border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Option 1: DM Reel Scroll Blocker
                    RuleRowItem(
                        title = "Block Reels Scrolling in DMs",
                        description = "Catches the 'Send to chat' pill and stops scrolling beyond a shared clip",
                        icon = Icons.Outlined.Chat,
                        checked = settings.blockNextReelScrollInDm,
                        onCheckedChange = { scope.launch { repo.setBlockNextReelScrollInDm(it) } }
                    )

                    HorizontalDivider(
                        color = Color(0x12FFFFFF),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Option 2: Allow Single Reel in DMs
                    RuleRowItem(
                        title = "Allow Single Reel in DMs",
                        description = "Permits watching the single video a friend shared without locking",
                        icon = Icons.Outlined.PlayCircle,
                        checked = settings.allowReelsInDm,
                        onCheckedChange = { scope.launch { repo.setAllowReelsInDm(it) } }
                    )

                    HorizontalDivider(
                        color = Color(0x12FFFFFF),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Option 3: Block FYP Feed
                    RuleRowItem(
                        title = "Block FYP Feed (Direct-Only)",
                        description = "Keeps you in your Direct Messages and redirects away from Home feed",
                        icon = Icons.Outlined.Home,
                        checked = settings.blockHomeFeed,
                        onCheckedChange = { scope.launch { repo.setBlockHomeFeed(it) } }
                    )

                    HorizontalDivider(
                        color = Color(0x12FFFFFF),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Option 4: Block Explore Grid
                    RuleRowItem(
                        title = "Block Explore / Search Grid",
                        description = "Prevents browsing the algorithmic explore grid",
                        icon = Icons.Outlined.Explore,
                        checked = settings.blockExploreTab,
                        onCheckedChange = { scope.launch { repo.setBlockExploreTab(it) } }
                    )
                }
            }
        }

        // ==========================================
        // 3. REST OF THE DETAILS BELOW THE OPTIONS
        // ==========================================

        // Focus Impact Stats
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ZenCardSurface
            ),
            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Focus Impact",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
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
                            .height(38.dp)
                            .width(1.dp),
                        color = Color(0x1FFFFFFF)
                    )
                    StatItem(
                        icon = Icons.Outlined.Timer,
                        value = "~${(settings.reelsBlockedCount * 1.5).toInt()}m",
                        label = "Time Saved"
                    )
                }
            }
        }

        // Floating Overlay Window Preview Card with Sakura Theme
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ZenCardSurface
            ),
            border = BorderStroke(1.dp, SakuraPink.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SinglePetal(tint = SakuraPink, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Motivational Quote Preview",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { previewQuote = MotivationalQuotes.getRandomQuote() },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Shuffle",
                            tint = SakuraPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF100E14),
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "“${previewQuote.quote}”",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFF0EBF5),
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "— ${previewQuote.author} (${previewQuote.category})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SakuraSoft
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
                        border = BorderStroke(1.dp, SakuraPink.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraSoft),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Reels Tab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
                        border = BorderStroke(1.dp, SecondaryAccent.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2B6FF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test DM Scroll", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

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
                containerColor = SakuraDeep
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "DMs", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Instagram DMs Directly",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }

        // Technical How It Works Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = ZenCardSurface.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, Color(0x10FFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "How ScrollControl Operates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "• Reels Tab Pill: Catches the active rounded capsule behind the Reels icon in the bottom bar.\n• DM Reel Scrolling: Intercepts the floating 'Send to chat' pill with your friend's avatar the moment you scroll past a shared video.\n• Floating Overlay: Appears directly over Instagram, mutes/pauses the video via audio focus, and captures touch to block scrolling.",
                    fontSize = 12.sp,
                    color = ZenMutedText,
                    lineHeight = 17.sp
                )
            }
        }

        // Reset Stats
        if (settings.reelsBlockedCount > 0) {
            TextButton(
                onClick = { scope.launch { repo.resetStats() } },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Blocked Stats", color = ZenMutedText, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RuleRowItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) SakuraPink else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = ZenMutedText,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SakuraPink,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF2C2834)
            )
        )
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SakuraPink,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = ZenMutedText
        )
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
