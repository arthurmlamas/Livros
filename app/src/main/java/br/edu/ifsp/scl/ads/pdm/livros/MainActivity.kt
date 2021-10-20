package br.edu.ifsp.scl.ads.pdm.livros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.livros.adapter.LivrosRvAdapter
import br.edu.ifsp.scl.ads.pdm.livros.controller.LivroController
import br.edu.ifsp.scl.ads.pdm.livros.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnLivroClickListener {

    companion object Extras {
        // const em Kotlin é pra transformar em estático
        const val EXTRA_LIVRO = "EXTRA_LIVRO"
        const val EXTRA_POSICAO = "EXTRA_POSICAO"
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var livroActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editarLivroActivityResultLauncher: ActivityResultLauncher<Intent>

    // Data source
    private val livrosList: MutableList<Livro> by lazy {
        livroController.buscarLivros()
    }

    // Controller
    private val livroController: LivroController by lazy {
        LivroController(this)
    }

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

    /*
    // Antigo
    private val livrosAdapter: LivrosAdapter by lazy {
        LivrosAdapter(this, R.layout.layout_livro, livrosList)
    }*/

    private val livrosAdapter: LivrosRvAdapter by lazy {
        LivrosRvAdapter(this, livrosList)
    }

    // LayoutManager
    private val livrosLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        // Associando Adapter e LayoutManger ao RecyclerView
        activityMainBinding.livrosRv.adapter = livrosAdapter
        activityMainBinding.livrosRv.layoutManager = livrosLayoutManager

        /*// Associando Adapter ao ListView (ANTIGO)
        activityMainBinding.livrosLv.adapter = livrosAdapter*/
        /*// EM JAVA
        activityMainBinding.livrosLv.setAdapter(livrosAdapter);*/

        // ANTIGO
        // registerForContextMenu(activityMainBinding.livrosLv)

        livroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    livroController.inserirLivro(this)
                    livrosList.add(this)
                    livrosAdapter.notifyDataSetChanged()

                    // NÃO EQUIVILANTE PARA RECYCLER VIEW
                    // livrosAdapter.add(this)

                    /*// EQUIVALENTE PARA LIST VIEW
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

        editarLivroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val posicao = resultado.data?.getIntExtra(EXTRA_POSICAO, -1)
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    if (posicao != null && posicao != -1) {
                        livroController.modificarLivro(this)
                        livrosList[posicao] = this
                        livrosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        // Utiliza-se "_" para parâmetros que não serão utilizados
        /*
        // Refatorando para a utilização do RecycleView (ANTIGO)
        activityMainBinding.livrosLv.setOnItemClickListener { _, _, posicao, _  ->
            val livro = livrosList[posicao]
            val consultarLivroIntent = Intent(this, LivroActivity::class.java)
            consultarLivroIntent.putExtra(EXTRA_LIVRO, livro)
            startActivity(consultarLivroIntent)
        }*/

        activityMainBinding.adicionarFab.setOnClickListener {
            livroActivityResultLauncher.launch(Intent(this, LivroActivity::class.java))
        }
    }

    // ANTIGO
    /*override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }*/

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val posicao = livrosAdapter.posicao
        val livro = livrosList[posicao]

        // ANTIGO
        // val posicao = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when (item.itemId) {
            R.id.editarLivroMi -> {
                // Editar livro
                // Toast.makeText(this, "${livro}", Toast.LENGTH_SHORT).show()
                val editarLivroIntent = Intent(this, LivroActivity::class.java)
                editarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                editarLivroIntent.putExtra(EXTRA_POSICAO, posicao)
                editarLivroActivityResultLauncher.launch(editarLivroIntent)

                true
            }
            R.id.removerLivroMi -> {
                // Remover livro
                with(AlertDialog.Builder(this)) {
                    setMessage("Confirma a remoção?")
                    setPositiveButton("Sim") { _, _ ->
                        livroController.apagarLivro(livro.titulo)
                        livrosList.removeAt(posicao)
                        livrosAdapter.notifyDataSetChanged()
                        Snackbar.make(activityMainBinding.root, "Livro removido", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Snackbar.make(activityMainBinding.root, "Remoção cancelada", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> { false }
        }
    }

    override fun onLivroClick(posicao: Int) {
        val livro = livrosList[posicao]
        val consultarLivroIntent = Intent(this, LivroActivity::class.java)
        consultarLivroIntent.putExtra(EXTRA_LIVRO, livro)
        startActivity(consultarLivroIntent)
    }
}