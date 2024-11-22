import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import projeto.expansao.activepoint.R
import projeto.expansao.activepoint.models.Administrador
import projeto.expansao.activepoint.models.UsuarioComum

class AdminAdapter(private val usuariosComSolicitacao: List<UsuarioComum>) : RecyclerView.Adapter<AdminAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_admin_item, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuariosComSolicitacao[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int {
        return usuariosComSolicitacao.size
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeTextView: TextView = itemView.findViewById(R.id.nome1)
        private val cpfTextView: TextView = itemView.findViewById(R.id.cpf1)
        private val telefoneTextView: TextView = itemView.findViewById(R.id.numero1)

        fun bind(usuario: UsuarioComum) {
            nomeTextView.text = usuario.nome
            cpfTextView.text = usuario.cpf
            telefoneTextView.text = usuario.numero
        }
    }
}



