package br.edu.ifsp.scl.ads.pdm.livros.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifsp.scl.ads.pdm.livros.R
import br.edu.ifsp.scl.ads.pdm.livros.databinding.LayoutLivroBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro

class LivrosAdapter(
    val contexto: Context,
    leiaute: Int,
    val listaLivros: MutableList<Livro>
    ): ArrayAdapter<Livro>(contexto, leiaute, listaLivros) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val livroLayoutView: View
        if (convertView != null) {
            // Célula recilcada
            livroLayoutView = convertView
        }
        else {
            // Inflar uma célula
            val layoutLivroBinding = LayoutLivroBinding.inflate(
                contexto.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            )
            with(layoutLivroBinding) {
                livroLayoutView = layoutLivroBinding.root
                livroLayoutView.tag = LivroLayoutHolder(tituloTv, primeiroAutorTv, editoraTv)
            }
        }

        //Alterar os dados da célula, seja nova ou reciclada
        val livro = listaLivros[position]

        with(livroLayoutView.tag as LivroLayoutHolder) {
            tituloTv.text = livro.titulo
            primeiroAutorTv.text = livro.primeiroAutor
            editoraTv.text = livro.editora
        }

        /*// O trecho acima é mais eficiente que este
        livroLayoutView.findViewById<TextView>(R.id.tituloTv).text = livro.titulo
        livroLayoutView.findViewById<TextView>(R.id.primeiroAutorTv).text = livro.primeiroAutor
        livroLayoutView.findViewById<TextView>(R.id.editoraTv).text = livro.editora*/

        return livroLayoutView
    }

    private data class LivroLayoutHolder(
        val tituloTv: TextView,
        val primeiroAutorTv: TextView,
        val editoraTv: TextView
    )
}