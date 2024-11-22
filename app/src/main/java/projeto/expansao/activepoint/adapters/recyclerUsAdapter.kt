import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import projeto.expansao.activepoint.AdminLayout
import projeto.expansao.activepoint.BancoDeDados
import projeto.expansao.activepoint.R
import projeto.expansao.activepoint.models.Administrador
import projeto.expansao.activepoint.models.UsuarioComum

class recyclerUsAdapter(
    private val administradores: List<Administrador>, // Apenas administradores
    private val usuarioLogado: UsuarioComum // Informações do usuário logado
) : RecyclerView.Adapter<recyclerUsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgText: TextView = view.findViewById(R.id.img)
        val nomeText: TextView = view.findViewById(R.id.nome_admin)
        val cnpjText: TextView = view.findViewById(R.id.cnpj_admin)
        val localText: TextView = view.findViewById(R.id.local_admin)

        // Função que irá configurar o clique no item
        fun bind(
            administrador: Administrador,
            context: Context,
            usuarioLogado: UsuarioComum // Passando o usuário logado
        ) {
            itemView.setOnClickListener {
                try {
                    val bancoDeDados = BancoDeDados(context)
                    Log.d("RecyclerAdapter", "Banco de dados instanciado com sucesso.")

                    if (bancoDeDados.isDatabaseValid()) {
                        Log.d("RecyclerAdapter", "Banco de dados válido.")

                        if (usuarioLogado.nome.isNotEmpty() && usuarioLogado.cpf.isNotEmpty()) {
                            Log.d("RecyclerAdapter", "Dados do usuário logado: ${usuarioLogado.nome}, ${usuarioLogado.cpf}")

                            val usuarioComum = UsuarioComum(
                                nome = usuarioLogado.nome,
                                cpf = usuarioLogado.cpf,
                                numero = usuarioLogado.numero
                            )

                            bancoDeDados.marcarSolicitacao(usuarioComum.cpf)
                            Log.d("RecyclerAdapter", "Solicitação registrada no banco de dados.")

                            Toast.makeText(context, "Solicitação registrada para o administrador ${administrador.nomeDaLoja}", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("RecyclerAdapter", "Dados do usuário logado estão inválidos: Nome ou CPF vazio.")
                            Toast.makeText(context, "Dados do usuário logado inválidos.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("RecyclerAdapter", "Banco de dados inválido.")
                        Toast.makeText(context, "Banco de dados inválido", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("RecyclerAdapter", "Erro ao registrar a solicitação: ${e.message}", e)
                    Toast.makeText(context, "Erro ao registrar a solicitação. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comun_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = administradores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val administrador = administradores[position]

        holder.imgText.text = administrador.nome.firstOrNull()?.toString() ?: "N/A"
        holder.nomeText.text = "Loja: ${administrador.nomeDaLoja}"
        holder.cnpjText.text = "CNPJ: ${administrador.cnpj}"
        holder.localText.text = "Localização: ${administrador.local}"

        holder.bind(administrador, holder.itemView.context, usuarioLogado)
    }
}




