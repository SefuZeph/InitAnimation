package io.sefu.initanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.sefu.initanimation.ui.theme.InitAnimationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitAnimationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    Animatable()
                    AnimationPlayground()
                }
            }
        }
    }
}

enum class ShowAMoreAction {
    SHOW_MORE, SHOW_FIELD
}

enum class FabState {
    COLLAPSE, EXPAND
}

fun FabState.getAlternativestate(): FabState {
    return if (this == FabState.EXPAND) {
        FabState.COLLAPSE
    } else {
        FabState.EXPAND
    }
}

@Composable
fun AnimationPlayground() {
    var rotationState by remember { mutableStateOf(0f) }
    var showMoreAction by remember { mutableStateOf(ShowAMoreAction.SHOW_FIELD) }
    val rotationAnimatable = remember { Animatable(initialValue = 0f) }
    val fabRotation = remember { Animatable(initialValue = 0f) }

    var fabState by remember { mutableStateOf(FabState.COLLAPSE) }
    val scope = rememberCoroutineScope()

    val moreActionIcons = listOf(Icons.Outlined.Favorite,Icons.Rounded.ThumbUp,Icons.Rounded.Delete,Icons.Rounded.DateRange)

    val onFabClicked = {
        scope.launch {
            fabState = fabState.getAlternativestate()

            fabRotation.animateTo(
                targetValue = if (fabState == FabState.EXPAND) {
                    fabRotation.targetValue + 45f
                } else fabRotation.targetValue - 45f,
                animationSpec = tween(100, easing = LinearEasing)
            )

            if (showMoreAction == ShowAMoreAction.SHOW_MORE) {
                showMoreAction = ShowAMoreAction.SHOW_FIELD
            } else if (showMoreAction == ShowAMoreAction.SHOW_FIELD) {
                showMoreAction = ShowAMoreAction.SHOW_MORE
            }
            scope.launch {
                if (showMoreAction == ShowAMoreAction.SHOW_MORE) {
                    rotationState -= 10f
                } else if (showMoreAction == ShowAMoreAction.SHOW_FIELD) {
                    rotationState += 10f
                }

                rotationAnimatable.animateTo(
                    targetValue = rotationState % 360, animationSpec = TweenSpec(
                        durationMillis = 500, easing = FastOutSlowInEasing
                    )
                )
                delay(200)
                rotationState = 0f
                rotationAnimatable.animateTo(
                    targetValue = rotationState % 360, animationSpec = TweenSpec(
                        durationMillis = 500, easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }


    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .graphicsLayer(
                        rotationZ = rotationAnimatable.value,
                        transformOrigin = TransformOrigin(0.0f, 0.5f)
                    )
                    .background(
                        color = colorResource(id = R.color.primaryColor), shape = CircleShape
                    )
                    .clickable {
                        onFabClicked()

                    }, contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = colorResource(id = R.color.secondaryColor),
                                shape = CircleShape
                            )
                            .rotate(fabRotation.value), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add",
                            tint = Color.White
                        )
                    }

                    if (showMoreAction == ShowAMoreAction.SHOW_FIELD) {
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .fillMaxWidth()
                                .background(
                                    color = colorResource(id = R.color.secondaryColor),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.CenterStart
                        ){
                            Text(text = "Message", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.body2.copy(color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold))
                        }
                    } else if (showMoreAction == ShowAMoreAction.SHOW_MORE) {

                        moreActionIcons.forEach { icons ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = colorResource(id = R.color.secondaryColor),
                                        shape = CircleShape
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icons,
                                    contentDescription = "add",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }


                    }

                }
            }
        }
    }
}
