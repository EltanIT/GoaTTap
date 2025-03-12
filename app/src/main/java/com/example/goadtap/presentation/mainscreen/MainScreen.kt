package com.example.goadtap.presentation.mainscreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goadtap.CoinData
import com.example.goadtap.R
import com.example.goadtap.presentation.MainActivity
import com.example.goadtap.presentation.ui.theme.myFont
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun MainScreen(
    coinData: CoinData?,
    onRelease: () -> Unit,
    coinAnimations: SnapshotStateList<MainActivity.CoinAnimationData>,
    animationStart: Boolean,
    onTap: (offset: Offset) -> Unit,
    onOpenUpgrades: () -> Unit
) {

    var iconSize by remember { mutableStateOf(IntSize.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val swipeCount = remember { mutableStateOf(0) } // Счетчик пройденных точек
    val requiredSwipes = 600 // Сколько точек нужно пройти для начисления монет



    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                boxSize = it.size
            }
            .background(
                Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xffffffff),
                        0.25f to Color(0xFFCEE0F5),
                        1.0f to Color(0xff034A9B),
                    ),
                    center = Offset(boxSize.center.x.toFloat(), boxSize.center.y.toFloat()),
                    radius = boxSize.width.toFloat() + 100f
                ),
                RoundedCornerShape(32.dp)
            )
            .clip(RoundedCornerShape(32.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(34.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = coinData?.coins.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = myFont
                    )
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "за 1 клик",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = myFont
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )

                Spacer(modifier = Modifier.width(11.dp))

                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = "+${coinData?.tapValue}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xff7FEA20),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))

            val density = LocalDensity.current
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .height(with(density) { iconSize.width.toDp() })
                    .onGloballyPositioned {
                        iconSize = it.size
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tap_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { // Обнаруживаем жесты перетаскивания
                            detectDragGestures(
                                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                    val value =
                                        abs(dragAmount.y.toInt()) + abs(dragAmount.x.toInt())
                                    swipeCount.value +=
                                        if (value >= 300) 300
                                        else value // Увеличиваем счетчик
                                    Log.i(
                                        "clicker",
                                        "${change.position}  x: ${dragAmount.x}  y: ${dragAmount.y}"
                                    )
                                    if (swipeCount.value >= requiredSwipes) { // Проверяем, достигнуто ли нужное количество
                                        Log.i("clicker", "Tap! + ${swipeCount.value}")
                                        swipeCount.value = 0 // Сбрасываем счетчик


                                        val centerX = Random.nextInt(30, iconSize.width - 30)
                                        val centerY = Random.nextInt(30, iconSize.height - 30)

                                        onTap(
                                            Offset(
                                                centerX.toFloat(),
                                                centerY.toFloat()
                                            )
                                        ) // Начисляем монеты


                                    }
                                },
                                onDragEnd = onRelease,
                                onDragCancel = onRelease
                            )
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.goat_tap),
                    contentDescription = "Тап!",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(235.dp)
                )

                coinAnimations.forEachIndexed { index, data ->
                    CoinAnimation(
                        modifier = Modifier
                            .width(with(density) { iconSize.width.toDp() })
                            .height(with(density) { iconSize.height.toDp() }),
                        startPosition = data.offset,
                        endPosition = Offset(iconSize.width.toFloat() / 2f, iconSize.height.toFloat() / 2f),
                        animationStart = animationStart,
                        onAnimationEnd = {
                            coinAnimations.removeAt(0)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(Color(0xff7FEA20), RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .clickable {
                        onOpenUpgrades()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.upg_icon),
                    contentDescription = null,
                    Modifier.size(32.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(25.dp))
                Text(
                    text = "Улучшения",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = myFont
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(57.dp))
        }

    }
}
