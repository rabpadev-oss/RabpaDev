package com.rabpadev.app
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rabpadev.app.databinding.ActivitySettingsBinding
import android.webkit.CookieManager
import android.webkit.WebStorage
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var selectedPhotoUri: String = ""
    private val photoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try { contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            selectedPhotoUri = it.toString()
            binding.ivProfilePreview.setImageURI(it)
            AppSettings.setProfilePhotoUri(this, selectedPhotoUri)
            Toast.makeText(this, getString(R.string.photo_selected), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.switchPasswordAutofill.isChecked = AppSettings.isPasswordAutofillEnabled(this)
        binding.etDefaultPassword.setText(AppSettings.getDefaultPassword(this))
        binding.switchAutoProfile.isChecked = AppSettings.isAutoProfileEnabled(this)
        selectedPhotoUri = AppSettings.getProfilePhotoUri(this)
        if (selectedPhotoUri.isNotEmpty()) { try { binding.ivProfilePreview.setImageURI(Uri.parse(selectedPhotoUri)) } catch (e: Exception) { selectedPhotoUri = "" } }
        binding.btnChoosePhoto.setOnClickListener { photoPicker.launch("image/*") }
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
        binding.btnSaveSettings.setOnClickListener {
            AppSettings.setPasswordAutofillEnabled(this, binding.switchPasswordAutofill.isChecked)
            AppSettings.setDefaultPassword(this, binding.etDefaultPassword.text.toString())
            AppSettings.setAutoProfileEnabled(this, binding.switchAutoProfile.isChecked)
            if (selectedPhotoUri.isNotEmpty()) AppSettings.setProfilePhotoUri(this, selectedPhotoUri)
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
        }
    }
}
