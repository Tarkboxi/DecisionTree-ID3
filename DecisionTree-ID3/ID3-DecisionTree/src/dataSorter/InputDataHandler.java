package dataSorter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InputDataHandler {
	
	
	
	public void CrossValidationDataGenerator (int n)
	{
		int testDataColumnNumber = n;
	//	System.out.println(testDataColumnNumber);
    	DataStore dataStore = DataStore.get();
    	ArrayList<ArrayList<ArrayList<String>>> splitByFiveListOfData = new ArrayList<ArrayList<ArrayList<String>>>();
    	splitByFiveListOfData = dataStore.getTotalDataSplitIn5();
    	ArrayList<ArrayList<String>> listOfTestData  = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> listOfTrainingData  = new ArrayList<ArrayList<String>>();
    	if (n > 4)
		{
    		if ( n == 7 || n == 8 )
    		{
    			testDataColumnNumber = testDataColumnNumber/2 -1 ;
    		}
    		else if ( n == 5 || n == 6)
    		{
    			testDataColumnNumber = (testDataColumnNumber/2) - 2 ; 
    		}
    		else if (n == 9)
    		{
    			testDataColumnNumber = testDataColumnNumber/2;
    		}
    		Collections.shuffle(splitByFiveListOfData);
		}
		else
		{	
			testDataColumnNumber = n;
		}
    	for (ArrayList<ArrayList<String>> oneOfTheFiveDataInner : splitByFiveListOfData)
    	{
    //		System.out.println("The sizes ::"+oneOfTheFiveDataInner.size()+"  last elemt :: "+oneOfTheFiveDataInner.get(oneOfTheFiveDataInner.size()-1));
    		if(oneOfTheFiveDataInner != splitByFiveListOfData.get(testDataColumnNumber) )
    		{
    		//	System.out.println("adding");
    			listOfTrainingData.addAll(oneOfTheFiveDataInner);
    		}
    			if (oneOfTheFiveDataInner == splitByFiveListOfData.get(testDataColumnNumber))
    			{
    				listOfTestData.addAll(oneOfTheFiveDataInner); 				
    			}
    	}
    //	System.out.println("Total size of training data ::"+listOfTrainingData.size());
    //	System.out.println("Total size of test data ::"+listOfTestData.size());
    	dataStore.setListOfTestData(listOfTestData);
    	dataStore.setListOfTrainingData(listOfTrainingData);
	}
	
	public void createMapOfAttributes (String filePath, String fileType)
	{
		try 
		{
			File dataSetFile = new File(filePath);	
			FileInputStream fileReaderStream = new FileInputStream(dataSetFile);			 
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileReaderStream));
		 	ArrayList<ArrayList<String>> listOfAllLinesOfData  = new ArrayList<ArrayList<String>>();
	    	ArrayList<ArrayList<ArrayList<String>>> splitByFiveListOfData = new ArrayList<ArrayList<ArrayList<String>>>();
	    	ArrayList<ArrayList<String>> oneOfTheFiveData = new ArrayList<ArrayList<String>>();
	    
	    	String lineReadData = "";
	    	while((lineReadData=fileReader.readLine())!=null)
	    	{
	    		ArrayList<String> singleLineList = new ArrayList<String>();
	    		if (fileType.equalsIgnoreCase("ecoli"))
	    		{
		    		singleLineList = new ArrayList<String> (Arrays.asList(lineReadData.split("\\s+")) );
	    			singleLineList.remove(0);

	    		}
	    		else if (fileType.equalsIgnoreCase("bcancer"))
	    		{
		    		singleLineList = new ArrayList<String> (Arrays.asList(lineReadData.split(",")) );
	    			singleLineList.remove(0);

	    		}
	    		else
	    		{
	    		    singleLineList = new ArrayList<String> (Arrays.asList(lineReadData.split(",")) );
	    		}
	    		listOfAllLinesOfData.add(singleLineList);
	    	}
	    	
	    	fileReader.close();
	    	
	    	DataStore dataStore = DataStore.get();
	         if (fileType.equalsIgnoreCase("mushroom"))
	         {
	        	 dataStore.setTargetVariableColumnNumber(0);
	         }
	         if (fileType.equalsIgnoreCase("car"))
	         {
	        	 dataStore.setTargetVariableColumnNumber(6);
	         }
	         if (fileType.equalsIgnoreCase("ecoli"))
	         {
	        	 dataStore.setTargetVariableColumnNumber(7);
	        	 listOfAllLinesOfData =  new ArrayList<ArrayList<String>>( discretizer(listOfAllLinesOfData));
	         }
	         if (fileType.equalsIgnoreCase("bcancer"))
	         {
	        	 dataStore.setTargetVariableColumnNumber(9);
	         }
	         if (fileType.equalsIgnoreCase("letter"))
	         {
	        	 dataStore.setTargetVariableColumnNumber(0);
	         }
	   	    		    	
	    	
	    	
	        Collections.shuffle(listOfAllLinesOfData); 
	    	int divisionByFive = listOfAllLinesOfData.size()/5;
	    	int numOfLines = listOfAllLinesOfData.size();	    
	    	int i =0;
	    	while(i < numOfLines)
	    	{
	    		oneOfTheFiveData.add(listOfAllLinesOfData.get(i));
	    		if (i!=0 && i % divisionByFive == 0)
	    		{    			
	    			if (! (splitByFiveListOfData.size() == 4 ))
	    			{
		    			splitByFiveListOfData.add(oneOfTheFiveData);
		    			oneOfTheFiveData = new ArrayList<ArrayList<String>>();
	    			}
	    		}
	    		if(i % divisionByFive == 0 && i == numOfLines-1)
	    		{
	    			splitByFiveListOfData.add(oneOfTheFiveData);
	    		}
	    		else if(i == numOfLines-1)
	    		{
	    			splitByFiveListOfData.add(oneOfTheFiveData);
	    		}
	    	i++;	
	    	}	    	
	    	dataStore.setTotalDataSplitIn5(splitByFiveListOfData);
		}  	
		catch (FileNotFoundException e) 
		{
			System.out.println("the file path specified does not contain the necessary file");
		}
		catch (IOException ioE) 
		{
			System.out.println("there is a file IO read exception");
		}
	}
	  
		public void createInputFormat ()
		{
	    	DataStore storeData = DataStore.get();
	    	ArrayList<ArrayList<String>> listOfTrainingData  = new ArrayList<ArrayList<String>>();
	    	listOfTrainingData = storeData.getListOfTrainingData();

			HashMap <Integer, ArrayList<String>> columnsOfAttributes  = new HashMap <Integer, ArrayList<String>>();
	    	HashMap <Integer, HashMap<String, HashMap<String, Object>>> columnPropertiesOfValuesMap = new HashMap <Integer, HashMap<String, HashMap<String, Object>>>();
	    	HashMap<String, HashMap<String, Object>> propertiesMap = new HashMap<String, HashMap<String, Object>>();
	    	for (int i=0; i < listOfTrainingData.get(0).size(); i++)
	    	{
	    		columnsOfAttributes.put(i, new ArrayList<String>());
	    		columnPropertiesOfValuesMap.put(i, new HashMap<String, HashMap<String, Object>>());
	    	}
	    	
	 //   	System.out.println("Total size of Training data"+listOfTrainingData.size());   	
	    	int lineNumber = 0;
	    	for(ArrayList<String> singleLineList : listOfTrainingData)
	    	{
	    		lineNumber = lineNumber+1;
	    		int index = 0;
	    		for (String singleWord: singleLineList)	 
	    		{
	    			int columnNumber = index;
	    			index = index+1;

	    			HashMap<String, HashMap<String, Object>> mapOfProperties = new HashMap<String, HashMap<String, Object>>();
	    			try
    				{
    					mapOfProperties  = new HashMap (columnPropertiesOfValuesMap.get(columnNumber) );
    					if (mapOfProperties == null)
    					{
    						System.out.println("reinitializing at null");
	    					mapOfProperties = new HashMap<String, HashMap<String, Object>>();
    					}
    				}
    				catch (Exception e2)
    				{
    				//	System.out.println("reinitializing at exception");
    					mapOfProperties = new HashMap<String, HashMap<String, Object>>();
    				}
	    			ArrayList<String> valuesForAttribute = new ArrayList<String>();
	    			try
	    			{
	    				valuesForAttribute = columnsOfAttributes.get(columnNumber);
	    				if (valuesForAttribute == null)
	    				{
	    					valuesForAttribute = new ArrayList<String>();
	    				}
	    			}
	    			catch(Exception e5)
	    			{
	    				valuesForAttribute = new ArrayList<String>();
	    			}
	    			int countOfValue = 0;
	    			HashMap<String, Object> countOfValueInEachAttribMap = new HashMap<String, Object>();
	    			HashMap<String, Object> indexOfValueInEachAttribMap = new HashMap<String, Object>();

	    			try
	    			{
	    				countOfValueInEachAttribMap =  columnPropertiesOfValuesMap.get(columnNumber).get("numberOfRepetitions");	    				
	    				if (null == countOfValueInEachAttribMap)
	    				{
	    					countOfValueInEachAttribMap = new HashMap<String, Object>();
	    				}
	    				countOfValue = (Integer)countOfValueInEachAttribMap.get(singleWord);
	    			}
	    			catch (Exception e)
	    			{
	    				countOfValue = 0;
	    			}
	    			countOfValue = countOfValue + 1;		
	    			countOfValueInEachAttribMap.put(singleWord, countOfValue);
	    			mapOfProperties.put("numberOfRepetitions", countOfValueInEachAttribMap);
    				ArrayList<Integer> listOfIndices = new ArrayList<Integer>();
	    			try
	    			{
	    				 indexOfValueInEachAttribMap = mapOfProperties.get("indexList");
	    				 if (null == indexOfValueInEachAttribMap)
	    				 {
	    					 indexOfValueInEachAttribMap = new HashMap<String, Object>();
	    				 }
	    				 listOfIndices = (ArrayList<Integer>) indexOfValueInEachAttribMap.get(singleWord);
	    				 if (listOfIndices == null)
	    				 {
	    					 listOfIndices = new ArrayList<Integer>();
	    				 }
	    				 listOfIndices.add(lineNumber-1);
	    			}
	    			catch (Exception e3)
	    			{
	    				listOfIndices = new ArrayList<Integer>();
	    				listOfIndices.add(lineNumber-1);
	    			}
	    			indexOfValueInEachAttribMap.put(singleWord, listOfIndices);
	    			mapOfProperties.put("indexList", indexOfValueInEachAttribMap);

	    			valuesForAttribute.add(singleWord);
	    			columnsOfAttributes.put(columnNumber, valuesForAttribute);
	    			columnPropertiesOfValuesMap.put(columnNumber,mapOfProperties );
	    				    			
	    		}
	    	}
	   // 	System.out.println("number of data in training set ::"+lineNumber);
	        
	    	storeData.setAttributeNumber_listOfValuesMap(columnsOfAttributes);
	    	storeData.setattributeNumber_PropertiesMap(columnPropertiesOfValuesMap);

		}	
	
	
	public void createSubTreeOfAttributes (HashMap <Integer, ArrayList<String>> subTreeAttributeNumber_ValuesMap)
	{		
			DataStore storeData = DataStore.get();
    		storeData.setAttributeNumber_listOfValuesMap(subTreeAttributeNumber_ValuesMap);
	    //	System.out.println("done setting the data we need for the sub tree DATA");
    		HashMap <Integer, HashMap<String, HashMap<String, Object>>> columnPropertiesOfValuesMap = new HashMap <Integer, HashMap<String, HashMap<String, Object>>>();
	    	HashMap<String, HashMap<String, Object>> propertiesMap = new HashMap<String, HashMap<String, Object>>();
	    	
	    
	    	for( int columnNumber : subTreeAttributeNumber_ValuesMap.keySet())
	    	{
		    	int lineNumber = 0;

	    		for (String singleWord: subTreeAttributeNumber_ValuesMap.get(columnNumber))	 
	    		{
	    	    	lineNumber = lineNumber+1;

	    			HashMap<String, HashMap<String, Object>> mapOfProperties = new HashMap<String, HashMap<String, Object>>();
	    			try
    				{
    					mapOfProperties  = new HashMap (columnPropertiesOfValuesMap.get(columnNumber) );
    					if (mapOfProperties == null)
    					{
    						
	    					mapOfProperties = new HashMap<String, HashMap<String, Object>>();
    					}
    				}
    				catch (Exception e2)
    				{
    					mapOfProperties = new HashMap<String, HashMap<String, Object>>();
    				}
	    			int countOfValue = 0;
	    			HashMap<String, Object> countOfValueInEachAttribMap = new HashMap<String, Object>();
	    			HashMap<String, Object> indexOfValueInEachAttribMap = new HashMap<String, Object>();

	    			try
	    			{
	    				countOfValueInEachAttribMap =  columnPropertiesOfValuesMap.get(columnNumber).get("numberOfRepetitions");	    				
	    				if (null == countOfValueInEachAttribMap)
	    				{
	    					countOfValueInEachAttribMap = new HashMap<String, Object>();
	    				}
	    				countOfValue = (Integer)countOfValueInEachAttribMap.get(singleWord);
	    			}
	    			catch (Exception e)
	    			{
	    				countOfValue = 0;
	    			}
	    			countOfValue = countOfValue + 1;		
	    			countOfValueInEachAttribMap.put(singleWord, countOfValue);
	    			mapOfProperties.put("numberOfRepetitions", countOfValueInEachAttribMap);
    				ArrayList<Integer> listOfIndices = new ArrayList<Integer>();
	    			try
	    			{
	    				 indexOfValueInEachAttribMap = mapOfProperties.get("indexList");
	    				 if (null == indexOfValueInEachAttribMap)
	    				 {
	    					 indexOfValueInEachAttribMap = new HashMap<String, Object>();
	    				 }
	    				 listOfIndices = (ArrayList<Integer>) indexOfValueInEachAttribMap.get(singleWord);
	    				 if (listOfIndices == null)
	    				 {
	    					 listOfIndices = new ArrayList<Integer>();
	    				 }
	    				 listOfIndices.add(lineNumber-1);
	    			}
	    			catch (Exception e3)
	    			{
	    				listOfIndices = new ArrayList<Integer>();
	    				listOfIndices.add(lineNumber-1);
	    			}
	    			indexOfValueInEachAttribMap.put(singleWord, listOfIndices);
	    			mapOfProperties.put("indexList", indexOfValueInEachAttribMap);
	    			
	    					
	    			columnPropertiesOfValuesMap.put(columnNumber,mapOfProperties );
	    				    			
	    		}
		}
	    	
	    
	    	storeData.setattributeNumber_PropertiesMap(columnPropertiesOfValuesMap);
	    //	System.out.println("done setting the data we need for properties map for subtree");
	}
	
	public ArrayList<ArrayList<String>> discretizer(ArrayList<ArrayList<String>> listOfAllLinesOfData)
	{
		ArrayList<ArrayList<String>> discreteListOfAllLinesOfData = new ArrayList<ArrayList<String>>();
	
		for ( ArrayList<String> singleLine : listOfAllLinesOfData)
		{
			int index = 0;
			ArrayList<String> discreteNewLine = new ArrayList<String> (singleLine);
			for (String singleWord : singleLine)
			{
				if (index != 1 && index != 0 && index != 4 && index != 5 && index != 6)
				{
					index = index+1;
					continue;
				}
				float currentNumber = Float.parseFloat( singleWord );
				float newCurrentNumber = 0;
				if (index == 0)
				{
					ArrayList<Float> splitterList = new ArrayList<Float>();
					splitterList.add((float) 0.0);
					splitterList.add((float) 0.22);
					splitterList.add((float) 0.44);
					splitterList.add((float) 0.66);
					splitterList.add((float) 0.89);
					
					int indexOfSplitter = -1;
					for (float singleBound : splitterList)
					{
						indexOfSplitter = indexOfSplitter+1;
						if ( indexOfSplitter == 0)
						{
							continue;
						}
						else if (indexOfSplitter == 4)
						{
								newCurrentNumber = singleBound;
								break;
						}

						else if ( currentNumber < singleBound)
						{
							newCurrentNumber = splitterList.get(indexOfSplitter - 1);
							break;
						}						
					}
				}
				else if (index == 1)
				{
					ArrayList<Float> splitterList = new ArrayList<Float>();
					splitterList.add((float) 0.16);
					splitterList.add((float) 0.37);
					splitterList.add((float) 0.58);
					splitterList.add((float) 0.72);
					splitterList.add((float) 1.0);
					
					int indexOfSplitter = -1;
					for (float singleBound : splitterList)
					{
						indexOfSplitter = indexOfSplitter+1;
						if ( indexOfSplitter == 0)
						{
							continue;
						}
						else if (indexOfSplitter == 4)
						{
								newCurrentNumber = singleBound;
								break;
						}

						else if ( currentNumber < singleBound)
						{
							newCurrentNumber = splitterList.get(indexOfSplitter - 1);
							break;
						}						
					}
				}
				else if (index == 4)
				{
					ArrayList<Float> splitterList = new ArrayList<Float>();
					splitterList.add((float) 0.0);
					splitterList.add((float) 0.22);
					splitterList.add((float) 0.44);
					splitterList.add((float) 0.66);
					splitterList.add((float) 0.88);
					
					int indexOfSplitter = -1;
					for (float singleBound : splitterList)
					{
						indexOfSplitter = indexOfSplitter+1;
						if ( indexOfSplitter == 0)
						{
							continue;
						}
						else if (indexOfSplitter == 4)
						{
								newCurrentNumber = singleBound;
								break;
						}

						else if ( currentNumber < singleBound)
						{
							newCurrentNumber = splitterList.get(indexOfSplitter - 1);
							break;
						}						
					}
				}
				else if (index == 5)
				{
					ArrayList<Float> splitterList = new ArrayList<Float>();
					splitterList.add((float) 0.03);
					splitterList.add((float) 0.27);
					splitterList.add((float) 0.51);
					splitterList.add((float) 0.75);
					splitterList.add((float) 1.0);
					
					int indexOfSplitter = -1;
					for (float singleBound : splitterList)
					{
						indexOfSplitter = indexOfSplitter+1;
						if ( indexOfSplitter == 0)
						{
							continue;
						}
						else if (indexOfSplitter == 4)
						{
								newCurrentNumber = singleBound;
								break;
						}

						else if ( currentNumber < singleBound)
						{
							newCurrentNumber = splitterList.get(indexOfSplitter - 1);
							break;
						}						
					}
				}
				else if (index == 6)
				{
					ArrayList<Float> splitterList = new ArrayList<Float>();
					splitterList.add((float) 0.0);
					splitterList.add((float) 0.22);
					splitterList.add((float) 0.44);
					splitterList.add((float) 0.66);
					splitterList.add((float) 0.89);
					
					int indexOfSplitter = -1;
					for (float singleBound : splitterList)
					{
						indexOfSplitter = indexOfSplitter+1;
						if ( indexOfSplitter == 0)
						{
							continue;
						}
						else if (indexOfSplitter == 4)
						{
								newCurrentNumber = singleBound;
								break;
						}

						else if ( currentNumber < singleBound)
						{
							newCurrentNumber = splitterList.get(indexOfSplitter - 1);
							break;
						}						
					}
				}
				discreteNewLine.remove(index);
				discreteNewLine.add(index, String.valueOf( newCurrentNumber ) );
				index = index+1;				
			}
			discreteListOfAllLinesOfData.add(discreteNewLine);
		}
		
		for (ArrayList<String> shit : discreteListOfAllLinesOfData)
		{
			System.out.println(shit);
		}
		return discreteListOfAllLinesOfData;
	}

}
