import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Knapsack {

    private final int maxWeight;
    private int[][] population;
    private int[] weight;
    private int[] value;
    private int[] populationSize;
    private int[][] fitnessHistory;

    public Knapsack(int numItems, int solutionsPerPop, int maxWeight) {
        this.maxWeight = maxWeight;
        this.weight = IntStream.range(0, numItems).map(i -> new Random().nextInt(14) + 1).toArray();
        this.value = IntStream.range(0, numItems).map(i -> new Random().nextInt(740) + 10).toArray();
        this.populationSize = IntStream.of(solutionsPerPop, numItems).toArray();
        this.population = new int[populationSize[0]][populationSize[1]];
    }

    public void optimize(int numGenerations) {
        initPopulation();
        fitnessHistory = new int[numGenerations][populationSize[0]];

        int numParents = populationSize[0] / 2;
        int numOffsprings = populationSize[0] - numParents;

        for (int i = 0; i < numGenerations; i++) {
            int[] fitness = calculateFitness(weight, value, population, this.maxWeight);
            fitnessHistory[i] = fitness;

            int[][] parents = selection(fitness, numParents, population);
            int[][] offsprings = crossover(parents, numOffsprings);
            int[][] mutants = mutations(offsprings);

            rebuildPopulation(parents, mutants);
        }
    }

    public int[] getMaxFitnessFromLastGen() {
        int[] lastGen = fitnessHistory[fitnessHistory.length - 1];

        int[] maxFitness = IntStream.range(0, lastGen.length)
                .filter(x -> lastGen[x] == Arrays.stream(lastGen).max().getAsInt())
                .toArray();

        return population[maxFitness[0]];
    }

    public int[] getMaxHistoryFitnessGen() {
        int[] history = new int[fitnessHistory.length];

        for (int i = 0; i < fitnessHistory.length; i++) {
            history[i] = Arrays.stream(fitnessHistory[i]).max().getAsInt();
        }
        return history;
    }

    public double[] getAverageHistoryFitnessGen() {
        double[] history = new double[fitnessHistory.length];

        for (int i = 0; i < fitnessHistory.length; i++) {
            history[i] = Arrays.stream(fitnessHistory[i]).filter(x -> x != -999999).average().getAsDouble();
        }
        return history;
    }

    public int[] getWeight() {
        return this.weight;
    }

    public int[] getValue() {
        return this.value;
    }

    public void printItemsSummary() {
        System.out.format("%-8s%-8s%-8s\n", "Item", "Weight", "Value");
        for (int i = 0; i < this.weight.length; i++) {
            System.out.format("%-8s%-8s%-8s\n", String.valueOf(i + 1), String.valueOf(this.weight[i]),
                    String.valueOf(this.value[i]));
        }
    }

    public void printPopulation() {
        System.out.println("\nPopulation after generations");
        for (int i = 0; i < this.population.length; i++) {
            final int index = i;
            IntStream.range(0, this.population[index].length).forEach(x -> System.out.print(this.population[index][x] + " "));
            System.out.println();
        }
        System.out.println();
    }

    private static int[] calculateFitness(int[] weight, int[] value, int[][] population, int knapsackMaxWeight) {
        int[] fitness = new int[population.length];

        for (int i = 0; i < population.length; i++) {
            final int index = i;

            int weightSum = IntStream.range(0, population[index].length).map(x -> population[index][x] * weight[x]).sum();

            if (weightSum <= knapsackMaxWeight) {
                fitness[index] = IntStream.range(0, population[index].length)
                        .map(x -> population[index][x] * value[x])
                        .sum();
            } else {
                fitness[index] = 0;
            }
        }
        return fitness;
    }

    private static int[][] selection(int[] fitness, int numParents, int[][] population) {
        int[][] parents = new int[numParents][population[0].length];

        for (int i = 0; i < numParents; i++) {
            int[] maxFitnessIdx = IntStream.range(0, fitness.length)
                    .filter(x -> fitness[x] == Arrays.stream(fitness).max().getAsInt())
                    .toArray();
            parents[i] = population[maxFitnessIdx[0]];
            fitness[maxFitnessIdx[0]] = -999999;
        }
        return parents;
    }

    private static int[][] crossover(int[][] parents, int numOffsprings) {
        int offsprings[][] = new int[numOffsprings][parents[0].length];
        int crossoverPoint = parents[0].length / 2;
        int crossoverRate = 8;

        int i = 0;
        while (i < numOffsprings) {
            final int pos = i;
            if (new Random().nextInt(10) + 1 > crossoverRate) {
                continue;
            }
            int parent1Index = i % parents.length;
            int parent2Index = (i + 1) % parents.length;

            IntStream.range(0, parents[parent1Index].length).forEach(x -> {
                if (x < crossoverPoint) {
                    offsprings[pos][x] = parents[parent1Index][x];
                } else {
                    offsprings[pos][x] = parents[parent2Index][x];
                }
            });
            i += 1;
        }
        return offsprings;
    }

    private static int[][] mutations(int[][] offsprings) {
        int mutants[][] = new int[offsprings.length][offsprings[0].length];
        int mutationRate = 4;

        for (int i = 0; i < mutants.length; i++) {
            mutants[i] = offsprings[i];
            if (new Random().nextInt(10) + 1 > mutationRate) {
                continue;
            }
            int randomValue = new Random().nextInt(offsprings[0].length);
            if (mutants[i][randomValue] == 0) {
                mutants[i][randomValue] = 1;
            } else {
                mutants[i][randomValue] = 0;
            }
        }
        return mutants;
    }

    private void initPopulation() {
        IntStream.range(0, population.length).forEach(i ->
                IntStream.range(0, population[i].length).forEach(j ->
                        population[i][j] = new Random().nextInt(2)
                )
        );
    }

    private void rebuildPopulation(int[][] parents, int[][] mutants) {
        IntStream.range(0, parents.length).forEach(x -> {
            population[x] = parents[x];
        });
        IntStream.range(parents.length, populationSize[0]).forEach(x -> {
            population[x] = mutants[x - parents.length];
        });
    }
}
