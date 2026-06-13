package com.rabpadev.app
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.rabpadev.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val URL_HOME = "https://www.instagram.com/"
    private val URL_EMAIL = "https://accountscenter.instagram.com/profiles/"
    private val URL_A2F = "https://accountscenter.instagram.com/password_and_security/two_factor/"
    private val PREFS_AF = "rabpadev_autofill"
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val handler = Handler(Looper.getMainLooper())
    private var autoFillRunnable: Runnable? = null

    private val filePicker: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uris: Array<Uri>? = when {
                    data?.clipData != null -> Array(data.clipData!!.itemCount) { i -> data.clipData!!.getItemAt(i).uri }
                    data?.data != null -> arrayOf(data.data!!)
                    else -> null
                }
                fileUploadCallback?.onReceiveValue(uris)
            } else fileUploadCallback?.onReceiveValue(null)
            fileUploadCallback = null
        }

    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
    }

    inner class AutoFillBridge {
        @JavascriptInterface
        fun onPageReady() { handler.post { runAutoFillIfEnabled() } }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        setupWebView()
        setupBack()
        if (savedInstanceState == null) binding.webView.loadUrl(URL_HOME)
        else binding.webView.restoreState(savedInstanceState)
    }


    private val USER_AGENTS = listOf(
        "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; Samsung Galaxy S23) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.6312.40 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 14; Redmi Note 13 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.72 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; OPPO Reno10 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.105 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 12; vivo V25 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 14; Pixel 7a) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.52 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; Samsung Galaxy A54) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.6312.99 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 14; Xiaomi 14 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.60 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 12; Realme GT Neo 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6045.193 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; OnePlus 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.90 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 14; Samsung Galaxy S24 Ultra) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; Poco X5 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6167.143 Mobile Safari/537.36",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Mobile/15E148 Safari/604.1",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1",
        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36"
    )

    private fun randomUserAgent(): String = USER_AGENTS.random()

    private fun applyRandomUserAgent() {
        val ua = randomUserAgent()
        binding.webView.settings.userAgentString = ua
        getSharedPreferences("mikasa_settings", Context.MODE_PRIVATE)
            .edit().putString("last_ua", ua).apply()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val wv = binding.webView
        CookieManager.getInstance().apply { setAcceptCookie(true); setAcceptThirdPartyCookies(wv, true) }
        wv.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true; databaseEnabled = true
            setSupportMultipleWindows(true); javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true; useWideViewPort = true; setSupportZoom(true)
            builtInZoomControls = true; displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT; allowFileAccess = true
            allowContentAccess = true; mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            val savedUa = getSharedPreferences("mikasa_settings", Context.MODE_PRIVATE).getString("last_ua", null)
            userAgentString = savedUa ?: randomUserAgent()
        }
        wv.addJavascriptInterface(AutoFillBridge(), "AndroidBridge")
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                val url = r?.url?.toString() ?: return false
                return when {
                    url.startsWith("https://www.instagram.com") ||
                    url.startsWith("https://accountscenter.instagram.com") ||
                    url.startsWith("https://www.facebook.com") ||
                    url.startsWith("https://l.instagram.com") ||
                    url.startsWith("https://i.instagram.com") -> { v?.loadUrl(url); false }
                    url.startsWith("intent://") -> {
                        try { val i = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            if (i.resolveActivity(packageManager) != null) startActivity(i)
                        } catch (e: Exception) {}
                        true
                    }
                    else -> false
                }
            }
            override fun onPageStarted(v: WebView?, url: String?, f: android.graphics.Bitmap?) {
                super.onPageStarted(v, url, f)
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0
                cancelAutoFill()
            }
            override fun onPageFinished(v: WebView?, url: String?) {
                super.onPageFinished(v, url)
                binding.progressBar.visibility = View.GONE
                CookieManager.getInstance().flush()
                scheduleAutoFill()
            }
        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView?, p: Int) {
                binding.progressBar.progress = p
                if (p == 100) binding.progressBar.visibility = View.GONE
            }
            override fun onShowFileChooser(wv2: WebView?, cb: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                fileUploadCallback?.onReceiveValue(null); fileUploadCallback = cb
                if (AppSettings.isAutoProfileEnabled(this@MainActivity)) {
                    val uri = AppSettings.getRandomPhoto(this@MainActivity)
                    if (uri != null) {
                        try {
                            fileUploadCallback?.onReceiveValue(arrayOf(Uri.parse(uri)))
                            fileUploadCallback = null
                            Toast.makeText(this@MainActivity, getString(R.string.random_photo_used), Toast.LENGTH_SHORT).show()
                            return true
                        } catch (e: Exception) {}
                    }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); return true
                }
                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"; addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                filePicker.launch(intent); return true
            }
            override fun onCreateWindow(v: WebView?, d: Boolean, u: Boolean, msg: Message?): Boolean {
                val nw = WebView(this@MainActivity); nw.settings.javaScriptEnabled = true
                (msg?.obj as? WebView.WebViewTransport)?.webView = nw; msg?.sendToTarget(); return true
            }
        }
    }

    private fun scheduleAutoFill() {
        cancelAutoFill()
        autoFillRunnable = Runnable { runAutoFillIfEnabled() }
        handler.postDelayed(autoFillRunnable!!, 1200)
    }

    private fun cancelAutoFill() {
        autoFillRunnable?.let { handler.removeCallbacks(it) }
        autoFillRunnable = null
    }

    private fun runAutoFillIfEnabled() {
        if (AppSettings.isPasswordAutofillEnabled(this)) {
            val p = AppSettings.getDefaultPassword(this)
            if (p.isNotEmpty()) injectPassword(p)
        }
        if (AppSettings.isAutoFillEnabled(this)) injectSmartFill()
    }

    // Safe escape: replace \ with \\ and ' with \x27 (JS hex - no Kotlin literal issues)
    private fun jsStr(s: String): String = s.replace("\\", "\\\\").replace("'", "\\x27")

    private fun injectSmartFill() {
        val nm = jsStr(if (AppSettings.getFillName(this).isNotEmpty()) AppSettings.getFillName(this) else RandomData.randomName())
        val us = jsStr(if (AppSettings.getFillUsername(this).isNotEmpty()) AppSettings.getFillUsername(this) else RandomData.randomUsername())
        val bd = jsStr(RandomData.randomBirthday(AppSettings.getAgeMin(this), AppSettings.getAgeMax(this)))
        val yr = jsStr(RandomData.birthdayToYear(bd))
        val pw = jsStr(AppSettings.getDefaultPassword(this))

        val js = "(function(){" +
            "if(window.__mf)return;window.__mf=true;" +
            "var NM='" + nm + "',US='" + us + "',PW='" + pw + "',BD='" + bd + "',YR='" + yr + "';" +
            "var NP=['name','nama','full','nombre','nom','nome','ten','имя','姓名','名前','이름'];" +
            "var UP=['username','user','pengguna','utilisateur','benutzername','用户名','ユーザー','사용자'];" +
            "var PP=['password','sandi','contraseña','passwort','пароль','密码','パスワード','비밀번호'];" +
            "var AP=['age','umur','usia','edad','alter','年齢','나이'];" +
            "var BP=['birth','lahir','birthday','fecha','生日','생일','year','tahun','dob'];" +
            "function mt(el,pats){" +
            "var t=((el.placeholder||'')+(el.name||'')+(el.id||'')+(el.getAttribute('aria-label')||'')).toLowerCase();" +
            "try{if(el.id){var lb=document.querySelector('label[for=\"'+el.id+'\"]');if(lb)t+=lb.innerText.toLowerCase();}}catch(e){}" +
            "return pats.some(function(p){return t.indexOf(p)>=0;});}" +
            "function sv(el,v){if(!v||el.dataset.mf)return;" +
            "try{var d=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');" +
            "if(d&&d.set)d.set.call(el,v);else el.value=v;" +
            "el.dataset.mf='1';" +
            "el.dispatchEvent(new Event('input',{bubbles:true}));" +
            "el.dispatchEvent(new Event('change',{bubbles:true}));" +
            "el.dispatchEvent(new Event('blur',{bubbles:true}));}catch(e){}}" +
            "function fi(el){" +
            "if(!el)return;" +
            "var t=(el.type||'').toLowerCase();" +
            "if(t==='hidden'||t==='submit'||t==='button'||t==='checkbox'||t==='radio')return;" +
            "if(t==='password'||mt(el,PP)){sv(el,PW);return;}" +
            "if(mt(el,UP)){sv(el,US);return;}" +
            "if(mt(el,NP)){sv(el,NM);return;}" +
            "if(mt(el,BP)){sv(el,BD||YR);return;}" +
            "if(mt(el,AP)){sv(el,(new Date().getFullYear()-parseInt(YR||'2000')).toString());return;}}" +
            "document.querySelectorAll('input,textarea').forEach(fi);" +
            "document.querySelectorAll('select').forEach(function(sel){" +
            "if(mt(sel,BP)||mt(sel,AP)){" +
            "Array.from(sel.options).forEach(function(o){" +
            "if(o.value===YR||o.text===YR){sel.value=YR;sel.dispatchEvent(new Event('change',{bubbles:true}));}});}});" +
            "var obs=new MutationObserver(function(ms){ms.forEach(function(m){" +
            "m.addedNodes.forEach(function(n){if(n.nodeType!==1)return;" +
            "if(n.tagName==='INPUT'||n.tagName==='TEXTAREA')fi(n);" +
            "n.querySelectorAll&&n.querySelectorAll('input,textarea').forEach(fi);});});});" +
            "obs.observe(document.body||document.documentElement,{childList:true,subtree:true});" +
            "document.addEventListener('focusin',function(e){" +
            "if(e.target&&(e.target.tagName==='INPUT'||e.target.tagName==='TEXTAREA'))fi(e.target);},true);})()"
        binding.webView.evaluateJavascript(js, null)
    }

    private fun injectPassword(pwd: String) {
        val safe = jsStr(pwd)
        val js = "(function(){" +
            "var els=document.querySelectorAll('input[type=password]');" +
            "if(!els.length)return;" +
            "var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "els.forEach(function(el){s.call(el,'" + safe + "');" +
            "el.dispatchEvent(new Event('input',{bubbles:true}));" +
            "el.dispatchEvent(new Event('change',{bubbles:true}));});})()"
        binding.webView.evaluateJavascript(js, null)
    }

    private fun copyCookie() {
        val url = binding.webView.url ?: URL_HOME
        val cookies = CookieManager.getInstance().getCookie(url)
        if (cookies.isNullOrEmpty()) {
            Toast.makeText(this, "Tidak ada cookie", Toast.LENGTH_SHORT).show(); return
        }
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Cookie", cookies))
        Toast.makeText(this, getString(R.string.cookie_copied), Toast.LENGTH_SHORT).show()
    }

    private fun showAutoFillDialog() {
        val p = getSharedPreferences(PREFS_AF, Context.MODE_PRIVATE)
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_autofill, null)
        val etName = v.findViewById<TextInputEditText>(R.id.etName)
        val etUser = v.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = v.findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = v.findViewById<TextInputEditText>(R.id.etPhone)
        val etBday = v.findViewById<TextInputEditText>(R.id.etBirthday)
        val etPwd = v.findViewById<TextInputEditText>(R.id.etPassword)
        etName.setText(p.getString("af_name", ""))
        etUser.setText(p.getString("af_username", ""))
        etEmail.setText(p.getString("af_email", ""))
        etPhone.setText(p.getString("af_phone", ""))
        etBday.setText(p.getString("af_birthday", ""))
        val sp = p.getString("af_password", "")
        etPwd.setText(if (!sp.isNullOrEmpty()) sp else AppSettings.getDefaultPassword(this))
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.autofill_title))
            .setView(v)
            .setPositiveButton(getString(R.string.autofill_btn_fill)) { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                if (name.isEmpty() && email.isEmpty()) {
                    Toast.makeText(this, getString(R.string.autofill_empty), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                p.edit().putString("af_name", name)
                    .putString("af_username", etUser.text.toString().trim())
                    .putString("af_email", email)
                    .putString("af_phone", etPhone.text.toString().trim())
                    .putString("af_birthday", etBday.text.toString().trim())
                    .putString("af_password", etPwd.text.toString().trim()).apply()
                injectManualFill(name, etUser.text.toString().trim(), email,
                    etPhone.text.toString().trim(), etBday.text.toString().trim(),
                    etPwd.text.toString().trim())
                Toast.makeText(this, getString(R.string.autofill_filled), Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton(getString(R.string.autofill_btn_save)) { _, _ ->
                p.edit().putString("af_name", etName.text.toString().trim())
                    .putString("af_username", etUser.text.toString().trim())
                    .putString("af_email", etEmail.text.toString().trim())
                    .putString("af_phone", etPhone.text.toString().trim())
                    .putString("af_birthday", etBday.text.toString().trim())
                    .putString("af_password", etPwd.text.toString().trim()).apply()
                Toast.makeText(this, getString(R.string.autofill_saved), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null).show()
    }

    private fun injectManualFill(name: String, user: String, email: String,
                                  phone: String, bday: String, pwd: String) {
        val nm = jsStr(name); val us = jsStr(user); val em = jsStr(email)
        val ph = jsStr(phone); val bd = jsStr(bday); val pw = jsStr(pwd)
        val js = "(function(){" +
            "function f(sels,v){if(!v)return;" +
            "var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "sels.forEach(function(sel){document.querySelectorAll(sel).forEach(function(el){" +
            "s.call(el,v);el.dispatchEvent(new Event('input',{bubbles:true}));" +
            "el.dispatchEvent(new Event('change',{bubbles:true}));});});}" +
            "f(['input[name=name]','input[name=fullName]','input[autocomplete=name]'],'" + nm + "');" +
            "f(['input[name=username]','input[autocomplete=username]'],'" + us + "');" +
            "f(['input[type=email]','input[name=email]','input[name=emailOrPhone]'],'" + em + "');" +
            "f(['input[type=tel]','input[name=phone]'],'" + ph + "');" +
            "f(['input[name=birthday]'],'" + bd + "');" +
            "f(['input[type=password]','input[name=password]'],'" + pw + "');" +
            "})()"
        binding.webView.evaluateJavascript(js, null)
    }

    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.clear_data_title))
            .setMessage(getString(R.string.clear_data_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
                WebStorage.getInstance().deleteAllData()
                binding.webView.clearCache(true)
                binding.webView.clearHistory()
                binding.webView.clearFormData()
                applyRandomUserAgent()
                binding.webView.loadUrl(URL_HOME)
                Toast.makeText(this, getString(R.string.clear_data_success), Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.no), null).show()
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exit_title))
            .setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no), null).show()
    }

    private fun setupBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) binding.webView.goBack() else showExitDialog()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu); return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_home -> { binding.webView.loadUrl(URL_HOME); true }
        R.id.action_email -> { binding.webView.loadUrl(URL_EMAIL); true }
        R.id.action_a2f -> { binding.webView.loadUrl(URL_A2F); true }
        R.id.action_refresh -> { binding.webView.reload(); true }
        R.id.action_copy_cookie -> { copyCookie(); true }
        R.id.action_autofill -> { showAutoFillDialog(); true }
        R.id.action_authenticator -> { startActivity(Intent(this, AuthenticatorActivity::class.java)); true }
        R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
        R.id.action_clear_data -> { showClearDataDialog(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(out: Bundle) { super.onSaveInstanceState(out); binding.webView.saveState(out) }
    override fun onResume() { super.onResume(); binding.webView.resumeTimers(); binding.webView.onResume() }
    override fun onPause() { super.onPause(); cancelAutoFill(); binding.webView.pauseTimers(); binding.webView.onPause(); CookieManager.getInstance().flush() }
    override fun onDestroy() { super.onDestroy(); cancelAutoFill(); binding.webView.destroy() }
}
