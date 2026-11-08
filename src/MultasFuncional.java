import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MultasFuncional {

    enum TipoUsuario { ALUNO, PROFESSOR }

    record EmprestimoInfo(String usuario, TipoUsuario tipo, String livro,
                          LocalDate dataPrevista, LocalDate dataDevolucao) {}


    static Function<Integer, Double> criarCalculadoraMulta(double valorPorDia) {
        return dias -> dias * valorPorDia;
    }

    static final Map<TipoUsuario, Function<Integer, Double>> REGRAS_MULTA = Map.of(
            TipoUsuario.ALUNO, criarCalculadoraMulta(1.00),
            TipoUsuario.PROFESSOR, criarCalculadoraMulta(0.50));


    static int diasAtraso(EmprestimoInfo e) {
        long dias = ChronoUnit.DAYS.between(e.dataPrevista(), e.dataDevolucao());
        return (int) Math.max(0, dias);
    }

    static double multa(EmprestimoInfo e) {
        return REGRAS_MULTA.get(e.tipo()).apply(diasAtraso(e));
    }

    static final Predicate<EmprestimoInfo> estaAtrasado = e -> diasAtraso(e) > 0;

    static double totalMultas(List<EmprestimoInfo> lista) {
        return lista.stream()
                .filter(estaAtrasado)
                .mapToDouble(MultasFuncional::multa)
                .sum();
    }

    static Map<TipoUsuario, Double> multasPorTipo(List<EmprestimoInfo> lista) {
        return lista.stream()
                .collect(Collectors.groupingBy(EmprestimoInfo::tipo, TreeMap::new,
                        Collectors.summingDouble(MultasFuncional::multa)));
    }

    static String descreverAtrasados(List<EmprestimoInfo> lista) {
        return lista.stream()
                .filter(estaAtrasado)
                .map(e -> e.usuario() + " (" + diasAtraso(e) + " dias)")
                .collect(Collectors.joining(", "));
    }


    static double somaRecursiva(List<Double> valores) {
        if (valores.isEmpty()) return 0.0;                              // caso base
        return valores.get(0) + somaRecursiva(valores.subList(1, valores.size())); // caso recursivo
    }


    static void verificar(String nome, double esperado, double obtido) {
        boolean ok = Math.abs(esperado - obtido) < 0.001;
        System.out.printf("%-45s esperado=%.2f obtido=%.2f -> %s%n", nome, esperado, obtido, ok ? "OK" : "FALHOU");
    }

    public static void main(String[] args) {
        List<EmprestimoInfo> emprestimos = List.of(
                new EmprestimoInfo("Ana",    TipoUsuario.ALUNO,     "Java: Como Programar",   LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 14)),
                new EmprestimoInfo("Carlos", TipoUsuario.PROFESSOR, "Estruturas de Dados",    LocalDate.of(2026, 11, 18), LocalDate.of(2026, 11, 22)),
                new EmprestimoInfo("Bruno",  TipoUsuario.ALUNO,     "Clean Code",             LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 9)),
                new EmprestimoInfo("Marina", TipoUsuario.ALUNO,     "Banco de Dados",         LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 17)),
                new EmprestimoInfo("Paula",  TipoUsuario.PROFESSOR, "Engenharia de Software", LocalDate.of(2026, 11, 18), LocalDate.of(2026, 11, 18)));

        System.out.println("=== Multa por empréstimo ===");
        emprestimos.forEach(e -> System.out.printf("%-7s %-10s atraso=%d dia(s) multa=R$ %.2f%n",
                e.usuario(), e.tipo(), diasAtraso(e), multa(e)));

        System.out.println("\n=== Resultados do pipeline ===");
        System.out.println("Atrasados: " + descreverAtrasados(emprestimos));
        System.out.printf("Total de multas: R$ %.2f%n", totalMultas(emprestimos));
        System.out.println("Multas por tipo: " + multasPorTipo(emprestimos));

        List<Double> multas = emprestimos.stream().map(MultasFuncional::multa).toList();
        System.out.printf("Total via recursão: R$ %.2f%n", somaRecursiva(multas));

        System.out.println("\n=== Testes (entrada -> saída esperada) ===");
        verificar("Aluno, 4 dias de atraso (R$ 1,00/dia)", 4.00, REGRAS_MULTA.get(TipoUsuario.ALUNO).apply(4));
        verificar("Professor, 4 dias de atraso (R$ 0,50/dia)", 2.00, REGRAS_MULTA.get(TipoUsuario.PROFESSOR).apply(4));
        verificar("Devolução antes do prazo (Bruno)", 0.00, multa(emprestimos.get(2)));
        verificar("Devolução no prazo (Paula)", 0.00, multa(emprestimos.get(4)));
        verificar("Total de multas da lista", 13.00, totalMultas(emprestimos));
        verificar("Soma recursiva == soma por stream", totalMultas(emprestimos), somaRecursiva(multas));
        verificar("Lista vazia", 0.00, totalMultas(List.of()));

        System.out.println("\n=== Imutabilidade ===");
        System.out.println("Tamanho da lista original após os cálculos: " + emprestimos.size() + " (inalterada)");
    }
}