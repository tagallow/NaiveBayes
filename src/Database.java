/*
 * Filename: NaiveBayes.java
 * 
 * Name: Tom Galloway
 * ULID: tagallo
 * Course: IT340
 * Instructor: Dr. Califf
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a Database class used to perform Naive Bayes classifications.
 * This class stores and organizes all the data read in by the NaiveBayes program.
 * @author Tom
 *
 */
public class Database
{
	/* attMap is a map where each key is an attribute, and each value
	 * is an array list of that attribute's possible values. This is used
	 * to be able to figure out which index in classMap to increment when
	 * keeping track of how many times a value occurs in a file */
	private Map<String,ArrayList<String>> attMap;
	
	/* attList is an ArrayList with the name of each attribute. This is useful when we need
	 * to loop through each attribute, we can have a string of the attribute itself,
	 * and a respective index representation */
	private ArrayList<String> attList;
	
	/* classMap is a map where each key is the name of a classification, and 
	 * each value is an ArrayList of integer arrays. Each index in the ArrayList represents 
	 * each attribute under that classification. The each index in each integer array
	 * represents the number of times that corresponding value of that attribute is
	 * encountered in the training process. The order of the values corresponds to 
	 * the order that they were read in by the metadata file. */
	private Map<String,ArrayList<Integer>[]> classMap;
	
	/* classList is an ArrayList with the name of each classification. It is
	 * used the same way as attList, but for classifications instead. */
	private ArrayList<String> classList;
	
	/* An integer array use to count the number of times each classification 
	 * is encountered during training */
	private int[] classCount;
	
	/**
	 * A default constructor which initializes all of the above variables except
	 * classCount, which is not needed until after training, when we know the number 
	 * of classifications
	 */
	public Database()
	{
		attMap = new HashMap<String,ArrayList<String>>();
		attList = new ArrayList<String>();
		classMap = new HashMap<String,ArrayList<Integer>[]>();
	}
	
	/**
	 * Adds an attribute to the database
	 * @param att - The name of the attribute
	 */
	public void addAttribute(String att)
	{
		ArrayList<String> list = new ArrayList<String>();
		attMap.put(att, list);
		attList.add(att);
	}
	/**
	 * Adds an attribute and value pair to attMap
	 * @param att - The attribute (key)
	 * @param value - The value of the attribute
	 */
	public void addPair(String att,String value)
	{
		attMap.get(att).add(value);
	}
	/**
	 * Returns the number of attributes in the database
	 * @return The number of attributes
	 */
	public int getNumAttributes()
	{
		return attList.size();
	}
	/**
	 * learn is used to read in a datafile and fills the maps with the
	 * number of times each attribute and value is encountered.
	 * @param classification - The classification of the current line
	 * @param values - A String array with each value. The index in the array determines the attribute
	 */
	public void learn(String classification,String[] values)
	{
		int valueIndex=0;
		int previousValue=0;

		//for each attribute
		for(int i=0;i<attList.size();i++)
		{
			//index of the attribute's value to be incremented
			valueIndex=attMap.get(attList.get(i)).indexOf(values[i]); 

			//storing previous value
			previousValue = classMap.get(classification)[i].get(valueIndex); 

			/* Since the Integer class is being used, the previous value must be removed
			 * then added with +1 */
			classMap.get(classification)[i].remove(valueIndex);
			classMap.get(classification)[i].add(valueIndex, previousValue+1);
			
			classCount[classList.indexOf(classification)]++;
		}
	}
	@SuppressWarnings("unchecked")
	/**
	 * preprareLists is used to initialize the maps and ArrayLists after the metadata file is
	 * done being read in. 
	 */
	public void prepareLists()
	{
		ArrayList<Integer>[] temp;
		String currentAttribute;
		
		//the list of classes 
		classList = attMap.get("class");
		
		attList.remove("class");
		
		//to keep track of every time an instance of a classification is added to the DB
		classCount = new int[classList.size()]; 
		
		//for every classification
		for(int i=0;i<classList.size();i++)
		{
			//every classification has an ArrayList of counters for each attribute
			temp = new ArrayList[attList.size()]; 

			for(int j=0;j<temp.length;j++)
			{
				currentAttribute = attList.get(j);
				
				//set the arrayList to the correct size (the number of values for its attribute)
				temp[j] = new ArrayList<Integer>(); 
				
				for(int k=0;k<attMap.get(currentAttribute).size();k++)
				{
					temp[j].add(new Integer(1)); //initializes everything to 1 to smooth data 
				}
			}
			//The classification name, and its appropriate ArrayList are added to classMap
			classMap.put(classList.get(i), temp);
		}
	}
	/**
	 * analyzeLine is used to estimate the appropriate classification
	 * for a line in a datafile, based on our learned database.
	 * @param values - A string of values
	 * @return - The estimated classification
	 */
	public String analyzeLine(String[] values)
	{
		double probability=1;
		double[] classProb = new double[classList.size()];
		String result;
		
		for(int i=0;i<classList.size();i++)
		{
			for(int j=0;j<values.length;j++)
			{
				//multiply the probability of each attribute
				probability*=probHelper(i,j,values[j]);
			}
			classProb[i]=probability;
			probability=1;
		}
		result = classList.get(max(classProb));
		
		return result;
	}
	/*
	 * probHelper assists analyzeLine by calculating the probability for a specified
	 * set of conditions 
	 * @param classNum - The index of the classification
	 * @param attNum - The index of the attribute
	 * @param value - The value as a string
	 * @return - The calculated probability
	 */
	private double probHelper(int classNum,int attNum,String value)
	{
		String classString = classList.get(classNum);
		int attTotal=0; //the total number of times the attribute's value occurs
		
		//index of the attList index, used to get the number of times this value occurred in the current classification
		int index=attMap.get(attList.get(attNum)).indexOf(value);  
		
		//the total number of times this attribute occurs in the DB
		for(int i=0;i<classMap.get(classString)[attNum].size();i++)
		{
			attTotal+=classMap.get(classString)[attNum].get(i);
		}
		//number of times the value occurred in this classification
		double occurances=classMap.get(classString)[attNum].get(index); 
		
		return occurances/attTotal; 
	}
	/*
	 * We need the index of the maximum value, not the actual max value.
	 * @param list
	 * @return The index with the maximum value
	 */
	private int max(double[] list)
	{
		int currMax=0;
		for(int i=1;i<list.length;i++)
		{
			if(list[i]>list[currMax])
				currMax=i;
		}
		return currMax;
	}
}
