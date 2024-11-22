package projeto.expansao.activepoint

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import projeto.expansao.activepoint.models.Administrador
import projeto.expansao.activepoint.models.UsuarioComum


class BancoDeDados(context: Context) : SQLiteOpenHelper(context, "ActivePointDB", null, 4) { // Versão do banco atualizada para 4

    companion object {
        const val TABELA = "usuarios"
        const val TABELA_ADMIN = "admin"
        const val NOME = "nome"
        const val EMAIL = "email"
        const val SENHA = "senha"
        const val TELEFONE = "telefone"
        const val CPF = "cpf"
        const val CNPJ = "cnpj"
        const val NUMERO = "numero"
        const val NOME_DA_LOJA = "nome_da_loja"
        const val LOCAL = "local" // Novo campo "local"
        const val SOLICITACAO = "solicitacao"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUsuarios = """
        CREATE TABLE $TABELA (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            $NOME TEXT,
            $EMAIL TEXT,
            $SENHA TEXT,
            $TELEFONE TEXT,
            $CPF TEXT,
            $SOLICITACAO INTEGER DEFAULT 0  -- Novo campo para controle da solicitação
        )
        """.trimIndent()

        val createTableAdmin = """
            CREATE TABLE $TABELA_ADMIN (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $NOME TEXT,
                $EMAIL TEXT,
                $SENHA TEXT,
                $NUMERO TEXT,
                $CNPJ TEXT,
                $NOME_DA_LOJA TEXT,
                $LOCAL TEXT  -- Campo "local" adicionado
            )
        """.trimIndent()

        db?.execSQL(createTableUsuarios)
        db?.execSQL(createTableAdmin)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseUpgrade", "Atualizando banco de dados de versão $oldVersion para $newVersion")

        if (oldVersion < 4) {
            try {
                // Adiciona a coluna 'solicitacao' na tabela de usuários
                db?.execSQL("ALTER TABLE $TABELA ADD COLUMN $SOLICITACAO INTEGER DEFAULT 0")
                Log.d("DatabaseUpgrade", "Coluna 'solicitacao' adicionada com sucesso.")
            } catch (e: Exception) {
                Log.e("DatabaseUpgrade", "Erro ao atualizar a tabela $TABELA: ${e.message}", e)
            }
        }
        verificarColunaSolicitacao()
    }

    // Verificar se o banco de dados está válido
    fun isDatabaseValid(): Boolean {
        return try {
            // Verifica se o banco de dados pode ser acessado (isso pode variar dependendo do seu banco de dados)
            // Exemplo:
            val db = readableDatabase
            db != null
        } catch (e: Exception) {
            false
        }
    }

    fun adicionarColunaSolicitacao() {
        val db = writableDatabase
        val query = "ALTER TABLE usuarios ADD COLUMN solicitacao INTEGER DEFAULT 0"
        db.execSQL(query)
    }

    fun verificarColunaSolicitacao() {
        val db = readableDatabase
        val cursor = db.rawQuery("PRAGMA table_info(usuarios)", null)

        var colunaExistente = false
        while (cursor.moveToNext()) {
            val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            if (columnName == "solicitacao") {
                colunaExistente = true
                break
            }
        }
        cursor.close()

        if (!colunaExistente) {
            adicionarColunaSolicitacao()
        }
    }


    fun marcarSolicitacao(cpf: String) {
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("solicitacao", 1)  // Marcando a solicitação
            }

            val rowsAffected = db.update("usuarios", values, "cpf = ?", arrayOf(cpf))
            if (rowsAffected == 0) {
                throw Exception("Nenhuma linha foi atualizada. O CPF pode não existir.")
            }
        } catch (e: Exception) {
            Log.e("BancoDeDados", "Erro ao registrar solicitação: ${e.message}", e)
            throw e  // Re-lançando a exceção para que seja capturada no Adapter
        }
    }



    // Método para apagar todos os dados das tabelas, mantendo a estrutura
    fun limparDados() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val rowsDeletedUsuarios = db.delete(TABELA, null, null)
            val rowsDeletedAdmin = db.delete(TABELA_ADMIN, null, null)
            db.setTransactionSuccessful()
            Log.d("Cadastro", "$rowsDeletedUsuarios registros apagados da tabela $TABELA.")
            Log.d("Cadastro", "$rowsDeletedAdmin registros apagados da tabela $TABELA_ADMIN.")
        } finally {
            db.endTransaction()
        }
    }


    fun getUsuariosComunsComSolicitacao(): List<UsuarioComum> {
        val usuarios = mutableListOf<UsuarioComum>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nome, cpf, telefone FROM usuarios WHERE solicitacao = 1", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val nomeIndex = cursor.getColumnIndexOrThrow("nome")
                    val cpfIndex = cursor.getColumnIndexOrThrow("cpf")
                    val telefoneIndex = cursor.getColumnIndexOrThrow("telefone")

                    val nome = cursor.getString(nomeIndex)
                    val cpf = cursor.getString(cpfIndex)
                    val numero = cursor.getString(telefoneIndex)
                    usuarios.add(UsuarioComum(nome, cpf, numero))
                } while (cursor.moveToNext())
            } else {
                Log.e("DBError", "Nenhum usuário com solicitação encontrado.")
            }
        } catch (e: Exception) {
            Log.e("DBError", "Erro ao recuperar usuários com solicitação", e)
        } finally {
            cursor.close()
            db.close()
        }

        Log.d("DBQuery", "Total de usuários com solicitação encontrados: ${usuarios.size}")
        return usuarios
    }


    // Verificar login para usuários comuns e administradores
    fun verificarLogin(email: String, senha: String): Map<String, String>? {
        val db = readableDatabase

        // Log antes da consulta para administrador
        Log.d("LoginVerification", "Consultando como administrador com email: $email e senha: $senha")

        // Verifica se é um administrador
        val queryAdmin = "SELECT * FROM $TABELA_ADMIN WHERE $EMAIL = ? AND $SENHA = ?"
        db.rawQuery(queryAdmin, arrayOf(email, senha)).use { cursor ->
            if (cursor.moveToFirst()) {
                Log.d("LoginVerification", "Administrador encontrado: ${cursor.getString(cursor.getColumnIndexOrThrow(EMAIL))}")
                // Encontrou um administrador, retorna as informações
                return mapOf(
                    "nome" to (cursor.getString(cursor.getColumnIndexOrThrow(NOME)) ?: "N/A"),
                    "email" to (cursor.getString(cursor.getColumnIndexOrThrow(EMAIL)) ?: "N/A"),
                    "numero" to (cursor.getString(cursor.getColumnIndexOrThrow(NUMERO)) ?: "N/A"),
                    "cnpj" to (cursor.getString(cursor.getColumnIndexOrThrow(CNPJ)) ?: "N/A"),
                    "nome_da_loja" to (cursor.getString(cursor.getColumnIndexOrThrow(NOME_DA_LOJA)) ?: "N/A"),
                    "local" to (cursor.getString(cursor.getColumnIndexOrThrow(LOCAL)) ?: "N/A"),
                    "tipo" to "admin"
                )
            } else {
                Log.d("LoginVerification", "Administrador não encontrado.")
            }
        }

        // Se não for administrador, verifica se é um usuário comum
        Log.d("LoginVerification", "Consultando como usuário comum com email: $email e senha: $senha")
        val queryUsuarioComum = "SELECT * FROM $TABELA WHERE $EMAIL = ? AND $SENHA = ?"
        db.rawQuery(queryUsuarioComum, arrayOf(email, senha)).use { cursor ->
            if (cursor.moveToFirst()) {
                Log.d("LoginVerification", "Usuário comum encontrado: ${cursor.getString(cursor.getColumnIndexOrThrow(EMAIL))}")
                // Encontrou um usuário comum, retorna as informações
                return mapOf(
                    "nome" to (cursor.getString(cursor.getColumnIndexOrThrow(NOME)) ?: "N/A"),
                    "email" to (cursor.getString(cursor.getColumnIndexOrThrow(EMAIL)) ?: "N/A"),
                    "telefone" to (cursor.getString(cursor.getColumnIndexOrThrow(TELEFONE)) ?: "N/A"),
                    "cpf" to (cursor.getString(cursor.getColumnIndexOrThrow(CPF)) ?: "N/A"),
                    "tipo" to "comum"
                )
            } else {
                Log.d("LoginVerification", "Usuário comum não encontrado.")
            }
        }

        // Caso não tenha encontrado um usuário de nenhum tipo
        Log.d("LoginVerification", "Nenhum usuário encontrado.")
        return null
    }


    fun getListaAdministradores(): List<Administrador> {
        val lista = mutableListOf<Administrador>()
        val query = "SELECT * FROM ${BancoDeDados.TABELA_ADMIN}"
        val cursor = readableDatabase.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                try {
                    val nome = cursor.getString(cursor.getColumnIndexOrThrow(BancoDeDados.NOME))
                    val numero = cursor.getString(cursor.getColumnIndexOrThrow(BancoDeDados.NUMERO))
                    val cnpj = cursor.getString(cursor.getColumnIndexOrThrow(BancoDeDados.CNPJ))
                    val nomeDaLoja = cursor.getString(cursor.getColumnIndexOrThrow(BancoDeDados.NOME_DA_LOJA))
                    val local = cursor.getString(cursor.getColumnIndexOrThrow(BancoDeDados.LOCAL))

                    // Adicionando o administrador à lista
                    lista.add(Administrador(nome, numero, cnpj, nomeDaLoja, local))

                    // Verificando se os dados estão sendo lidos
                    Log.d("Database", "Administrador: $nome, $nomeDaLoja, $cnpj, $local")
                } catch (e: Exception) {
                    Log.e("DatabaseError", "Erro ao acessar dados da coluna: ${e.message}")
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Verificando o tamanho da lista e retorno
        Log.d("Database", "Total de administradores encontrados: ${lista.size}")
        return lista
    }
    fun logout(context: Context) {
        val dbHelper = BancoDeDados(context) // 'context' vem da Activity
        val db = dbHelper.writableDatabase


        // Limpa dados de SharedPreferences
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Redireciona para a tela de login
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    fun getLojaInfoByCPF(context: Context, cpf: String): Pair<String, String>? {
        val dbHelper = BancoDeDados(context)  // Passa o context para o banco de dados
        val db = dbHelper.readableDatabase

        // Consultando a tabela 'admin' para obter o nome da loja e o local com base no CPF
        val query = "SELECT nome_da_loja, local FROM ${BancoDeDados.TABELA_ADMIN} WHERE cpf = ?"
        val cursor = db.rawQuery(query, arrayOf(cpf))

        var nomeLoja: String? = null
        var localLoja: String? = null

        if (cursor.moveToFirst()) {
            nomeLoja = cursor.getString(cursor.getColumnIndexOrThrow("nome_da_loja"))
            localLoja = cursor.getString(cursor.getColumnIndexOrThrow("local"))
        }

        cursor.close()
        db.close()

        return if (nomeLoja != null && localLoja != null) {
            Pair(nomeLoja, localLoja)  // Retorna o nome da loja e o local
        } else {
            null  // Caso não encontre o CPF
        }
    }
    }



