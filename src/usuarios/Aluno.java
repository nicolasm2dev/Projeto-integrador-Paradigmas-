package usuarios;

public class Aluno extends Usuario {
    private final String matricula;

    public Aluno(int id, String nome, String matricula) {
        super(id, nome);
        this.matricula = matricula;
    }

    @Override public int getPrazoDias()          { return 7; }
    @Override public int getLimiteEmprestimos()  { return 3; }
    @Override public double calcularMulta(int diasAtraso) { return diasAtraso * 1.00; }

    public String getMatricula() { return matricula; }
}
