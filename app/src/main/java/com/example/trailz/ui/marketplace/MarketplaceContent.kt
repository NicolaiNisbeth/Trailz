import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.trailz.ui.marketplace.StudyPlan
import com.example.trailz.ui.marketplace.StudyPlansViewModel

var t = listOf("test","test","test","test","test","test","test","test","test","test","test")

@ExperimentalUnitApi
@OptIn(ExperimentalAnimationApi::class) // AnimatedVisibility
@Composable
fun Market_place(viewModel: StudyPlansViewModel) {
    val studyPlans = viewModel.studyplans.observeAsState(listOf())
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(MaterialTheme.colors.background),
        state = listState,
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items( count=studyPlans.value.size,) { index ->
            myItem(studyPlans.value.get(index), index = index )
        }
    }
}

@ExperimentalUnitApi
@Composable
fun Market_place(viewModel: List<String>) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        val (topview, bottomview) = createRefs()
        Row(Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.35f)
            //.background(MaterialTheme.colors.primary)
            .constrainAs(topview){
                top.linkTo(parent.top)}){
        }
        LazyColumn(
            Modifier
                .constrainAs(bottomview){
                    top.linkTo(topview.bottom)
                }
                .offset(y = -50.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items( count=viewModel.size,) { index ->
                myItem(viewModel.get(index), index = index )
            }
        }
    }
}

@ExperimentalUnitApi
@Composable
fun myItem(text: String, index: Int){
    Box(
        modifier = Modifier
            .defaultMinSize(200.dp, 100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.secondary)
            .clickable {}
            .fillMaxWidth(0.8f)
        ){
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ){
            val (text1, text2,text3) = createRefs()
            Text(
                text = "$text: $index ",
                Modifier.constrainAs(text3){

                },
                color = Color.White,
                fontSize = 30.sp
            )
            Text(text = "$text: $index ",
                Modifier
                    .constrainAs(text1){bottom.linkTo(parent.bottom)
                                       start.linkTo(parent.start)},
                color = Color.White
            )
            Text(text = "$text: $index ",
                Modifier
                    .constrainAs(text2){
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)},
                color = Color.White
            )
        }
    }
}

@ExperimentalUnitApi
@Composable
fun myItem(studyplan: StudyPlan, index: Int){
    Box(
        modifier = Modifier
            .defaultMinSize(200.dp, 100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.secondary)
            .clickable {}
            .fillMaxWidth(0.8f)
    ){
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ){
            val (text1, text2,text3) = createRefs()
            Text(
                text = "${studyplan.titel}: $index ",
                Modifier.constrainAs(text3){

                },
                color = Color.White,
                fontSize = 30.sp
            )
            Text(text = "${studyplan.owner}: $index ",
                Modifier
                    .constrainAs(text1){bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)},
                color = Color.White
            )
            Text(text = "${studyplan.titel}: $index ",
                Modifier
                    .constrainAs(text2){
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)},
                color = Color.White
            )
        }
    }
}

@ExperimentalUnitApi
@Composable
@Preview()
fun preview(){
        Market_place(t)
}
