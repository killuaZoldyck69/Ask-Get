/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

public class SessionManager {
    public static String loggedInEmail;
    public static String loggedInName;

    public static void setUser(String name, String email) {
        loggedInName = name;
        loggedInEmail = email;
    }

    public static String getLoggedInName() {
        return loggedInName;
    }

    public static String getLoggedInEmail() {
        return loggedInEmail;
    }
}

