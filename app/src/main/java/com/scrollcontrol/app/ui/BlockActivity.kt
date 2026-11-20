package com.scrollcontrol.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
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
import com.scrollcontrol.app.ui.theme.*

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
            BackHandler {
                goToHomeScreen()
            }

            var currentQuote by remember { mutableStateOf(MotivationalQuotes.getRandomQuote()) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD90A090E))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Intercept outside touches */ }
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ZenCardSurface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                SakuraPink.copy(alpha = 0.7f),
                                SecondaryAccent.copy(alpha = 0.4f),
                                Color(0x22FFFFFF)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF251F2A),
                                border = BorderStroke(1.dp, SakuraPink.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    SinglePetal(tint = SakuraPink, modifier = Modifier.size(10.dp))
                                    Text(
                                        text = if (isDmScroll) "DM Reel Scroll Locked" else "Reels Scrolling Locked",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SakuraSoft,
                                        letterSpacing = 0.4.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { goToHomeScreen() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = ZenMutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(SakuraPink.copy(alpha = 0.25f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            SakuraFlower(
                                modifier = Modifier.size(54.dp),
                                petalGradient = listOf(Color(0xFFFF9EB5), Color(0xFFFF4D74)),
                                stamenColor = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isDmScroll) "Return to Chat" else "Mindful Pause",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isDmScroll)
                                "You watched the clip and started scrolling. Keep the real conversation going!"
                            else
                                "Take a deep breath. Cherish your focus and step out of the endless feed.",
                            fontSize = 12.sp,
                            color = ZenMutedText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF100E14),
                            border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SakuraPink.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = currentQuote.category.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SakuraSoft,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                            letterSpacing = 0.4.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = { currentQuote = MotivationalQuotes.getRandomQuote() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "New Quote",
                                            tint = SakuraPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = SakuraPink.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentQuote.quote,
                                        fontSize = 13.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFF3EDF7),
                                        lineHeight = 19.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "— ${currentQuote.author}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakuraSoft,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { openInstagramDms() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SakuraDeep
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDmScroll) "Return to Chat" else "Back to DMs",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { openInstagramFeed() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF231F2A)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Back to Feed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { goToHomeScreen() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF332E3C)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Exit Instagram",
                                fontSize = 13.sp,
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
