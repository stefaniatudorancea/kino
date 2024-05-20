package com.example.kino.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.kino.R
import com.example.kino.rules.navigation.NavigationItem
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(toolbarTitle: String, isDoctor: Boolean, chatScreen: Boolean = false, photoUrl: String? = null){
    TopAppBar(
        title = {
            Row {
                if (chatScreen) {
                    ImageForChat(imageUrl = photoUrl)
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    text = toolbarTitle,
                    color = Color.White
                )
            }
        },
        actions = {
            // Adăugăm iconița de profil doar când isDoctor este false
            if (!isDoctor) {
                IconButton(onClick = { PostOfficeAppRouter.navigateTo(Screen.ProfileScreen) }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle, // Presupunând că folosești iconița de profil standard
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if(!isDoctor) MaterialTheme.colorScheme.primary else colorResource(id = R.color.toolbarBlue),
            titleContentColor = MaterialTheme.colorScheme.inversePrimary
        ),
    )
}

@Composable
fun AppTabBarRoutinesExercises(currentTab: Int, onTabSelected: (Screen) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(currentTab) }
    val tabTitles = listOf("Routines", "Exercises")
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = colorResource(id = R.color.toolbarBlue),
        contentColor = colorResource(id = R.color.listBlue),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = colorResource(id = R.color.listBlue),
                height = 3.dp
            )
        },
        modifier = Modifier.height(56.dp)
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                text = { Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp) },
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                        when (title) {
                            "Routines" -> onTabSelected(Screen.RoutinesListScreen)
                            "Exercises" -> onTabSelected(Screen.ExercisesListScreen)
                        }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAppBar(navigationItems: List<NavigationItem>, pageIndex: Int?){
    NavigationBar {
        navigationItems.forEachIndexed{ index, item ->
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
fun ImageForChat(imageUrl: String?) {
    val painter = if (!imageUrl.isNullOrEmpty()) {
        rememberImagePainter(data = imageUrl)
    } else {
        painterResource(id = R.drawable.default_profile) // O imagine default din resurse
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(45.dp)
            .clip(CircleShape),
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
