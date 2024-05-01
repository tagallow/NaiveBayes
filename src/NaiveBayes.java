/*
 * Filename: NaiveBayes.java
 * 
 * Name: Tom Galloway
 * ULID: tagallo
 * Course: IT340
 * Instructor: Dr. Califf
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This is an implementation of Naive Bayes classification to
 * read in data files of an appropriate format and perform
 * various calculations with them
 * @author Tom Galloway
 *
 */
public class NaiveBayes
{
	public static Database db;

	public static void main(String[] args)
	{	
		menu();
	}
	/**
	 * This is the main menu that interacts with the user.
	 */
	public static void menu()
	{
		Scanner in = new Scanner(System.in);
		int choice=-1;
		while(choice!=0)
		{
			System.out.println("1. Enter training data files");
			System.out.println("2. Provide classifications for a set of data");
			System.out.println("3. Check the accuracy of an existing file");
			System.out.println("0. Exit");
			
			System.out.print("Choice:");
			
			choice = in.nextInt();
			switch(choice)
			{
			case(0):
				break;
			case(1):
				trainingdata();
				break;
			case(2):
				if(db == null)
					System.out.println("You need to train the system first");
				else
					classify();
				break;
			case(3):
				if(db == null)
					System.out.println("You need to train the system first");
				else
					accuracyReport();
				break;
			default:
				System.out.println("Invalid Choice.");
			}
			System.out.println();
		}
	}
	/**
	 * This is the method used to take in all the training data. A new database is
	 * created, the filenames are read in and opened, then each file is passed to another
	 * helper method.
	 */
	public static void trainingdata()
	{
		db = new Database();
		Scanner in = new Scanner(System.in);
		Scanner metaDataFile=null,dataFile=null;
		System.out.println("Enter the name of the metadata file");
		try
		{
//			metaDataFile = new Scanner(new File("car.meta"));
			metaDataFile = new Scanner(new File(in.nextLine()));
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not Found");
			System.exit(1);
		}
		System.out.println("Enter the name of the data file");
		try
		{
//			dataFile = new Scanner(new File("car.train"));
			dataFile = new Scanner(new File(in.nextLine()));
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not Found");
			System.exit(1);
		}
		readMetaData(metaDataFile);
		db.prepareLists();
		train(dataFile);
		metaDataFile.close();
		dataFile.close();
	}
	/**
	 * This method accepts the metadata file and parses each line
	 * into the attributes and their respective values. The 
	 * attributes are then added to the DB
	 * @param file - The metadata file
	 */
	public static void readMetaData(Scanner file)
	{
		StringTokenizer st = null;
		String currAttribute = new String();
		
		while(file.hasNext())
		{
			st = new StringTokenizer(file.nextLine(),":,");
			currAttribute = st.nextToken();
			db.addAttribute(currAttribute);
			while(st.hasMoreTokens())
			{
				db.addPair(currAttribute,st.nextToken());
			}
		}
	}
	/**
	 * The train method is used to read in a data file and record
	 * the statistics of how frequently various attributes appear
	 * for certain classifications
	 * @param file - the data file
	 */
	public static void train(Scanner file)
	{
		StringTokenizer st = null;
		String[] values = new String[db.getNumAttributes()];
		
		while(file.hasNext())
		{
			st = new StringTokenizer(file.nextLine(),",");
			for(int i=0;i<values.length;i++)
			{
				if(!st.hasMoreTokens())
				{
					System.out.println("Data file not compatable with current knowledge base");
					System.exit(1);
				}
				values[i]=st.nextToken();
			}

			if(!st.hasMoreTokens())
			{
				System.out.println("Data file not compatable with current knowledge base");
				System.exit(1);
			}
			db.learn(st.nextToken(), values);
			if(st.hasMoreTokens())
			{
				System.out.println("Data file not compatable with current knowledge base");
				System.exit(1);
			}
		}
	}
	/**
	 * Classify prompts the user for an input and output file. It parses
	 * the input file, ignoring the classification data, then writes out a new
	 * file with our calculated classifications.
	 */
	public static void classify()
	{
		String calculatedClass;	
		Scanner in = new Scanner(System.in);
		Scanner inputFile=null;
		PrintWriter outputFile=null;
		StringTokenizer st = null;
		String[] values = new String[db.getNumAttributes()];
		
		System.out.println("Enter the name of the input file");
		try
		{
			inputFile = new Scanner(new File(in.nextLine()));
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not Found");
			System.exit(1);
		}
		System.out.println("Enter the name of the output file");
		try
		{
			outputFile = new PrintWriter(new File(in.nextLine()));
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not Found");
			System.exit(1);
		}
		while(inputFile.hasNext())
		{
			st = new StringTokenizer(inputFile.nextLine(),",");
			for(int i=0;i<values.length;i++)
			{
				if(!st.hasMoreTokens())
				{
					System.out.println("Data file not compatable with current knowledge base");
					System.exit(1);
				}
				values[i]=st.nextToken();
			}
			calculatedClass=db.analyzeLine(values);
			for(int i=0;i<values.length;i++)
			{
				outputFile.print(values[i]+",");
			}
			outputFile.print(calculatedClass + "\n");
		}
		System.out.println();
		inputFile.close();
		outputFile.close();
	}
	/**
	 * accuracyReport is used to read in a file, calculate a classification for each line
	 * using the learned data, then compare our results with the classifications indicated in the file.
	 */
	public static void accuracyReport()
	{
		int total=0,correct=0;
		String calculatedClass;
		Scanner in = new Scanner(System.in);
		Scanner inputFile=null;
		StringTokenizer st = null;
		String[] values = new String[db.getNumAttributes()];
		DecimalFormat df = new DecimalFormat("% ###.##");
		
		System.out.println("Enter the name of the input file");
		try
		{
			inputFile = new Scanner(new File(in.nextLine()));
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not Found");
			System.exit(1);
		}
		while(inputFile.hasNext())
		{
			st = new StringTokenizer(inputFile.nextLine(),",");
			for(int i=0;i<values.length;i++)
			{
				if(!st.hasMoreTokens())
				{
					System.out.println("Data file not compatable with current knowledge base");
					System.exit(1);
				}
				values[i]=st.nextToken();
			}
			calculatedClass=db.analyzeLine(values);
			if(st.nextToken().equals(calculatedClass))
				correct++;
			total++;
		}
		System.out.println();
		System.out.println("Score: "+df.format((double)correct/total));
		inputFile.close();
	}
}
