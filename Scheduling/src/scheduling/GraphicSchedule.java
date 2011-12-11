package scheduling;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* FrameDemo.java requires no other files. */
public class GraphicSchedule extends JPanel {
    private static ScheduleModel sm;
    private static GraphicSchedule gs;
    private static double completionTime;
    
    private void createAndShowGUI(int numProcessors) {
        JFrame  application  =  new  JFrame();
        final int INDENT = 15;
        application.setSize(INDENT * (2 + (int) completionTime), 150 + 60 * (1 + numProcessors));
        application.setVisible(true);
        application.setResizable(false);
        application.add(new GraphicSchedule());
    }
    
    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        final int INDENT = 15;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawString("Completion Time " + completionTime, INDENT, 35);
        //draw scales
        for (int i = 0; i <= (int) completionTime; i++)
        {
            g.drawLine(INDENT * (i + 1), height - 55, INDENT * (i + 1), height - 40);
            if (i % 5 == 0)
            {
                g.drawString("" + i, INDENT * (i + 1) - 3 - 2 * (i / 10), height - 25);
            }
        }
        //draw rectangles and label
        double[][] taskSchedule = sm.getTaskSchedule();
        int x, y, duration;
        for (int i = 1; i < taskSchedule[0].length - 1; i++) {
            //start time
            x = (int) (INDENT * (1 + taskSchedule[0][i]));
            y = (int) (20 + 60 * (1 + taskSchedule[2][i]));
            duration = (int) (INDENT * (taskSchedule[1][i] - taskSchedule[0][i]));
            //fill rectangular area
            g.setColor(Color.GRAY);
            g.fillRect(x, y, duration, 50);
            //draw border
            g.setColor(Color.BLACK);
            g.drawRect(x - 1, y - 1, duration + 1, 51);
            //label
            g.drawString("T" + i, x + 3, y + 28);
        }
    }
    

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        sm = new ScheduleModel(args[0], Integer.parseInt(args[1]));
        completionTime = sm.getTaskSchedule()[1][sm.getNames().length - 1];
        new GraphicSchedule().createAndShowGUI(Integer.parseInt(args[1]));
        }
}

