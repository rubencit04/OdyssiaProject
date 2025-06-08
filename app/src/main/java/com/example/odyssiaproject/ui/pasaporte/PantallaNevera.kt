package com.example.odyssiaproject.ui.pasaporte

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.odyssiaproject.R
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.odyssiaproject.model.FotoRecuerdo
import com.example.odyssiaproject.model.Recuerdo
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class PolaroidStyle {
    GridItem,
    Amplified
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault())
    return sdf.format(Date(timestamp)).uppercase()
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaNevera(
    viewModel: PasaporteViewModel,
    onNavigateHome: () -> Unit,
    onLaunchPhotoPicker: () -> Unit
) {
    val estadoPanelInferior = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val imanesEnNevera by viewModel.imanesEnNevera.observeAsState(initial = emptyList())
    val fotosEnNevera by viewModel.fotosEnNevera.observeAsState(initial = emptyList())
    val albumDeFotos by viewModel.albumDeFotos.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val isDeleteMode by viewModel.isDeleteMode.observeAsState(initial = false)
    val isAlbumDeleteMode by viewModel.isAlbumDeleteMode.observeAsState(initial = false)
    val itemAmpliado by viewModel.itemAmpliado.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.cargarEstadoInicial()
    }

    ModalBottomSheetLayout(
        sheetState = estadoPanelInferior,
        sheetShape = MaterialTheme.shapes.large,
        sheetContent = {
            PanelSeleccionRecuerdos(
                recuerdos = viewModel.listaDeRecuerdosDisponibles,
                albumDeFotos = albumDeFotos,
                isAlbumDeleteMode = isAlbumDeleteMode,
                alSeleccionarRecuerdo = { recuerdo ->
                    viewModel.añadirIman(recuerdo)
                    scope.launch { estadoPanelInferior.hide() }
                },
                onAddPhotoClicked = { onLaunchPhotoPicker() },
                onSelectPhotoFromAlbum = { foto ->
                    viewModel.colocarFotoEnNevera(foto)
                    scope.launch { estadoPanelInferior.hide() }
                },
                onToggleAlbumDeleteMode = { viewModel.toggleAlbumDeleteMode() },
                onDeletePhotoFromAlbum = { viewModel.eliminarFotoDelAlbum(it) }
            )
        }
    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFDDE3E6), Color(0xFFE9ECEF))
                        )
                    )
            ) {
                LienzoNevera(
                    imanes = imanesEnNevera,
                    fotos = fotosEnNevera,
                    isLoading = isLoading,
                    isDeleteMode = isDeleteMode,
                    onImanClicked = { iman ->
                        if (isDeleteMode) {
                            viewModel.eliminarIman(iman)
                        } else {
                            viewModel.ampliarItem(iman)
                        }
                    },
                    onFotoClicked = { foto ->
                        if (isDeleteMode) {
                            viewModel.eliminarFotoDeLaNevera(foto)
                        } else {
                            viewModel.ampliarItem(foto)
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (isDeleteMode) {
                        FloatingActionButton(
                            onClick = { viewModel.toggleDeleteMode() },
                            backgroundColor = Color(0xFF5294FF)
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Salir de modo borrado", tint = Color.White)
                        }
                    } else {
                        if (imanesEnNevera.isNotEmpty() || fotosEnNevera.isNotEmpty()) {
                            FloatingActionButton(
                                onClick = { viewModel.toggleDeleteMode() },
                                backgroundColor = Color(0xFF5294FF)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Entrar a modo borrado", tint = Color.White)
                            }
                        }
                        FloatingActionButton(
                            onClick = { scope.launch { estadoPanelInferior.show() } },
                            backgroundColor = Color(0xFF5294FF)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Recuerdo", tint = Color.White)
                        }
                        FloatingActionButton(
                            onClick = onNavigateHome,
                            backgroundColor = Color(0xFF5294FF)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Volver a Casa", tint = Color.White)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = itemAmpliado != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .clickable { viewModel.cerrarVistaAmpliada() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.padding(32.dp)) {
                            when (val item = itemAmpliado) {
                                is Recuerdo -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = item.idImagen),
                                        contentDescription = item.nombrePais,
                                        modifier = Modifier
                                            .size(250.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                is FotoRecuerdo -> {
                                    FotoPolaroid(
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        foto = item,
                                        isDeleteMode = false,
                                        onClick = { viewModel.cerrarVistaAmpliada() },
                                        style = PolaroidStyle.Amplified
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LienzoNevera(
    modifier: Modifier = Modifier,
    imanes: List<Recuerdo>,
    fotos: List<FotoRecuerdo>,
    isLoading: Boolean,
    isDeleteMode: Boolean,
    onImanClicked: (Recuerdo) -> Unit,
    onFotoClicked: (FotoRecuerdo) -> Unit
) {
    var freezerBounds by remember { mutableStateOf(Rect.Zero) }
    var mainFridgeBounds by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .border(2.dp, Color(0xFFB0B0B0), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .padding(end = 24.dp)
                    .onGloballyPositioned { freezerBounds = it.boundsInParent() }
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(100.dp)
                        .align(Alignment.CenterEnd)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFB0B0B0), Color(0xFFD0D0D0), Color(0xFF909090))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .padding(end = 24.dp)
                    .onGloballyPositioned { mainFridgeBounds = it.boundsInParent() }
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(200.dp)
                        .align(Alignment.CenterEnd)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFB0B0B0), Color(0xFFD0D0D0), Color(0xFF909090))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (freezerBounds != Rect.Zero) {
                val imanSize = 55.dp
                val gridCellPadding = 8.dp
                val numRows = 3
                val imanSizePx = with(LocalDensity.current) { imanSize.toPx() }
                val cellPaddingPx = with(LocalDensity.current) { gridCellPadding.toPx() }
                val paddingStartPx = cellPaddingPx
                val paddingEndPx = with(LocalDensity.current) { (24.dp + gridCellPadding).toPx() }
                val availableWidth = freezerBounds.width - paddingStartPx - paddingEndPx
                val numberOfColumns = (availableWidth / (imanSizePx + cellPaddingPx)).toInt().coerceAtLeast(1)

                imanes.forEachIndexed { index, iman ->
                    if (index < numberOfColumns * numRows) {
                        val row = index / numberOfColumns
                        val col = index % numberOfColumns
                        val totalGridWidth = (imanSizePx + cellPaddingPx) * numberOfColumns - cellPaddingPx
                        val horizontalPaddingOffset = (availableWidth - totalGridWidth) / 2
                        val offsetX = freezerBounds.left + paddingStartPx + horizontalPaddingOffset + col * (imanSizePx + cellPaddingPx)
                        val offsetY = freezerBounds.top + cellPaddingPx + row * (imanSizePx + cellPaddingPx)

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .size(imanSize)
                                .clip(CircleShape)
                                .clickable { onImanClicked(iman) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = iman.idImagen),
                                contentDescription = iman.nombrePais,
                                modifier = Modifier.fillMaxSize()
                            )
                            AnimatedVisibility(visible = isDeleteMode) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (mainFridgeBounds != Rect.Zero) {
                val numColumns = 3
                val numRows = 3
                val cellPadding = 16.dp
                val cellPaddingPx = with(LocalDensity.current) { cellPadding.toPx() }
                val paddingStartPx = cellPaddingPx
                val paddingEndPx = with(LocalDensity.current) { (24.dp + cellPadding).toPx() }
                val availableWidth = mainFridgeBounds.width - paddingStartPx - paddingEndPx
                val availableHeight = mainFridgeBounds.height - (cellPaddingPx * 2)
                val cellWidth = (availableWidth - (cellPaddingPx * (numColumns - 1))) / numColumns
                val cellHeight = (availableHeight - (cellPaddingPx * (numRows - 1))) / numRows
                val polaroidWidthPx = minOf(cellWidth, cellHeight * 0.85f)
                val polaroidWidthDp = with(LocalDensity.current) { polaroidWidthPx.toDp() }

                fotos.forEachIndexed { index, foto ->
                    if (index < numColumns * numRows) {
                        val row = index / numColumns
                        val col = index % numColumns
                        val offsetX = mainFridgeBounds.left + paddingStartPx + col * (polaroidWidthPx + cellPaddingPx)
                        val offsetY = mainFridgeBounds.top + cellPaddingPx + row * ((polaroidWidthPx / 0.85f) + cellPaddingPx)

                        FotoPolaroid(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .width(polaroidWidthDp),
                            foto = foto,
                            isDeleteMode = isDeleteMode,
                            onClick = { onFotoClicked(foto) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PanelSeleccionRecuerdos(
    recuerdos: List<Recuerdo>,
    albumDeFotos: List<FotoRecuerdo>,
    isAlbumDeleteMode: Boolean,
    alSeleccionarRecuerdo: (Recuerdo) -> Unit,
    onAddPhotoClicked: () -> Unit,
    onSelectPhotoFromAlbum: (FotoRecuerdo) -> Unit,
    onToggleAlbumDeleteMode: () -> Unit,
    onDeletePhotoFromAlbum: (FotoRecuerdo) -> Unit
) {
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val tabs = listOf("Insignias", "Fotos")
    val colorActivo = Color(0xFF5294FF)
    val colorInactivo = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f), thickness = 4.dp, modifier = Modifier.width(40.dp).clip(RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = colorActivo,
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title, fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal, color = if (pagerState.currentPage == index) colorActivo else colorInactivo, fontSize = 16.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> {
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 80.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                        items(recuerdos, key = { it.nombrePais }) { recuerdo ->
                            ItemRecuerdoEnGrid(recuerdo = recuerdo, alHacerClick = { alSeleccionarRecuerdo(recuerdo) })
                        }
                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 4.dp, top = 52.dp)
                        ) {
                            item {
                                BotonAnadirGaleria(onClick = onAddPhotoClicked, modifier = Modifier.padding(4.dp))
                            }
                            items(albumDeFotos, key = { it.uri }) { foto ->
                                FotoPolaroid(
                                    modifier = Modifier.padding(4.dp),
                                    foto = foto,
                                    isDeleteMode = isAlbumDeleteMode,
                                    onClick = {
                                        if (isAlbumDeleteMode) {
                                            onDeletePhotoFromAlbum(foto)
                                        } else {
                                            onSelectPhotoFromAlbum(foto)
                                        }
                                    }
                                )
                            }
                        }

                        if (albumDeFotos.isNotEmpty()) {
                            IconButton(
                                onClick = onToggleAlbumDeleteMode,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAlbumDeleteMode) Icons.Default.Done else Icons.Default.Delete,
                                    contentDescription = "Modo Borrado Álbum",
                                    tint = if (isAlbumDeleteMode) colorActivo else colorInactivo
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemRecuerdoEnGrid(recuerdo: Recuerdo, alHacerClick: () -> Unit) {
    Card(onClick = alHacerClick, modifier = Modifier.padding(8.dp), elevation = 4.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(id = recuerdo.idImagen), contentDescription = recuerdo.nombrePais, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = recuerdo.nombrePais, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FotoPolaroid(
    modifier: Modifier = Modifier,
    foto: FotoRecuerdo,
    isDeleteMode: Boolean,
    onClick: () -> Unit,
    style: PolaroidStyle = PolaroidStyle.GridItem
) {
    val fechaFontSize = when (style) {
        PolaroidStyle.GridItem -> 10.sp
        PolaroidStyle.Amplified -> 18.sp
    }

    Box(modifier = modifier.aspectRatio(0.85f)) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(foto.uri)),
                    contentDescription = "Recuerdo fotográfico",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = formatTimestamp(foto.timestamp), fontSize = fechaFontSize, fontWeight = FontWeight.Bold, color = Color(0xFF5294FF))
                }
            }
        }
        AnimatedVisibility(visible = isDeleteMode, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = 0.5f))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Foto", tint = Color.White, modifier = Modifier.size(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BotonAnadirGaleria(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.85f),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.7f)),
        backgroundColor = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f)
                        .padding(8.dp)
                        .background(Color.Black, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.weight(0.25f))
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir desde Galería", modifier = Modifier.size(30.dp), tint = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Añadir", fontSize = 12.sp, color = Color.Black)
            }
        }
    }
}