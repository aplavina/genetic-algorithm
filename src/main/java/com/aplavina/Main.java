package com.aplavina;

import java.util.*;

public class Main {
    private static final int POPULATION = 4;
    private static final float MUTATION_PROBABILITY = 0.1f;
    private static final int NUMBER_OF_ITERATIONS = 200;
    private static final int[][] DISTANCES_MATRIX =
            {
                    {0, 4, 5, 3, 8},
                    {4, 0, 7, 6, 8},
                    {5, 7, 0, 7, 9},
                    {3, 6, 7, 0, 9},
                    {8, 9, 9, 9, 0}
            };

    public static void main(String[] args) {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(DISTANCES_MATRIX, MUTATION_PROBABILITY, POPULATION);
        geneticAlgorithm.iterate(NUMBER_OF_ITERATIONS);
        Map<List<Integer>, Integer> res = geneticAlgorithm.getDistances();
        System.out.println(res);
    }
}