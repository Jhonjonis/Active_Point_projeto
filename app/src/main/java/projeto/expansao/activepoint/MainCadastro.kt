package projeto.expansao.activepoint

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import projeto.expansao.activepoint.databinding.CadastroBinding

class MainCadastro : Activity() {

    private lateinit var binding: CadastroBinding

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clearViews() {
        binding.edtTextNome.text.clear()
        binding.edtTxtEmail.text.clear()
        binding.edtPass.text.clear()
        binding.edtPhone.text.clear()
        binding.cnpj.text.clear()
        binding.nomeloja.text.clear()
        binding.local.text.clear()
        binding.cpf.text.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener para o RadioGroup
        binding.radioGroupOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioOption1 -> {
                    binding.cnpj.visibility = View.GONE
                    binding.nomeloja.visibility = View.GONE
                    binding.local.visibility = View.GONE
                }
                R.id.radioOption2 -> {
                    binding.cnpj.visibility = View.VISIBLE
                    binding.nomeloja.visibility = View.VISIBLE
                    binding.local.visibility = View.VISIBLE
                }
            }
        }

        // Listener para o botão "Fazer Cadastro"
        binding.cadastar.setOnClickListener {
            // Captura as informações inseridas pelo usuário
            val nomeCompleto = binding.edtTextNome.text.toString()
            val email = binding.edtTxtEmail.text.toString()
            val senha = binding.edtPass.text.toString()
            val numeroT = binding.edtPhone.text.toString()
            val cnpj = binding.cnpj.text.toString()
            val nomeLoja = if (binding.nomeloja.visibility == View.VISIBLE) {
                binding.nomeloja.text.toString()
            } else {
                ""  // Se o campo nomeLoja não estiver visível, passamos uma string vazia
            }
            val local = binding.local.text.toString() // Captura o valor de "local"
            val cpf = binding.cpf.text.toString() // Campo CPF

            // Verificação de campos obrigatórios
            if (nomeCompleto.isEmpty() || email.isEmpty() || senha.isEmpty() || numeroT.isEmpty() || cpf.isEmpty()) {
                showToast("Preencha todos os campos obrigatórios.")
                return@setOnClickListener
            }

            // Verifica se o usuário é uma loja (checkbox marcado)
            val isLoja = binding.radioOption2.isChecked

            val controlador = BD_Controller(this)

            // Inserção de dados no banco de dados, dependendo se é um usuário comum ou uma loja
            val resultado = if (isLoja) {
                // Inserir dados na tabela admin (para lojas)
                controlador.inserirDadosAdmin(nomeCompleto, email, senha, numeroT, cnpj, nomeLoja, local)
            } else {
                // Inserir dados na tabela de usuários comuns (login)
                controlador.inserirDados(nomeCompleto, email, senha, numeroT, cpf) // Passando CPF
            }

            // Exibe uma mensagem de resultado
            showToast(resultado)

            // Limpa os campos após o cadastro
            clearViews()

            // Redireciona para a tela de Login após o cadastro
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a tela de cadastro
        }
// Listener para o botão "Fazer Login"
        binding.fazerLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a tela atual
        }
    }
}


