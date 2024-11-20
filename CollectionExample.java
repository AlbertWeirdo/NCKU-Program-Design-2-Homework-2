import java.util.*;

public class CollectionExample {
    public static void main(String[] args) {

        List<String> stringList = new ArrayList<>();
        stringList.add("Apple");
        stringList.add("Banana");
        stringList.add("Cherry");

        System.out.println("ArrayList of Strings:");
        for (String fruit : stringList) {
            System.out.println(fruit);
        }

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "One");
        map.put(2, "Two");
        map.put(3, "Three");

        System.out.println("\nHashMap of Integer to String:");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
