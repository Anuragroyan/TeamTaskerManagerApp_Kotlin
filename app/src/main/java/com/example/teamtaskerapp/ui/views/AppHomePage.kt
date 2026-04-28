package com.example.teamtaskerapp.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.teamtaskerapp.R
import com.example.teamtaskerapp.ui.models.Board
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun AppHomePage(
    onProfileClick: () -> Unit,
    onCreateBoardClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onBoardClick: (Board) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf("Loading...") }
    var userEmail by remember { mutableStateOf("") }
    var userImageUrl by remember { mutableStateOf("") }

    // ✅ Load boards live from Firestore instead of a hardcoded list
    var boards by remember { mutableStateOf<List<Board>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch user info
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userName = doc.getString("name") ?: "No Name"
                        userEmail = doc.getString("email") ?: auth.currentUser?.email ?: ""
                        userImageUrl = doc.getString("imageUrl") ?: ""
                    } else {
                        userName = auth.currentUser?.displayName ?: "User"
                        userEmail = auth.currentUser?.email ?: ""
                    }
                }
        }
    }

    // ✅ Real-time listener for boards collection
    DisposableEffect(Unit) {
        val listener = db.collection("boards")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                boards = snapshot.documents.mapNotNull { doc ->
                    // ✅ Manual mapping — no toObject(), no copy() error
                    Board(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                }
                isLoading = false
            }
        onDispose { listener.remove() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                DrawerContent(
                    name = userName,
                    email = userEmail,
                    imageUrl = userImageUrl,
                    onProfileClick = {
                        scope.launch { drawerState.close() }
                        onProfileClick()
                    },
                    onSignOutClick = {
                        scope.launch { drawerState.close() }
                        onSignOutClick()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    color = Color(0xFFE57373),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                        }
                        Text(
                            text = "TeamTasker",
                            modifier = Modifier.padding(start = 12.dp),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateBoardClick,
                    containerColor = Color(0xFFE57373),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFA4D4CE))
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFE57373)
                        )
                    }
                    boards.isEmpty() -> {
                        Text(
                            text = "No boards yet. Tap + to create one!",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(boards) { board ->
                                BoardItem(board = board, onClick = { onBoardClick(board) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    name: String,
    email: String,
    imageUrl: String,
    onProfileClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE57373))
                .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                AsyncImage(
                    model = imageUrl.ifEmpty { R.drawable.user },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.user),
                    placeholder = painterResource(R.drawable.user)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = email, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = { Text("My Profile", fontSize = 14.sp) },
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        NavigationDrawerItem(
            label = { Text("Sign Out", fontSize = 14.sp) },
            selected = false,
            onClick = onSignOutClick,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )
    }
}

@Composable
fun BoardItem(board: Board, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ AsyncImage instead of painterResource — no more crash
            AsyncImage(
                model = board.imageUrl.ifEmpty { R.drawable.user },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.user),
                error = painterResource(R.drawable.user)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    board.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "Created by: ${board.author}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}