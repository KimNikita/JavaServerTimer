/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalab.timer;

/**
 *
 * @author Никита
 */
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class EventDialog extends JDialog implements ActionListener {

    public boolean ok = false;
    public int hour = -1;
    public int minute = -1;
    public String desc = "";
    private JButton btnOk;
    private JButton btnCancel;
    private JSpinner hourSpin;
    private JSpinner minuteSpin;
    private JTextField descBox;

    public EventDialog(Frame parent) {
        super(parent, "Type Event", true);
        Point loc = parent.getLocation();
        setLocation(loc.x + 80, loc.y + 80);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel hourLabel = new JLabel("Type hour");
        JLabel minuteLabel = new JLabel("Type minute");
        JLabel descLabel = new JLabel("Type description");

        SpinnerModel hourModel = new SpinnerNumberModel(20, 0, 23, 1);
        hourSpin = new JSpinner(hourModel);
        SpinnerModel minuteModel = new SpinnerNumberModel(30, 0, 59, 1);
        minuteSpin = new JSpinner(minuteModel);
        descBox = new JTextField(30);
        JLabel spacer1 = new JLabel(" ");
        JLabel spacer2 = new JLabel(" ");

        btnOk = new JButton("Ok");
        btnOk.addActionListener(this);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);

        panel.add(hourLabel);
        panel.add(minuteLabel);
        panel.add(hourSpin);
        panel.add(minuteSpin);
        panel.add(descLabel);
        panel.add(spacer1);
        panel.add(descBox);
        panel.add(spacer2);
        panel.add(btnOk);
        panel.add(btnCancel);
        getContentPane().add(panel);
        pack();
        this.setSize(300, 200);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == btnOk) {
            hour = (Integer) hourSpin.getValue();
            if (hour > 23 || hour < 0) {
                hour = 0;
            }
            minute = (Integer) minuteSpin.getValue();
            if (minute > 59 || minute < 0) {
                minute = 0;
            }
            desc = descBox.getText();
            ok = true;
        }
        dispose();
    }
}
