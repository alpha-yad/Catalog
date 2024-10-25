import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Point {
    double x;
    double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class test {
   public static void main(String[] args) {
      String input = "./test.json"; // Specify the correct path to your JSON file
      try {
          // Load the JSON data
          String content = new String(Files.readAllBytes(Paths.get(input)));
          System.out.println("Loaded JSON content: " + content); // Debug print
          
          int n = extractInt(content, "\"n\":");
          int k = extractInt(content, "\"k\":");

          List<Point> points = new ArrayList<>();

          // Adjusted regex pattern
          Pattern pattern = Pattern.compile("\"(\\d+)\":\\s*\\{\\s*\"base\":\\s*\"(\\d+)\",\\s*\"value\":\\s*\"([0-9a-zA-Z]+)\"\\s*\\}");
          Matcher matcher = pattern.matcher(content);

          while (matcher.find()) {
              int x = Integer.parseInt(matcher.group(1));
              int base = Integer.parseInt(matcher.group(2));
              int y = decodeValue(base, matcher.group(3));
              System.out.printf("Decoded Point: (%d, %d)%n", x, y); // Debug print
              points.add(new Point(x, y));
          }

          double c = LagrangeInterpolation(points);
          System.out.format("The constant term c is: %.2f%n", c);

      } catch (Exception e) {
          e.printStackTrace();
      }
  }

    // Method to calculate the constant c using Lagrange interpolation
    public static double LagrangeInterpolation(List<Point> coordinate) {
      double c = 0; // Initialize constant term
      int k = coordinate.size(); // Number of points
      System.out.println("Coordinates: " + coordinate);
      for (int i = 0; i < k; i++) {
          double l_i = 1; // Initialize L_i(0) to 1
          for (int j = 0; j < k; j++) {
              if (i != j) {
                  // Calculate L_i(0) using x_j values
                  l_i *= (0 - coordinate.get(j).x) / (coordinate.get(i).x - coordinate.get(j).x);
              }
          }
          // Add the contribution of this point
          double contribution = coordinate.get(i).y * l_i;
          c += contribution;
          System.out.println("i: "+i+", l_i: " + l_i + ", contribution: " + contribution + ", c: " + c);
      }
      return c; // Return the final constant term
  }

    // Method to decode the value of Y from a given base
    private static int decodeValue(int base, String value) {
        return Integer.parseInt(value, base);
    }

    // Method to extract integer values from JSON content
    private static int extractInt(String content, String key) {
        Pattern pattern = Pattern.compile(key + "\\s*([^,}]+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).trim());
        }
        throw new IllegalArgumentException("Key not found: " + key);
    }
}
