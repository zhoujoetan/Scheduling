package scheduling;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ScheduleModel {

    /**
     * @param args
     */
    private final int START = 0;
    private  int  numProcessors  =  0;
    private  String[]  names;
    private  double[]  weights;
    private  double[][]  M;
    private  double[]  priority;
    private  double[][]  taskSchedule;
    private  ArrayList<String>  AL  =  new  ArrayList<String>();
    private  ArrayList<Double>  processorEndTime;
    public static void main(String[] args) {

    }

    public  void  setSize(int  n){
        names  =  new  String[n];
        M  =  new  double[n][n];
        weights  =  new  double[n];
        priority  =  new  double[n];
        taskSchedule  =  new  double[3][n];
    }
    
    public double[] getPriority() {
        return priority;
    }
    
    public int getNumProcessors() {
        return numProcessors;
    }
    
    public String[] getNames() {
        return names;
    }
    
    public double[] getWeights() {
        return weights;
    }
    
    public double[][] getM() {
        return M;
    }
    
    public ArrayList<String> getAL() {
        return AL;
    }

    public double[][] getTaskSchedule() {
        return taskSchedule;
    }
    
    public void getSpecs(String arg) {
        try {

            /*  Sets up a file reader to read the file passed on the command
                line one character at a time */
            FileReader input = new FileReader(arg);

            /* Filter FileReader through a Buffered read to read a line at a
               time */
            BufferedReader bufRead = new BufferedReader(input);

            // Read first line
            String line;
            line = bufRead.readLine();
            AL.add("start"); //add the first placeholder

            // Read through file one line at time. Print line # and line
            while (line != null){
                AL.add(line);
                line = bufRead.readLine();
            }

            AL.add("end"); //add the last placeholder
            bufRead.close();

        }catch (ArrayIndexOutOfBoundsException e){
            /* If no file was passed on the command line, this expception is
            generated. A message indicating how to the class should be
            called is displayed */
            System.out.println("Usage: java ReadFile filename\n");          

        }catch (IOException e){
            // If another exception is generated, print a stack trace
            e.printStackTrace();
        }

        int n = AL.size();
        this.setSize(n);
        //process the data in ArrayList
        String[] nameArray;
        for (int i = 0; i < n; i++) {
            nameArray = AL.get(i).split("[ ]+");
            int length = nameArray.length;
            //put the first parameter into name[]
            names[i] = nameArray[0];
            //put the second parameter into weight[]
            if (length >= 2) {
                weights[i] = Double.parseDouble(nameArray[1]);
            }
            if (length == 2) {
                //set prerequisite to start if there is no other ones
                M[0][i] = 1;
            }
        }
            
        for (int i = 0; i < n; i++) {
            nameArray = AL.get(i).split("[ ]+");            
            //mark on the prerequisite matrix 
            for (int j = 2; j < nameArray.length; j++) {
                String name = nameArray[j];
                for (int k = 0; k < names.length; k++)
                {
                    if (name.equals(names[k])) {
                        //if task k is found in prerequisite list, then k is i's prerequisite
                        M[k][i] = 1;
                        break;
                    }
                }
            }
        }
        //set every task the prerequisite of the end task
        for (int i = 1; i < n - 1; i++)
        {
            M[i][n - 1] = 1;
        }
    }

    public static double[][] multiply(double[][] A, double[][] B) {
        //the length of the first matrix must equal to height of the second matrix
        if (A[0].length != B.length)
        {
            throw new RuntimeException();
        }
        double[][] retMatrix = new double[A.length][B[0].length];
        for (int i = 0; i < retMatrix.length; i++) {
            for (int j = 0; j < retMatrix[0].length; j++) {
                for (int k = 0; k < A[0].length; k++) {
                    retMatrix[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return retMatrix;
    }

    public static double[][] copyMatrix(double[][] A) {
        double[][] retMatrix = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                retMatrix[i][j] = A[i][j];
            }
        }
        return retMatrix;
    }

    public static ArrayList<Double> copyArrayListDouble(ArrayList<Double> v) {
        ArrayList<Double> retArrayList = new ArrayList<Double>();
        for (Double o : v)
        {
            retArrayList.add(o);
        }
        return retArrayList;
    }

    public void removeTransitivity() {
        double[][] MCopy = ScheduleModel.copyMatrix(M);
        double[][] MMul = ScheduleModel.copyMatrix(M);
        double[][] MIdentity = ScheduleModel.copyMatrix(M);
        
        //B = I + A + A2 + A3 + … +Ak, remove transitivity
        for (int k = 0; k < MCopy.length - 1; k++) {
            MCopy = ScheduleModel.multiply(MCopy, MIdentity); 
            for (int i = 0; i < MCopy.length; i++) {
                for (int j = 0; j < MCopy[0].length; j++) {
                    MMul[i][j] += MCopy[i][j];
                }
            }
        }
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                //remove redundancy for those indirect connections which are connected directly
                if (MMul[i][j] > 1 && M[i][j] != 0) {
                    M[i][j] = 0;
                }
                
            }
        }

    }

    public void prioritize (int a) {
        boolean isDependentExist = false;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < priority.length - 1; i++) {
            if (M[a][i] == 1) {
                prioritize(i);
                //recursively call itself, finding out the maximum weight path
                if (max < priority[i])
                {
                    max = priority[i];
                }
                isDependentExist = true;
            }
        }
        //if a task has no following tasks, directly return its weight
        if (!isDependentExist) {
            max = 0;
        }
        priority[a] = weights[a] + max;
        max = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < priority.length - 1; i++) {
            if (max < priority[i]) {
                max = priority[i];
            }
        }
        priority[START] = max;
        return;
    }

    public boolean[] processesAvailable(double t) {
        boolean[] available = new boolean[weights.length];
        available[START] = false;
        available[weights.length - 1] = false;
        for (int i = 1; i < weights.length; i++) {
            //the task must not have started
            if (taskSchedule[1][i] < 0.01) {
                //check if its prerequisite is done
                available[i] = true;
                for (int j = 1; j < weights.length; j++) {
                    if (Math.abs(M[j][i] - 1) < 0.01) {
                        //if the prerequisite is not finished or has not started
                        if (taskSchedule[1][j] > t || taskSchedule[1][j] < 0.01) {
                            available[i] = false;
                            break;
                        }
                        
                    }
                }
            }
            else {
                available[i] = false;
            }
        }
        return available;
    }

    public void scheduleTasks(int numOfProcessors) {
        double currentTime = Double.POSITIVE_INFINITY;
        boolean waiting = false;
        processorEndTime = new ArrayList<Double>();
        int currentProcessor = 0;
        //initialize the processor end time array
        for (int i = 0; i < numOfProcessors; i++) {
            processorEndTime.add(new Double(0));
        }
        //iterate until last task has not started
        while (taskSchedule[1][names.length - 1] < 0.01) {
            for (int i = 0; i < processorEndTime.size(); i++) {
                double getTime = processorEndTime.get(i).doubleValue();
                //get the processor which performs next task
                if (waiting) {
                    //if waiting, the getTime should be larger than its current time
                    if (currentTime > getTime && processorEndTime.get(currentProcessor).doubleValue() < getTime) {
                        currentTime = getTime;
                    }
                }
                else if (currentTime > getTime)
                {
                    currentTime = getTime;
                    currentProcessor = i;
                }
                                
            }
                        
            boolean[] isTaskFree = processesAvailable(currentTime);
            int taskNo = 0;
            boolean freeTaskExists = false;
            double maxPriority = Double.NEGATIVE_INFINITY;
            //try to find out the task with highest priority that can be performed 
            for (int i = 1; i < names.length; i++) {
                if (isTaskFree[i] == true && maxPriority < priority[i]) {
                    freeTaskExists = true;
                    taskNo = i;
                    maxPriority = priority[i];
                }
            }
            if (freeTaskExists)
            {
                //set start and end time for current task
                taskSchedule[0][taskNo] = currentTime;
                taskSchedule[1][taskNo] = currentTime + weights[taskNo];
                taskSchedule[2][taskNo] = currentProcessor;
                //change the finished task and add new time to wait
                processorEndTime.set(currentProcessor, new Double(taskSchedule[1][taskNo]));
                waiting = false;
            }
            else {
                waiting = true;
                if (processorEndTime.get(currentProcessor).doubleValue() < currentTime) {
                    processorEndTime.set(currentProcessor, new Double(currentTime));
                }
            }
            currentTime = Double.POSITIVE_INFINITY;
        }

    }
    
    public ScheduleModel(String arg, int numProcessors) {
        getSpecs(arg);
        removeTransitivity();
        prioritize(0);
        scheduleTasks(numProcessors);
        printTaskScheduling();
    }
    
    private void printTaskScheduling() {
        for (int i = 1; i < taskSchedule[0].length - 1; i++)
        {
            System.out.println("Task " + (i + 1) + " performed by processor " + taskSchedule[2][i] +
                               "starting from " + taskSchedule[0][i] + "ending in " + taskSchedule[1][i]);
        }
    }
    
    public ScheduleModel() {}
    

}

