package projeto.expansao.activepoint

import AdminAdapter
import UsuarioAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import projeto.expansao.activepoint.databinding.ComunLayoutBinding
import projeto.expansao.activepoint.models.UsuarioComum
import recyclerUsAdapter


class ComunLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ComunLayoutBinding
    private lateinit var bancoDeDados: BancoDeDados
    private lateinit var recyclerViewAdapter: recyclerUsAdapter
    private lateinit var usuarioLogado: UsuarioComum // Armazenando as informações do usuário logado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Luis gostoso"

        binding = ComunLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bancoDeDados = BancoDeDados(this)

        // Obtenção de dados de login (se necessário)
        val email = intent.getStringExtra("email") ?: ""
        val senha = intent.getStringExtra("senha") ?: ""


        try {
            // Verificação do login
            val dadosUsuario = bancoDeDados.verificarLogin(email, senha)

            if (dadosUsuario == null) {
                // Se não encontrar, exibe erro
                Log.e("LoginError", "Usuário não encontrado com o email $email")
                Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
            } else {
                // Preencher os dados do usuário logado
                val nomeCompleto = dadosUsuario["nome"] ?: "Nome não encontrado"
                val cpf = dadosUsuario["cpf"] ?: "CPF não encontrado"
                val numero = dadosUsuario["telefone"] ?: "Número não encontrado"

                usuarioLogado = UsuarioComum(nomeCompleto, cpf, numero) // Criando o objeto UsuarioComum com os dados do login

                // Atualizando o TextView com as informações do usuário
                binding.NomeUs.text = nomeCompleto
                binding.cpfUs.text = "CPF: $cpf"
                binding.numeroUs.text = "Número: $numero"

                // Preencher o RecyclerView com os dados de administradores
                val administradores = bancoDeDados.getListaAdministradores()

                recyclerViewAdapter = recyclerUsAdapter(administradores, usuarioLogado) // Passando o usuário logado
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ComunLayoutActivity)
                    adapter = recyclerViewAdapter
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao fazer login. Tente novamente.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        supportActionBar?.title = "Active Point" // Muda o título na ActionBar

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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




