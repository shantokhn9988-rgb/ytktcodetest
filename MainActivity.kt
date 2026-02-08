package com.example.simpleyoutube

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

// 1. Simple Data
data class Video(val id: String, val title: String, val url: String, val thumb: String)

val videoList = listOf(
    Video("1", "Big Buck Bunny", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Big_buck_bunny_poster_big.jpg/800px-Big_buck_bunny_poster_big.jpg"),
    Video("2", "Elephant Dream", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "https://upload.wikimedia.org/wikipedia/commons/e/e8/Elephants_Dream_s5_both.jpg")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            
            // 2. Navigation Control
            NavHost(navController = navController, startDestination = "feed") {
                
                // Screen A: The List
                composable("feed") {
                    LazyColumn {
                        items(videoList) { video ->
                            Column(modifier = Modifier.clickable { 
                                navController.navigate("player/${video.id}") 
                            }) {
                                AsyncImage(
                                    model = video.thumb, 
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(text = video.title, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }

                // Screen B: The Player
                composable("player/{id}") { backStack ->
                    val id = backStack.arguments?.getString("id")
                    val video = videoList.find { it.id == id }
                    if (video != null) VideoPlayer(video.url)
                }
            }
        }
    }
}

// 3. The Video Player Component
@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    
    // Create player
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            prepare()
            playWhenReady = true
        }
    }

    // Clean up when leaving screen
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // Render the UI
    AndroidView(
        factory = { PlayerView(context).apply { player = exoPlayer } },
        modifier = Modifier.fillMaxSize()
    )
}
