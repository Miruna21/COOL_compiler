package cool.tester;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

import cool.compiler.Compiler;

public class Tester3 {
    // java -cp "bin;lib/antlr-4.8-complete.jar;%CLASSPATH%" cool.tester.Tester2
    public static void main(String[] args) throws IOException {
        final String TEST_DIR_NAME = "tests/tema3";
        var testDir = new File(TEST_DIR_NAME);

        var filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {

                return name.endsWith(".cl") && ! name.endsWith("main.cl") && name.contains("21");
//                return name.endsWith(".cl") && ! name.endsWith("main.cl");
            }
        };

        var oldOut = System.out;
        var oldErr = System.err;

        var total = 0;

        var files = testDir.listFiles(filenameFilter);
        Arrays.sort(files);
        for (var file : files) {
            var inPath = file.getPath();
            var outPath = inPath.replace(".cl", ".out");
            var newOut = new PrintStream(outPath, "UTF-8");
            System.setOut(newOut);
            System.setErr(newOut);
            Compiler.main(new String[] { inPath });

            oldOut.printf("%-30s -> ", file.getName());
//            var result = compare(outPath, inPath.replace(".cl", ".ref"), oldOut);
//            if (result)
//                total += 5;

            oldOut.println("-----");
        }

        oldOut.println("Total: " + total);

        System.setOut(oldOut);
        System.setErr(oldErr);
    }

}
