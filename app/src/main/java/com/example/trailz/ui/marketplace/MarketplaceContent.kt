import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet


var t = mutableListOf("test","test","test","test","test","test","test","test","test","test","test")

@ExperimentalUnitApi
@Composable
fun Market_place() {
    ConstraintLayout(constraintSet = ConstraintSet {  }) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)){}
            Row{
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){

                }
                Row{
                    LazyColumn(
                        contentPadding = PaddingValues(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        items( count=t.size,) { index ->
                            myItem(t[index], index = index )
                        }
                    }
                }
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
@Preview()
fun preview(){
        Market_place()
}
