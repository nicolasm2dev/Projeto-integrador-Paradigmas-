package usuarios;

public class Professor extends Usuario {
    private final String departamento;

    public Professor(int id, String nome, String departamento) {
        super(id, nome);
        this.departamento = departamento;
    }

    @Override public int getPrazoDias() { return 15; }

    @Override public int getLimiteEmprestimos() { return 5; }

    @Override public double calcularMulta(int diasAtraso) { return diasAtraso * 0.50; }

    public String getDepartamento() { return departamento; }
}
