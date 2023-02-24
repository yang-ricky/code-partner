package com.plugin.codepartner;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.codehaus.groovy.control.messages.Message;

public class HelloAction extends AnAction {

    @Override
    public void actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent e) {
        Project project = e.getProject();
        String txt = Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Hello, " + txt + "!", "Information", Messages.getInformationIcon());
    }
}
