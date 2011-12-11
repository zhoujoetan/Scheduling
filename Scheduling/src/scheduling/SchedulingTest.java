package scheduling;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;


/**
 * A JUnit test case class.
 * Every method starting with the word "test" will be called when running
 * the test with JUnit.
 */
public class SchedulingTest extends TestCase {
 
  @Test
  public void testConstructor1(){
    ScheduleModel sm = new ScheduleModel();
    assertFalse( sm==null );
  }  
  
  @Test
  public void testSetSize(){
    for(int i = 1; i<33; i++){
      ScheduleModel sm = new ScheduleModel();
      sm.setSize(i);
      assertTrue(sm.getNames().length==i);
      assertTrue(sm.getWeights().length==i);
      assertTrue(sm.getM().length==i);
      assertTrue(sm.getM()[0].length==i);
      assertTrue(sm.getNumProcessors()==0);
    }
  }
  
  @Test
  public void testGetSpecs(){
      ScheduleModel sm = new ScheduleModel();
      sm.getSpecs("E:/Study/CIT592/CIT592_Scheduling/specs1.txt");
      assertTrue(sm.getAL().size()==12);
      assertTrue(sm.getAL().get(5).equals("GC 3 DE"));
      int i=12;
      assertTrue(sm.getNames().length==i);
      assertTrue(sm.getWeights().length==i);
      assertTrue(sm.getM().length==i);
      assertTrue(sm.getM()[0].length==i);
      assertTrue(sm.getNumProcessors()==0);
      String[] st ={ null, "FP", "FW", "BW", "DE", "GC", "CW", "CR", "RP", "LP", "FA", null };
      double[] w = { 0.0, 7.0, 7.0, 7.0, 2.0, 3.0, 2.0, 2.0, 8.0, 8.0, 18.0, 0.0 };
      for (i=1; i<11; i++){
        assertTrue(sm.getNames()[i].equals(st[i]));
        assertTrue(sm.getWeights()[i]==w[i]);
      }
  }
  
  @Test
  public void testMakeM(){
      ScheduleModel sm = new ScheduleModel();
      sm.getSpecs("./specs1.txt");
      double[][] MKey={ 
        { 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, 
        { 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
        { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 } };
      for (int i=0; i<12; i++){
        for (int j=0; j<12; j++){
          double[][] gotM = sm.getM();
          assertTrue(MKey[i][j]==gotM[i][j]);
        }
      }
  }
  
  @Test
  public void testMultiply(){
    double[][] A = {{1,2,3},{4,5,6}};
    double[][] B = {{1,2},{3,4},{5,6}};
    double[][] ABkey = {{22,28},{49,64}};
    double[][] BAkey = {{9,12,15},{19,26,33},{29,40,51}};
    double[][] AB=ScheduleModel.multiply(A,B);
    assertTrue(AB.length==2);
    assertTrue(AB[0].length==2);
    double[][] BA=ScheduleModel.multiply(B,A);
    assertTrue(BA.length==3);
    assertTrue(BA[0].length==3);
    for (int i=0; i<2; i++){
      for (int j=0; j<2; j++){
          assertTrue(ABkey[i][j]==AB[i][j]);
          assertTrue(BAkey[i][j]==BA[i][j]);
      }
    }
    
  }

  @Test
  public void testCopyMatrix(){
    double[][] A = {{1,2,3},{4,5,6}};
    double[][] B = {{1,2},{3,4},{5,6}};
    double[][] Ac = ScheduleModel.copyMatrix(A);
    double[][] Bc = ScheduleModel.copyMatrix(B);
    for (int i=0; i<2; i++){
      for (int j=0; j<3; j++){
          assertTrue(A[i][j]==Ac[i][j]);
      }
    }
    for (int i=0; i<3; i++){
      for (int j=0; j<2; j++){
          assertTrue(B[i][j]==Bc[i][j]);
      }
    }
  }
  
 
  @Test
  public void testRemoveTransitivity(){
    ScheduleModel sm = new ScheduleModel();
    sm.getSpecs("specs1.txt");
    sm.removeTransitivity();
    double[][] Key={ 
      { 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, 
      { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 } };
    double[][] Student=sm.getM();
    for (int i=0; i<12; i++){
        for (int j=0; j<12; j++){
          assertTrue(Key[i][j]==Student[i][j]);
        }
    } 
  }

  @Test
  public void testPrioritize(){
        ScheduleModel sm = new ScheduleModel();
        sm.getSpecs("specs1.txt");
        sm.removeTransitivity();
        double[] priorityInit=sm.getPriority();
        for (int i=0; i<12; i++){
         assertTrue(priorityInit[i]==0); 
        }  
        sm.prioritize(0);
        double[] priorityFinal=sm.getPriority();
        double[] key = { 32.0, 32.0, 25.0, 25.0, 30.0, 28.0, 12.0, 10.0, 8.0, 8.0, 18.0, 0.0 };
        assertTrue(priorityInit.length==12);
        assertTrue(priorityFinal.length==12);
        for (int i=0; i<12; i++){
         assertTrue(priorityFinal[i]==key[i]); 
        }
  }
  
  
  @Test
  public void testScheduleTasks(){
        ScheduleModel sm = new ScheduleModel();
        sm.getSpecs("specs1.txt");
        sm.removeTransitivity();
        sm.prioritize(0);
        sm.scheduleTasks(2);
        assertTrue(sm.getTaskSchedule()[1][11]==32.0);
  }
  
  @Test
  public void testConstructor2(){
    ScheduleModel sm  = new ScheduleModel("specs1.txt",2);
    assertTrue(sm.getTaskSchedule()[1][11]==32.0);
  }
  
  @Test
  public void testSeveralCases(){
    ScheduleModel sm  = new ScheduleModel("specs1.txt",3);
    assertTrue(sm.getTaskSchedule()[1][11]==39.0);
    sm  = new ScheduleModel("specs10.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][18]-41.6)<0.01);    
    sm  = new ScheduleModel("specs5.txt",3); //You need to deal with "more than one space" in your data file
    assertTrue(Math.abs(sm.getTaskSchedule()[1][11]-64)<0.01);    
    sm  = new ScheduleModel("specs5.txt",4);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][11]-64)<0.01);    
    sm  = new ScheduleModel("specs2.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][9]-27)<0.01);    
    sm  = new ScheduleModel("specs2.txt",3);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][9]-27)<0.01);    
    sm  = new ScheduleModel("specs3.txt",3);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-12)<0.01);        
    sm  = new ScheduleModel("specs3.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-18)<0.01);        
    sm  = new ScheduleModel("specs4.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-13)<0.01);    
    sm  = new ScheduleModel("specs4.txt",5);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-10)<0.01);    
    sm  = new ScheduleModel("specs5.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][11]-78)<0.01);    
    sm  = new ScheduleModel("specs5.txt",5);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][11]-64)<0.01);    
    sm  = new ScheduleModel("specs5.txt",10);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][11]-64)<0.01);    
    sm  = new ScheduleModel("specs3b.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-17.4)<0.01);    
    sm  = new ScheduleModel("specs3b.txt",4);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][10]-11.8)<0.01);       
    sm  = new ScheduleModel("specs6.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][13]-30)<0.01);   //T1 is substring of T10 etc
    sm  = new ScheduleModel("specs6.txt",6);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][13]-17)<0.01);      
    sm  = new ScheduleModel("specs7.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][7]-21)<0.01);    
    sm  = new ScheduleModel("specs7.txt",3);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][7]-14)<0.01);      
    sm  = new ScheduleModel("specs8.txt",1);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][12]-89.6)<0.01);    
    sm  = new ScheduleModel("specs8.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][12]-46.2)<0.01);       
    sm  = new ScheduleModel("specs9.txt",2);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][9]-34.5)<0.01);    
    sm  = new ScheduleModel("specs9.txt",4);
    assertTrue(Math.abs(sm.getTaskSchedule()[1][9]-31.2)<0.01);    
  }
  

}//end of file
