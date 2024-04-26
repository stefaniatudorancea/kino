package com.example.kino.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.rules.navigation.NavigationItem
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.navigation.PostOfficeAppRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(toolbarTitle: String){
    TopAppBar(
        title = {
            Text(text = toolbarTitle, color = colorResource(id = R.color.white))
                },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.inversePrimary
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAppBar(navigationViewModel: NavigationViewModel = viewModel(), pageIndex: Int?){
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    NavigationBar {
        navigationViewModel.navigationItemsList.forEachIndexed{ index, item ->
            NavigationBarItem(
                selected = index == pageIndex,
                onClick = {
                    item.destination?.let { PostOfficeAppRouter.navigateTo(it) }
                          },
                icon = {
                    BadgedBox(badge = {
                        if(item.badgeCount != null){
                            Text(text = item.badgeCount.toString())
                        } else if(item.hasNews == true){
                            Badge()
                        }
                    }) {
                        (if(index == pageIndex) {
                            item.selectedIcon
                        } else item.unselectedIcon)?.let {
                            Icon(imageVector = it,
                                contentDescription = item.titile)
                        }
                        }

                },
                label = { item.titile?.let { androidx.compose.material3.Text(text = it) } })
        }
    }

}
@Composable
fun NavigationItemRow(item: NavigationItem){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 16.dp)){
        //Icon(imageVector = item.icon, contentDescription = "")
        Spacer(modifier = Modifier.width(18.dp))
        item.titile?.let { NormalTextComponent(value = it) }
    }
}
