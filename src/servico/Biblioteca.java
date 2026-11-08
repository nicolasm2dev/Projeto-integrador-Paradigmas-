package servico;

import modelo.Emprestimo;
import modelo.Livro;
import usuarios.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private final List<Livro> livros = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    public void cadastrarLivro(Livro livro)         { livros.add(livro); }
    public void cadastrarUsuario(Usuario usuario)   { usuarios.add(usuario); }

    public Emprestimo realizarEmprestimo(Usuario usuario, Livro livro, LocalDate data) {
        usuario.registrarEmprestimo();
        try {
            livro.emprestar();
        } catch (IllegalStateException e) {
            usuario.registrarDevolucao();
            throw e;
        }
        Emprestimo e = new Emprestimo(livro, usuario, data);
        emprestimos.add(e);
        return e;
    }

    public double realizarDevolucao(Emprestimo emprestimo, LocalDate data) {
        emprestimo.finalizar(data);
        emprestimo.getLivro().devolver();
        emprestimo.getUsuario().registrarDevolucao();
        int atraso = emprestimo.calcularDiasAtraso();
        return emprestimo.getUsuario().calcularMulta(atraso); // polimorfismo
    }

    public List<Livro> listarDisponiveis() {
        List<Livro> disponiveis = new ArrayList<>();
        for (Livro l : livros) {
            if (l.isDisponivel()) disponiveis.add(l);
        }
        return disponiveis;
    }
}