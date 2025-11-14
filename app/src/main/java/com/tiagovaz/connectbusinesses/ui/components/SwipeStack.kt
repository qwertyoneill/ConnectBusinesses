package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun <T> SwipeStack(
    items: List<T>,
    onSwipeLeft: (T) -> Unit,
    onSwipeRight: (T) -> Unit,
    cardContent: @Composable (T, Boolean, Color, Color) -> Unit
) {
    var index by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    var screenWidthPx by remember { mutableStateOf(0f) }
    val swipeThreshold by derivedStateOf { screenWidthPx * 0.30f }

    // ✨ Animações para o próximo card (fade + escala + subida leve)
    var animateNext by remember { mutableStateOf(false) }
    val nextAlpha by animateFloatAsState(
        targetValue = if (animateNext) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = ""
    )
    val nextTranslationY by animateDpAsState(
        targetValue = if (animateNext) 0.dp else 40.dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = ""
    )
    val nextScale by animateFloatAsState(
        targetValue = if (animateNext) 1f else 0.92f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = ""
    )

    if (index < items.size) {
        val currentItem = items[index]
        val nextItem = items.getOrNull(index + 1)
        val rotation by derivedStateOf { offsetX.value / 40f } // leque suave 🎴

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    screenWidthPx = coords.size.width.toFloat()
                },
            contentAlignment = Alignment.Center
        ) {
            // 🎞️ Próximo card no fundo (entra subtilmente)
            if (nextItem != null) {
                val translationYNextPx = with(LocalDensity.current) { nextTranslationY.toPx() }

                Box(
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = nextAlpha,
                            scaleX = nextScale,
                            scaleY = nextScale,
                            translationY = translationYNextPx
                        )
                ) {
                    cardContent(nextItem, false, Color.Black.copy(alpha = 0.1f), Color.Transparent)
                }
            }

            // 💫 Card ativo (com leve rotação e sombra dinâmica)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                when {
                                    offsetX.value > swipeThreshold -> {
                                        onSwipeRight(currentItem)
                                        animateNext = false
                                        index++
                                        scope.launch {
                                            offsetX.snapTo(0f)
                                            animateNext = true
                                        }
                                    }

                                    offsetX.value < -swipeThreshold -> {
                                        onSwipeLeft(currentItem)
                                        animateNext = false
                                        index++
                                        scope.launch {
                                            offsetX.snapTo(0f)
                                            animateNext = true
                                        }
                                    }

                                    else -> {
                                        scope.launch {
                                            offsetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                }
                            }
                        )
                    }
                    .graphicsLayer(
                        translationX = offsetX.value,
                        rotationZ = rotation,
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    )
            ) {
                val shadowColor = when {
                    offsetX.value > 0f -> Color(0xFF00C853).copy(alpha = 0.35f)
                    offsetX.value < 0f -> Color(0xFFD50000).copy(alpha = 0.35f)
                    else -> Color.Black.copy(alpha = 0.2f)
                }

                cardContent(currentItem, isDragging, shadowColor, shadowColor)
            }
        }
    }
}
