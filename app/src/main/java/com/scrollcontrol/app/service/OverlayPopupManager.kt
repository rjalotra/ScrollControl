package com.scrollcontrol.app.service

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.scrollcontrol.app.data.MotivationalQuotes
import com.scrollcontrol.app.ui.BlockActivity
import com.scrollcontrol.app.ui.theme.PrimaryAccent
import com.scrollcontrol.app.ui.theme.SecondaryAccent

/**
 * Custom LifecycleOwner required to host Jetpack Compose within a WindowManager Overlay Service.
 */
class OverlayLifecycleOwner : SavedStateRegistryOwner, ViewModelStoreOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    fun onCreate() {
        savedStateRegistryController.performRestore(Bundle())
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }
}

/**
 * Truecaller-style floating overlay window manager that draws directly on top of Instagram
 * without launching the ScrollControl application or switching tasks.
 *
 * It intercepts all touch events to block scrolling, and requests audio focus to
 * immediately pause Instagram's reel video and audio playback.
 */
class OverlayPopupManager(private val context: Context) {

    companion object {
        private const val TAG = "OverlayPopupManager"
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private var overlayView: ComposeView? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null

    val isShowing: Boolean
        get() = overlayView != null

    fun show(
        reason: String,
        onGoToDms: () -> Unit,
        onGoToFeed: () -> Unit,
        onGoHome: () -> Unit
    ) {
        if (isShowing) return // Already showing, prevent duplicate triggers and flashing

        // Pause Instagram reel video and audio immediately via audio focus
        pauseInstagramPlayback()

        val isDmScroll = reason == BlockActivity.REASON_DM_SCROLL

        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        // Window parameters: Modal window with hardware dimming.
        // Captures ALL touch gestures so scrolling Instagram is physically blocked.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            windowType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            dimAmount = 0.72f
        }

        val owner = OverlayLifecycleOwner()
        owner.onCreate()
        lifecycleOwner = owner

        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setViewTreeViewModelStoreOwner(owner)

            setContent {
                var currentQuote by remember { mutableStateOf(MotivationalQuotes.getRandomQuote()) }

                // Full-screen backdrop container that consumes all touches to block scrolling underneath
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* Intercept all touches outside the card so reels cannot be scrolled */ }
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Truecaller-style compact floating card
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161A22)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    PrimaryAccent.copy(alpha = 0.8f),
                                    SecondaryAccent.copy(alpha = 0.5f),
                                    Color(0x33FFFFFF)
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
                                .padding(20.dp)
                        ) {
                            // Top Bar: Status Pill & Dismiss Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF242730),
                                    border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryAccent)
                                        )
                                        Text(
                                            text = if (isDmScroll) "DM Reel Scroll Locked" else "Reels Scrolling Locked",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFA6C9),
                                            letterSpacing = 0.4.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        hide()
                                        onGoHome()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color(0xFF8B949E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Compact Icon Container
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
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
                                    contentDescription = null,
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Title
                            Text(
                                text = if (isDmScroll) "Back to Your Chat" else "Pause & Reflect",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (isDmScroll)
                                    "You began scrolling reels from a chat. Don't fall into the feed — head back to your friends!"
                                else
                                    "Reels engagement detected. Protect your attention.",
                                fontSize = 12.sp,
                                color = Color(0xFFA0A6B2),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Motivational Quote Box
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0F1218),
                                border = BorderStroke(1.dp, Color(0xFF242A36)),
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
                                            color = SecondaryAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = currentQuote.category.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFD4B0FF),
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                                letterSpacing = 0.4.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                currentQuote = MotivationalQuotes.getRandomQuote()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "New Quote",
                                                tint = Color(0xFF8B949E),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = null,
                                            tint = PrimaryAccent.copy(alpha = 0.8f),
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
                                            color = Color(0xFFE6EDF3),
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "— ${currentQuote.author}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9CA3AF),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action 1: Back to DMs (Primary Action)
                            Button(
                                onClick = {
                                    hide()
                                    onGoToDms()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryAccent
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

                            // Action 2: Back to Home Feed
                            Button(
                                onClick = {
                                    hide()
                                    onGoToFeed()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF262626)
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

                            // Action 3: Exit Instagram
                            OutlinedButton(
                                onClick = {
                                    hide()
                                    onGoHome()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF333B4A)),
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

        try {
            windowManager.addView(composeView, params)
            overlayView = composeView
            Log.d(TAG, "Overlay window added successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding overlay view to WindowManager", e)
            owner.onDestroy()
            lifecycleOwner = null
            overlayView = null
            resumePlayback()
        }
    }

    fun hide() {
        val view = overlayView ?: return
        try {
            windowManager.removeView(view)
            Log.d(TAG, "Overlay window removed.")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay view", e)
        } finally {
            overlayView = null
            lifecycleOwner?.onDestroy()
            lifecycleOwner = null
            resumePlayback()
        }
    }

    /**
     * Pauses Instagram's reel player by requesting transient audio focus.
     * Instagram's video playback engine (ExoPlayer) automatically pauses audio and video.
     */
    private fun pauseInstagramPlayback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setOnAudioFocusChangeListener { /* handle if needed */ }
                    .build()

                audioFocusRequest?.let { audioManager?.requestAudioFocus(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
            Log.d(TAG, "Requested audio focus to pause Instagram playback.")
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting audio focus", e)
        }
    }

    private fun resumePlayback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error abandoning audio focus", e)
        }
    }
}
