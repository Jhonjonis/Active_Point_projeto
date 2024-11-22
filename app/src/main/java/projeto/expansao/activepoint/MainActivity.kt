package projeto.expansao.activepoint

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import projeto.expansao.activepoint.databinding.LoginBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var bancoDeDados: BancoDeDados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bancoDeDados = BancoDeDados(this)

        binding.btnLogin.setOnClickListener {
            // Obtém os valores inseridos pelo usuário
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtPass.text.toString().trim()

            Log.d("Login", "Email: $email, Senha: $senha")

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false

            try {
                val usuario = bancoDeDados.verificarLogin(email, senha)

                if (usuario == null) {
                    Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
                } else {
                    val tipoUsuario = usuario["tipo"]

                    if (tipoUsuario.equals("admin", ignoreCase = true)) {
                        Log.d("Login", "Redirecionando para AdminLayout")
                        val intent = Intent(this, AdminLayout::class.java).apply {
                            putExtra("email", email)
                            putExtra("senha", senha)
                        }
                        startActivity(intent)
                        finish() // Fecha a tela de login
                    } else if (tipoUsuario.equals("comum", ignoreCase = true)) {
                        Log.d("Login", "Redirecionando para ComunLayoutActivity")
                        val intent = Intent(this, ComunLayoutActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("senha", senha)
                        }
                        startActivity(intent)
                        finish() // Fecha a tela de login
                    } else {
                        Toast.makeText(this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Erro ao tentar fazer login: ${e.message}", e)
                Toast.makeText(this, "Erro ao fazer login. Tente novamente.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }

        binding.btnCadastro.setOnClickListener {
            val intent = Intent(this, MainCadastro::class.java)
            startActivity(intent)
            finish()
        }

        val bancoDeDados = BancoDeDados(this)
        bancoDeDados.verificarColunaSolicitacao()
    }




}


