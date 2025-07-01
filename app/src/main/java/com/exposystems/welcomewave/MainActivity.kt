package com.exposystems.welcomewave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exposystems.welcomewave.navigation.Screen
import com.exposystems.welcomewave.ui.admin.AdminAddEditEmployeeScreen
import com.exposystems.welcomewave.ui.admin.AdminEmployeeListScreen
import com.exposystems.welcomewave.ui.adminlogin.AdminLoginScreen
import com.exposystems.welcomewave.ui.employeeselect.EmployeeSelectScreen
import com.exposystems.welcomewave.ui.guestdetails.GuestDetailsScreen
import com.exposystems.welcomewave.ui.theme.WelcomeWaveTheme
import com.exposystems.welcomewave.ui.welcome.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WelcomeWaveTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Welcome.route
                ) {
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onGuestNavigate = {
                                navController.navigate(Screen.EmployeeSelect.route)
                            },
                            onAdminNavigate = {
                                navController.navigate(Screen.AdminLogin.route)
                            }
                        )
                    }

                    composable(Screen.EmployeeSelect.route) {
                        EmployeeSelectScreen(
                            onEmployeeSelected = { employeeId ->
                                navController.navigate(Screen.GuestDetails.createRoute(employeeId))
                            }
                        )
                    }

                    composable(
                        route = Screen.GuestDetails.route,
                        arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
                    ) {
                        GuestDetailsScreen(
                            onCheckInComplete = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // --- Admin Flow ---
                    composable(Screen.AdminLogin.route) {
                        AdminLoginScreen(
                            onLoginSuccess = {
                                // Change this to navigate to the correct route
                                navController.navigate(Screen.AdminEmployeeList.route) {
                                    popUpTo(Screen.AdminLogin.route) { inclusive = true }
                                }
                            },
                            onNavigateUp = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable(Screen.AdminEmployeeList.route) {
                        AdminEmployeeListScreen(
                            onAddEmployeeClicked = {
                                navController.navigate(Screen.AdminAddEditEmployee.route)
                            },
                            onNavigateUp = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable(Screen.AdminAddEditEmployee.route) {
                        AdminAddEditEmployeeScreen(
                            onNavigateUp = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}