package com.example.sociva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sociva.ui.SocivaViewModel
import com.example.sociva.ui.components.SocivaLogo
import com.example.ui.theme.SocivaBlue

@Composable
fun AuthScreen(
  viewModel: SocivaViewModel,
  modifier: Modifier = Modifier
) {
  var isSignUp by remember { mutableStateOf(false) }
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("alex.rivera@example.com") }
  var password by remember { mutableStateOf("SocivaSecure2026!") }
  var showPassword by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Logo & Tagline
        SocivaLogo(showTagline = true, size = 48.dp)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isSignUp) "Create your account" else "Welcome back to Sociva",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        // Switch Login / Sign Up tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (!isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { isSignUp = false }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Log In",
              fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal,
              color = if (!isSignUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { isSignUp = true }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Sign Up",
              fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal,
              color = if (isSignUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Form Fields
        if (isSignUp) {
          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email address") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
              Icon(
                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null
              )
            }
          },
          visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = { viewModel.login(email) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("auth_submit_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SocivaBlue)
        ) {
          Text(
            if (isSignUp) "Create Account" else "Log In to Sociva",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
          )
        }

        // Quick Demo Fill
        FilledTonalButton(
          onClick = {
            viewModel.login("alex.rivera@sociva.com")
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Instant Demo Login (Alex Rivera)")
        }

        Text(
          text = "By continuing, you agree to Sociva's Terms of Service and Privacy Policy.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          fontSize = 11.sp
        )
      }
    }
  }
}
