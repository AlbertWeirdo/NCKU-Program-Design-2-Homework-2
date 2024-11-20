import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Object.*;

public class CodeGenerator {
    public static void main(String[] args) {

        // read file
        if (args.length == 0) {
            System.err.println("");
            return;
        }

        String fileName = args[0];
        System.out.println("File name: " + fileName);
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("Unable to read the file " + fileName);
            e.printStackTrace();
            return;
        }

        // System.out.println(mermaidCode);
        MermaidToString mermaidToString = new MermaidToString();
        EliminateSpaceEnding eliminateSpaceEnding = new EliminateSpaceEnding();

        mermaidToString.readLine(mermaidCode);
        mermaidToString.className();
        int lineCount = mermaidToString.getlineCount();
        String[] contentLines = mermaidToString.getcontentLines();
        mermaidToString.setContentLines(eliminateSpaceEnding.eliminateSpaceEnding(contentLines, lineCount));
        String[] className = mermaidToString.getclassNames();
        int classCount = mermaidToString.getclassCount();

        // write in file
        for (int i = 0; i < classCount; i++) {
            // i = 1;
            System.out.println(className[i]);
            // System.out.println(className[i].length());
            // System.out.println(className[i].indexOf('\r'));
            try {
                String output = className[i] + ".java";
                String content = mermaidToString.writeInContent(className[i]);
                File file = new File(output);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(content);
                }
                System.out.println("Java class has been generated: " + output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class MermaidToString {
    private String[] contentLines = new String[1000];
    private int lineCount = 0;
    private String[] className = new String[1000];
    private int classCount = 0;
    StringBuilder stringBuilder = new StringBuilder();

    public void readLine(String mermaidCode) {
        Scanner scan = new Scanner(mermaidCode);
        scan.useDelimiter("\n");

        int loc = 0;
        while ((mermaidCode.indexOf("\n", loc) != -1)) {
            this.lineCount++;
            loc = mermaidCode.indexOf("\n", loc) + 1;
        }
        if (!mermaidCode.substring(mermaidCode.length() - 1, mermaidCode.length()).equals("\n")) {
            this.lineCount++;
        }

        // for (int i = 0; i < mermaidCode.length(); i++) {
        // if ((mermaidCode.indexOf("\n", i) != -1)) {
        // this.lineCount++;
        // i = mermaidCode.indexOf("\n", i) + 1;
        // }
        // }

        System.out.println(this.lineCount);
        for (int i = 0; i < this.lineCount; i++) {
            if ((i == (lineCount - 1))
                    && !(mermaidCode.substring(mermaidCode.length() - 1, mermaidCode.length()).equals("\n"))) {
                this.contentLines[i] = mermaidCode.substring(mermaidCode.lastIndexOf("\n") + 1);
            } else {
                this.contentLines[i] = scan.next();
            }
            System.out.println(this.contentLines[i]);
        }
    }

    public void className() {

        for (int i = 0; i < this.lineCount; i++) {
            if (this.contentLines[i].indexOf("class ") >= 0) {
                int loc = this.contentLines[i].indexOf("class ") + 6;
                // this.className[this.classCount] = this.contentLines[i].substring(loc);
                if (this.contentLines[i].indexOf('\r') >= 0) {
                    int enterLoc = this.contentLines[i].indexOf('\r');
                    this.className[this.classCount] = this.contentLines[i].substring(loc, enterLoc);
                } else {
                    this.className[this.classCount] = this.contentLines[i].substring(loc);
                }
                // System.out.println(this.className[this.classCount]);
                // System.out.println(this.classCount);
                this.classCount++;
            }

        }
    }

    public String writeInContent(String className) {
        String writeInContent = "";
        int startLine = 0;
        int endLine = 0;
        // int plainContentLen = className.length() + 3; // "className : "

        // System.out.println("plainContentLen: " + plainContentLen);
        // System.out.println("lineCount: " + lineCount);
        // System.out.println("classCount: " + classCount);

        for (int i = 0; i < this.lineCount; i++) {
            if (this.contentLines[i].indexOf("class " + className) != (-1)) {
                startLine = i;
                if (className.equals(this.className[this.classCount - 1])) {
                    endLine = this.lineCount;
                    break;
                }
            }
            if ((this.contentLines[i].indexOf("class ") != (-1))
                    && (this.contentLines[i].indexOf("class " + className) == (-1))) {
                endLine = i - 1;
            }
        }
        // System.out.println("startLine: " + startLine);
        // System.out.println("endLine: " + endLine);
        System.out.println(lineCount);

        for (int i = startLine; i <= endLine; i++) {
            System.out.println("contentLines[i]= " + contentLines[i]);
            if (contentLines[i] == null) {
                continue;
            }
            // System.out.println("contentLines[i].length()= " + contentLines[i].length());
            else {
                String sign = "";
                int signLoc = 0;
                if (contentLines[i].indexOf("+") >= 0) {
                    signLoc = contentLines[i].indexOf("+");
                    sign = contentLines[i].substring(signLoc, signLoc + 1);
                } else if (contentLines[i].indexOf("-") >= 0) {
                    signLoc = contentLines[i].indexOf(("-"));
                    sign = contentLines[i].substring(signLoc, signLoc + 1);
                }
                if (contentLines[i].indexOf("class ") >= 0) {
                    writeInContent += "public class " + className + " {\n";
                    // System.out.println("It's a class");
                } else if (contentLines[i].indexOf(className) == -1) {
                    continue;
                } else if (contentLines[i].indexOf("(") == (-1)) { // attribute type
                    // int signLoc = contentLines[i].indexOf(className) + plainContentLen;
                    // String sign = contentLines[i].substring(signLoc, signLoc + 1);

                    // int attributeEnd = contentLines[i].length(); // to delete the space at the
                    // end of the line
                    // for (int j = 0; j < contentLines[i].length() - signLoc; j++) {
                    // if (contentLines[i].substring(contentLines[i].length() - j - 1,
                    // contentLines[i].length())
                    // .equals(" ")) {
                    // attributeEnd -= 1;
                    // } else {
                    // break;
                    // }
                    // }

                    if (sign.equals("+")) { // contentLine[i].indexOf("+")>=0 contentLine[i].indexOf("-")>=0
                        // writeInContent += "\s\s\s\s" + "public " + contentLines[i].substring(signLoc
                        // + 1, attributeEnd)
                        // + ";\n";
                        writeInContent += "\s\s\s\s" + "public " + contentLines[i].substring(signLoc + 1) + ";\n";
                    } else {
                        writeInContent += "\s\s\s\s" + "private " + contentLines[i].substring(signLoc + 1) + ";\n";
                    }
                    // System.out.println(contentLines[i].substring(signLoc, signLoc + 1));
                    // System.out.println("It's a attribute");
                } else if (contentLines[i].indexOf("(") >= 0) { // method
                    // int signLoc = contentLines[i].indexOf(className) + plainContentLen;
                    // String sign = contentLines[i].substring(signLoc, signLoc + 1);
                    String methodName = contentLines[i].substring(signLoc + 1, contentLines[i].indexOf("("));
                    System.out.println("methodName: " + methodName);
                    // System.out.println("sign: " + contentLines[i].substring(signLoc, signLoc+1));
                    // String returnType = contentLines[i].substring(contentLines[i].indexOf(") ") +
                    // 2);
                    String returnType = "";
                    int paranthLoc = contentLines[i].indexOf(")") + 1;
                    if (contentLines[i].indexOf("int", paranthLoc) != (-1)) {
                        returnType = "int";
                    } else if (contentLines[i].indexOf("String", paranthLoc) != (-1)) {
                        returnType = "String";
                    } else if (contentLines[i].indexOf("boolean", paranthLoc) != (-1)) {
                        returnType = "boolean";
                    } else if (contentLines[i].indexOf("void", paranthLoc) != (-1)) {
                        returnType = "void";
                    }

                    System.out.println("returnType: " + returnType);

                    if (methodName.indexOf("set") == 0 // setMethod
                            && (methodName.substring(3, 4).equals(methodName.substring(3, 4).toUpperCase()))) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " "
                                    + contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1) + " {\n";
                            writeInContent += "\s\s\s\s\s\s\s\sthis." + methodName.substring(3).toLowerCase() + " = "
                                    + methodName.substring(3).toLowerCase() + ";\n";
                            writeInContent += "\s\s\s\s}\n";
                        } else { // private
                            writeInContent += "\s\s\s\s" + "private " + returnType + " "
                                    + contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1) + " {\n";
                            writeInContent += "\s\s\s\s}\n";
                        }

                    } else if (methodName.indexOf("get") == 0 // getMethod
                            && (methodName.substring(3, 4).equals(methodName.substring(3, 4).toUpperCase()))) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " "
                                    + contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1) + " {\n";
                            writeInContent += "\s\s\s\s\s\s\s\sreturn " + methodName.substring(3).toLowerCase() + ";\n";
                            writeInContent += "\s\s\s\s}\n";
                        } else { // private
                            writeInContent += "\s\s\s\s\s\s\s\sreturn " + methodName.substring(3).toLowerCase() + ";\n";
                            writeInContent += "\s\s\s\s}\n";
                        }
                    } else if (returnType.equals("int")) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return 0;}\n";
                        } else { // private
                            writeInContent += "\s\s\s\s" + "private " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return 0;}\n";
                        }
                    } else if (returnType.equals("String")) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return \"\";}\n";
                        } else {
                            writeInContent += "\s\s\s\s" + "private " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return \"\";}\n";
                        }
                    } else if (returnType.equals("boolean")) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return false;}\n";
                        } else {
                            writeInContent += "\s\s\s\s" + "private " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {return false;}\n";
                        }
                    } else if (returnType.equals("void")) {
                        if (sign.equals("+")) { // public
                            writeInContent += "\s\s\s\s" + "public " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {;}\n";
                        } else {
                            writeInContent += "\s\s\s\s" + "private " + returnType + " ";
                            writeInContent += contentLines[i].substring(signLoc + 1, contentLines[i].indexOf(")") + 1);
                            writeInContent += " {;}\n";
                        }
                    }

                }
            }

        }
        writeInContent += "}\n";
        return writeInContent;

    }

    public String[] getcontentLines() {
        return contentLines;
    }

    public int getlineCount() {
        return lineCount;
    }

    public String[] getclassNames() {
        return className;
    }

    public int getclassCount() {
        return classCount;
    }

    public void setContentLines(String[] contentLines) {
        this.contentLines = contentLines;
    }

}

class EliminateSpaceEnding {
    public String[] eliminateSpaceEnding(String[] contentLines, int lineCount) {
        for (int i = 0; i < lineCount; i++) {
            int contentLinesLen = contentLines[i].length();
            if (contentLinesLen > 1) {
                while (contentLines[i].substring(contentLinesLen - 1, contentLinesLen).equals(" ")) {
                    contentLines[i] = contentLines[i].substring(0, contentLinesLen - 1);
                    // System.out.println(contentLines[i]);
                    if (contentLinesLen == 1) {
                        contentLines[i] = "";
                        break;
                    }
                    contentLinesLen -= 1;
                }
            }
        }
        return contentLines;
    }
}