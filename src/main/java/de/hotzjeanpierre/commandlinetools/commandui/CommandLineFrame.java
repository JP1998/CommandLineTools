/*
 *     Copyright 2018 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hotzjeanpierre.commandlinetools.commandui;

import de.hotzjeanpierre.commandlinetools.command.ICommandLine;
import de.hotzjeanpierre.commandlinetools.command.ICommandLineApplication;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.commandui.streams.TextComponentInputStream;
import de.hotzjeanpierre.commandlinetools.commandui.streams.TextComponentOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Jonny on 16.04.2018.
 */
public class CommandLineFrame extends JFrame implements ICommandLine {

    private static final String DEFAULT_TITLE = "CommandLineTools v1.0.0-SNAPSHOT";

    private JPanel rootPanel;
    private JTextArea cliTextArea;
    private JScrollPane scrollPane;

    public CommandLineFrame() {
        new Thread(this::init).start();

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        this.initView();


        TextComponentOutputStream newStdOut = new TextComponentOutputStream(cliTextArea);
        TextComponentInputStream newStdIn = new TextComponentInputStream(cliTextArea);

        System.setErr(new PrintStream(newStdOut));
        System.setOut(new PrintStream(newStdOut));
        System.setIn(newStdIn);

        this.pack();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                CommandLineFrame.this.onDispose();
//                dispose();

                associatedApplication.onCLITermination();
                onDispose();
                dispose();
            }
        });

        this.setVisible(true);
        this.setSize(600, 400);

        this.setTitle(DEFAULT_TITLE);

        synchronized (this) {
            this.notify();
        }
    }

    private void initView() {
        rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(Color.BLACK);

        cliTextArea = new JTextArea();
        cliTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        cliTextArea.setForeground(Color.WHITE);
        cliTextArea.setBackground(Color.BLACK);

        cliTextArea.setCaretColor(Color.WHITE);
        cliTextArea.setSelectionColor(Color.GRAY);
        cliTextArea.setSelectedTextColor(Color.LIGHT_GRAY);

        scrollPane = new JScrollPane(cliTextArea);
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel westPanel = new JPanel();
        westPanel.setMinimumSize(new Dimension(0, 0));
        westPanel.setMaximumSize(new Dimension(0, 0));
        westPanel.setBackground(Color.BLACK);
        rootPanel.add(westPanel, BorderLayout.WEST);

        JPanel northPanel = new JPanel();
        northPanel.setMinimumSize(new Dimension(0, 0));
        northPanel.setMaximumSize(new Dimension(0, 0));
        northPanel.setBackground(Color.BLACK);
        rootPanel.add(northPanel, BorderLayout.NORTH);

        JPanel eastPanel = new JPanel();
        eastPanel.setMinimumSize(new Dimension(0, 0));
        eastPanel.setMaximumSize(new Dimension(0, 0));
        eastPanel.setBackground(Color.BLACK);
        rootPanel.add(eastPanel, BorderLayout.EAST);

        JPanel southPanel = new JPanel();
        southPanel.setMinimumSize(new Dimension(0, 0));
        southPanel.setMaximumSize(new Dimension(0, 0));
        southPanel.setBackground(Color.BLACK);
        rootPanel.add(southPanel, BorderLayout.SOUTH);

        this.add(rootPanel);
    }

    private void onDispose() {
        try {
            System.out.close();
            System.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ICommandLineApplication associatedApplication;

    @Override
    public void setupCLI(ICommandLineApplication associatedApplication) {
        this.associatedApplication = associatedApplication;
    }

    @Override
    public void onStartExecution(String cmd) {
        setTitle(StringProcessing.format("Executing \"{0}\"", cmd));
    }

    @Override
    public void onEndExecution() {
        setTitle(DEFAULT_TITLE);
    }

    @Override
    public void clearCLI() {
        this.cliTextArea.setText("");
    }

    @Override
    public void disposeCLI() {
        this.dispose();
    }
}
