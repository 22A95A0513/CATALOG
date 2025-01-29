import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            // Read and parse the JSON file
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("input.json"));
            
            // Extract the test cases
            JSONArray testCases = (JSONArray) jsonObject.get("testCases");

            // Process each test case
            for (Object obj : testCases) {
                JSONObject testCase = (JSONObject) obj;
                
                // Extract the keys
                JSONObject keys = (JSONObject) testCase.get("keys");
                int n = Integer.parseInt(keys.get("n").toString());
                int k = Integer.parseInt(keys.get("k").toString());
                
                // Store the decoded roots
                Map<Integer, BigDecimal> roots = new HashMap<>();
                
                for (int i = 1; i <= n; i++) {
                    if (testCase.containsKey(String.valueOf(i))) {
                        JSONObject root = (JSONObject) testCase.get(String.valueOf(i));
                        int x = i;
                        int base = Integer.parseInt(root.get("base").toString());
                        String value = root.get("value").toString();
                        BigDecimal y = new BigDecimal(new java.math.BigInteger(value, base));
                        roots.put(x, y);
                        System.out.println("Decoded value for x = " + x + " is y = " + y);
                    }
                }

                // Find the constant term 'c' using Lagrange interpolation
                BigDecimal c = findConstantTerm(roots);
                System.out.println("\nSecret: " + c.setScale(0, RoundingMode.HALF_UP));
                System.out.println("--------------------------------------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigDecimal findConstantTerm(Map<Integer, BigDecimal> roots) {
        BigDecimal constantTerm = BigDecimal.ZERO;

        for (Map.Entry<Integer, BigDecimal> entry1 : roots.entrySet()) {
            int xi = entry1.getKey();
            BigDecimal yi = entry1.getValue();
            BigDecimal li = BigDecimal.ONE;

            for (Map.Entry<Integer, BigDecimal> entry2 : roots.entrySet()) {
                int xj = entry2.getKey();
                if (xi != xj) {
                    BigDecimal numerator = BigDecimal.valueOf(-xj);
                    BigDecimal denominator = BigDecimal.valueOf(xi - xj);
                    li = li.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP));
                }
            }

            constantTerm = constantTerm.add(yi.multiply(li));
        }

        return constantTerm;
    }
}
