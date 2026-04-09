package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun <T> SwipeStack(
    items: List<T>,
    onSwipeLeft: (T) -> Unit,
    onSwipeRight: (T) -> Unit,
    cardContent: @Composable (T, Boolean, Color, Color) -> Unit
) {

    var index by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }

    var screenWidthPx by remember { mutableStateOf(0f) }

    val swipeThreshold by derivedStateOf { screenWidthPx * 0.30f }

    // animação do próximo card
    var animateNext by remember { mutableStateOf(false) }

    val nextAlpha by animateFloatAsState(
        targetValue = if (animateNext) 1f else 0f,
        animationSpec = tween(400),
        label = ""
    )

    val nextScale by animateFloatAsState(
        targetValue = if (animateNext) 1f else 0.92f,
        animationSpec = tween(400),
        label = ""
    )

    val nextTranslationY by animateDpAsState(
        targetValue = if (animateNext) 0.dp else 40.dp,
        animationSpec = tween(400),
        label = ""
    )

    if (index < items.size) {

        val currentItem = items[index]

        val nextItem = items.getOrNull(index + 1)

        val rotation by derivedStateOf {
            offsetX.value / 40f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    screenWidthPx = it.size.width.toFloat()
                },
            contentAlignment = Alignment.Center
        ) {

            // card seguinte
            if (nextItem != null) {

                val translationYNextPx =
                    with(LocalDensity.current) { nextTranslationY.toPx() }

                Box(
                    modifier = Modifier.graphicsLayer(
                        alpha = nextAlpha,
                        scaleX = nextScale,
                        scaleY = nextScale,
                        translationY = translationYNextPx
                    )
                ) {

                    cardContent(
                        nextItem,
                        false,
                        Color.Black.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                }
            }

            // card principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {

                        detectDragGestures(

                            onDragStart = {
                                isDragging = true
                            },

                            onDragEnd = {

                                isDragging = false

                                when {

                                    // swipe right
                                    offsetX.value > swipeThreshold -> {

                                        scope.launch {

                                            offsetX.animateTo(
                                                targetValue = screenWidthPx * 1.5f,
                                                animationSpec = tween(250)
                                            )

                                            onSwipeRight(currentItem)

                                            offsetX.snapTo(0f)

                                            animateNext = false
                                            index++

                                            animateNext = true
                                        }
                                    }

                                    // swipe left
                                    offsetX.value < -swipeThreshold -> {

                                        scope.launch {

                                            offsetX.animateTo(
                                                targetValue = -screenWidthPx * 1.5f,
                                                animationSpec = tween(250)
                                            )

                                            onSwipeLeft(currentItem)

                                            offsetX.snapTo(0f)

                                            animateNext = false
                                            index++

                                            animateNext = true
                                        }
                                    }

                                    else -> {

                                        scope.launch {

                                            offsetX.animateTo(
                                                0f,
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

                                    offsetX.snapTo(
                                        offsetX.value + dragAmount.x
                                    )
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

                    offsetX.value > 0f ->
                        Color(0xFF00C853).copy(alpha = 0.35f)

                    offsetX.value < 0f ->
                        Color(0xFFD50000).copy(alpha = 0.35f)

                    else ->
                        Color.Black.copy(alpha = 0.2f)
                }

                cardContent(
                    currentItem,
                    isDragging,
                    shadowColor,
                    shadowColor
                )

                // overlay LIKE
                if (offsetX.value > 120f) {

                    if (offsetX.value > 120f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(28.dp)
                                .graphicsLayer(rotationZ = -12f)
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFF00C853),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "LIKE",
                                color = Color(0xFF00C853),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (offsetX.value < -120f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(28.dp)
                                .graphicsLayer(rotationZ = 12f)
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFFD50000),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "PASS",
                                color = Color(0xFFD50000),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // overlay PASS
                if (offsetX.value < -120f) {

                    Text(
                        text = "PASS",
                        color = Color(0xFFD50000),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(32.dp)
                    )
                }
            }
        }
    }
}