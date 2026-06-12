package com.rabpadev.app
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
data class TotpAccount(val id: String, val name: String, val secret: String)
object TotpRepository {
    private const val PREFS = "totp_accounts"
    private const val KEY = "accounts_json"
    fun getAll(c: Context): MutableList<TotpAccount> {
        val json = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(json); val list = mutableListOf<TotpAccount>()
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); list.add(TotpAccount(o.getString("id"), o.getString("name"), o.getString("secret"))) }
        return list
    }
    fun save(c: Context, accounts: List<TotpAccount>) {
        val arr = JSONArray()
        for (a in accounts) { val o = JSONObject(); o.put("id", a.id); o.put("name", a.name); o.put("secret", a.secret); arr.put(o) }
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
    fun add(c: Context, a: TotpAccount) { val l = getAll(c); l.add(a); save(c, l) }
    fun delete(c: Context, id: String) { save(c, getAll(c).filter { it.id != id }) }
}