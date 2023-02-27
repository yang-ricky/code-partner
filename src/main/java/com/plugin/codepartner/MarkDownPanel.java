package com.plugin.codepartner;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MarkDownPanel {
    public JEditorPane getMarkDownPanel() {
        String javaCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        editorPane.setPreferredSize(new Dimension(300, 200));

        try {
            String html = toHtml(javaCode);
            editorPane.setText(html);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return editorPane;
    }

    private String toHtml(String javaCode) throws IOException {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(javaCode);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String body = renderer.render(document);
        String html = String.format("<html><head>%s</head><body>%s</body></html>", getStyleTags(), body);
        return html;
    }

    private String getStyleTags() {
        String style = "<style>%s</style>";

        // Add styles for CODE_SM
        String codeSm = ".CODE_SM { font-family: monospace; font-size: 12px; background-color: #f4f4f4; padding: 5px 10px; border: 1px solid #e1e1e1; }";

        return String.format(style, codeSm);
    }
}
