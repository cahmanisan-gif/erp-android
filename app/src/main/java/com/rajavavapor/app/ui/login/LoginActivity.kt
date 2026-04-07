package com.rajavavapor.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.data.SessionManager
import com.rajavavapor.app.util.AnimationHelper
import com.rajavavapor.app.databinding.ActivityLoginBinding
import com.rajavavapor.app.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.updatePadding(top = bars.top, bottom = bars.bottom, left = bars.left, right = bars.right)
            insets
        }

        val doLogin = {
            val username = binding.etUsername.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""
            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "Username dan password wajib diisi", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.login(this, username, password)
            }
        }

        binding.btnLogin.setOnClickListener {
            AnimationHelper.hapticClick(it)
            AnimationHelper.bounceClick(it)
            doLogin()
        }

        // Enter key on password field triggers login
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                doLogin()
                true
            } else false
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success == true) {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        binding.btnFaceLogin.setOnClickListener {
            val session = SessionManager(this)
            val cabangId = session.getCabangId()
            startActivity(Intent(this, FaceLoginActivity::class.java).apply {
                putExtra("cabang_id", cabangId)
            })
        }
    }
}
