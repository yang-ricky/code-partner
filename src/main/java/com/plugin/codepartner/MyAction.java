package com.plugin.codepartner;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyAction extends AnAction {

    private JTree tree;

    private static final Logger LOG = Logger.getInstance(MyAction.class);

    public void actionPerformed(AnActionEvent e) {
        // 获取当前的项目对象
        Project project = e.getProject();


        // 创建一个面板
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 创建一个新的树组件
        tree = new JTree();

        // 添加树节点选择监听器
        MyTreeSelectionListener listener = new MyTreeSelectionListener(project);
        tree.addTreeSelectionListener(listener);

        tree.setPreferredSize(new Dimension(300, 150));

        // 创建一个根节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        root.setAllowsChildren(true); // 允许根节点有子节点
        for (int i = 1; i <= 2; i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Item " + i);
            root.add(node);
        }

        // 将根节点添加到树中
        tree.setModel(new DefaultTreeModel(root));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // 开启拖拽支持
        tree.setDragEnabled(true);
        tree.setDropMode(javax.swing.DropMode.ON_OR_INSERT);

        // 为树添加拖放监听器
        tree.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return DnDConstants.ACTION_COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                TreePath path = tree.getSelectionPath();
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if (node instanceof DefaultMutableTreeNode) {
                        return new MyTransferable((DefaultMutableTreeNode)node);
                    }
                }
                return null;
            }
        });

        tree.setDropTarget(new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                // 当用户在树上释放拖动节点时调用此方法
                try {
                    Transferable transferable = dtde.getTransferable();
                    if (transferable.isDataFlavorSupported(MyTransferable.DATA_FLAVOR)) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable.getTransferData(MyTransferable.DATA_FLAVOR);
                        TreePath path = tree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (parent.getAllowsChildren()) {
                            int index = parent.getChildCount();
                            DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.getUserObject());
                            node.removeFromParent(); // 将节点从原来的位置删除
                            copy.setAllowsChildren(node.getAllowsChildren()); // 拷贝子节点属性
                            parent.insert(copy, index);
                            ((DefaultTreeModel) tree.getModel()).reload(parent);
                            dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        }
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        // 创建添加按钮
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e1 -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                node = root;
            }
            int index = node.getChildCount() + 1;
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Item " + index);
            ((DefaultTreeModel)tree.getModel()).insertNodeInto(newNode, node, node.getChildCount());
        });

        // 创建删除按钮
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e1 -> {
            Object node = tree.getLastSelectedPathComponent();
            if (node != null && node != root) {
                ((DefaultMutableTreeNode)node).removeFromParent();
                ((DefaultTreeModel)tree.getModel()).reload(root);
            }
        });

        // 将按钮添加到面板中
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel);

        // 将树组件添加到面板中
        panel.add(new JScrollPane(tree));

        // 显示面板
        new DialogBuilder().centerPanel(panel).show();
    }

    class MyTreeSelectionListener implements TreeSelectionListener {

        private final Project project;

        public MyTreeSelectionListener(Project project) {
            this.project = project;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                // 获取要打开的文件
                File file = new File("/Users/yang/Code/b2c/el-b2b-onboarding/src/main/java/com/eflabs/b2b/hk2/AppBinder1.java");

                // 如果文件存在
                if (file.exists()) {
                    // 获取文件对应的 VirtualFile
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                    if (virtualFile != null) {
                        // 打开文件并跳转到指定行
                        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile, 10), true);
                    }
                }
            }
        }
    }



    // 实现Transferable接口，表示可以拖拽的数据
    public static class MyTransferable implements Transferable {
        public static final DataFlavor DATA_FLAVOR = new DataFlavor(DefaultMutableTreeNode.class, "JTree Node");
        private List<DataFlavor> flavors = new ArrayList<>();
        private DefaultMutableTreeNode node;

        public MyTransferable(DefaultMutableTreeNode node) {
            this.node = node;
            flavors.add(DATA_FLAVOR);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors.toArray(new DataFlavor[0]);
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavors.contains(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DATA_FLAVOR)) {
                return node;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }
}