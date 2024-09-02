package NP_LAB8.ex2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;




public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase==1) {
            System.out.println(component.print(""));
        } else if(testCase==2) {
            System.out.println(composite.print(""));
        } else if (testCase==3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level","1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level","2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level","3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            System.out.println(main.print(""));
        }
    }
}

interface XMLComponent {
    void addAttribute(String attribute, String value);
    String print(String indent);
}

abstract class XMLElement implements XMLComponent {
    Map<String, String> attributes;
    String tag;
    public XMLElement(String tag) {
        this.tag = tag;
        attributes = new LinkedHashMap<>();
    }

    public String displayAttributes() {
        return attributes.entrySet()
                .stream()
                .map(i -> String.format("%s=\"%s\"", i.getKey(), i.getValue()))
                .collect(Collectors.joining(" "));
    }

    @Override
    public void addAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }
}

class XMLLeaf extends XMLElement{
    String innerHTML;

    public XMLLeaf(String tag, String innerHTML) {
        super(tag);
        this.innerHTML = innerHTML;
    }

    @Override
    public String print(String indent) {
        return String.format("%s<%s%s>%s</%s>\n",
                indent,
                tag,
                displayAttributes().isEmpty() ? "" : " " + displayAttributes(),
                innerHTML,
                tag);
    }
}

class XMLComposite extends XMLElement {
    List<XMLComponent> components;
    public XMLComposite(String tag) {
        super(tag);
        this.components = new ArrayList<>();
    }

    public void addComponent(XMLComponent component) {
        components.add(component);
    }

    @Override
    public String print(String indent) {
        return String.format("%s<%s %s>\n%s%s</%s>\n",
                indent,
                tag,
                displayAttributes(),
                components.stream().map(i -> i.print(indent + "\t")).collect(Collectors.joining("")),
                indent,
                tag);
    }
}
