package com.ams.mylogin.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ams.mylogin.R
import com.ams.mylogin.ui.theme.MyLoginTheme


@Composable
fun GoogleSignInButton(
    text: String = "Continuar con Google",
    loadingText: String = "Iniciando...",
    isLoading: Boolean = false,
    onClicked: () -> Unit // Usa esta función para manejar el clic
) {
    Button(
        onClick = { onClicked() }, // Aquí usamos el parámetro onClicked
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.Gray
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_google),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )

                Text(text = text, color = Color.White)
                Spacer(modifier = Modifier.width(30.dp))
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPrevisew() {

    MyLoginTheme {
        GoogleSignInButton(

            isLoading = false,
            onClicked = {
                //authViewModel.signInWithGoogle()
            }
        )
    }
}

