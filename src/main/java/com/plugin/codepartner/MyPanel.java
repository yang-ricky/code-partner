package com.plugin.codepartner;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class MyPanel extends JPanel {
    public MyPanel() {
        // set the layout manager
        setLayout(new BorderLayout(800, 500));

        MarkDownPanel markDownPanel = new MarkDownPanel();

        add(markDownPanel.getMarkDownPanel());

        // set the size of the panel
        setPreferredSize(new Dimension(800, 500));
    }
}
