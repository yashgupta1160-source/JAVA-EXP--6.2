import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Main {

    // Part C Product class kept inside main file to satisfy 3-file constraint
    public static class Product {
        private final String name;
        private final double price;
        private final String category;

        public Product(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }

        @Override
        public String toString() {
            return String.format("Product{name='%s', price=%.2f, category='%s'}", name, price, category);
        }
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Part A - Sort Employees (Lambda List.sort)");
            System.out.println("2. Part B - Filter & Sort Students (Streams)");
            System.out.println("3. Part C - Product Stream Operations (group, max, average)");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> partA();
                case "2" -> partB();
                case "3" -> partC();
                case "0" -> { System.out.println("Exiting... Goodbye!"); return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ================= Part A =================
    private static void partA() {
        System.out.println("\n--- Part A: Employee Sorting Using Lambda Expressions ---");
        List<Employee> employees = readEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees entered.");
            return;
        }
        while (true) {
            System.out.println("\nSort by: 1) Name 2) Age 3) Salary Asc 4) Salary Desc 0) Back");
            System.out.print("Choice: ");
            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1" -> {
                    employees.sort(Comparator.comparing(Employee::getName));
                    printList("Sorted by Name", employees);
                }
                case "2" -> {
                    employees.sort(Comparator.comparingInt(Employee::getAge));
                    printList("Sorted by Age", employees);
                }
                case "3" -> {
                    employees.sort(Comparator.comparingDouble(Employee::getSalary));
                    printList("Sorted by Salary Asc", employees);
                }
                case "4" -> {
                    employees.sort(Comparator.comparingDouble(Employee::getSalary).reversed());
                    printList("Sorted by Salary Desc", employees);
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static List<Employee> readEmployees() {
        System.out.print("Enter number of employees: ");
        int n = readPositiveInt();
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Employee %d name: ", i + 1);
            String name = scanner.nextLine().trim();
            System.out.print("Age: ");
            int age = readPositiveInt();
            System.out.print("Salary: ");
            double salary = readPositiveDouble();
            list.add(new Employee(name, age, salary));
        }
        return list;
    }

    // ================= Part B =================
    private static void partB() {
        System.out.println("\n--- Part B: Filter & Sort Students Using Streams ---");
        System.out.print("Enter number of students: ");
        int n = readPositiveInt();
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Student %d name: ", i + 1);
            String name = scanner.nextLine().trim();
            System.out.print("Marks (%): ");
            double marks = readNonNegativeDouble();
            students.add(new Student(name, marks));
        }

        System.out.println("Students scoring > 75%, sorted by marks descending:");
        List<String> names = students.stream()
                .filter(s -> s.getMarks() > 75)
                .sorted(Comparator.comparingDouble(Student::getMarks).reversed())
                .map(Student::getName)
                .toList();
        if (names.isEmpty()) {
            System.out.println("(None)");
        } else {
            names.forEach(System.out::println);
        }
    }

    // ================= Part C =================
    private static void partC() {
        System.out.println("\n--- Part C: Product Stream Operations ---");
        System.out.print("Enter number of products: ");
        int n = readPositiveInt();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Product %d name: ", i + 1);
            String name = scanner.nextLine().trim();
            System.out.print("Price: ");
            double price = readPositiveDouble();
            System.out.print("Category: ");
            String category = scanner.nextLine().trim();
            products.add(new Product(name, price, category));
        }
        if (products.isEmpty()) { System.out.println("No products entered."); return; }

        // Group by category
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        System.out.println("\nProducts Grouped By Category:");
        byCategory.forEach((cat, list) -> {
            System.out.println(cat + ": " + list.stream().map(Product::getName).collect(Collectors.joining(", ")));
        });

        // Most expensive product per category
        Map<String, Optional<Product>> maxPerCat = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.maxBy(Comparator.comparingDouble(Product::getPrice))));
        System.out.println("\nMost Expensive Product In Each Category:");
        maxPerCat.forEach((cat, opt) -> System.out.println(cat + " -> " + opt.map(p -> p.getName() + " (" + p.getPrice() + ")").orElse("-")));

        // Average price overall
        double avg = products.stream().collect(Collectors.averagingDouble(Product::getPrice));
        System.out.printf("\nAverage Price of All Products: %.2f\n", avg);
    }

    // ================= Utility Input Methods =================
    private static int readPositiveInt() { return readNumber(() -> Integer.parseInt(scanner.nextLine().trim()), x -> x > 0, "Enter a positive integer."); }
    private static double readPositiveDouble() { return readNumber(() -> Double.parseDouble(scanner.nextLine().trim()), x -> x > 0, "Enter a positive number."); }
    private static double readNonNegativeDouble() { return readNumber(() -> Double.parseDouble(scanner.nextLine().trim()), x -> x >= 0, "Enter a non-negative number."); }

    private static <T extends Number> T readNumber(Supplier<T> supplier, java.util.function.Predicate<T> valid, String errorMsg) {
        while (true) {
            try {
                T value = supplier.get();
                if (valid.test(value)) return value;
            } catch (NumberFormatException ignored) { }
            System.out.print(errorMsg + " Try again: ");
        }
    }

    private static <T> void printList(String title, List<T> list) {
        System.out.println("\n" + title + ":");
        list.forEach(System.out::println);
    }
}
