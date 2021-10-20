package br.edu.ifsp.scl.ads.pdm.livros.controller

import br.edu.ifsp.scl.ads.pdm.livros.MainActivity
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro
import br.edu.ifsp.scl.ads.pdm.livros.model.LivroDAO
import br.edu.ifsp.scl.ads.pdm.livros.model.LivroSqlite

class LivroController(mainActivity: MainActivity) {

    private val livroDAO: LivroDAO = LivroSqlite(mainActivity)

    fun inserirLivro(livro: Livro) = livroDAO.criarLivro(livro)

    fun buscarLivro(titulo: String) = livroDAO.recuperarLivro(titulo)

    fun buscarLivros() = livroDAO.recuperarLivros()

    fun modificarLivro(livro: Livro) = livroDAO.atualizarLivro(livro)

    fun apagarLivro(titulo: String) = livroDAO.removerLivro(titulo)


}