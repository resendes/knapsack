import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Application {


    public static void main(String[] args) {
        int numItems = 10;
        int maxWeight = 35;
        int solutionsPerPop = 12;
        int numGenerations = 100;

        Knapsack knapsack = new Knapsack(numItems, solutionsPerPop, maxWeight);
        knapsack.printItemsSummary();
        knapsack.optimize(numGenerations);

        knapsack.printPopulation();

        System.out.println("\nLast gen (weight)");
        int[] maxFitnessFromLastGen = knapsack.getMaxFitnessFromLastGen();
        IntStream.range(0, maxFitnessFromLastGen.length).forEach(i -> System.out.print((maxFitnessFromLastGen[i] * knapsack.getWeight()[i]) + " "));
        System.out.println("\nSum: " + IntStream.range(0, maxFitnessFromLastGen.length).map(i -> maxFitnessFromLastGen[i] * knapsack.getWeight()[i]).sum());

        System.out.println("\nAverage max");
        int[] maxHistoryFitnessGen = knapsack.getMaxHistoryFitnessGen();
        IntStream.range(0, maxHistoryFitnessGen.length).forEach(i -> System.out.print((maxHistoryFitnessGen[i] + " ")));
        System.out.println();

        System.out.println("\nAverage history");
        double[] averageHistoryFitnessGen = knapsack.getAverageHistoryFitnessGen();
        IntStream.range(0, averageHistoryFitnessGen.length).forEach(i -> System.out.print((averageHistoryFitnessGen[i] + " ")));
        System.out.println();
    }

}
