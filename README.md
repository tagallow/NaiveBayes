# Naive Bayes Classifier

This is a Naive Bayes classification system.  Compile this program by navigating to the folder containing the source files then executing:

`javac *.java`

After that, you can run the program by executing:

`java NaiveBayes`

This will bring you to the main menu with a list of all your options. Before you can do anything you must first train the system (option 1). To do this you must have two existing files to train the system with. The first file contains the metadata, which allows the system to prepare it's data structures for the actual data it is going to learn from. The metadata file must contain a list of attributes, and each attributes possible values, ending with the classification. The next file contains the actual learning data. Each line is one example, each attribute is separated by a comma.

After training the program you can run the other two options to use the program to analyze other data. Option 2 allows you to read a set of data that may or may not have classifications, and the program will provide a classification for each line. The new file (specified by you) will be the exact same format as the input and training files.

Option 3 reads in a file and compares the programs estimated classifications with the classifications specified in the file. The percentage of matches is printed to the screen at the end.
