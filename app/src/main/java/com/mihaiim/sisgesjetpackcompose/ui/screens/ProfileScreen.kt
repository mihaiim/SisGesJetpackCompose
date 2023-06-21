package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.others.PictureFileProvider
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryButton
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryTextField
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.theme.Grey
import com.mihaiim.sisgesjetpackcompose.ui.theme.Parsley
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.ProfileViewModel

@ExperimentalPermissionsApi
@Composable
fun ProfileScreen(
    navController: NavController,
    checkAndRequestCameraPermission: () -> PermissionStatus,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        val firstName by remember { viewModel.firstName }
        val lastName by remember { viewModel.lastName }
        val email by remember { viewModel.email }
        val password by remember { viewModel.password }
        val scrollState = rememberScrollState()

        SetObservables(viewModel, context, navController)

        Column(modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
            .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ImagePicker(
                context = context,
                viewModel = viewModel,
                checkAndRequestCameraPermission = checkAndRequestCameraPermission,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            PrimaryTextField(
                text = firstName,
                valueChanged = { viewModel.firstName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.first_name),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryTextField(
                text = lastName,
                valueChanged = { viewModel.lastName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.last_name),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryTextField(
                text = email,
                valueChanged = { viewModel.email.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.email),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryTextField(
                text = password,
                valueChanged = { viewModel.password.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.password),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                isPassword = true,
            )
            Spacer(modifier = Modifier.height(40.dp))
            PrimaryButton(
                text = stringResource(R.string.save),
                onClick = {
                    viewModel.updateUser()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun ImagePicker(
    context: Context,
    viewModel: ProfileViewModel,
    checkAndRequestCameraPermission: () -> PermissionStatus,
    modifier: Modifier = Modifier,
) {

    val firstName by remember { viewModel.firstName }
    val lastName by remember { viewModel.lastName }
    val photoUri by remember { viewModel.photoUri }
    val profilePictureDialogVisible = remember { mutableStateOf(false)}

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            viewModel.tempPhotoUri?.let { viewModel.setPhotoUri(it) }
        }
    }

    val choosePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { it?.let { viewModel.setPhotoUri(it) } }

    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { profilePictureDialogVisible.value = true },
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUri)
                .crossfade(true)
                .build(),
            contentDescription = "$firstName $lastName",
            placeholder = painterResource(id = R.drawable.ic_default_profile_picture),
            error = painterResource(id = R.drawable.ic_default_profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
        )
        Box(modifier = Modifier
            .size(32.dp, 32.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .align(Alignment.BottomEnd),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Parsley),
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxSize(),
            )
        }
    }

    if (profilePictureDialogVisible.value) {
        var removePictureAction: (() -> Unit)? = null
        if (viewModel.user.photoUri != null) {
            removePictureAction = {
                profilePictureDialogVisible.value = false
                viewModel.removeProfilePicture()
            }
        }
        ProfilePictureDialog(
            takePicture = {
                profilePictureDialogVisible.value = false
                when (val permissionStatus = checkAndRequestCameraPermission()) {
                     is PermissionStatus.Granted -> {
                        val uri = PictureFileProvider.getTempFileUri(context)
                        viewModel.tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    }
                    is PermissionStatus.Denied -> {
                        if (!permissionStatus.shouldShowRationale) {
                            Toast.makeText(
                                context,
                                R.string.camera_permission_denied_message_profile_picture,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            chooseFromGalleryPicture = {
                profilePictureDialogVisible.value = false
                choosePictureLauncher.launch("image/*")
            },
            onDismiss = { profilePictureDialogVisible.value = false },
            removePictureAction = removePictureAction,
        )
    }
}

@Composable
fun ProfilePictureDialog(
    takePicture: () -> Unit,
    chooseFromGalleryPicture: () -> Unit,
    onDismiss: () -> Unit,
    removePictureAction: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.take_picture),
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { takePicture() },
                )
                Spacer(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Grey),
                )
                Text(
                    text = stringResource(R.string.choose_from_gallery),
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { chooseFromGalleryPicture() },
                )
                if (removePictureAction != null) {
                    Spacer(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Grey),
                    )
                    Text(
                        text = stringResource(R.string.remove_profile_picture),
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { removePictureAction() },
                    )
                }
            }
        },
        buttons = {}
    )
}

@Composable
private fun SetObservables(
    viewModel: ProfileViewModel,
    context: Context,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.successFlow.collect {
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.pictureRemovedFlow.collect {
            Toast.makeText(
                context,
                R.string.profile_picture_removed_successfully,
                Toast.LENGTH_LONG,
            ).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.failFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.errorResIdFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}