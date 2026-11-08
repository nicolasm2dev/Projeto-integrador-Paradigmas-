package modelo;

import usuarios.Usuario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprestimo {
    private final Livro livro;
    private final Usuario usuario;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevista;
    private LocalDate dataDevolucao;

    public Emprestimo(Livro livro, Usuario usuario, LocalDate dataEmprestimo) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataEmprestimo.plusDays(usuario.getPrazoDias());
    }

    public void finalizar(LocalDate data) { this.dataDevolucao = data; }

    public int calcularDiasAtraso() {
        LocalDate referencia = (dataDevolucao != null) ? dataDevolucao : LocalDate.now();
        long dias = ChronoUnit.DAYS.between(dataPrevista, referencia);
        return (int) Math.max(0, dias);
    }

    public boolean estaAtrasado()  { return calcularDiasAtraso() > 0; }
    public boolean isAtivo()       { return dataDevolucao == null; }

    public Livro getLivro()               { return livro; }
    public Usuario getUsuario()           { return usuario; }
    public LocalDate getDataEmprestimo()  { return dataEmprestimo; }
    public LocalDate getDataPrevista()    { return dataPrevista; }
}
