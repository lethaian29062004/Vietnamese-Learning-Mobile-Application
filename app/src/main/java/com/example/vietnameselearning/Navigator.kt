package com.example.vietnameselearning

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    navController: NavHostController,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    // Create 'remembered' state for the UI.
    // When message changes, the Bottom Bar will automatically update.
    var message by remember { mutableStateOf("") }

    // Type-Safe Navigation
    val navigateToAddCard = fun() {
        navController.navigate(AddCardRoute)
    }
    val navigateToStudyCards = fun() {
        navController.navigate(StudyCardsRoute)
    }

    val navigateToFilterCards = fun() {
        navController.navigate(FilterCardsRoute)
    }

    val navigateToSearchCards = fun(en: String, exactEn: Boolean, vn: String, exactVn: Boolean) {
        navController.navigate(
            SearchCardsRoute(
                enWord = en,
                exactEn = exactEn,
                vnWord = vn,
                exactVn = exactVn
            )
        )
    }

    val navigateToLogin = fun() {
        navController.navigate(LoginRoute)
    }
    val navigateToToken = fun(email: String) {
        navController.navigate(TokenRoute(email = email))
    }

    val navigateToHome = fun() {
        // Clears the navigation history (Back Stack).
        // Prevents the user from returning to the Login/Token screens ofter logging in.
        navController.navigate(HomeRoute) {
            popUpTo(HomeRoute) { inclusive = true }
        }
    }

    val navigateToEditCard = fun(flashCard: FlashCard) {
        navController.navigate(EditCardRoute(uid = flashCard.uid))
    }


    // -------------------------------------------

    val changeMessage = fun(text:String){
        message = text
    }


    // 'remember' + 'mutableStateOf': Creates a list that automatically updates the UI when data changes
    // Initially, the list is empty.
    var allFlashCards by remember {mutableStateOf<List<FlashCard>>(emptyList<FlashCard>())}

    // Fetches data from the Database and updates the state variable (flashcard_list) in memory
    val getFlashCards: suspend () -> Unit = {
        allFlashCards = flashCardDao.getAll()
    }

    // Fetches filtered data from Database
    // Converts Boolean UI flags to Int for SQLite (1=true, 0=false)
    val getFilteredFlashCards: suspend (String, Boolean, String, Boolean) -> Unit =
        { en, exactEn, vn, exactVn ->
            val enInt = if (exactEn) 1 else 0
            val vnInt = if (exactVn) 1 else 0
            allFlashCards = flashCardDao.getFilteredFlashCards(en, enInt, vn, vnInt)
        }

    // Provides a read-only view of the current flashcard list from memory
    val readStateAllFlashCards = fun(): List<FlashCard> {
        return allFlashCards
    }


    // Instead of passing the entire DAO to screens, we define specific methods here.
    // This allows screens to perform database tasks without having direct access to the entire 'flashCardDao'
    val insertFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.insertAll(flashCard)
    }

    val deleteFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.delete(flashCard)
    }

    val updateFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.update(flashCard)
    }


    // Tracks the current screen in the navigation history to update the UI (show 'Back' if not at HomeScreen).
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Vietnamese Learning App",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    // Check if the current screen is NOT HomeScreen
                    val currentRoute = navBackStackEntry?.destination?.route
                    val startRoute = HomeRoute::class.qualifiedName

                    // If the current route EXISTS AND IS NOT HomeRoute, display the Back button.
                    if (currentRoute != null && currentRoute != startRoute) {
                        Button(
                            modifier = Modifier.semantics{contentDescription="navigateBack"},
                            onClick = {
                                // This command pops the current screen off the history stack,
                                // automatically showing the previous screen.
                                navController.navigateUp()
                            }) {
                            Text("Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Message"
                            },
                        textAlign = TextAlign.Center,
                        text = message
                    )
                })
        }
    ) { innerPadding ->
        // NavHost is the Map of the application
        // It manages which screen to display based on the current 'navController' state.
        NavHost(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            navController = navController,
            startDestination = HomeRoute
        ) {
            // HomeScreen
            composable<HomeRoute> {
                HomeScreen(
                    changeMessage = changeMessage,
                    navigateToAddCard = navigateToAddCard,
                    navigateToStudyCards = navigateToStudyCards,
                    navigateToSearchCards = navigateToFilterCards, // UPDATED: Goes to Filter Screen
                    navigateToLogin = navigateToLogin
                )
            }
            // AddCardScreen
            composable<AddCardRoute> {
                AddCardScreen(
                    changeMessage = changeMessage,
                    insertFlashCard = insertFlashCard
                )
            }
            // StudyCardScreen
            composable<StudyCardsRoute> {
                StudyCardsScreen(
                    changeMessage = changeMessage,
                    flashCardDao = flashCardDao,
                    networkService = networkService
                )
            }

            // FilterCardsScreen
            composable<FilterCardsRoute> {
                FilterCardsScreen(
                    changeMessage = changeMessage,
                    onSearchClicked = navigateToSearchCards,
                )
            }


            composable<SearchCardsRoute> { backStackEntry ->
                val route: SearchCardsRoute = backStackEntry.toRoute()
                SearchCardsScreen(
                    changeMessage = changeMessage,
                    readStateAllFlashCards = readStateAllFlashCards,
                    getFilteredFlashCards = getFilteredFlashCards,
                    deleteFlashCard = deleteFlashCard,
                    navigateToEditCard = navigateToEditCard,

                    enQuery = route.enWord,
                    exactEn = route.exactEn,
                    vnQuery = route.vnWord,
                    exactVn = route.exactVn
                )
            }
            // LoginScreen
            composable<LoginRoute> {
                LoginScreen(
                    changeMessage = changeMessage,
                    networkService = networkService,
                    navigateToToken = navigateToToken
                )
            }

            /*
            These screens require specific input data (Arguments) to function.
             Example: To show a card, we need to know WHICH card (uid).

             Use 'backStackEntry.toRoute()' to extract this data.
             'backStackEntry' holds all navigation info.
             'toRoute()' to convert it into a specific route.
            */


            // TokenScreen
            composable<TokenRoute> { backStackEntry ->
                val route: TokenRoute = backStackEntry.toRoute()
                TokenScreen(
                    email = route.email,
                    changeMessage = changeMessage,
                    navigateToHome = navigateToHome
                )
            }


            // EditCardScreen
            composable<EditCardRoute> { backStackEntry ->
                val route: EditCardRoute = backStackEntry.toRoute()
                EditCardScreen(
                    uid = route.uid,
                    changeMessage = changeMessage,
                    getFlashCardById = { flashCardDao.getById(it) },
                    updateFlashCard = updateFlashCard,
                    getFlashCards = getFlashCards,
                    navigateBack = { navController.navigateUp() },
                    networkService = networkService
                )
            }
        }
    }
}