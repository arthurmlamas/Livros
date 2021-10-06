package br.edu.ifsp.scl.ads.pdm.livros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.livros.adapter.LivrosAdapter
import br.edu.ifsp.scl.ads.pdm.livros.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro

class MainActivity : AppCompatActivity() {

    companion object Extras {
        // const em Kotlin é pra transformar em estático
        const val EXTRA_LIVRO = "EXTRA_LIVRO"
        const val EXTRA_POSICAO = "EXTRA_POSICAO"
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var livroActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editarlivroActivityResultLauncher: ActivityResultLauncher<Intent>

    // Data source
    private val livrosList: MutableList<Livro> = mutableListOf()

    // Adapter
    /*private val livrosAdapter: ArrayAdapter<String> by lazy {
        *//*val livrosStringList = mutableListOf<String>()
        livrosList.forEach { livro -> livrosStringList.add(livro.toString()) }
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, livrosStringList)*//*

        // DA MESMA FORMA
        ArrayAdapter(this, android.R.layout.simple_list_item_1, livrosList.run {
            val livrosStringList = mutableListOf<String>()
            this.forEach { livro -> livrosStringList.add(livro.toString()) }
            livrosStringList
        })
    }*/

    private val livrosAdapter: LivrosAdapter by lazy {
        LivrosAdapter(this, R.layout.layout_livro, livrosList)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        // Inicializando lista de livros
        inicializarLivrosList()

        // Associando Adapter ao ListView
        activityMainBinding.livrosLv.adapter = livrosAdapter
        /*// EM JAVA
        activityMainBinding.livrosLv.setaAdpater(livrosAdapter);*/

        registerForContextMenu(activityMainBinding.livrosLv)

        livroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    livrosAdapter.add(this)
                    /*// EQUIVALENTE
                    livrosList.add(this)
                    livrosAdapter.notifyDataSetChanged()*/
                }

                /*// EQUIVALENTE
                val livro = resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)
                if (livro != null) {
                    livrosList.add(livro)
                    livrosAdapter.add(livro.toString())
                }*/
            }
        }

        editarlivroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val posicao = resultado.data?.getIntExtra(EXTRA_POSICAO, -1)
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    if (posicao != null && posicao != -1) {
                        livrosList[posicao] = this
                        livrosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        // Utiliza-se "_" para parâmetros que não serão utilizados
        activityMainBinding.livrosLv.setOnItemClickListener { _, _, posicao, _  ->
            val livro = livrosList[posicao]
            val consultarLivroIntent = Intent(this, LivroActivity::class.java)
            consultarLivroIntent.putExtra(EXTRA_LIVRO, livro)
            startActivity(consultarLivroIntent)
        }

        activityMainBinding.adicionarFab.setOnClickListener {
            livroActivityResultLauncher.launch(Intent(this, LivroActivity::class.java))
        }
    }

    private fun inicializarLivrosList() {
        for (indice in 1..10) {
            livrosList.add(
                Livro(
                    "Título ${indice}",
                    "ISBN ${indice}",
                    "Primeiro autor ${indice}",
                    "Editora ${indice}",
                    indice,
                    indice
                )
            )
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val posicao = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when (item.itemId) {
            R.id.editarLivroMi -> {
                // Editar livro
                val livro = livrosList[posicao]
                // Toast.makeText(this, "${livro}", Toast.LENGTH_SHORT).show()
                val editarLivroIntent = Intent(this, LivroActivity::class.java)
                editarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                editarLivroIntent.putExtra(EXTRA_POSICAO, posicao)
                editarlivroActivityResultLauncher.launch(editarLivroIntent)

                true
            }
            R.id.removerLivroMi -> {
                // Remover livro
                livrosList.removeAt(posicao)
                livrosAdapter.notifyDataSetChanged()
                true
            }
            else -> { false }
        }
    }
}