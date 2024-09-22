package com.github.thed2lab.analysis;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

// opencsv
import com.opencsv.CSVWriter;
import net.bytebuddy.jar.asm.Attribute;

// weka.jar
import weka.core.Instances;
import weka.core.Range;
import weka.core.Utils;
import weka.core.converters.CSVLoader;
// weka classifiers
import weka.classifiers.*;
import weka.classifiers.bayes.*;
import weka.classifiers.meta.*;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.*;
import weka.classifiers.misc.*;
import weka.classifiers.rules.*;
import weka.classifiers.trees.*;

// weka experiment
import weka.experiment.*;

import javax.swing.*;

public class WekaExperiment {

	private WekaParameters params;

	public WekaExperiment(WekaParameters params) {
		this.params = params;
	}
    
    // public void run() throws Exception {
	// 	File[] dataset = params.getDataSet();
	// 	boolean isClassification = params.getIsClassification();
	// 	Classifier[] classifiers = isClassification ? getClassificationClassifiers() : getRegressionClassifiers();

	// 	for (int i = 0; i < dataset.length; i++) {
	// 		// Load .CSV training datafiles
	// 		CSVLoader loader = new CSVLoader();
	// 		loader.setSource(dataset[i]);
	// 		Instances trainingDataSet = loader.getDataSet();

	// 		// Set the class index to the last field in the data set,
	// 		// i.e predict the last column in the given dataset
	// 		trainingDataSet.setClassIndex(trainingDataSet.numAttributes() - 1);
			
	// 		for (Classifier c : classifiers) {
	// 			c.buildClassifier(trainingDataSet);
	// 			Evaluation eval = new Evaluation(trainingDataSet);
	// 			eval.crossValidateModel(c, trainingDataSet, 10, new Random(1));
				
	// 			//System.out.println(eval.fMeasure(1)+" "+eval.precision(1)+" "+eval.recall(1)+" ");
	// 			System.out.println(c.getClass().getSimpleName() + " " + dataset[i].getName() + " " + eval.pctCorrect());
	// 		}
	// 	}
		
	// 	System.out.println("Predictions Complete.");
    // }

	public void run() throws Exception {
		File[] dataset = params.getDataSet();
		boolean isClassification = params.getIsClassification();
		Classifier[] classifiers = isClassification ? getClassificationClassifiers() : getRegressionClassifiers();
		
		String outputDirectory = params.getDirectory();
		File d = new File(outputDirectory);
			if (!d.exists()) {
				d.mkdirs();
		}
		
		ArrayList<ClassifierResult[]> allResults = new ArrayList<>();
		ArrayList<String> fileNames = new ArrayList<String>();

		for (File f : dataset) {
			String fileName = f.getName().replace(".csv", "");
			fileNames.add(fileName);
			System.out.println("Conducting experiment on " + fileName);

			ResultMatrix matrix = runExperiment(f, classifiers, isClassification, outputDirectory);
			ClassifierResult[] res = getClassificationExperimentResults(classifiers, matrix);
			
			allResults.add(res);
		}

		writeResultsToCSV(classifiers, allResults, fileNames, outputDirectory + "/predictions.csv");
		System.out.println("Predictions Complete.");
	}

	public ResultMatrix runExperiment(File f, Classifier[] classifiers, boolean isClassification, String outputDirectory) throws Exception {
		// setup weka.experiment
		Experiment exp = new Experiment();
		exp.setPropertyArray(new Classifier[0]);
		exp.setUsePropertyIterator(true);

		// setup for classification or regression
		SplitEvaluator se = null;
		Classifier sec = null;

		if (isClassification) {
			se = new ClassifierSplitEvaluator();
			sec = ((ClassifierSplitEvaluator) se).getClassifier();
		} else {
			se = new RegressionSplitEvaluator();
			sec = ((RegressionSplitEvaluator) se).getClassifier();
		}

		// cross validation
		CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
		cvrp.setNumFolds(10);
		cvrp.setSplitEvaluator(se);

		PropertyNode[] propertyPath = new PropertyNode[2];
		propertyPath[0] = new PropertyNode(se,
				new PropertyDescriptor("splitEvaluator", CrossValidationResultProducer.class),
				CrossValidationResultProducer.class);

		propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()), se.getClass());

		exp.setResultProducer(cvrp);
		exp.setPropertyPath(propertyPath);

		// set classifiers here
		exp.setPropertyArray(classifiers);

		DefaultListModel model = new DefaultListModel();

		// set dataset here
		model.addElement(f);

		exp.setDatasets(model);

		// *this is important for WEKA experimenter calculations*
		InstancesResultListener irl = new InstancesResultListener();

		File outputFile = new File(outputDirectory + "/ " + f.getName().replace(".csv", "") + "_InstancesResultListener.csv");
		//outputFile.createNewFile();
		irl.setOutputFile(outputFile);
		exp.setResultListener(irl);

		exp.initialize();
		exp.runExperiment();
		exp.postProcess();

		PairedCorrectedTTester tester = new PairedCorrectedTTester();
		Instances result = new Instances(new BufferedReader(new FileReader(irl.getOutputFile())));

		tester.setInstances(result);
		tester.setSortColumn(-1);

		tester.setRunColumn(result.attribute("Key_Run").index());
		if (isClassification) {
			tester.setFoldColumn(result.attribute("Key_Fold").index());
		}
		tester.setDatasetKeyColumns(new Range("" + (result.attribute("Key_Dataset").index() + 1)));
		tester.setResultsetKeyColumns(new Range("" + (result.attribute("Key_Scheme").index() + 1) + ","
				+ (result.attribute("Key_Scheme_options").index() + 1) + ","
				+ (result.attribute("Key_Scheme_version_ID").index() + 1)));
		tester.setResultMatrix(new ResultMatrixPlainText());
		tester.setDisplayedResultsets(null);
		tester.setSignificanceLevel(0.05);
		tester.setShowStdDevs(true);

		if (isClassification) {
			tester.multiResultsetFull(0, result.attribute("Percent_correct").index());
		} else {
			tester.multiResultsetFull(0, result.attribute("Root_mean_squared_error").index());
		}

		ResultMatrix matrix = tester.getResultMatrix();
		//irl.getOutputFile().delete();
		// FileHandler.writeToText(matrix.toString(), outputDirectory, "predictions.txt");
		return matrix;
	}

	private static ClassifierResult[] getClassificationExperimentResults(Classifier[] classifiers, ResultMatrix matrix) {
		ClassifierResult[] classifierResults = new ClassifierResult[classifiers.length];

		for (int i = 0; i < matrix.getColCount(); i++) {
			classifierResults[i] = new ClassifierResult(classifiers[i], (double) Math.round(matrix.getMean(i, 0) * 100) / 100, matrix.getSignificance(i, 0));
		}

		return classifierResults;
	}

    private static Classifier[] getClassificationClassifiers() throws Exception {
		Classifier[] classifiers = new Classifier[46];

		// set baseline classifier here
		classifiers[0] = new ZeroR();

		// bayes
		classifiers[1] = new BayesNet();
		classifiers[2] = new NaiveBayes();
		classifiers[3] = new NaiveBayesMultinomialText();
		classifiers[4] = new NaiveBayesUpdateable();
        classifiers[5] = new NaiveBayesMultinomial();
        classifiers[6] = new NaiveBayesMultinomialUpdateable();

		// functions
		classifiers[7] = new Logistic();
		classifiers[8] = new MultilayerPerceptron();
		classifiers[9] = new SGD();
		classifiers[10] = new SGDText();
		classifiers[11] = new SimpleLogistic();
		classifiers[12] = new SMO();
		classifiers[13] = new VotedPerceptron();

		// lazy
		classifiers[14] = new IBk();
		classifiers[15] = new KStar();
		classifiers[16] = new LWL();

		// meta classifiers
		classifiers[17] = new AdaBoostM1();
		classifiers[18] = new AttributeSelectedClassifier();
		classifiers[19] = new Bagging();
		classifiers[20] = new ClassificationViaRegression();
		classifiers[21] = new CVParameterSelection();
		classifiers[22] = new FilteredClassifier();
		classifiers[23] = new IterativeClassifierOptimizer();
		classifiers[24] = new LogitBoost();
		classifiers[25] = new MultiClassClassifier();
		classifiers[26] = new MultiClassClassifierUpdateable();
		classifiers[27] = new MultiScheme();
		classifiers[28] = new RandomCommittee();
		classifiers[29] = new RandomizableFilteredClassifier();
		classifiers[30] = new RandomSubSpace();
		classifiers[31] = new Stacking();
		classifiers[32] = new Vote();
		classifiers[33] = new WeightedInstancesHandlerWrapper();

		// Misc
		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setOptions(Utils.splitOptions("-M"));
		classifiers[34] = imc;

		// Rules
		classifiers[35] = new DecisionTable();
		classifiers[36] = new JRip();
		classifiers[37] = new OneR();
		classifiers[38] = new PART();

        // Tree
		classifiers[39] = new DecisionStump();
		classifiers[40] = new HoeffdingTree();
		classifiers[41] = new J48();
		classifiers[42] = new LMT();
		classifiers[43] = new RandomForest();
		classifiers[44] = new RandomTree();
		classifiers[45] = new REPTree();
        //classifiers[46] = new DPCTree(); 

		return classifiers;
	}

    private static Classifier[] getRegressionClassifiers() {
		Classifier[] classifiers = new Classifier[20];

		// set baseline classifier here
		classifiers[0] = new ZeroR();

		// functions
		classifiers[1] = new GaussianProcesses();
		classifiers[2] = new LinearRegression();
		classifiers[3] = new MultilayerPerceptron();
		classifiers[4] = new SimpleLinearRegression();
		classifiers[5] = new SMOreg();

		// meta
		classifiers[6] = new Bagging();
		classifiers[7] = new CVParameterSelection();
		classifiers[8] = new RegressionByDiscretization();

		classifiers[9] = new MultiScheme();
		classifiers[10] = new RandomCommittee();
		classifiers[11] = new RandomizableFilteredClassifier();
		classifiers[12] = new RandomSubSpace();
		classifiers[13] = new Stacking();
		classifiers[14] = new Vote();
		classifiers[15] = new WeightedInstancesHandlerWrapper();

		// rules
		classifiers[16] = new DecisionTable();
		classifiers[17] = new M5Rules();

		// trees
		classifiers[18] = new M5P();
		classifiers[19] = new REPTree();

        // misc
        // classifiers[20] = new ElasticNet();
        // classifiers[21] = new IsotonicRegression();
        // classifiers[22] = new LeastMedSq();
        classifiers[20] = new IBk();
        classifiers[21] = new KStar();
        classifiers[22] = new LWL();
        classifiers[23] = new DecisionStump();
        // classifiers[27] = new DPCTree();
        classifiers[24] = new RandomForest();
        classifiers[25] = new AdditiveRegression();
        // classifiers[30] = new IterativeAbsoluteErrorRegression();
        classifiers[26] = new AttributeSelectedClassifier();

		return classifiers;
	}

	private static void writeResultsToCSV(Classifier[] classifiers, ArrayList<ClassifierResult[]> results, ArrayList<String> fileNames, String fileLocation) {
		// first create file object for file placed at location
		// specified by filepath
		File file = new File(fileLocation);
		try {
			// create FileWriter object with file as parameter
			FileWriter outputfile = new FileWriter(file);

			// create CSVWriter object filewriter object as parameter
			CSVWriter writer = new CSVWriter(outputfile);

			// adding header to csv
			String[] header = new String[fileNames.size() + 1];
			header[0] = "Classifier Name";

			for (int i = 0; i < fileNames.size(); i++) {
				header[i + 1] = fileNames.get(i);
			}

			writer.writeNext(header);

			// add data to csv

			for (int i = 0; i < classifiers.length; i++) {
				String[] data = new String[fileNames.size() + 1];
				data[0] = classifiers[i].getClass().getSimpleName();
				for (int j = 0; j < results.size(); j++) {

					ClassifierResult r = results.get(j)[i];
					int sig = r.getSignificance();
					String score = r.getScore().toString();
					if (sig != 0) {
						switch (sig) {
							case 1:
								data[j + 1] = score + " v";
								break;
							case 2:
								data[j + 1] = score + " *";
								break;
							default:
								data[j + 1] = String.format("%s %d", score, sig);
						}
					} else {
						data[j + 1] = score;
					}

				}
				writer.writeNext(data);
			}

			// closing writer connection
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
