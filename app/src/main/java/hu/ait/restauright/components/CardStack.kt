package hu.ait.restauright.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.restauright.Data.restaurant_result.Businesse
import hu.ait.restauright.screen.RestaurantsViewModel
import kotlin.math.roundToInt
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardStack(
    modifier: Modifier = Modifier,
    items: List<Businesse>,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f)},
    velocityThreshold: Dp = 125.dp,
    onSwipeLeft: (item: Businesse) -> Unit = {},
    onSwipeRight: (item: Businesse) -> Unit = {},
    onNavigateToResults: (String) -> Unit = {},
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel(),
    sessionId: String
) {
    var options by remember {
        mutableStateOf(items.size - 1)
    }

    if (options == -1) {
        onNavigateToResults(sessionId)
    }

    val cardStackController = rememberCardStackController()

    cardStackController.onSwipeLeft = {
        onSwipeLeft(items[options])
        options--
    }

    cardStackController.onSwipeRight = {
        onSwipeRight(items[options])
        options--
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        val stack = createRef()

        Box(
            modifier = modifier
                .constrainAs(stack) {
                    top.linkTo(parent.top)
                }
                .draggableStack(
                    controller = cardStackController,
                    thresholdConfig = thresholdConfig,
                    velocityThreshold = velocityThreshold,
                    onRightSwipe = {
                        restaurantsViewModel.voteForRestaurant(
                            items[items.size - options - 1],
                            sessionId
                        )
                    }
                )
                .fillMaxHeight()
        ) {
            items.asReversed().forEachIndexed { index, item ->
                Card(
                    modifier = Modifier
                        .moveTo(
                            x = if (index == options) cardStackController.offsetX.value else 0f,
                            y = if (index == options) cardStackController.offsetY.value else 0f
                        )
                        .visible(visible = index == options || index == options - 1)
                        .graphicsLayer(
                            rotationZ = if (index == options) cardStackController.rotation.value else 0f,
                            scaleX = if (index < options) cardStackController.scale.value else 1f,
                            scaleY = if (index < options) cardStackController.scale.value else 1f
                        ),
                    item,
                    cardStackController,
                    sessionId = sessionId
                )
            }
        }
    }
}
@Composable
fun Card(
    modifier: Modifier = Modifier,
    item: Businesse,
    cardStackController: CardStackController,
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel(),
    sessionId: String
) {
    Box(modifier = modifier) {
        if (item.imageUrl != null) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
        }

        Column(
            modifier = modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(text = item.alias!!, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 25.sp)

            Text(text = item.categories.toString(), color = Color.White, fontSize = 20.sp)

            Row {
                IconButton(
                    modifier = modifier.padding(50.dp, 0.dp, 0.dp, 0.dp),
                    onClick = { cardStackController.swipeLeft() },
                ) {
                    Icon(
                        Icons.Default.Close, contentDescription = "", tint = Color.White, modifier =
                        modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    modifier = modifier.padding(0.dp, 0.dp, 50.dp, 0.dp),
                    onClick = {
                        cardStackController.swipeRight()
                        restaurantsViewModel.voteForRestaurant(item, sessionId)
                    }
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder, contentDescription = "", tint = Color.White, modifier =
                        modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }
            }
        }
    }
}

// create modifier functions
fun Modifier.moveTo(
    x: Float,
    y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.visible(
    visible: Boolean = true
) = this.then(Modifier.layout{ measurable, constraints ->
    val placeable = measurable.measure(constraints)

    if (visible) {
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
})