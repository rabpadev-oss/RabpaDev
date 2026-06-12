package com.rabpadev.app
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rabpadev.app.databinding.ActivitySettingsBinding
import android.webkit.CookieManager
import android.webkit.WebStorage
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    // Multi-photo picker (Android 13+)
    private val multiPhotoPicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        var added = 0
        uris.forEach { uri ->
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            AppSettings.addPhoto(this, uri.toString())
            added++
        }
        if (added > 0) {
            updatePhotoCount()
            Toast.makeText(this, "$added foto ditambahkan!", Toast.LENGTH_SHORT).show()
        }
    }
    // Fallback single picker
    private val singlePhotoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try { contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            AppSettings.addPhoto(this, it.toString())
            updatePhotoCount()
            Toast.makeText(this, getString(R.string.photo_added), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        // Load settings
        binding.switchAutoFill.isChecked = AppSettings.isAutoFillEnabled(this)
        binding.etFillName.setText(AppSettings.getFillName(this))
        binding.etFillUsername.setText(AppSettings.getFillUsername(this))
        binding.etAgeMin.setText(AppSettings.getAgeMin(this).toString())
        binding.etAgeMax.setText(AppSettings.getAgeMax(this).toString())
        binding.switchPasswordAutofill.isChecked = AppSettings.isPasswordAutofillEnabled(this)
        binding.etDefaultPassword.setText(AppSettings.getDefaultPassword(this))
        binding.switchAutoProfile.isChecked = AppSettings.isAutoProfileEnabled(this)
        updatePhotoCount()
        binding.btnAddPhoto.setOnClickListener { multiPhotoPicker.launch("image/*") }
        binding.btnViewPhotos.setOnClickListener { showPhotoListDialog() }
        binding.btnOpenAuthenticator.setOnClickListener { startActivity(Intent(this, AuthenticatorActivity::class.java)) }
        binding.btnClearCookies.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Cookies").setMessage("Hapus semua cookies?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> CookieManager.getInstance().removeAllCookies(null); CookieManager.getInstance().flush(); Toast.makeText(this, getString(R.string.cookies_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnClearCache.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Cache").setMessage("Hapus semua cache?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> WebStorage.getInstance().deleteAllData(); Toast.makeText(this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnClearSession.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Session").setMessage("Hapus session login?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> CookieManager.getInstance().removeSessionCookies(null); CookieManager.getInstance().flush(); Toast.makeText(this, getString(R.string.session_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnSaveSettings.setOnClickListener { saveSettings() }
    }
    private fun updatePhotoCount() {
        val count = AppSettings.getPhotoList(this).size
        binding.tvPhotoCount.text = "$count foto tersimpan"
    }
    private fun showPhotoListDialog() {
        val photos = AppSettings.getPhotoList(this)
        if (photos.isEmpty()) { Toast.makeText(this, getString(R.string.photo_list_empty), Toast.LENGTH_SHORT).show(); return }
        val labels = photos.mapIndexed { i, uri -> "Foto ${i+1}: ...${uri.takeLast(30)}" }.toTypedArray()
        MaterialAlertDialogBuilder(this).setTitle("Daftar Foto Tersimpan (${photos.size})")
            .setItems(labels) { _, idx ->
                MaterialAlertDialogBuilder(this).setTitle("Hapus foto ini?")
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        AppSettings.removePhoto(this, photos[idx]); updatePhotoCount()
                        Toast.makeText(this, getString(R.string.photo_deleted), Toast.LENGTH_SHORT).show()
                    }.setNegativeButton(getString(R.string.cancel), null).show()
            }
            .setNegativeButton("Tutup", null).show()
    }
    private fun saveSettings() {
        AppSettings.setAutoFillEnabled(this, binding.switchAutoFill.isChecked)
        AppSettings.setFillName(this, binding.etFillName.text.toString().trim())
        AppSettings.setFillUsername(this, binding.etFillUsername.text.toString().trim())
        AppSettings.setAgeMin(this, binding.etAgeMin.text.toString().toIntOrNull() ?: 20)
        AppSettings.setAgeMax(this, binding.etAgeMax.text.toString().toIntOrNull() ?: 49)
        AppSettings.setPasswordAutofillEnabled(this, binding.switchPasswordAutofill.isChecked)
        AppSettings.setDefaultPassword(this, binding.etDefaultPassword.text.toString())
        AppSettings.setAutoProfileEnabled(this, binding.switchAutoProfile.isChecked)
        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
    }
}
