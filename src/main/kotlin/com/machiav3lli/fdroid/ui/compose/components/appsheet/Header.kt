package com.machiav3lli.fdroid.ui.compose.components.appsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.machiav3lli.fdroid.R
import com.machiav3lli.fdroid.entity.ActionState
import com.machiav3lli.fdroid.entity.DownloadState
import com.machiav3lli.fdroid.ui.compose.components.MainActionButton
import com.machiav3lli.fdroid.ui.compose.components.SecondaryActionButton
import com.machiav3lli.fdroid.ui.compose.icons.Phosphor
import com.machiav3lli.fdroid.ui.compose.icons.phosphor.Code
import com.machiav3lli.fdroid.ui.compose.utils.NetworkImage
import com.machiav3lli.fdroid.utility.extension.text.formatSize

@Composable
fun AppInfoHeader(
    modifier: Modifier = Modifier,
    mainAction: ActionState?,
    possibleActions: Set<ActionState>,
    onAction: (ActionState?) -> Unit = { }
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainActionButton(
                    modifier = Modifier.weight(1f),
                    actionState = mainAction ?: ActionState.Install,
                    onClick = {
                        onAction(mainAction)
                    }
                )
            }
            AnimatedVisibility(visible = possibleActions.isNotEmpty()) {
                FlowRow(
                    modifier = modifier
                        .fillMaxWidth(),
                    mainAxisAlignment = MainAxisAlignment.Center,
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    possibleActions.forEach {
                        SecondaryActionButton(packageState = it) {
                            onAction(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarHeader(
    modifier: Modifier = Modifier,
    icon: String? = null,
    appName: String,
    packageName: String,
    state: DownloadState? = null,
    actions: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NetworkImage(
                    modifier = Modifier.size(56.dp),
                    data = icon
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = appName, style = MaterialTheme.typography.titleLarge)
                    Text(text = packageName, style = MaterialTheme.typography.bodyMedium)
                }
                Box { actions() }
            }

            AnimatedVisibility(visible = state is DownloadState) {
                DownloadProgress(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    totalSize = if (state is DownloadState.Downloading) state.total ?: 1L else 1L,
                    isIndeterminate = state !is DownloadState.Downloading,
                    downloaded = if (state is DownloadState.Downloading) state.downloaded else 0L,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceCodeCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .padding(vertical = 18.dp, horizontal = 12.dp),
        color = Color.Transparent
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Phosphor.Code,
            contentDescription = stringResource(id = R.string.source_code),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DownloadProgress(
    modifier: Modifier = Modifier,
    totalSize: Long,
    downloaded: Long?,
    isIndeterminate: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        if (isIndeterminate) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeDefaults.Large),
            )
        } else {
            Text(
                text = "${downloaded?.formatSize()}/${totalSize.formatSize()}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeDefaults.Large),
                progress = downloaded?.toFloat()?.div(totalSize) ?: 1f
            )
        }
    }
}