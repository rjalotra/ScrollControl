package com.scrollcontrol.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrollcontrol.app.data.MotivationalQuotes
import com.scrollcontrol.app.service.InstagramReelsBlockerService
import com.scrollcontrol.app.ui.theme.PrimaryAccent
import com.scrollcontrol.app.ui.theme.SecondaryAccent

class BlockActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BLOCK_REASON = "EXTRA_BLOCK_REASON"
        const val REASON_REELS_TAB = "REELS_TAB"
        const val REASON_DM_SCROLL = "DM_SCROLL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val blockReason = intent?.getStringExtra(EXTRA_BLOCK_REASON) ?: REASON_REELS_TAB
        val isDmScroll = blockReason == REASON_DM_SCROLL

        setContent {
            // When user taps Back, safely return to Android Home screen rather than back to Reels
            BackHandler {
                goToHomeScreen()
            }

            var currentQuote by remember { mutableStateOf(MotivationalQuotes.getRandomQuote()) }

            // Full-screen dimmed backdrop that consumes all touches to block scrolling underneath
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD9090C12)) // ~85% dark frosted backdrop over Instagram
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Intercept touch so clicking outside does not dismiss or trigger reels */ }
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Centered Elevated Pop-up Card
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF161A22)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                PrimaryAccent.copy(alpha = 0.6f),
                                SecondaryAccent.copy(alpha = 0.4f),
                                Color(0x22FFFFFF)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Header Status Pill
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF262626),
                            border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryAccent)
                                )
                                Text(
                                    text = if (isDmScroll) "DM Reel Scrolling Locked" else "Reels Scrolling Locked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFA6C9),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Icon Container
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryAccent.copy(alpha = 0.25f), SecondaryAccent.copy(alpha = 0.15f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDmScroll) Icons.Default.Chat else Icons.Default.Shield,
                                contentDescription = "Blocked",
                                tint = PrimaryAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Modal Title
                        Text(
                            text = if (isDmScroll) "Back to Your Chat" else "Pause & Reflect",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isDmScroll)
                                "You watched the shared clip and started scrolling more reels. Head back to your conversation!"
                            else
                                "Reels tab engagement was detected. Take a breath instead of doomscrolling.",
                            fontSize = 13.sp,
                            color = Color(0xFFA0A6B2),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Motivational Quote Box
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF0F1218),
                            border = BorderStroke(1.dp, Color(0xFF242A36)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SecondaryAccent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = currentQuote.category.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFD4B0FF),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            currentQuote = MotivationalQuotes.getRandomQuote()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "New Quote",
                                            tint = Color(0xFF8B949E),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = PrimaryAccent.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentQuote.quote,
                                        fontSize = 15.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE6EDF3),
                                        lineHeight = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "— ${currentQuote.author}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9CA3AF),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action 1: Back to DMs / Chat (Primary Action)
                        Button(
                            onClick = { openInstagramDms() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryAccent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDmScroll) "Return to Chat" else "Back to DMs",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action 2: Back to Home Feed
                        Button(
                            onClick = { openInstagramFeed() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF262626)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Back to Feed",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action 3: Exit Instagram
                        OutlinedButton(
                            onClick = { goToHomeScreen() },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF333B4A)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exit Instagram",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openInstagramDms() {
        InstagramReelsBlockerService.suppressBlockingUntil = System.currentTimeMillis() + 4000L
        InstagramReelsBlockerService.pendingNavigateToHome = true

        try {
            val dmIntent = Intent(Intent.ACTION_VIEW, Uri.parse("instagram://direct-inbox")).apply {
                setPackage("com.instagram.android")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(dmIntent)
        } catch (e: Exception) {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.instagram.android")?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }
        finish()
    }

    private fun openInstagramFeed() {
        // Pause blocker for 5 seconds so user can browse posts on the Home feed without re-locking
        InstagramReelsBlockerService.suppressBlockingUntil = System.currentTimeMillis() + 5000L
        InstagramReelsBlockerService.pendingNavigateToHome = true
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.instagram.android")?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        } catch (e: Exception) {
            // ignore
        }
        finish()
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}
