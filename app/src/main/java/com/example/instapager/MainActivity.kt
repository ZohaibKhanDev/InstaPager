package com.example.instapager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.enmanuelbergling.walkthrough.model.WalkScrollStyle
import com.enmanuelbergling.walkthrough.ui.components.InstagramPager
import com.example.instapager.ui.theme.InstaPagerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstaPagerTheme {
                WalkThroughScreen()
            }
        }
    }
}

val IMAGES = listOf(
    R.drawable.ic_explore,
    R.drawable.ic_booking,
    R.drawable.ic_wizard_payment,
    R.drawable.ic_wizard_waiting_room,
    R.drawable.ic_notification,
)

private const val LOREM_IPSUM = "Lorem ipsum odor amet, consectetuer adipiscing elit."

data class WalkStep(
    val imageResource: Int,
    val description: String
)

val WALK_STEPS = IMAGES.map {
    WalkStep(
        it, description = LOREM_IPSUM
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WalkThroughScreen() {
    val pageCount = 5
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                WalkThrough(
                    steps = WALK_STEPS,
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    bottomButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < WALK_STEPS.size - 1) {
                                        pagerState.animateScrollToPage(
                                            pagerState.currentPage + 1,
                                            animationSpec = tween(500)
                                        )
                                    } else {
                                        snackBarHostState.showSnackbar("The walk has ended")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(.7f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            AnimatedContent(
                                targetState = pagerState.currentPage < WALK_STEPS.size - 1,
                                label = "text button animation"
                            ) { forward ->
                                if (forward) {
                                    Text(text = "Next")
                                } else {
                                    Text(text = "Get started")
                                }
                            }
                        }
                    },
                    skipButton = {
                        SkipButton {
                            scope.launch {
                                snackBarHostState.showSnackbar("The walk has been skipped")
                            }
                        }
                    },
                    scrollStyle = WalkScrollStyle.Instagram(boxAngle = 20)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalkThrough(
    steps: List<WalkStep>,
    pagerState: PagerState,
    modifier: Modifier,
    bottomButton: @Composable () -> Unit,
    skipButton: @Composable () -> Unit,
    scrollStyle: WalkScrollStyle
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InstagramPager(state = pagerState) { index, pageModifier ->
                PageUi(index = index, modifier = pageModifier)
            }
            bottomButton()
            skipButton()
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PageUi(index: Int, modifier: Modifier) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = WALK_STEPS[index].imageResource),
            contentDescription = "Image for step $index",
            modifier = Modifier
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )
    }
}



@Composable
fun SkipButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = "Skip")
    }
}
