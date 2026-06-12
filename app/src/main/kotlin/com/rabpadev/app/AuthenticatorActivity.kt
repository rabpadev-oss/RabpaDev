package com.rabpadev.app
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rabpadev.app.databinding.ActivityAuthenticatorBinding
import java.util.UUID
class AuthenticatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticatorBinding
    private lateinit var adapter: TotpAdapter
    private val accounts = mutableListOf<TotpAccount>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        accounts.addAll(TotpRepository.getAll(this))
        adapter = TotpAdapter(accounts) { acc ->
            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    TotpRepository.delete(this, acc.id); accounts.removeAll { it.id == acc.id }
                    adapter.notifyDataSetChanged(); updateEmpty()
                    Toast.makeText(this, getString(R.string.authenticator_deleted), Toast.LENGTH_SHORT).show()
                }.setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { showAddDialog() }
        // Quick OTP button
        binding.btnQuickGenerate.setOnClickListener {
            val raw = binding.etQuickSecret.text.toString().trim()
            if (raw.isEmpty()) { Toast.makeText(this, "Masukkan secret key dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val secret = TotpGenerator.extractSecret(raw)
            if (secret == null || !TotpGenerator.isValidSecret(secret)) {
                Toast.makeText(this, getString(R.string.authenticator_invalid_secret), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            try {
                val code = TotpGenerator.generate(secret)
                val raw6 = code.replace(" ", "")
                binding.tvQuickOtp.visibility = View.VISIBLE
                binding.tvQuickOtp.text = code
                // Auto copy
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("OTP", raw6))
                Toast.makeText(this, getString(R.string.authenticator_auto_copy, code), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        updateEmpty()
    }
    private fun updateEmpty() {
        binding.tvEmpty.visibility = if (accounts.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (accounts.isEmpty()) View.GONE else View.VISIBLE
    }
    private fun showAddDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_totp, null)
        val tilName = view.findViewById<TextInputLayout>(R.id.tilTotpName)
        val tilSecret = view.findViewById<TextInputLayout>(R.id.tilTotpSecret)
        val etName = view.findViewById<TextInputEditText>(R.id.etTotpName)
        val etSecret = view.findViewById<TextInputEditText>(R.id.etTotpSecret)
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.authenticator_add)).setView(view)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val name = etName.text.toString().trim()
                val rawSecret = etSecret.text.toString().trim()
                if (name.isEmpty()) { tilName.error = "Nama tidak boleh kosong"; return@setPositiveButton }
                val secret = TotpGenerator.extractSecret(rawSecret)
                if (secret == null || !TotpGenerator.isValidSecret(secret)) { tilSecret.error = getString(R.string.authenticator_invalid_secret); return@setPositiveButton }
                val acc = TotpAccount(UUID.randomUUID().toString(), name, secret)
                TotpRepository.add(this, acc); accounts.add(acc)
                adapter.notifyItemInserted(accounts.size - 1); updateEmpty()
                Toast.makeText(this, getString(R.string.authenticator_added), Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.cancel), null).show()
    }
    override fun onResume() { super.onResume(); adapter.startTicking() }
    override fun onPause() { super.onPause(); adapter.stopTicking() }
}
