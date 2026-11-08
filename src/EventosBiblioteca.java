import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class EventosBiblioteca {

    enum TipoEvento { EMPRESTIMO_REALIZADO, RESERVA_CRIADA, LIVRO_DEVOLVIDO }

    record Evento(TipoEvento tipo, String livro, String usuario, LocalDate data) {}

    static class GerenciadorEventos {
        private final Map<TipoEvento, List<Consumer<Evento>>> ouvintes = new EnumMap<>(TipoEvento.class);

        void registrar(TipoEvento tipo, Consumer<Evento> ouvinte) {
            ouvintes.computeIfAbsent(tipo, t -> new ArrayList<>()).add(ouvinte);
        }

        void publicar(Evento evento) {
            ouvintes.getOrDefault(evento.tipo(), List.of()).forEach(o -> o.accept(evento));
        }
    }

    static class Biblioteca {
        private final GerenciadorEventos eventos;
        private final Map<String, String> emprestimos = new HashMap<>(); // livro -> usuário

        Biblioteca(GerenciadorEventos eventos) { this.eventos = eventos; }

        void emprestar(String livro, String usuario, LocalDate data) {
            if (emprestimos.containsKey(livro)) {
                throw new IllegalStateException("Livro " + livro + " já está emprestado.");
            }
            emprestimos.put(livro, usuario);
            eventos.publicar(new Evento(TipoEvento.EMPRESTIMO_REALIZADO, livro, usuario, data));
        }

        void reservar(String livro, String usuario, LocalDate data) {
            eventos.publicar(new Evento(TipoEvento.RESERVA_CRIADA, livro, usuario, data));
        }

        void devolver(String livro, LocalDate data) {
            String usuario = emprestimos.remove(livro);
            if (usuario == null) {
                throw new IllegalStateException("Livro " + livro + " não estava emprestado.");
            }
            eventos.publicar(new Evento(TipoEvento.LIVRO_DEVOLVIDO, livro, usuario, data));
        }
    }

    static class ServicoReservas {
        private final Map<String, Deque<String>> filas = new HashMap<>();

        void aoCriarReserva(Evento e) {
            filas.computeIfAbsent(e.livro(), l -> new ArrayDeque<>()).addLast(e.usuario());
            System.out.println("[RESERVAS] " + e.usuario() + " entrou na fila do livro " + e.livro()
                    + " (posição " + filas.get(e.livro()).size() + ")");
        }

        void aoDevolver(Evento e) {
            Deque<String> fila = filas.get(e.livro());
            if (fila == null || fila.isEmpty()) {
                System.out.println("[RESERVAS] Nenhuma reserva para o livro " + e.livro());
                return;
            }
            String proximo = fila.pollFirst();
            System.out.println("[NOTIFICAÇÃO] " + proximo + ": o livro " + e.livro()
                    + " foi devolvido e está disponível para você!");
            System.out.println("[RESERVAS] Restam " + fila.size() + " na fila de " + e.livro());
        }
    }

    public static void main(String[] args) {
        GerenciadorEventos gerenciador = new GerenciadorEventos();
        Biblioteca biblioteca = new Biblioteca(gerenciador);
        ServicoReservas reservas = new ServicoReservas();
        AtomicInteger totalDevolucoes = new AtomicInteger();

        for (TipoEvento tipo : TipoEvento.values()) {
            gerenciador.registrar(tipo, e -> System.out.println(
                    "[LOG] " + e.data() + " | " + e.tipo() + " | livro=" + e.livro() + " | usuário=" + e.usuario()));
        }
        gerenciador.registrar(TipoEvento.RESERVA_CRIADA, reservas::aoCriarReserva);
        gerenciador.registrar(TipoEvento.LIVRO_DEVOLVIDO, reservas::aoDevolver);
        gerenciador.registrar(TipoEvento.LIVRO_DEVOLVIDO, e -> totalDevolucoes.incrementAndGet());

        System.out.println("=== Cenário ===");
        biblioteca.emprestar("L001", "Ana", LocalDate.of(2026, 11, 3));
        biblioteca.emprestar("L002", "Carlos", LocalDate.of(2026, 11, 3));
        biblioteca.reservar("L001", "Bruno", LocalDate.of(2026, 11, 4));
        biblioteca.reservar("L001", "Marina", LocalDate.of(2026, 11, 5));

        System.out.println("\n--- Ana devolve o L001 (tem 2 reservas) ---");
        biblioteca.devolver("L001", LocalDate.of(2026, 11, 8));

        System.out.println("\n--- Carlos devolve o L002 (sem reservas) ---");
        biblioteca.devolver("L002", LocalDate.of(2026, 11, 9));

        System.out.println("\n=== Resumo ===");
        System.out.println("Total de devoluções contadas pelo ouvinte extra: " + totalDevolucoes.get());
    }
}