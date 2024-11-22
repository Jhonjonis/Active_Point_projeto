package projeto.expansao.activepoint

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class BD_Controller(context: Context) {

    private val database: BancoDeDados = BancoDeDados(context)
    private val writableDatabase: SQLiteDatabase = database.writableDatabase
    private val readableDatabase: SQLiteDatabase = database.readableDatabase

    // Método para inserir dados de usuários comuns
    fun inserirDados(nome: String, email: String, senha: String, telefone: String, cpf: String): String {
        val values = ContentValues().apply {
            put(BancoDeDados.NOME, nome)
            put(BancoDeDados.EMAIL, email)
            put(BancoDeDados.SENHA, senha)
            put(BancoDeDados.TELEFONE, telefone)
            put(BancoDeDados.CPF, cpf)  // Adiciona o CPF
        }
        val result = writableDatabase.insert(BancoDeDados.TABELA, null, values)
        return if (result == -1L) "Erro no cadastro do Login!" else "Login Cadastrado!"
    }

    // Método para inserir dados de administradores (lojas)
    fun inserirDadosAdmin(nome: String, email: String, senha: String, numero: String, cnpj: String, nomeDaLoja: String, local: String): String {
        val values = ContentValues().apply {
            put(BancoDeDados.NOME, nome)
            put(BancoDeDados.EMAIL, email)
            put(BancoDeDados.SENHA, senha)
            put(BancoDeDados.NUMERO, numero)
            put(BancoDeDados.CNPJ, cnpj)
            put(BancoDeDados.NOME_DA_LOJA, nomeDaLoja)
            put(BancoDeDados.LOCAL, local)  // Adiciona o campo local
        }
        val result = writableDatabase.insert(BancoDeDados.TABELA_ADMIN, null, values)
        return if (result != -1L) "Cadastro realizado com sucesso!" else "Erro ao realizar cadastro."
    }

    // Método para verificar login de usuários (comum ou administrador)
    fun verificarLogin(email: String, senha: String): Map<String, String>? {
        // Consultando primeiro a tabela de usuários comuns
        val queryUsuarios = "SELECT * FROM ${BancoDeDados.TABELA} WHERE ${BancoDeDados.EMAIL} = ? AND ${BancoDeDados.SENHA} = ?"
        val cursorUsuarios: Cursor = readableDatabase.rawQuery(queryUsuarios, arrayOf(email, senha))

        return if (cursorUsuarios.moveToFirst()) {
            val usuario = mutableMapOf<String, String>()
            usuario["nome"] = cursorUsuarios.getString(cursorUsuarios.getColumnIndex(BancoDeDados.NOME)) ?:"N/A"
            usuario["email"] = cursorUsuarios.getString(cursorUsuarios.getColumnIndex(BancoDeDados.EMAIL))
            usuario["telefone"] = cursorUsuarios.getString(cursorUsuarios.getColumnIndex(BancoDeDados.TELEFONE))
            usuario["cpf"] = cursorUsuarios.getString(cursorUsuarios.getColumnIndex(BancoDeDados.CPF))  // Recupera o CPF
            usuario["tipo"] = "comum" // Marca como usuário comum

            cursorUsuarios.close()
            usuario
        } else {
            cursorUsuarios.close()
            // Caso não encontre na tabela de usuários comuns, verifica se é administrador
            val queryAdmin = "SELECT * FROM ${BancoDeDados.TABELA_ADMIN} WHERE ${BancoDeDados.EMAIL} = ? AND ${BancoDeDados.SENHA} = ?"
            val cursorAdmin: Cursor = readableDatabase.rawQuery(queryAdmin, arrayOf(email, senha))

            if (cursorAdmin.moveToFirst()) {
                val admin = mutableMapOf<String, String>()
                admin["nome"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.NOME))
                admin["email"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.EMAIL))
                admin["numero"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.NUMERO))
                admin["cnpj"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.CNPJ))
                admin["nome_da_loja"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.NOME_DA_LOJA))
                admin["local"] = cursorAdmin.getString(cursorAdmin.getColumnIndex(BancoDeDados.LOCAL))  // Recupera o campo "local"
                admin["tipo"] = "admin" // Marca como administrador

                cursorAdmin.close()
                return admin
            }
            cursorAdmin.close()
            null // Retorna null se não encontrar nas duas tabelas
        }
    }

}
