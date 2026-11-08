package usuarios;

public abstract class Usuario {
    private final int id;
    private final String nome;
    private int emprestimosAtivos = 0;

    protected Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }


    public abstract int getPrazoDias();
    public abstract int getLimiteEmprestimos();
    public abstract double calcularMulta(int diasAtraso);

    public boolean podeEmprestar() {
        return emprestimosAtivos < getLimiteEmprestimos();
    }

    public void registrarEmprestimo() {
        if (!podeEmprestar()) {
            throw new IllegalStateException(nome + " atingiu o limite de " + getLimiteEmprestimos() + " empréstimos.");
        }
        emprestimosAtivos++;
    }

    public void registrarDevolucao() {
        if (emprestimosAtivos > 0) emprestimosAtivos--;
    }

    public int getId(){ return id; }

    public String getNome(){ return nome; }

    public int getEmprestimosAtivos(){ return emprestimosAtivos; }
}