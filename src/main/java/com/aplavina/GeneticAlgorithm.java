package com.aplavina;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticAlgorithm {
    private final int population;
    private final int[][] distancesMatrix;
    private final float mutationProbability;
    private final Map<List<Integer>, Integer> routesDistancesMap;

    public GeneticAlgorithm(int[][] distancesMatrix, float mutationProbability, int population) {
        this.population = population;
        this.distancesMatrix = distancesMatrix;
        this.mutationProbability = mutationProbability;
        int numberOfCities = distancesMatrix.length;
        Set<List<Integer>> permutations = getRandomPermutations(
                IntStream.range(0, numberOfCities).boxed().toList(), population);
        this.routesDistancesMap = permutations
                .stream().collect(Collectors.toMap(
                        route -> route,
                        route -> calculateRouteDistance(distancesMatrix, route)
                ));
    }

    public void iterate(int numberOfIterations) {
        int iteration = 1;
        while (iteration <= numberOfIterations) {
            List<List<Integer>> routes = new ArrayList<>(routesDistancesMap.keySet().stream().toList());
            while (routes.size() > 1) {
                List<Integer> firstParent = routes.remove(ThreadLocalRandom.current().nextInt(0, routes.size()));
                routes.remove(firstParent);
                List<Integer> secondParent = routes.remove(ThreadLocalRandom.current().nextInt(0, routes.size()));
                routes.remove(secondParent);
                int firstPoint = ThreadLocalRandom.current().nextInt(1, firstParent.size() - 1);
                int secondPoint = ThreadLocalRandom.current().nextInt(1, firstParent.size() - 1);
                while (secondPoint == firstPoint) {
                    secondPoint = ThreadLocalRandom.current().nextInt(1, firstParent.size() - 1);
                }
                addChildren(firstParent, secondParent, firstPoint, secondPoint);
            }
            iteration++;
        }
    }

    public Map<List<Integer>, Integer> getDistances() {
        return new HashMap<>(routesDistancesMap);
    }

    private void addChildren(List<Integer> firstParent, List<Integer> secondParent, int firstPoint, int secondPoint) {
        if (firstPoint > secondPoint) {
            int tmp = firstPoint;
            firstPoint = secondPoint;
            secondPoint = tmp;
        }
        List<Integer> firstChild = new ArrayList<>();
        List<Integer> secondChild = new ArrayList<>();
        for (int i = 0; i < firstParent.size(); ++i) {
            if (i >= firstPoint && i <= secondPoint) {
                firstChild.add(secondParent.get(i));
                secondChild.add(firstParent.get(i));
            } else {
                firstChild.add(-1);
                secondChild.add(-1);
            }
        }
        int firstParentIndex = firstPoint + 1;
        int secondParentIndex = firstPoint + 1;
        for (int i = 0; i < firstChild.size() - 1; ++i) {
            if (i >= firstPoint && i <= secondPoint) {
                continue;
            }
            while (firstChild.contains(firstParent.get(firstParentIndex))) {
                firstParentIndex = (firstParentIndex + 1) % firstParent.size();
            }
            while (secondChild.contains(secondParent.get(secondParentIndex))) {
                secondParentIndex = (secondParentIndex + 1) % secondParent.size();
            }
            firstChild.set(i, firstParent.get(firstParentIndex));
            secondChild.set(i, secondParent.get(secondParentIndex));
        }
        firstChild.set(firstParent.size() - 1, firstChild.get(0));
        mutate(firstChild);
        secondChild.set(secondChild.size() - 1, secondChild.get(0));
        mutate(secondChild);
        routesDistancesMap.put(firstChild, calculateRouteDistance(distancesMatrix, firstChild));
        routesDistancesMap.put(secondChild, calculateRouteDistance(distancesMatrix, secondChild));
        shrinkRoutesTable();
    }

    private void shrinkRoutesTable() {
        List<List<Integer>> sortedRoutes = routesDistancesMap
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();
        for (int i = population + 1; i < sortedRoutes.size(); ++i) {
            routesDistancesMap.remove(sortedRoutes.get(i));
        }
    }

    private void mutate(List<Integer> child) {
        if (ThreadLocalRandom.current().nextFloat(1.0f) <= mutationProbability) {
            int firstPoint = ThreadLocalRandom.current().nextInt(1, child.size() - 1);
            int secondPoint = ThreadLocalRandom.current().nextInt(1, child.size() - 1);
            while (secondPoint == firstPoint) {
                secondPoint = ThreadLocalRandom.current().nextInt(1, child.size() - 1);
            }
            int tmp = child.get(firstPoint);
            child.set(firstPoint, child.get(secondPoint));
            child.set(secondPoint, tmp);
        }
    }

    private static Set<List<Integer>> getRandomPermutations(List<Integer> values, int population) {
        Set<List<Integer>> permutations = new HashSet<>();
        while (permutations.size() < population) {
            List<Integer> toShuffle = new ArrayList<>(values);
            Collections.shuffle(toShuffle);
            toShuffle.add(toShuffle.get(0));
            permutations.add(toShuffle);
        }
        return permutations;
    }

    private static int calculateRouteDistance(int[][] distancesMatrix, List<Integer> route) {
        int distance = 0;
        for (int i = 0; i < route.size() - 1; ++i) {
            distance += distancesMatrix[route.get(i)][route.get(i + 1)];
        }
        return distance;
    }
}
