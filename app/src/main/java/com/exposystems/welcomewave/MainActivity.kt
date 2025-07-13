package com.exposystems.welcomewave

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exposystems.welcomewave.navigation.Screen
import com.exposystems.welcomewave.ui.admin.AdminAddEditEmployeeScreen
import com.exposystems.welcomewave.ui.admin.AdminEmployeeListScreen
import com.exposystems.welcomewave.ui.admin.AdminVisitorLogScreen
import com.exposystems.welcomewave.ui.adminlogin.AdminLoginScreen
import com.exposystems.welcomewave.ui.checkout.CheckOutScreen
import com.exposystems.welcomewave.ui.confirmation.ConfirmationScreen
import com.exposystems.welcomewave.ui.employeeselect.EmployeeSelectScreen
import com.exposystems.welcomewave.ui.guestdetails.GuestDetailsScreen
import com.exposystems.welcomewave.ui.preregistered.PreRegisteredGuestListScreen // Import the new screen
import com.exposystems.welcomewave.ui.theme.WelcomeWaveTheme
import com.exposystems.welcomewave.ui.welcome.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

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
                            },
                            onCheckOutNavigate = {
                                navController.navigate(Screen.CheckOut.route)
                            },
                            // UPDATED #1: Added the new navigation action
                            onPreRegisteredNavigate = {
                                navController.navigate(Screen.PreRegisteredGuestList.route)
                            }
                        )
                    }

                    composable(Screen.PreRegisteredGuestList.route) {
                        PreRegisteredGuestListScreen(
                            onNavigateUp = { navController.navigateUp() },
                            // UPDATED: Add the check-in complete navigation action
                            onCheckInComplete = {
                                navController.navigate(Screen.Confirmation.route) {
                                    // Clear the back stack up to the Welcome screen
                                    popUpTo(Screen.Welcome.route)
                                }
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

                    composable(Screen.AdminLogin.route) {
                        AdminLoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.AdminEmployeeList.route) {
                                    popUpTo(Screen.AdminLogin.route) { inclusive = true }
                                }
                            },
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }

                    composable(Screen.AdminEmployeeList.route) {
                        AdminEmployeeListScreen(
                            navController = navController,
                            onAddEmployeeClicked = {
                                navController.navigate(Screen.AdminAddEditEmployee.createRoute("-1"))
                            },
                            onEditEmployeeClicked = { employeeId ->
                                navController.navigate(Screen.AdminAddEditEmployee.createRoute(employeeId))
                            },
                            onViewLogClicked = {
                                navController.navigate(Screen.AdminVisitorLog.route)
                            },
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }

                    composable(
                        route = Screen.AdminAddEditEmployee.route,
                        arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
                    ) {
                        AdminAddEditEmployeeScreen(
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }

                    composable(
                        route = Screen.GuestDetails.route,
                        arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
                    ) {
                        GuestDetailsScreen(
                            onCheckInComplete = {
                                navController.navigate(Screen.Confirmation.route) {
                                    popUpTo(Screen.Welcome.route)
                                }
                            },
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }

                    composable(Screen.Confirmation.route) {
                        ConfirmationScreen(
                            onTimeout = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.CheckOut.route) {
                        CheckOutScreen(onNavigateUp = { navController.navigateUp() })
                    }

                    composable(Screen.AdminVisitorLog.route) {
                        AdminVisitorLogScreen(onNavigateUp = { navController.navigateUp() })
                    }
                }
            }
        }
    }
}