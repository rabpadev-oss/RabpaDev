package com.rabpadev.app
import android.content.Context
object AppSettings {
    private const val PREFS = "mikasa_settings"
    private const val KEY_PASSWORD_AUTOFILL = "password_autofill_enabled"
    private const val KEY_DEFAULT_PASSWORD = "default_password"
    private const val KEY_AUTO_PROFILE = "auto_profile_enabled"
    private const val KEY_PROFILE_URI = "profile_photo_uri"
    fun isPasswordAutofillEnabled(ctx: Context): Boolean = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PASSWORD_AUTOFILL, false)
    fun setPasswordAutofillEnabled(ctx: Context, v: Boolean) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_PASSWORD_AUTOFILL, v).apply()
    fun getDefaultPassword(ctx: Context): String = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_DEFAULT_PASSWORD, "") ?: ""
    fun setDefaultPassword(ctx: Context, v: String) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_DEFAULT_PASSWORD, v).apply()
    fun isAutoProfileEnabled(ctx: Context): Boolean = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_AUTO_PROFILE, false)
    fun setAutoProfileEnabled(ctx: Context, v: Boolean) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTO_PROFILE, v).apply()
    fun getProfilePhotoUri(ctx: Context): String = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PROFILE_URI, "") ?: ""
    fun setProfilePhotoUri(ctx: Context, v: String) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PROFILE_URI, v).apply()
}
