import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import projeto.expansao.activepoint.R
import projeto.expansao.activepoint.models.UsuarioComum

class UsuarioAdapter(private val usuarios: List<UsuarioComum>) :
    RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    // ViewHolder que conecta os IDs do layout com os campos do modelo
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgText: TextView = view.findViewById(R.id.img)  // Mudança do nome para algo mais intuitivo
        val nomeText: TextView = view.findViewById(R.id.nome)  // Nome do usuário
        val cpfText: TextView = view.findViewById(R.id.cpf)  // CPF
        val numeroText: TextView = view.findViewById(R.id.numero)  // Número de contato
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comun_list_view, parent, false)  // Mesmo layout do admin, você pode usar outro se preferir
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = usuarios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = usuarios[position]

        // Usando a primeira letra do nome como a imagem (img)
        holder.imgText.text = usuario.nome.firstOrNull()?.toString() ?: ""  // Inicial do nome

        holder.nomeText.text = "Nome: ${usuario.nome}"
        holder.cpfText.text = "CPF: ${usuario.cpf}"
        holder.numeroText.text = "Número: ${usuario.numero}"
    }
}
