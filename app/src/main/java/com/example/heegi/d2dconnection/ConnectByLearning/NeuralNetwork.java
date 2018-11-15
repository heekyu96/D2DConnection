package com.example.heegi.d2dconnection.ConnectByLearning;

import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NeuralNetwork {
    private static int inputNodes;
    private static int hiddenNodes;
    private static int outputNodes;

    private double[][] wih;
    private double[][] who;

    final static int testCnt = 23;
    final static int[] testLabel = new int[testCnt];
    final static double[][] testLabelData = new double[testCnt][9];
    static double[][] testData = new double[testCnt][9];

    final static int trainCnt = 100;
    final static double[][] trainLabelData = new double[trainCnt][9];
    static double[][] trainData = new double[trainCnt][9];

    public NeuralNetwork(int i, int h, int o, InputStream wih, InputStream who) {
        inputNodes = i;
        hiddenNodes = h;
        outputNodes = o;

        this.wih = new double[inputNodes][hiddenNodes];
        this.who = new double[hiddenNodes][outputNodes];

        loadWeight(wih, who);
    }

    public int query(double[] inputLayer) {
        Log.d("inInputQuery", inputLayer[0] + "/" + inputLayer[1] + "/" + inputLayer[2] + "/" + inputLayer[3] + "/" + inputLayer[4] + "/" + inputLayer[5] + "/" + inputLayer[6] + "/" + inputLayer[7] + "/" + inputLayer[8]);

        double[] hiddenLayer = multiply(wih, inputLayer);
        hiddenLayer = sigmoidActivation(hiddenLayer);
        Log.d("inhiddenQuery", hiddenLayer[0] + "/" + hiddenLayer[1] + "/" + hiddenLayer[2] + "/" + hiddenLayer[3] + "/" + hiddenLayer[4] + "/" + hiddenLayer[5] + "/" + hiddenLayer[6] + "/" + hiddenLayer[7] + "/" + hiddenLayer[8]);


        double[] outputLayer = multiply(who, hiddenLayer);
        outputLayer = sigmoidActivation(outputLayer);

        Log.d("inOutputQuery", outputLayer[0] + "/" + outputLayer[1] + "/" + outputLayer[2] + "/" + outputLayer[3] + "/" + outputLayer[4] + "/" + outputLayer[5] + "/" + outputLayer[6] + "/" + outputLayer[7] + "/" + outputLayer[8]);

        int maxIdx = 0;
        for (int i = 1; i < outputLayer.length; i++) {
            if (outputLayer[maxIdx] < outputLayer[i]) {
                maxIdx = i;
            }
        }

        for (int i = 0; i < 9; i++) {
            Log.d("who", who[i][0] + "/" + who[i][1] + "/" + who[i][2] + "/" + who[i][3] + "/" + who[i][4] + "/" + who[i][5] + "/" + who[i][6] + "/" + who[i][7] + "/" + who[i][8]);

        }

        return maxIdx + 1;
    }

    public void train(double[] inputLayer, double[] target) {
        Log.d("inputQ_in_train", inputLayer[0] + "/" + inputLayer[1] + "/" + inputLayer[2] + "/" + inputLayer[3] + "/" + inputLayer[4] + "/" + inputLayer[5] + "/" + inputLayer[6] + "/" + inputLayer[7] + "/" + inputLayer[8]);

        double[] hiddenLayer = multiply(wih, inputLayer);
        hiddenLayer = sigmoidActivation(hiddenLayer);
        Log.d("inputQ_out_train", hiddenLayer[0] + "/" + hiddenLayer[1] + "/" + hiddenLayer[2] + "/" + hiddenLayer[3] + "/" + hiddenLayer[4] + "/" + hiddenLayer[5] + "/" + hiddenLayer[6] + "/" + hiddenLayer[7] + "/" + hiddenLayer[8]);


        double[] outputLayer = multiply(who, hiddenLayer);
        outputLayer = sigmoidActivation(outputLayer);

        Log.d("outputQ_out_train", outputLayer[0] + "/" + outputLayer[1] + "/" + outputLayer[2] + "/" + outputLayer[3] + "/" + outputLayer[4] + "/" + outputLayer[5] + "/" + outputLayer[6] + "/" + outputLayer[7] + "/" + outputLayer[8]);

//        int maxIdx = 0;
//        for (int i = 1; i < outputLayer.length; i++) {
//            if (outputLayer[maxIdx] < outputLayer[i]) {
//                maxIdx = i;
//            }
//        }
//
//        for(int i=0;i<9;i++){
//            Log.d("who",who[i][0]+"/"+who[i][1]+"/"+who[i][2]+"/"+who[i][3]+"/"+who[i][4]+"/"+who[i][5]+"/"+who[i][6]+"/"+who[i][7]+"/"+who[i][8]);
//
//        }
        double[] outputError = sub(target,inputLayer);
        double[] hiddenError = multiply(who,outputError);




    }

    public void testQuery() {
        int cnt = 0;
        for (int i = 0; i < testCnt; i++) {
            if (testLabel[i] == query(testData[i])) {
                cnt++;
            }
        }
        Log.d("testQuery", cnt + "/" + testCnt + "");

    }

    private static double[] multiply(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += a[i][j] * x[j];
        return y;
    }

    private static double[] sub(double[] a, double[] b) {
        if (a.length != b.length) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[a.length];
        for (int i = 0; i < a.length; i++)
            y[i] = a[i]-b[i];

        return y;
    }

    private double[] sigmoidActivation(double[] layer) {
        double[] result = new double[layer.length];
        for (int i = 0; i < layer.length; i++) {
            result[i] = 1.0 / (1.0 + Math.pow(Math.E, (-1.0 * layer[i])));
        }

        return result;
    }

    public void loadWeight(InputStream wih, InputStream who) {
        InputStreamReader inputStreamReader;
        String[] record;

        try {
            inputStreamReader = new InputStreamReader(wih);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            CSVReader read = new CSVReader(reader);

            int row = 0;
            while ((record = read.readNext()) != null) {
                int col = 0;
                for (String str : record) {
                    this.wih[row][col++] = Double.valueOf(str);
                    Log.d("readCsvWih", this.wih[row][col - 1] + "");
                }
                row++;
                Log.d("readCsvWih", " / ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStreamReader = new InputStreamReader(who);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            CSVReader read = new CSVReader(reader);

            int row = 0;
            while ((record = read.readNext()) != null) {
                int col = 0;
                for (String str : record) {
                    this.who[row][col++] = Double.valueOf(str);
                    Log.d("readCsvWho", this.who[row][col - 1] + "");
                }
                row++;
                Log.d("readCsvWho", " / ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataSet(InputStream test) {
        InputStreamReader inputStreamReader;
        String[] record;

        try {
            inputStreamReader = new InputStreamReader(test);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            CSVReader read = new CSVReader(reader);

            int cases = 0;
            while ((record = read.readNext()) != null) {
                testLabel[cases] = Integer.valueOf(record[0]);
                for (int i = 1; i <= inputNodes; i++) {
                    // TODO: 2018-10-29 input
                    testData[cases][i - 1] = (Double.valueOf(record[i]) / 100.0 * 0.99) + 0.99;
                    Log.d("readCsvTest", testData[cases][i - 1] + "");
                }
                cases++;
                Log.d("readCsvTest", " / ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < testLabel.length; i++) {
            for (int j = 0; j < outputNodes; j++) {
                if (testLabel[i] == j + 1) {
                    testLabelData[i][j] = 0.99;
                } else {
                    testLabelData[i][j] = 0.01;
                }
            }
        }
    }
}
