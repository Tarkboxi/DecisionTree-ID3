package mainPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import dataSorter.DataStore;
import dataSorter.InputDataHandler;
import treeCalculator.TreeBuilder;
import treeCalculator.TreeOperations;


class ID3 {
    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception 
    {
    	 String filePath = ""; 
    	 String fileType = "";
         System.out.println("ID 3 ALGORITHM : Enter your choice of dataset to use\n1 Mushroom\n2 Car\n3 Ecoli\n4 Letter-Recognition\n5 Breast cancer\n\nEnter a number from 1-5");
         Scanner inputScanner = new Scanner(System.in);
         String choice = inputScanner.nextLine();
         switch (choice)
         {
         case "1":
              filePath = "src\\dataFiles\\mushroom.data";
              fileType = "mushroom";
        	break;
         case "2":
        	  filePath = "src\\dataFiles\\car.data";
              fileType = "car";
        	 break;
         case "3":
        	 filePath = "src\\dataFiles\\ecoli.data";
             fileType = "ecoli";
        	 break;
         case "4":
        	 filePath = "src\\dataFiles\\letter-recognition.data";
             fileType = "letter";
        	 break;
         case "5":
        	 filePath = "src\\dataFiles\\breast-cancer-wisconsin.data";
             fileType = "bcancer";
        	 break;
         default:
        	 filePath = "";
        	 System.out.println("wrong input entered, too lazy to loop back to the selection. Run again please");
        	 System.exit(0);
        	 break;		
         }
         
         inputScanner.close();       
         InputDataHandler inputHandler = new InputDataHandler();   	 
         inputHandler.createMapOfAttributes(filePath, fileType); 	
         System.out.println("created test data and training data, calculating please wait :");
         int crossValidationCounter = 1;
         for (int i = 9 ; i >= 0 ; i--)
         {
        	 inputHandler.CrossValidationDataGenerator(i);
        	 inputHandler.createInputFormat();
         
        	 TreeOperations treeOperation = new TreeOperations();
        	 treeOperation.findTargetEntropy();
        	 Object parentNode = treeOperation.findBestClassifier();
        	 TreeBuilder tBuilder = new TreeBuilder();
   
        	 tBuilder.callBuildTree(parentNode);	    
        	 treeOperation.findResultOfTargets();
        	 System.out.println("done "+crossValidationCounter+" time 5 cross Validation");
        	 crossValidationCounter = crossValidationCounter+1;
         }
         DataStore dataStore = DataStore.get();
         ArrayList<Float> listOfAccuracies =  dataStore.getListOfAccuracies();
         System.out.println("\n List of success rates recieved : "+listOfAccuracies);
         float sum = 0f;
         for (float singlePercentage : listOfAccuracies)
         {
        	 sum = sum+singlePercentage;
         }
         int count = listOfAccuracies.size();
         float mean = (float) (sum/count) ;       
         float sumOfDifferences = 0f;
         for (float singlePercentage : listOfAccuracies)
         {
        	float difference =  (singlePercentage - mean) ;
        	float differenceSquared = (difference * difference) ;
        	sumOfDifferences = sumOfDifferences+differenceSquared ;
         }
         float variance = sumOfDifferences / count ;
         float stdDeviation = (float) Math.sqrt(variance);
         System.out.println("std. Deviation ::"+stdDeviation);
         System.out.println("average percentage of accuracy ::"+mean);
    }
    
    
 }


