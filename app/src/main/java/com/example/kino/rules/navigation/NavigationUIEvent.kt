package com.example.kino.rules.navigation
sealed class NavigationUIEvent(){
    object DoctorsListButtonClicked: NavigationUIEvent()
    object ChatsButtonClicked: NavigationUIEvent()
    object ProfileButtonClicked: NavigationUIEvent()

    object PatientsListButtonClicked: NavigationUIEvent()
    object RoutinesButtonClicked: NavigationUIEvent()
    object ProfileDoctorButtonClicked: NavigationUIEvent()


}