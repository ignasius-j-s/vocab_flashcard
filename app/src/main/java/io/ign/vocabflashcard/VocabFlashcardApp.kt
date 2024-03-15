package io.ign.vocabflashcard

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.ign.vocabflashcard.ui.navigation.VocabFlashcardNavHost

@Composable
fun VocabFlashCardApp(navController: NavHostController = rememberNavController()) {
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