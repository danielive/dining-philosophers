import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Daniel Chuev
 */
final class Manager {

    final private static SimpleDateFormat formatDate = new SimpleDateFormat("ss:SSS");
    private static List<Philosopher> philosophers = new ArrayList<>();
    private static List<Fork> forks = new ArrayList<>();
    private static boolean stateDined;
    private final static int max = 10;
    private final static int min = 1;
    private static List<Edge> edges = new ArrayList<>();
    private static List<Edge> edgesExecute = new ArrayList<>();

    void execute(final int count) {
        System.out.println("Number of philosophers: " + count);

        // set nodes
        for (int i = 0; i < count; i++) {
            philosophers.add(new Philosopher(i));
            forks.add(new Fork(i));
        }

        // execute
        while (true) {
            edges.clear();
            setStateDined(false);
            getPhilosophers().forEach(Philosopher::clear);
            getForks().forEach(Fork::clear);
            setEdges(count);
            Collections.sort(edges);

            while (!stateDined) {

                edgesExecute.clear();

                for (Edge edge : edges) {
                    if (choicePhilosopher(edge)) {
                        edgesExecute.add(edge);
                    }
                }

                edgesExecute.stream()
                        .parallel()
                        .forEach(this::diningPhilosopher);

                edges.forEach(this::choiceRestorePhilosopher);

                checkDined(count);
            }
        }
    }

    private void checkDined(final int count) {
        int i = 0;

        for (Philosopher phil : Manager.getPhilosophers()) {
            if (phil.getDined()) {
                i++;
            }
        }

        if (i == count) setStateDined(true);
    }

    private void choiceRestorePhilosopher(Edge edge) {
        if (!(Manager.getForks().get(edge.getPhilosopherOne().getNumber()).isState() && Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).isState()) &&
                (Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getState() && Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getState()) &&
                !Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getDined() && Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getTake()
                && edge.isStateEdge()) {
            Manager.getForks().get(edge.getPhilosopherOne().getNumber()).setState(true);
            Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setTake(false);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setDined(true);
        }
        else if (!(Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).isState() && Manager.getForks().get(edge.getPhilosopherOne().getNumber()).isState()) &&
                (Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getState() && Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getState()) &&
                !Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getDined() && Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getTake()
                && edge.isStateEdge()) {
            Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).setState(true);
            Manager.getForks().get(edge.getPhilosopherOne().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setTake(false);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setDined(true);
        }
    }

    private static void setStateDined(boolean stateDined) {
        Manager.stateDined = stateDined;
    }

    private void diningPhilosopher(Edge edge) {
        getPhilosophers().get(edge.getNumPhilosopher()).dining();
    }

    @NotNull
    private Boolean choicePhilosopher(Edge edge) {
        if ((Manager.getForks().get(edge.getPhilosopherOne().getNumber()).isState() && Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).isState()) &&
                !(Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getState() && Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getState()) &&
                !Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getDined() && !Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getTake()
                && !edge.isStateEdge()) {

            edge.setStateEdge(true);
            Manager.getForks().get(edge.getPhilosopherOne().getNumber()).setState(false);
            Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setTake(true);
            edge.setNumPhilosopher(edge.getPhilosopherOne().getNumber());
            System.out.printf("one edge: Node %s, Node %s%n" +
                    "Philosopher-%s takes forks %s and %s : %s%n", edge.getPhilosopherOne().getNumber() + 1, edge.getPhilosopherTwo().getNumber() + 1,
                    edge.getPhilosopherOne().getNumber() + 1, edge.getPhilosopherOne().getNumber() + 1, edge.getPhilosopherTwo().getNumber() + 1,
                    Philosopher.getFormatDate().format(new Date()));
            return true;
        } else if ((Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).isState() && Manager.getForks().get(edge.getPhilosopherOne().getNumber()).isState()) &&
                !(Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getState() && Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).getState()) &&
                !Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getDined() && !Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).getTake()
                && !edge.isStateEdge()) {

            edge.setStateEdge(true);
            Manager.getForks().get(edge.getPhilosopherTwo().getNumber()).setState(false);
            Manager.getForks().get(edge.getPhilosopherOne().getNumber()).setState(false);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherOne().getNumber()).setState(true);
            Manager.getPhilosophers().get(edge.getPhilosopherTwo().getNumber()).setTake(true);
            edge.setNumPhilosopher(edge.getPhilosopherTwo().getNumber());
            System.out.printf("two edge: Node %s, Node %s%n" +
                            "Philosopher-%s takes forks %s and %s : %s%n", edge.getPhilosopherTwo().getNumber() + 1, edge.getPhilosopherOne().getNumber() + 1,
                    edge.getPhilosopherTwo().getNumber() + 1, edge.getPhilosopherTwo().getNumber() + 1, edge.getPhilosopherOne().getNumber() + 1,
                    Philosopher.getFormatDate().format(new Date()));
            return true;
        }
        return false;
    }

    @Contract(pure = true)
    private static List<Philosopher> getPhilosophers() {
        return philosophers;
    }

    @Contract(pure = true)
    static List<Fork> getForks() {
        return forks;
    }

    private void setEdges(int count) {
        System.out.println("_________________________________\n");

        // set edges
        for (int i = 0; i < count; i++)
            if ((i+1) < count)
                edges.add(new Edge(philosophers.get(i), philosophers.get(i+1), new Random().nextInt((max - min + 1) + min)));

        // additionally
        for (int i = 0; i < count-2; i++)
            if ((i+2) != count-1)
                edges.add(new Edge(philosophers.get(i), philosophers.get(i+2), new Random().nextInt((max - min + 1) + min)));
            else
                edges.add(new Edge(philosophers.get(count-1), philosophers.get(0), new Random().nextInt((max - min + 1) + min)));

        System.out.println("_________________________________\n");
    }
}