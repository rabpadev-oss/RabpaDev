package com.rabpadev.app
import android.content.Context
import org.json.JSONArray
object AppSettings {
    private const val PREFS = "mikasa_settings"
    fun isPasswordAutofillEnabled(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("pwd_autofill", false)
    fun setPasswordAutofillEnabled(c: Context, v: Boolean) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("pwd_autofill", v).apply()
    fun getDefaultPassword(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("default_pwd", "") ?: ""
    fun setDefaultPassword(c: Context, v: String) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("default_pwd", v).apply()
    fun isAutoProfileEnabled(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("auto_profile", false)
    fun setAutoProfileEnabled(c: Context, v: Boolean) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("auto_profile", v).apply()
    fun isAutoFillEnabled(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("auto_fill", false)
    fun setAutoFillEnabled(c: Context, v: Boolean) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("auto_fill", v).apply()
    fun getFillName(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("fill_name", "") ?: ""
    fun setFillName(c: Context, v: String) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("fill_name", v).apply()
    fun getFillUsername(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("fill_username", "") ?: ""
    fun setFillUsername(c: Context, v: String) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("fill_username", v).apply()
    fun getAgeMin(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("age_min", 20)
    fun setAgeMin(c: Context, v: Int) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("age_min", v).apply()
    fun getAgeMax(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("age_max", 49)
    fun setAgeMax(c: Context, v: Int) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("age_max", v).apply()
    // Multi-photo list
    fun getPhotoList(c: Context): MutableList<String> {
        val json = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("photo_list", "[]") ?: "[]"
        val arr = JSONArray(json); val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        return list
    }
    fun savePhotoList(c: Context, list: List<String>) {
        val arr = JSONArray(); list.forEach { arr.put(it) }
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("photo_list", arr.toString()).apply()
    }
    fun addPhoto(c: Context, uri: String) { val l = getPhotoList(c); l.add(uri); savePhotoList(c, l) }
    fun removePhoto(c: Context, uri: String) { savePhotoList(c, getPhotoList(c).filter { it != uri }) }
    fun getRandomPhoto(c: Context): String? { val l = getPhotoList(c); return if (l.isEmpty()) null else l.random() }
}
