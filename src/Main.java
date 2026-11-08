import modelo.Emprestimo;
import modelo.Livro;
import servico.Biblioteca;
import usuarios.Aluno;
import usuarios.Professor;
import usuarios.Usuario;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Biblioteca bib = new Biblioteca();

        Livro l1 = new Livro("L001", "Java: Como Programar", "Deitel");
        Livro l2 = new Livro("L002", "Estruturas de Dados", "Cormen");
        Livro l3 = new Livro("L003", "Engenharia de Software", "Sommerville");
        Livro l4 = new Livro("L004", "Banco de Dados", "Date");
        Livro l5 = new Livro("L005", "Clean Code", "Martin");
        for (Livro l : new Livro[]{l1, l2, l3, l4, l5}) bib.cadastrarLivro(l);

        Usuario ana = new Aluno(1, "Ana", "2026001");
        Usuario carlos = new Professor(2, "Carlos", "Computação");
        bib.cadastrarUsuario(ana);
        bib.cadastrarUsuario(carlos);

        LocalDate hoje = LocalDate.of(2026, 11, 3);

        System.out.println("=== 1. Empréstimos ===");
        Emprestimo e1 = bib.realizarEmprestimo(ana, l1, hoje);
        Emprestimo e2 = bib.realizarEmprestimo(carlos, l2, hoje);
        System.out.println(ana.getNome() + " pegou '" + l1.getTitulo() + "' | devolver até " + e1.getDataPrevista());
        System.out.println(carlos.getNome() + " pegou '" + l2.getTitulo() + "' | devolver até " + e2.getDataPrevista());

        System.out.println("\n=== 2. Livro já emprestado ===");
        try {
            bib.realizarEmprestimo(carlos, l1, hoje);
        } catch (IllegalStateException ex) {
            System.out.println("Bloqueado: " + ex.getMessage());
        }

        System.out.println("\n=== 3. Limite de empréstimos do aluno ===");
        bib.realizarEmprestimo(ana, l3, hoje);
        bib.realizarEmprestimo(ana, l4, hoje);
        System.out.println(ana.getNome() + " tem " + ana.getEmprestimosAtivos() + " empréstimos ativos.");
        try {
            bib.realizarEmprestimo(ana, l5, hoje);
        } catch (IllegalStateException ex) {
            System.out.println("Bloqueado: " + ex.getMessage());
        }

        System.out.println("\n=== 4. Devolução com atraso (polimorfismo na multa) ===");

        double multaAna = bib.realizarDevolucao(e1, e1.getDataPrevista().plusDays(4));
        double multaCarlos = bib.realizarDevolucao(e2, e2.getDataPrevista().plusDays(4));
        System.out.printf("Aluno (%s): 4 dias de atraso -> multa R$ %.2f%n", ana.getNome(), multaAna);
        System.out.printf("Professor (%s): 4 dias de atraso -> multa R$ %.2f%n", carlos.getNome(), multaCarlos);

        System.out.println("\n=== 5. Livros disponíveis ===");
        for (Livro l : bib.listarDisponiveis()) System.out.println("- " + l);
    }
}