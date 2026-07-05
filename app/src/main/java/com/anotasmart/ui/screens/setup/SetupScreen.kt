package com.anotasmart.ui.screens.setup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anotasmart.model.ImageDirectory
import com.anotasmart.utils.ImageUtils

import com.yalantis.ucrop.UCrop
import java.io.File

import androidx.compose.material.icons.filled.ArrowBack
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    userViewModel: UserViewModel,
    onComplete: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val userPrefs by userViewModel.userPreferences.collectAsState()
    
    var userName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Inicializa os campos com os valores atuais quando as preferências forem carregadas
    LaunchedEffect(userPrefs.isLoaded) {
        if (userPrefs.isLoaded) {
            userName = userPrefs.userName
            companyName = userPrefs.companyName
            userPrefs.profileImagePath?.let { path ->
                if (path.isNotEmpty()) {
                    try {
                        croppedImageUri = File(path).toUri()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Verifica se é edição (se já existe configuração)
    val isEditing = userPrefs.userName.isNotBlank() && userPrefs.companyName.isNotBlank()

    // Launcher para o uCrop
    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.let { data ->
                    croppedImageUri = UCrop.getOutput(data)
                }
            }
        }
    )

    // Launcher para selecionar imagem inicial
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                val destinationUri = Uri.fromFile(File(context.cacheDir, "temp_crop_${System.currentTimeMillis()}.jpg"))
                val uCropIntent = ImageUtils.startUCrop(context, uri, destinationUri).getIntent(context)
                uCropLauncher.launch(uCropIntent)
            }
        }
    )

    val isConfirmEnabled = userName.isNotBlank() && companyName.isNotBlank() && croppedImageUri != null

    Scaffold(
        topBar = {
            if (isEditing && onBackClick != null) {
                CenterAlignedTopAppBar(
                    title = { Text("Editar Perfil") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (isEditing) Arrangement.Top else Arrangement.Center
            ) {
                if (!isEditing) {
                    Text(
                        text = "Bem-vindo ao AnotaSmart!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = if (isEditing) "Atualize suas informações abaixo." else "Preencha os dados obrigatórios para começar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Seletor de Foto de Perfil com uCrop
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (croppedImageUri != null) {
                        AsyncImage(
                            model = croppedImageUri,
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                Text(
                    text = if (croppedImageUri == null) "Adicionar Foto *" else "Trocar Foto",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (croppedImageUri == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo Nome do Usuário
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Seu Nome *") },
                    placeholder = { Text("Ex: João Silva") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = userName.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Nome da Empresa
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Nome da sua Empresa *") },
                    placeholder = { Text("Ex: Mercadinho do João") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = companyName.isBlank()
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        val finalPath = croppedImageUri?.let { uri ->
                            // Só salva se a URI mudou (se não for a mesma do banco)
                            if (uri.toString() != userPrefs.profileImagePath) {
                                ImageUtils.saveImageToInternalStorage(context, uri, ImageDirectory.PROFILE)
                            } else {
                                userPrefs.profileImagePath
                            }
                        }
                        userViewModel.saveInitialSetup(userName, companyName, finalPath) {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isConfirmEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if (isEditing) "SALVAR ALTERAÇÕES" else "COMEÇAR AGORA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
