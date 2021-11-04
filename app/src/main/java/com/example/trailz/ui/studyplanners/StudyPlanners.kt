package com.example.trailz.ui.studyplanners

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.trailz.R
import com.example.trailz.ui.common.compose.FavoriteButton

@ExperimentalMaterialApi
@Composable
fun StudyPlan(
    modifier: Modifier = Modifier,
    userId: String,
    title: String,
    checked: Boolean,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        onClick = { onStudyPlan(userId) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val imageModifier = Modifier
                .heightIn(max = 180.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp).align(Alignment.TopStart)
                )

                Text(
                    text = userId,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(bottom = 4.dp).align(Alignment.BottomStart)
                )

                FavoriteButton(
                    isChecked = checked,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colorOnChecked = MaterialTheme.colors.primary,
                    colorUnChecked = MaterialTheme.colors.onBackground
                ) {
                    onUpdateFavorite(userId, it)
                }
            }

            Spacer(Modifier.height(16.dp))

        }
    }
}