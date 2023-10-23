package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int SIZE = 24;
    private static float[] arr;

    public static void main(String[] args) {

        System.out.println("1 метод");
        firstCalculate(SIZE);
        System.out.println("2 метод");
        secondCalculate(SIZE);
        System.out.println("3 метод");
        thirdCalculate(5);
    }

    public static void formula(float[]arr){
        for (int i = 0; i < arr.length; i++)
            arr[i] = (float)(arr [i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
//        System.out.println(arr[0] + " " + arr[arr.length-1]);
    }
    public static void firstCalculate(int size){

        arr = new float [size];
        for (int i = 0; i < size; i++)
            arr[i] = 1;

        long startTime = System.currentTimeMillis();
        formula(arr);
        long endTime = System.currentTimeMillis();
        System.out.println(arr[0] + " " + arr[arr.length-1]);
        System.out.println((endTime - startTime) + "мс");


    }
    public static void secondCalculate(int size){
        int numThreads = 2;
        float[] array = new float[size];
        for(int i = 0;i< array.length;i++)
            array[i] = 1;

        long startTime = System.currentTimeMillis();

        int chunkSize = array.length / numThreads;

        Thread[] threads = new Thread[numThreads];
        float[][] arrs = new float[numThreads][];

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i + 1) * chunkSize;
            arrs[i] = Arrays.copyOfRange(array, start, end);

            int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < arrs[threadIndex].length; j++) {
                    int index = start + j;
                    float result = (float) (arrs[threadIndex][j] * Math.sin(0.2f + index / 5) * Math.cos(0.2f + index / 5) * Math.cos(0.4f + index / 2));
                    synchronized (array) {
                        array[index] = result;
                    }
                }

            });
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println(array[0] + " " + array[array.length-1]);
        System.out.println((endTime - startTime) + "мс");
    }
    public static void thirdCalculate(int numThreads) {
        float[] array = new float[SIZE];
        Arrays.fill(array, 1);

        long startTime = System.currentTimeMillis();

        int chunkSize = array.length / numThreads;
        int remainder = array.length % numThreads;

        Thread[] threads = new Thread[numThreads];
        float[][] chunks = new float[numThreads][];
        Object lock = new Object();
        int currentIndex = 0;

        for (int i = 0; i < numThreads; i++) {
            int start = currentIndex;
            int end = start + chunkSize ;
            if (i == 0) {
                end += remainder ;
            }
            chunks[i] = Arrays.copyOfRange(array, start, end);

            int threadIndex = i;
            threads[i] = new Thread(() -> {
                StringBuilder threadResult = new StringBuilder("Поток " + (threadIndex+1) + ": ");

                for (int j = 0; j < chunks[threadIndex].length; j++) {
                    int index = start + j;
                    float result = (float) (chunks[threadIndex][j] * Math.sin(0.2f + index / 5) * Math.cos(0.2f + index / 5) * Math.cos(0.4f + index / 2));
                    synchronized (lock) {
                        array[index] = result;
                    }
                    threadResult.append(result).append(" ");
                }

                System.out.println(threadResult);

            });
            currentIndex = end;
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println(array[0] + " " + array[array.length-1]);
        System.out.println((endTime - startTime) + "мс");

    }

}
