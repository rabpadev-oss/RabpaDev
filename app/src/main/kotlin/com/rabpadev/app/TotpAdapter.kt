package com.rabpadev.app
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
class TotpAdapter(private val accounts: MutableList<TotpAccount>, private val onDelete: (TotpAccount) -> Unit) : RecyclerView.Adapter<TotpAdapter.VH>() {
    private val handler = Handler(Looper.getMainLooper())
    private var ticker: Runnable? = null
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvAccountName)
        val tvCode: TextView = v.findViewById(R.id.tvTotpCode)
        val tvCountdown: TextView = v.findViewById(R.id.tvCountdown)
        val progress: CircularProgressIndicator = v.findViewById(R.id.progressCountdown)
        val btnCopy: MaterialButton = v.findViewById(R.id.btnCopy)
        val btnDelete: MaterialButton = v.findViewById(R.id.btnDelete)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(LayoutInflater.from(p.context).inflate(R.layout.item_totp, p, false))
    override fun getItemCount() = accounts.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val acc = accounts[pos]; h.tvName.text = acc.name
        fun update() {
            try {
                val code = TotpGenerator.generate(acc.secret); val secs = TotpGenerator.secondsRemaining()
                h.tvCode.text = code; h.tvCountdown.text = "${secs}s"
                h.progress.max = 30; h.progress.progress = secs
                val color = when { secs <= 5 -> ContextCompat.getColor(h.itemView.context, R.color.totp_red); secs <= 10 -> ContextCompat.getColor(h.itemView.context, R.color.totp_yellow); else -> ContextCompat.getColor(h.itemView.context, R.color.totp_green) }
                h.tvCode.setTextColor(color); h.progress.setIndicatorColor(color)
            } catch (e: Exception) { h.tvCode.text = "ERR" }
        }
        update()
        h.btnCopy.setOnClickListener {
            val raw = TotpGenerator.generateRaw(acc.secret)
            (it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("TOTP", raw))
            Toast.makeText(it.context, it.context.getString(R.string.authenticator_copy), Toast.LENGTH_SHORT).show()
        }
        h.btnDelete.setOnClickListener { onDelete(acc) }
    }
    fun startTicking() { ticker = object : Runnable { override fun run() { notifyDataSetChanged(); handler.postDelayed(this, 1000) } }; handler.post(ticker!!) }
    fun stopTicking() { ticker?.let { handler.removeCallbacks(it) } }
}