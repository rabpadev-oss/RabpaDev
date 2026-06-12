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
    private val filePicker: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        setupWebView(); setupBack()
        if (savedInstanceState == null) binding.webView.loadUrl(URL_HOME)
        else binding.webView.restoreState(savedInstanceState)
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val wv = binding.webView
        CookieManager.getInstance().apply { setAcceptCookie(true); setAcceptThirdPartyCookies(wv, true) }
        wv.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true; databaseEnabled = true
            setSupportMultipleWindows(true); javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true; useWideViewPort = true; setSupportZoom(true)
            builtInZoomControls = true; displayZoomControls = false; cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true; allowContentAccess = true; mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
        }
        wv.addJavascriptInterface(AutoFillBridge(), "AndroidBridge")
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                val url = r?.url?.toString() ?: return false
                return when {
                    url.startsWith("https://www.instagram.com") || url.startsWith("https://accountscenter.instagram.com") || url.startsWith("https://www.facebook.com") || url.startsWith("https://l.instagram.com") || url.startsWith("https://i.instagram.com") -> { v?.loadUrl(url); false }
                    url.startsWith("intent://") -> { try { val i = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); if (i.resolveActivity(packageManager) != null) startActivity(i) } catch (e: Exception) {}; true }
                    else -> false
                }
            }
            override fun onPageStarted(v: WebView?, url: String?, f: android.graphics.Bitmap?) {
                super.onPageStarted(v, url, f); binding.progressBar.visibility = View.VISIBLE; binding.progressBar.progress = 0; cancelAutoFill()
            }
            override fun onPageFinished(v: WebView?, url: String?) {
                super.onPageFinished(v, url); binding.progressBar.visibility = View.GONE; CookieManager.getInstance().flush()
                scheduleAutoFill()
            }
        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView?, p: Int) { binding.progressBar.progress = p; if (p == 100) binding.progressBar.visibility = View.GONE }
            override fun onShowFileChooser(wv2: WebView?, cb: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                fileUploadCallback?.onReceiveValue(null); fileUploadCallback = cb
                if (AppSettings.isAutoProfileEnabled(this@MainActivity)) {
                    val uri = AppSettings.getRandomPhoto(this@MainActivity)
                    if (uri != null) { try { fileUploadCallback?.onReceiveValue(arrayOf(Uri.parse(uri))); fileUploadCallback = null; Toast.makeText(this@MainActivity, getString(R.string.random_photo_used), Toast.LENGTH_SHORT).show(); return true } catch (e: Exception) {} }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); return true }
                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*"; addCategory(Intent.CATEGORY_OPENABLE); putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) }
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
    private fun cancelAutoFill() { autoFillRunnable?.let { handler.removeCallbacks(it) }; autoFillRunnable = null }
    private fun runAutoFillIfEnabled() {
        if (AppSettings.isPasswordAutofillEnabled(this)) { val p = AppSettings.getDefaultPassword(this); if (p.isNotEmpty()) injectPassword(p) }
        if (AppSettings.isAutoFillEnabled(this)) { injectSmartFill() }
    }
    private fun esc(s: String) = s.replace("\\","\\\\").replace("'","\\'").replace("\n","")
    private fun injectSmartFill() {
        val savedName = AppSettings.getFillName(this)
        val savedUser = AppSettings.getFillUsername(this)
        val name = if (savedName.isNotEmpty()) savedName else RandomData.randomName()
        val username = if (savedUser.isNotEmpty()) savedUser else RandomData.randomUsername()
        val bday = RandomData.randomBirthday(AppSettings.getAgeMin(this), AppSettings.getAgeMax(this))
        val year = RandomData.birthdayToYear(bday)
        val pwd = AppSettings.getDefaultPassword(this)
        val js = buildString {
            append("(function(){")
            append("if(window.__mfilled)return;window.__mfilled=true;")
            append("var NM='${esc(name)}',US='${esc(username)}',PW='${esc(pwd)}',BD='${esc(bday)}',YR='${esc(year)}';")
            append("var NP=['name','nama','full','nombre','nom','nome','имя','الاسم','姓名','名前','이름','tên'];")
            append("var UP=['username','user','pengguna','utilisateur','benutzername','用户名','ユーザー','사용자','compte','konto'];")
            append("var PP=['password','sandi','contraseña','passwort','пароль','密码','パスワード','비밀번호','mot de passe'];")
            append("var AP=['age','umur','usia','edad','alter','возраст','العمر','年齢','나이','tuổi','an'];")
            append("var BP=['birth','lahir','birthday','fecha','anniversaire','день','生日','誕生','생일','year','tahun','anno'];")
            append("function match(el,pats){var t=((el.placeholder||'')+(el.name||'')+(el.id||'')+(el.getAttribute('aria-label')||'')).toLowerCase();")
            append("if(el.id){var lb=document.querySelector('label[for="'+el.id+'"]');if(lb)t+=lb.innerText.toLowerCase();}")
            append("return pats.some(function(p){return t.indexOf(p)>=0;});}")
            append("function sv(el,v){if(!v||el.dataset.mf)return;try{")
            append("var d=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');")
            append("if(d&&d.set)d.set.call(el,v);else el.value=v;")
            append("el.dataset.mf='1';")
            append("['input','change','blur'].forEach(function(e){el.dispatchEvent(new Event(e,{bubbles:true}));});")
            append("}catch(e){}}")
            append("function fill(el){")
            append("if(el.type==='hidden'||el.type==='submit'||el.type==='button'||el.type==='checkbox'||el.type==='radio')return;")
            append("if(el.type==='password'||match(el,PP)){if(PW)sv(el,PW);return;}")
            append("if(match(el,UP)){if(US)sv(el,US);return;}")
            append("if(match(el,NP)){if(NM)sv(el,NM);return;}")
            append("if(match(el,BP)){sv(el,BD||YR);return;}")
            append("if(match(el,AP)){var ag=(new Date().getFullYear()-parseInt(YR||'2000')).toString();sv(el,ag);return;}")
            append("}")
            append("document.querySelectorAll('input,textarea').forEach(fill);")
            append("document.querySelectorAll('select').forEach(function(sel){")
            append("if(match(sel,BP)||match(sel,AP)){Array.from(sel.options).forEach(function(o){if(o.value===YR||o.text===YR){sel.value=YR;sel.dispatchEvent(new Event('change',{bubbles:true}));}});}")
            append("});")
            append("var obs=new MutationObserver(function(ms){ms.forEach(function(m){m.addedNodes.forEach(function(n){")
            append("if(n.nodeType!==1)return;")
            append("if(n.tagName==='INPUT'||n.tagName==='TEXTAREA')fill(n);")
            append("n.querySelectorAll&&n.querySelectorAll('input,textarea').forEach(fill);")
            append("});});});")
            append("obs.observe(document.body||document.documentElement,{childList:true,subtree:true});")
            append("document.addEventListener('focusin',function(e){if(e.target&&(e.target.tagName==='INPUT'||e.target.tagName==='TEXTAREA'))fill(e.target);},true);")
            append("})()")
        }
        binding.webView.evaluateJavascript(js, null)
    }
    private fun injectPassword(pwd: String) {
        val safe = esc(pwd)
        binding.webView.evaluateJavascript("(function(){var els=document.querySelectorAll('input[type="password"]');if(!els.length)return;var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;els.forEach(function(el){s.call(el,'$safe');el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));});})();", null)
    }
    private fun copyCookie() {
        val url = binding.webView.url ?: URL_HOME
        val cookies = CookieManager.getInstance().getCookie(url)
        if (cookies.isNullOrEmpty()) { Toast.makeText(this, "Tidak ada cookie untuk halaman ini", Toast.LENGTH_SHORT).show(); return }
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Cookie", cookies))
        Toast.makeText(this, getString(R.string.cookie_copied), Toast.LENGTH_SHORT).show()
    }
    private fun showAutoFillDialog() {
        val p = getSharedPreferences(PREFS_AF, Context.MODE_PRIVATE)
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_autofill, null)
        val etName = v.findViewById<TextInputEditText>(R.id.etName); val etUser = v.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = v.findViewById<TextInputEditText>(R.id.etEmail); val etPhone = v.findViewById<TextInputEditText>(R.id.etPhone)
        val etBday = v.findViewById<TextInputEditText>(R.id.etBirthday); val etPwd = v.findViewById<TextInputEditText>(R.id.etPassword)
        etName.setText(p.getString("af_name","")); etUser.setText(p.getString("af_username",""))
        etEmail.setText(p.getString("af_email","")); etPhone.setText(p.getString("af_phone",""))
        etBday.setText(p.getString("af_birthday",""))
        val sp=p.getString("af_password",""); etPwd.setText(if(!sp.isNullOrEmpty())sp else AppSettings.getDefaultPassword(this))
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.autofill_title)).setView(v)
            .setPositiveButton(getString(R.string.autofill_btn_fill)){_,_->
                val name=etName.text.toString().trim();val email=etEmail.text.toString().trim()
                if(name.isEmpty()&&email.isEmpty()){Toast.makeText(this,getString(R.string.autofill_empty),Toast.LENGTH_SHORT).show();return@setPositiveButton}
                p.edit().putString("af_name",name).putString("af_username",etUser.text.toString().trim()).putString("af_email",email).putString("af_phone",etPhone.text.toString().trim()).putString("af_birthday",etBday.text.toString().trim()).putString("af_password",etPwd.text.toString().trim()).apply()
                injectManualFill(name,etUser.text.toString().trim(),email,etPhone.text.toString().trim(),etBday.text.toString().trim(),etPwd.text.toString().trim())
                Toast.makeText(this,getString(R.string.autofill_filled),Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton(getString(R.string.autofill_btn_save)){_,_->
                p.edit().putString("af_name",etName.text.toString().trim()).putString("af_username",etUser.text.toString().trim()).putString("af_email",etEmail.text.toString().trim()).putString("af_phone",etPhone.text.toString().trim()).putString("af_birthday",etBday.text.toString().trim()).putString("af_password",etPwd.text.toString().trim()).apply()
                Toast.makeText(this,getString(R.string.autofill_saved),Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.cancel),null).show()
    }
    private fun injectManualFill(name:String,user:String,email:String,phone:String,bday:String,pwd:String) {
        binding.webView.evaluateJavascript("(function(){function f(s,v){if(!v)return;var sd=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;s.forEach(function(sel){document.querySelectorAll(sel).forEach(function(el){sd.call(el,v);el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));});});}f(['input[name="name"]','input[name="fullName"]','input[placeholder*="ama"]','input[autocomplete="name"]'],'${esc(name)}');f(['input[name="username"]','input[placeholder*="ser"]','input[autocomplete="username"]'],'${esc(user)}');f(['input[type="email"]','input[name="email"]','input[name="emailOrPhone"]'],'${esc(email)}');f(['input[type="tel"]','input[name="phone"]'],'${esc(phone)}');f(['input[name="birthday"]','input[placeholder*="lahir"]','input[placeholder*="birth"]'],'${esc(bday)}');f(['input[type="password"]','input[name="password"]'],'${esc(pwd)}');})()", null)
    }
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.clear_data_title)).setMessage(getString(R.string.clear_data_message))
            .setPositiveButton(getString(R.string.yes)){_,_->CookieManager.getInstance().removeAllCookies(null);CookieManager.getInstance().flush();WebStorage.getInstance().deleteAllData();binding.webView.clearCache(true);binding.webView.clearHistory();binding.webView.clearFormData();binding.webView.loadUrl(URL_HOME);Toast.makeText(this,getString(R.string.clear_data_success),Toast.LENGTH_SHORT).show()}
            .setNegativeButton(getString(R.string.no),null).show()
    }
    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.exit_title)).setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.yes)){_,_->finish()}.setNegativeButton(getString(R.string.no),null).show()
    }
    private fun setupBack() {
        onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){override fun handleOnBackPressed(){if(binding.webView.canGoBack())binding.webView.goBack()else showExitDialog()}})
    }
    override fun onCreateOptionsMenu(menu:Menu):Boolean{menuInflater.inflate(R.menu.menu_main,menu);return true}
    override fun onOptionsItemSelected(item:MenuItem):Boolean=when(item.itemId){
        R.id.action_home->{binding.webView.loadUrl(URL_HOME);true}
        R.id.action_email->{binding.webView.loadUrl(URL_EMAIL);true}
        R.id.action_a2f->{binding.webView.loadUrl(URL_A2F);true}
        R.id.action_refresh->{binding.webView.reload();true}
        R.id.action_copy_cookie->{copyCookie();true}
        R.id.action_autofill->{showAutoFillDialog();true}
        R.id.action_authenticator->{startActivity(Intent(this,AuthenticatorActivity::class.java));true}
        R.id.action_settings->{startActivity(Intent(this,SettingsActivity::class.java));true}
        R.id.action_clear_data->{showClearDataDialog();true}
        else->super.onOptionsItemSelected(item)
    }
    override fun onSaveInstanceState(out:Bundle){super.onSaveInstanceState(out);binding.webView.saveState(out)}
    override fun onResume(){super.onResume();binding.webView.resumeTimers();binding.webView.onResume()}
    override fun onPause(){super.onPause();cancelAutoFill();binding.webView.pauseTimers();binding.webView.onPause();CookieManager.getInstance().flush()}
    override fun onDestroy(){super.onDestroy();cancelAutoFill();binding.webView.destroy()}
}