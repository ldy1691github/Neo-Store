package com.machiav3lli.fdroid.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.machiav3lli.fdroid.PREFS_LANGUAGE_DEFAULT
import com.machiav3lli.fdroid.R
import com.machiav3lli.fdroid.content.Preferences
import com.machiav3lli.fdroid.ui.compose.components.prefs.PreferenceGroup
import com.machiav3lli.fdroid.ui.dialog.BaseDialog
import com.machiav3lli.fdroid.ui.dialog.EnumSelectionPrefDialogUI
import com.machiav3lli.fdroid.ui.dialog.IntInputPrefDialogUI
import com.machiav3lli.fdroid.ui.dialog.LanguagePrefDialogUI
import com.machiav3lli.fdroid.ui.dialog.StringInputPrefDialogUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrefsPersonalPage() {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    var dialogPref by remember { mutableStateOf<Preferences.Key<*>?>(null) }
    val onPrefDialog = { pref: Preferences.Key<*> ->
        dialogPref = pref
        openDialog.value = true
    }
    val personalPrefs = listOf(
        Preferences.Key.Language,
        Preferences.Key.Theme,
        Preferences.Key.DefaultTab,
        Preferences.Key.ShowScreenshots,
        Preferences.Key.UpdatedApps,
        Preferences.Key.NewApps,
    )
    val cachePrefs = listOf(
        Preferences.Key.ReleasesCacheRetention,
        Preferences.Key.ImagesCacheRetention,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                PreferenceGroup(
                    heading = stringResource(id = R.string.prefs_personalization),
                    keys = personalPrefs,
                    onPrefDialog = onPrefDialog
                )
            }
            item {
                PreferenceGroup(
                    heading = stringResource(id = R.string.prefs_cache),
                    keys = cachePrefs,
                    onPrefDialog = onPrefDialog
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (openDialog.value) {
            BaseDialog(openDialogCustom = openDialog) {
                when (dialogPref?.default?.value) {
                    PREFS_LANGUAGE_DEFAULT -> LanguagePrefDialogUI(
                        openDialogCustom = openDialog
                    )
                    is String -> StringInputPrefDialogUI(
                        prefKey = dialogPref as Preferences.Key<String>,
                        openDialogCustom = openDialog
                    )
                    is Int -> IntInputPrefDialogUI(
                        prefKey = dialogPref as Preferences.Key<Int>,
                        openDialogCustom = openDialog
                    )
                    is Preferences.Enumeration<*> -> EnumSelectionPrefDialogUI(
                        prefKey = dialogPref as Preferences.Key<Preferences.Enumeration<*>>,
                        openDialogCustom = openDialog
                    )
                    else -> {}
                }
            }
        }
    }
}