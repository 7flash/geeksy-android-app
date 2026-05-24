package com.example.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// Let's define the color palette for our Galaxy theme
val GalaxyDark = Color(0xFF03030F)       // Absolute space backdrop
val GalaxyMidnight = Color(0xFF0C0A24)   // Outer space indigo/navy
val GalaxyDeepPurple = Color(0xFF1E1035) // Deep nebula purple
val NeonCyan = Color(0xFF00F2FE)         // Galactic cyber neon
val NeonMagenta = Color(0xFFEC008C)      // Plasma fire pink
val CosmicPurple = Color(0xFF8A2BE2)     // Bright starlight violet
val HyperSpaceGreen = Color(0xFF39FF14)  // AI online indicator
val StellarMutedGray = Color(0xFF707191) // Space dust gray

/**
 * An animated star structure for twinkling stars canvas
 */
private data class Star(
    val xPercent: Float,
    val yPercent: Float,
    val size: Float,
    val speedFactor: Float,
    val baseAlpha: Float
)

/**
 * TwinklingStarsBackground draws a highly immersive moving stardust background.
 * Uses Compose canvas and infinite transitions for a lively galaxy vibe.
 */
@Composable
fun TwinklingStarsBackground(modifier: Modifier = Modifier) {
    // Generate static list of stars once
    val stars = remember {
        List(110) {
            Star(
                xPercent = Random.nextFloat(),
                yPercent = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 1f,
                speedFactor = Random.nextFloat() * 0.5f + 0.5f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.4f
            )
        }
    }

    // Infinite animation values
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy_nebula")
    val twinkleFactor by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Math.PI.toFloat() * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    // Smooth nebula sweep background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GalaxyDark,
                        GalaxyMidnight,
                        GalaxyDeepPurple,
                        GalaxyDark
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw nebulous glows (faint radial halos)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CosmicPurple.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.3f),
                    radius = size.width * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.7f),
                    radius = size.width * 0.7f
                )
            )

            // Draw each twinkling star
            stars.forEach { star ->
                val x = star.xPercent * size.width
                val y = star.yPercent * size.height
                
                // Modulate opacity using sine wave with custom star speed
                val alphaMultiplier = kotlin.math.sin(twinkleFactor * star.speedFactor).coerceIn(-1.0f, 1.0f) * 0.3f + 0.7f
                val finalAlpha = (star.baseAlpha * alphaMultiplier).coerceIn(0.1f, 1f)

                drawCircle(
                    color = Color.White.copy(alpha = finalAlpha),
                    radius = star.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

/**
 * Animated glowing orb representing Geeksy's live cosmic presence
 */
@Composable
fun CosmicOrb(
    isThinking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    
    // Scale pulsation based on whether it is thinking
    val duration = if (isThinking) 1300 else 3000
    val maxPulse = if (isThinking) 1.25f else 1.08f
    val minPulse = if (isThinking) 0.95f else 0.96f

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = minPulse,
        targetValue = maxPulse,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(100.dp)
    ) {
        // Blur neon backing halo
        Box(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.5f), NeonMagenta.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .blur(16.dp)
        )

        // Pulsing active core
        Box(
            modifier = Modifier
                .size((56 * pulseScale).dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonCyan, NeonMagenta, CosmicPurple, NeonCyan)
                    ),
                    shape = CircleShape
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GalaxyMidnight.copy(alpha = 0.9f),
                            GalaxyDark.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            // Internal galactic core (mini orb)
            Canvas(modifier = Modifier.size(24.dp)) {
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = if (isThinking) listOf(NeonMagenta, CosmicPurple) else listOf(NeonCyan, CosmicPurple),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                )
            }
        }
    }
}

/**
 * Draws a futuristic neon outline card for galaxy messages
 */
@Composable
fun FuturisticNeonCard(
    modifier: Modifier = Modifier,
    borderColors: List<Color> = listOf(NeonCyan, CosmicPurple),
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GalaxyMidnight.copy(alpha = 0.7f),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    // Frame gradient outline
                    val strokeWidth = borderWidth.toPx()
                    val brush = Brush.linearGradient(
                        colors = borderColors,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                    drawRoundRect(
                        brush = brush,
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                        style = Stroke(width = strokeWidth)
                    )
                }
                .padding(2.dp) // Offset content slightly from outer border line
        ) {
            content()
        }
    }
}

/**
 * MarkdownOrTextBubble processes simple markdown formatting strings
 * (specifically strong markdown "**bold**", code blocks "```code```", and bullet points)
 * and formats them beautifully into an annotated text block.
 */
@Composable
fun MarkdownOrTextBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    val formattedText = remember(text) {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { lineIndex, line ->
                // Apply bullet points for lines starting with "* " or "- "
                val isBullet = line.trimStart().startsWith("* ") || line.trimStart().startsWith("- ")
                var currentLine = line
                if (isBullet) {
                    withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.Bold)) {
                        append("  ➢  ")
                    }
                    currentLine = line.trimStart().substring(2)
                }

                // Check for block code tags
                if (currentLine.contains("`")) {
                    val textParts = currentLine.split("`")
                    textParts.forEachIndexed { index, part ->
                        if (index % 2 == 1) { // Odd indexes are block codes
                            withStyle(
                                SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonCyan,
                                    background = GalaxyMidnight,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(part)
                            }
                        } else {
                            // Apply double asterisks bold within code parts
                            appendBoldFormattedText(part)
                        }
                    }
                } else {
                    appendBoldFormattedText(currentLine)
                }

                if (lineIndex < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    Text(
        text = formattedText,
        color = Color.White,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        modifier = modifier
    )
}

/**
 * Helper to process strong **bold** styling
 */
private fun androidx.compose.ui.text.AnnotatedString.Builder.appendBoldFormattedText(fragment: String) {
    if (fragment.contains("**")) {
        val boldParts = fragment.split("**")
        boldParts.forEachIndexed { i, boldPart ->
            if (i % 2 == 1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append(boldPart)
                }
            } else {
                append(boldPart)
            }
        }
    } else {
        append(fragment)
    }
}

/**
 * Animated flashing indicator for message streaming / placeholder
 */
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "indicator_blink")
    
    val dotCount = 3
    val dots = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(offsetMillis = index * 150)
            ),
            label = "dot_blink_$index"
        )
    }

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { dotAlpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        brush = Brush.linearGradient(colors = listOf(NeonCyan, CosmicPurple)),
                        shape = CircleShape
                    )
                    .blur(if (dotAlpha.value > 0.7f) 1.dp else 0.dp) // Neon glowing halo
                    .graphicsLayerAlpha(dotAlpha.value)
            )
        }
    }
}

// Simple modifier helper to set layout alpha on a Box elegantly
private fun Modifier.graphicsLayerAlpha(alpha: Float): Modifier = this.graphicsLayer {
    this.alpha = alpha
}
