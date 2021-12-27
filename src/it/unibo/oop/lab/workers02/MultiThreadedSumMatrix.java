package it.unibo.oop.lab.workers02;

import java.util.List;

import java.util.ArrayList;

/**
 * 
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     */
    public MultiThreadedSumMatrix(final int nthread) {
        super();
        if (nthread < 1) {
            throw new IllegalArgumentException();
        }
        this.nthread = nthread;
    }

    private final class Worker extends Thread {

        private final double [][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elements to sum up for this worker
         */
        private Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double element : this.matrix[i]) {
                    this.res += element;
                }
            }
        }

        /**
         * Returns the result of summing up the doubles within the matrix.
         * 
         * @return the sum of every element in the matrix
         */
        public double getResult() {
            return this.res;
        }
    }

    /**
     * 
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length / nthread + matrix.length % nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
}
