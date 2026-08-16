package aston.task1;

public class Core {
    public static void execute() {
        var map = new HashMap<String, Integer>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("one", 10);

        System.out.println(map.get("one"));     // 10
        System.out.println(map.remove("two"));  // 2
        System.out.println(map.get("two"));     // null
        System.out.println(map.size());         // 1

        map.put(null, 100);
        System.out.println(map.get(null));      // 100
    }
}
