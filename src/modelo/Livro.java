package modelo;

public class Livro {
    private final String codigo;
    private final String titulo;
    private final String autor;
    private boolean disponivel = true;

    public Livro(String codigo, String titulo, String autor) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
    }

    public void emprestar() {
        if (!disponivel) {
            throw new IllegalStateException("Livro '" + titulo + "' já está emprestado.");
        }
        disponivel = false;
    }

    public void devolver() {
        disponivel = true;
    }

    public boolean isDisponivel() { return disponivel; }
    public String getCodigo()     { return codigo; }
    public String getTitulo()     { return titulo; }
    public String getAutor()      { return autor; }

    @Override
    public String toString() { return codigo + " - " + titulo + " (" + autor + ")"; }
}
