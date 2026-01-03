package com.ozodrukh.feature.chat.ui

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.gradientTint(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Creates an offscreen buffer
    .drawWithCache {
        onDrawWithContent {
            drawContent() // Draws the icon (as a black mask)
            drawRect(     // Draws the gradient "over" it, keeping only the icon shape
                brush = brush,
                blendMode = BlendMode.SrcIn
            )
        }
    }

@Composable
fun GlowingIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    brush: Brush,
    glowColor: Color,
    glowRadius: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val painter = rememberVectorPainter(image = imageVector)
        val density = LocalDensity.current

        val blurPaint = remember(density, glowRadius) {
            Paint().apply {
                asFrameworkPaint().maskFilter = BlurMaskFilter(
                    with(density) { glowRadius.toPx() },
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        }

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawIntoCanvas { canvas ->
                canvas.saveLayer(
                    Rect(0f, 0f, size.width, size.height),
                    blurPaint
                )

                with(painter) {
                    draw(
                        size = this@Canvas.size,
                        colorFilter = ColorFilter.tint(glowColor)
                    )
                }

                canvas.restore()
            }
        }
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.gradientTint(brush),
            tint = Color.Black // gets overwritten by gradientTint
        )
    }
}

@Composable
fun AiToggleButton(
    isAiSuggestionsEnabled: Boolean,
    shouldGlow: Boolean,
    onToggleAiSuggestions: () -> Unit
) {
    val activeGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2196F3), // Blue
            Color(0xFF9C27B0)  // Purple
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val disabledColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    IconButton(onClick = onToggleAiSuggestions) {
        if (shouldGlow) {
            GlowingIcon(
                imageVector = Icons.Default.Star,
                contentDescription = "Disable Suggestions",
                brush = activeGradient,
                glowColor = Color(0xFF9C27B0),
                modifier = Modifier.size(24.dp).alpha(glowAlpha)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Enable Suggestions",
                tint = if (isAiSuggestionsEnabled) Color(0xFF9C27B0) else disabledColor
            )
        }
    }
}