package io.ign.vocabflashcard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.ign.vocabflashcard.ui.navigation.VocabFlashcardNavHost

@Composable
fun VocabFLashCardApp(navController: NavHostController = rememberNavController()) {
    VocabFlashcardNavHost(navController = navController)
}

//@Composable
//fun SearchBar(modifier: Modifier = Modifier) {
//    var text = remember { mutableStateOf("") }
//
//    TextField(
//        value = "",
//        onValueChange = {},
//        leadingIcon = {
//            Icon(imageVector = Icons.Default.Search, contentDescription = null)
//        },
//        modifier = modifier
//            .fillMaxWidth()
//            .heightIn(56.dp)
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SearchBarPreview() {
//    SearchBar()
//}