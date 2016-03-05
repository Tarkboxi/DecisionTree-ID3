package treeCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import dataSorter.DataStore;

public class TreeOperations {
	
	public void findTargetEntropy()
	{
		DataStore dataStore = DataStore.get();
		HashMap<Integer, ArrayList<String>> attributeValueMap = dataStore.getAttributeNumber_listOfValuesMap();
		HashMap<Integer,HashMap<String, HashMap<String, Object>>>countOfAttribValues = dataStore.getattributeNumber_PropertiesMap();
		int targetVariableColumnNumber = dataStore.getTargetVariableColumnNumber();		
		int totalnumOfTargetVariables = attributeValueMap.get(targetVariableColumnNumber).size();
		HashMap<String, Object> countOfValuesMap = new HashMap<String, Object>( countOfAttribValues.get(targetVariableColumnNumber).get("numberOfRepetitions"));
		float Entropy = (float) 0.0;
		for ( String singleValueinAttr : countOfValuesMap.keySet())
		{
			int countOfCurrentValue = (int) countOfValuesMap.get(singleValueinAttr);
			float probability =  ((float)countOfCurrentValue/totalnumOfTargetVariables);
			Entropy = (float) (Entropy + ((probability*(-1))* ( (Math.log(probability) )/ (Math.log(2))  ) ));
		}
		dataStore.setTargetEntropy(Entropy);
	}
	
	public  int findBestClassifier()
    {
		DataStore dataStore = DataStore.get();
		HashMap<Integer, ArrayList<String>> allAttributeColumnsWithValues = dataStore.getAttributeNumber_listOfValuesMap();
		float targetEntropy = dataStore.getTargetEntropy();
		int targetVariableColumnNumber = dataStore.getTargetVariableColumnNumber();

		HashMap <Integer, HashMap<String, ArrayList<String>>> ColumnNumbersWithValuesAndListOfTargets = new HashMap <Integer, HashMap<String, ArrayList<String>>>();
		HashMap<Integer,Float> infoGainForEachAttribute = new HashMap<Integer, Float>();
		for ( int columnNumberOfAttribute : allAttributeColumnsWithValues.keySet())
		{
			float informationGainForAttribute = (float) 0;
			float finalInformationGainForAttribute = (float) 0;

			if (targetVariableColumnNumber == columnNumberOfAttribute)
			{
				continue;
			}
			ArrayList<String> oneAttributeColumnValues= new ArrayList(allAttributeColumnsWithValues.get(columnNumberOfAttribute) );
			int ColumnLength = oneAttributeColumnValues.size();
			HashMap <String, ArrayList<String>> valueOfAttribWithTargetValueList= new HashMap<String,ArrayList<String>>();
			int indexCount = 0;
			for (String singleValueInsideAttribute : oneAttributeColumnValues)
			{
				int indexToSearch = indexCount;
				indexCount = indexCount+1;
				String targetVariableForCurrent = allAttributeColumnsWithValues.get(targetVariableColumnNumber).get(indexToSearch);
				try
				{
					ArrayList<String> numberOfValues = new ArrayList<String>();
					numberOfValues = valueOfAttribWithTargetValueList.get(singleValueInsideAttribute);
					numberOfValues.add(targetVariableForCurrent);
					valueOfAttribWithTargetValueList.put(singleValueInsideAttribute, numberOfValues);
				}
				catch(Exception e)
				{
					ArrayList<String> listOfTargetsForValue = new ArrayList<String>();
					listOfTargetsForValue.add(targetVariableForCurrent);
					valueOfAttribWithTargetValueList.put(singleValueInsideAttribute, listOfTargetsForValue);
				}
				ColumnNumbersWithValuesAndListOfTargets.put(columnNumberOfAttribute, valueOfAttribWithTargetValueList);
			}
		
			for ( String eachValue : valueOfAttribWithTargetValueList.keySet())
			{
				ArrayList<String> targetsForThisValue = new ArrayList(valueOfAttribWithTargetValueList.get(eachValue) );
				HashMap<String, Integer> countOfValueInEachAttrib = new HashMap<String, Integer>();
				
				for ( String singleTargetValue : targetsForThisValue )
				{
					int countOfValue = 0;
	    			try
	    			{
	    				countOfValue = countOfValueInEachAttrib.get(singleTargetValue);
	    			}
	    			catch (Exception e)
	    			{
	    				countOfValue = 0;
	    			}
	    			countOfValue = countOfValue + 1;	
	    			countOfValueInEachAttrib.put(singleTargetValue, countOfValue);	
				}
				float entropy = (float) 0.0;
				for ( String singleTargetValue : countOfValueInEachAttrib.keySet())
				{
					float probability = (float)( countOfValueInEachAttrib.get(singleTargetValue))/ ( valueOfAttribWithTargetValueList.get(eachValue).size());
					entropy = (float) (entropy + ((probability*(-1))* ( (Math.log(probability) )/ (Math.log(2))  ) ));
				}
				informationGainForAttribute =  informationGainForAttribute + ( (float) (valueOfAttribWithTargetValueList.get(eachValue).size())/ColumnLength )* entropy ;
			}
			finalInformationGainForAttribute = targetEntropy - informationGainForAttribute;
			infoGainForEachAttribute.put(columnNumberOfAttribute,finalInformationGainForAttribute);
		    
			
		}
		dataStore.setAttributeNumber_Values_listOfTargets(ColumnNumbersWithValuesAndListOfTargets);
		
		int columnOfLargestGain = 9999;
		if (infoGainForEachAttribute.isEmpty())
		{
			return 9999;
		}
		ArrayList<Float> listOfInfoGains = new ArrayList<Float>();
		for ( Integer columnNumber : infoGainForEachAttribute.keySet())
		{
			listOfInfoGains.add(infoGainForEachAttribute.get(columnNumber));
		}
		float MaxInfo = Collections.max(listOfInfoGains);
		for ( Integer columnNumber : infoGainForEachAttribute.keySet())
		{
			if (infoGainForEachAttribute.get(columnNumber) == MaxInfo)
			{
				columnOfLargestGain = columnNumber;
			}
		}
		return columnOfLargestGain;
    }
	
		
	public void findResultOfTargets ( )
	{
		DataStore dataStore = DataStore.get();
		int targetValueColumnNumber = dataStore.getTargetVariableColumnNumber();
		ArrayList<ArrayList<String>> listOfPossiblePaths = new ArrayList<ArrayList<String>>();
		for(String singleUnsplittedLine : dataStore.getListOfPaths())
		{
			ArrayList<String> singlePath= new ArrayList( Arrays.asList (singleUnsplittedLine.split("-\\*-") ) );
			listOfPossiblePaths.add(singlePath);
		}
		ArrayList<ArrayList<String>> listOfTestData = new ArrayList<ArrayList<String>>(dataStore.getListOfTestData());
    	ArrayList<String> targetColumnPredicted = new ArrayList<String>();
    	
		
    	for (ArrayList<String> singleLineList : listOfTestData)
    	{ 		
    		ArrayList<ArrayList<String>> listOfRemainingPossiblePaths = new ArrayList<ArrayList<String>>();
    		listOfRemainingPossiblePaths = listOfPossiblePaths;
    		String requiredResult = "";
    		int travelDepth = 1 ;
    		while ( listOfRemainingPossiblePaths.size() > 1 )
    		{
    			listOfRemainingPossiblePaths = isolateTree(listOfRemainingPossiblePaths,singleLineList ,travelDepth, targetValueColumnNumber);
    			travelDepth = travelDepth+1;
    		}
    		if (listOfRemainingPossiblePaths.size() == 1)
    		{
    			 requiredResult = listOfRemainingPossiblePaths.get(0).get(listOfRemainingPossiblePaths.get(0).size()-1);
    		}
    		if (requiredResult.equalsIgnoreCase(singleLineList.get(targetValueColumnNumber)))
			{
    			targetColumnPredicted.add(requiredResult);
			}
    	}
    	float percentageAccuracy = ( (float) targetColumnPredicted.size() / listOfTestData.size() ) * 100 ;
    	ArrayList<Float> listOfAccuracies = new ArrayList<Float>();
    	try
    	{
    		listOfAccuracies = dataStore.getListOfAccuracies();
			listOfAccuracies.add(percentageAccuracy);
    		if (listOfAccuracies == null)
    		{
    			listOfAccuracies = new ArrayList<Float>();
    			listOfAccuracies.add(percentageAccuracy);
    		}
    	}
    	catch (Exception e)
    	{
			listOfAccuracies = new ArrayList<Float>();
			listOfAccuracies.add(percentageAccuracy);
    	}
    	dataStore.setListOfAccuracies(listOfAccuracies);
    	
	}
	
	
	public ArrayList<ArrayList<String>> isolateTree (ArrayList<ArrayList<String>> listOfPossiblePaths,ArrayList<String> singleLineList, int travelDepth, int targetVariableColumnNumber)
	{
		ArrayList<ArrayList<String>> listOfRemainingPossiblePaths = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> singlePath : listOfPossiblePaths)
		{
			if (travelDepth > singlePath.size()-1)
			{
				continue;
			}
			String currentOpenChoice = singlePath.get(travelDepth) ;
			int columnNumber = 0;
			int subStringToTake = 2;
			try
			{
				columnNumber = Integer.parseInt( currentOpenChoice.substring(0,2));
				subStringToTake = 3;
			}
			catch (Exception e)
			{
				try
				{						
					columnNumber = Integer.parseInt( currentOpenChoice.substring(0,1));
				}
				catch(Exception e2)
				{
					System.out.println("number Format exception ::"+currentOpenChoice);   						
					break;
				}		
			}
			if (travelDepth == singlePath.size()-1)
			{
				listOfRemainingPossiblePaths = new ArrayList<ArrayList<String>>();
				listOfPossiblePaths.add(singlePath);
				return listOfRemainingPossiblePaths;
			}
			if ( singleLineList.get(columnNumber).equalsIgnoreCase(currentOpenChoice.substring(subStringToTake)))
			{
				listOfRemainingPossiblePaths.add(singlePath);
			} 			
		}
		return listOfRemainingPossiblePaths;
	}
}
    		 	

