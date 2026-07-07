import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Supplier> suppliers = new ArrayList<>();

    public static void main(String[] args) {
        loadSampleData();
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== ProcureAI Copilot — Supplier Search ===");

        while (true) {
            System.out.print("\nSearch (name/category) or 'quit'/'list'/'top': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }
            if (input.equalsIgnoreCase("list")) {
                listAll();
                continue;
            }
            if (input.equalsIgnoreCase("top")) {
                topBySpend();
                continue;
            }
            search(input);
        }
        scanner.close();
    }

    private static void loadSampleData() {
        suppliers.add(new Supplier(1, "Acme Corp", "Technology", 1250000));
        suppliers.add(new Supplier(2, "GlobalParts Inc", "Manufacturing", 890000));
        suppliers.add(new Supplier(3, "DataSync Solutions", "Technology", 2100000));
        suppliers.add(new Supplier(4, "FreshSupply Co", "Food & Beverage", 450000));
        suppliers.add(new Supplier(5, "BuildRite Materials", "Construction", 670000));
        suppliers.add(new Supplier(6, "TechVendor Pro", "Technology", 3200000));
        suppliers.add(new Supplier(7, "MedEquip Direct", "Healthcare", 1800000));
    }

    private static void listAll() {
        printHeader();
        for (Supplier s : suppliers) {
            s.display();
        }
    }

    private static void search(String query) {
        String q = query.toLowerCase();
        printHeader();
        boolean found = false;
        for (Supplier s : suppliers) {
            if (s.getName().toLowerCase().contains(q) ||
                s.getCategory().toLowerCase().contains(q)) {
                s.display();
                found = true;
            }
        }
        if (!found) System.out.println("No suppliers found for: " + query);
    }

    private static void topBySpend() {
        List<Supplier> sorted = new ArrayList<>(suppliers);
        sorted.sort((a, b) -> Double.compare(b.getAnnualSpend(), a.getAnnualSpend()));
        System.out.println("\n--- Top Suppliers by Spend ---");
        printHeader();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            sorted.get(i).display();
        }
    }

    private static void printHeader() {
        System.out.println("ID  | Name                | Category         | Spend");
        System.out.println("----|---------------------|------------------|------------");
    }
}
