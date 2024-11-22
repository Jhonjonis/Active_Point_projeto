package projeto.expansao.activepoint

import AdminAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import projeto.expansao.activepoint.databinding.AdminLayoutBinding
import projeto.expansao.activepoint.models.UsuarioComum

class AdminLayout : AppCompatActivity() {

    private lateinit var binding: AdminLayoutBinding
    private lateinit var bancoDeDados: BancoDeDados
    private lateinit var recyclerViewAdapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bancoDeDados = BancoDeDados(this)

        // Obtenção de dados de login
        val email = intent.getStringExtra("email") ?: ""
        val senha = intent.getStringExtra("senha") ?: ""

        try {
            val dadosUsuario = bancoDeDados.verificarLogin(email, senha)

            if (dadosUsuario == null) {
                Log.e("LoginError", "Usuário não encontrado com o email $email")
                Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
            } else {
                val tipoUsuario = dadosUsuario["tipo"]

                if (tipoUsuario == "admin") {
                    Log.d("LoginSuccess", "Administrador logado com sucesso.")

                    val nomeDaLoja = dadosUsuario["nome_da_loja"] ?: "Loja não encontrada"
                    val cnpj = dadosUsuario["cnpj"] ?: "CNPJ não encontrado"
                    val numero = dadosUsuario["numero"] ?: "Número não encontrado"

                    binding.nome.text = nomeDaLoja
                    binding.cpf.text = "CNPJ: $cnpj"
                    binding.numero.text = "Número: $numero"

                    // Busca os usuários com solicitação registrada
                    val usuariosComunsComSolicitacao =
                        bancoDeDados.getUsuariosComunsComSolicitacao()

                    if (usuariosComunsComSolicitacao.isNotEmpty()) {
                        recyclerViewAdapter = AdminAdapter(usuariosComunsComSolicitacao)
                        binding.recyclerViewMain.apply {
                            layoutManager = LinearLayoutManager(this@AdminLayout)
                            adapter = recyclerViewAdapter
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Nenhum usuário com solicitação encontrado.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("LoginError", "Tipo de usuário não é admin.")
                    Toast.makeText(this, "Você está logado como usuário comum.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao fazer login. Tente novamente.", Toast.LENGTH_SHORT).show()
            Log.e("AdminLayoutError", "Erro ao tentar fazer login: ${e.message}", e)
        }

        val button = findViewById<Button>(R.id.button8)

        button.setOnClickListener {
            // Cria o AlertDialog para a entrada do CPF
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Digite seu CPF")

            // Cria o campo de entrada para o CPF
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER  // Apenas números
            input.setTextColor(Color.BLACK)  // Cor do texto (preto)
            builder.setView(input)

            // Configura o botão "OK" para capturar o CPF
            builder.setPositiveButton("OK") { dialog, which ->
                val cpf = input.text.toString()  // Captura o CPF
                if (cpf.isNotEmpty()) {
                    // Obtém as informações da loja pelo CPF
                    val lojaInfo = bancoDeDados.getLojaInfoByCPF(this, cpf)

                    if (lojaInfo != null) {
                        val (nomeLoja, localLoja) = lojaInfo

                        // Registro de ponto realizado (simulação)
                        Toast.makeText(this, "Ponto registrado com sucesso!", Toast.LENGTH_SHORT).show()

                        // Passar os dados para a tela do usuário
                        val intent = Intent(this, ComunLayoutActivity::class.java).apply {
                            putExtra("nomeLoja", nomeLoja)
                            putExtra("localLoja", localLoja)
                        }
                        startActivity(intent) // Abre a tela do usuário com os dados
                    } else {
                        Toast.makeText(this, "CPF não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "CPF não pode ser vazio", Toast.LENGTH_SHORT).show()
                }
            }

            // Configura o botão "Cancelar"
            builder.setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.etdborda)
            // Exibe o dialog
            builder.show()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.principal, menu)


        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Perfil -> {


                Toast.makeText(this, "Option 1 selecionada!", Toast.LENGTH_SHORT).show()


                true
            }
            R.id.Configurações -> {
                Toast.makeText(this, "Option 2 selecionada!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.Sair->{
                bancoDeDados.logout(this)
                Toast.makeText(this, "Logout Efetuado!", Toast.LENGTH_SHORT).show()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


