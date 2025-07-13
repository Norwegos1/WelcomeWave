package com.exposystems.welcomewave.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome_screen")
    data object EmployeeSelect : Screen("employee_select_screen")
    data object CheckOut : Screen("check_out_screen")
    data object GuestDetails : Screen("guest_details_screen/{employeeId}") {
        // CHANGED: Accept String employeeId
        fun createRoute(employeeId: String) = "guest_details_screen/$employeeId"
    }
    object PreRegisteredGuestList : Screen("pre_registered_guest_list")
    data object Confirmation : Screen("confirmation_screen")
    data object AdminLogin : Screen("admin_login_screen")
    data object AdminEmployeeList : Screen("admin_employee_list_screen")
    data object AdminVisitorLog : Screen("admin_visitor_log_screen")
    data object AdminAddEditEmployee : Screen("admin_add_edit_employee_screen/{employeeId}") {
        // CHANGED: Accept String employeeId
        fun createRoute(employeeId: String) = "admin_add_edit_employee_screen/$employeeId"
    }
}