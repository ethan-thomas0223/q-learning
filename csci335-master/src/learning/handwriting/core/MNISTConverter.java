package learning.handwriting.core;

import learning.handwriting.gui.DrawingEditor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class MNISTConverter {

    final static int MNIST_SIZE = 28;
    final static int MNIST_BYTES = MNIST_SIZE * MNIST_SIZE;

    final static int MAX_IMAGES_FILE = 60000;

    public static String pathify(String... dirs) {
        StringBuilder result = new StringBuilder();
        for (String dir: dirs) {
            result.append(File.separatorChar);
            result.append(dir);
        }
        result.append(File.separatorChar);
        return result.toString();
    }

    final static String BASE_DIR = pathify("Users", "ferrer", "Desktop", "mnist_data");
    final static String TRAINING_DATA = BASE_DIR + "train-images-idx3-ubyte";
    final static String TRAINING_LABELS = BASE_DIR + "train-labels-idx1-ubyte";
    final static String TESTING_DATA = BASE_DIR + "t10k-images-idx3-ubyte";
    final static String TESTING_LABELS = BASE_DIR + "t10k-labels-idx1-ubyte";

    public static void main(String[] args) throws IOException {
        System.out.println(minMaxMean(TRAINING_DATA));
        System.out.println(minMaxMean(TESTING_DATA));
        trainAndSave(TRAINING_LABELS, TRAINING_DATA, "mnist_train_60000");
        trainAndSave(TESTING_LABELS, TESTING_DATA, "mnist_test_10000");
    }

    static void trainAndSave(String labels, String data, String title) throws IOException {
        SampleData training = readLabelsAndImages(labels, data);
        File out = new File(title);
        PrintWriter writer = new PrintWriter(new FileWriter(out));
        writer.println(training);
        writer.close();
    }

    static class MinMaxMean {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY, sum = 0;
        int count = 0;

        public void bump(int value) {
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            count += 1;
        }

        public double getMin() {return min;}
        public double getMax() {return max;}
        public double getMean() {return sum / count;}

        public String toString() {
            return String.format("count:%d;min:%5.2f;max:%5.2f;mean:%5.2f", count, getMin(), getMax(), getMean());
        }
    }

    public static MinMaxMean minMaxMean(String imageFilename) throws IOException {
        MinMaxMean mmm = new MinMaxMean();
        for (int[] image: allImageBytes(imageFilename)) {
            for (int i = 0; i < image.length; i++) {
                if (image[i] > 0)
                    mmm.bump(image[i]);
            }
        }
        return mmm;
    }

    public static SampleData readLabelsAndImages(String labelFile, String imageFile) throws IOException {
        return readImages(imageFile, readLabels(labelFile));
    }

    public static ArrayList<String> readLabels(String filename) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        byte[] bytes = Files.readAllBytes(new File(filename).toPath());
        for (int i = 8; i < bytes.length; i++) {
            result.add(Byte.toString(bytes[i]));
        }
        return result;
    }

    static final int IMG_HEADER_SIZE = 16;
    static final int BYTE_MASK = 0xFF;

    public static ArrayList<int[]> allImageBytes(String filename) throws IOException {
        ArrayList<int[]> images = new ArrayList<>();
        byte[] bytes = Files.readAllBytes(new File(filename).toPath());
        int imageCount = 0;
        int[] currentImage = new int[MNIST_BYTES];
        for (int i = IMG_HEADER_SIZE; i < bytes.length; i++) {
            if (imageCount >= MNIST_BYTES) {
                imageCount = 0;
                images.add(currentImage);
                currentImage = new int[MNIST_BYTES];
            }
            else {
                currentImage[(i - IMG_HEADER_SIZE) % MNIST_BYTES] = bytes[i] & BYTE_MASK;
            }
            imageCount += 1;
        }
        return images;
    }

    public static SampleData readImages(String filename, ArrayList<String> labels) throws IOException {
        ArrayList<int[]> imageBytes = allImageBytes(filename);
        assert imageBytes.size() == labels.size();

        SampleData result = new SampleData();
        for (int i = 0; i < Math.min(MAX_IMAGES_FILE, imageBytes.size()); i++) {
            Drawing d = new Drawing(MNIST_SIZE, MNIST_SIZE);
            for (int b = 0; b < imageBytes.get(i).length; b++) {
                d.set(b % MNIST_SIZE, b / MNIST_SIZE, imageBytes.get(i)[b] > 0);
            }
            result.addDrawing(labels.get(i), d.resized(DrawingEditor.DRAWING_WIDTH, DrawingEditor.DRAWING_HEIGHT));
        }
        return result;
    }
}
