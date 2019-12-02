import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class knapsack {

    public static void main(String[] args) {
        int n = 10;
        int knapsackMaxWeight = 35;
        int solutionsPerPop = 8;
        int numGenerations = 50;

        int[] itemNumber = IntStream.range(1, n + 1).toArray();

        int[] weight = IntStream.range(1, n + 1).map(i -> new Random().nextInt(14) + 1).toArray();
        IntStream.range(0, weight.length).forEach(i -> System.out.println(String.format("Item %d Weight %d", i + 1, weight[i])));

        int[] value = IntStream.range(1, n + 1).map(i -> new Random().nextInt(740) + 10).toArray();

        int[] populationSize = {solutionsPerPop, itemNumber.length};
        int[][] initialPopulation = new int[populationSize[0]][populationSize[1]];

        IntStream.range(0, initialPopulation.length).forEach(i ->
                IntStream.range(0, initialPopulation[i].length).forEach(j ->
                        initialPopulation[i][j] = new Random().nextInt(2)
                )
        );

        optimize(weight, value, initialPopulation, populationSize, numGenerations, knapsackMaxWeight);

    }

    private static List<int[]> optimize(int[] weight, int[] value, int[][] population,
                                        int[] populationSize, int numGenerations, int knapsackMaxWeight) {
        List<int[]> fitnessHistory = new ArrayList<>();
        List<int[]> parameters = new ArrayList<>();
        int numParents = populationSize[0] / 2;
        int numOffsprings = populationSize[0] - numParents;

        for (int i = 0; i < numGenerations; i++) {
            int[] fitness = calculateFitness(weight, value, population, knapsackMaxWeight);
            fitnessHistory.add(fitness);
            int[][] parents = selection(fitness, numParents, population);
            int[][] offsprings = crossover(parents, numOffsprings);
            int[][] mutants = mutations(offsprings);

            IntStream.range(0, parents.length).forEach(x -> {
                population[x] = parents[x];
            });
            IntStream.range(parents.length, mutants.length*2).forEach(x -> {
                population[x] = mutants[x-parents.length];
            });
        }

        int[] fitnessLastGen = calculateFitness(weight, value, population, knapsackMaxWeight);
        int[] maxFitness = IntStream.range(0, fitnessLastGen.length)
                .filter(x -> fitnessLastGen[x] == Arrays.stream(fitnessLastGen).max().getAsInt())
                .toArray();
        parameters.add(population[maxFitness[0]]);

        IntStream.range(0, parameters.get(0).length).forEach(i -> System.out.print(parameters.get(0)[i] + " "));


        return parameters;
    }

    private static int[] calculateFitness(int[] weight, int[] value, int[][] population, int knapsackMaxWeight) {
        int[] fitness = new int[population.length];

        for (int i = 0; i < population.length; i++) {
            final int index = i;

            int weightSum = IntStream.range(0, population[i].length).map(x -> population[index][x] * weight[x]).sum();

            if (weightSum <= knapsackMaxWeight) {
                fitness[i] = IntStream.range(0, population[i].length)
                        .map(x -> population[index][x] * value[x])
                        .sum();
            } else {
                fitness[i] = 0;
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

}
