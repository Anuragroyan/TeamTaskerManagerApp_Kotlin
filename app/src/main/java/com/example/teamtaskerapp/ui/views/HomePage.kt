package com.example.teamtaskerapp.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamtaskerapp.R

// Define the brand colors
val TeamTaskerBlue = Color(0xFF0091FF)
val LightGrayBackground = Color(0xFFE8E8F0)

@Composable
fun TeamTaskerWelcomeScreen(
    onGetStarted: () -> Unit // ADDED: Parameter to handle navigation
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Header Logo/Text
        Text(
            text = "TeamTasker",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = TeamTaskerBlue,
            fontFamily = FontFamily.Monospace
        )

        // 2. Illustration
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Team Collaboration Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // 3. Middle Text Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Let's Get started",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Collaborate and Plan Together Across Multiple Devices on TeamTasker's Minimalist Interface.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // 4. Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sign In Button
            Button(
                onClick = onGetStarted, // UPDATED: Triggers navigation
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TeamTaskerBlue),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "SIGN IN",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Sign Up Button
            Button(
                onClick = onGetStarted, // UPDATED: Also triggers navigation flow
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBackground),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "SIGN UP",
                    color = TeamTaskerBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}